package application;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.ArrayList;

//Author: Erik Barns
public class Game implements Runnable {
	Player player1;
	Player player2;
	Board board;
	minMax cpu = new minMax(5);
	boolean ai;
	Scoreboard scoreboard;
	
	public Game(Player p1, Player p2,Board b,boolean playai,Scoreboard sb){
		player1 = p1;
		player2 = p2;
		board = b;
		ai = playai;
	}
	
	public Game(Player p1,Board b,boolean playai, Scoreboard sb){
		player1 = p1;
		board = b;
		ai = playai;
	}

	//if plyer is playing AI initiate game loop and MinMax algorithm 
	public void playAI() throws IOException, ClassNotFoundException, SocketException{
		System.out.println(board.gameOver());
		while(!board.gameOver()){
			try{
				System.out.println("waiting for board");
				Object o = player1.waitBoard();
				if(o == null){
					board.gameOver = true;
					return;
				}
				else if(o instanceof Integer){
					sendScoreBoard(board,player1);
					continue;
				}
				else
					board = (Board) o;
				if(board.saveGame){
					System.out.println("Writing board");
					board.ai_lvl = cpu.getMaxDepth();
					writeBoard(board);
					board.saveGame = false;
					
				}
				else{
					printBoard();
					cpu.min_Max(board,0,false);//calculate cpu move
					board.setBoard(cpu.getResponse());//set cpu move
					board.setScore(cpu.getnewScore());
					printBoard();
					player1.sendBoard(board);
				}
			}
			catch (SocketException e){
				System.out.println("Player has leftjjj");
				board.gameOver = true;
			}
		}
		processScore(board,true);
	}
	
	//if player is playing another player initiate game loop that waits for board and passes between the two.
	public void playPlayer() throws IOException, ClassNotFoundException{
		while(!board.gameOver()){
			Object o;
			while((o = player1.waitBoard()) != null && o.getClass() != board.getClass()){
				if(o instanceof Integer){
					sendScoreBoard(board,player1);
				}
				player2.writeObject(o);
			}
			System.out.println(o);
			if(o == null){
				board.gameOver = true;
				System.out.println("hey");
				player2.writeObject("The Player has left the game!");
				player2.sendBoard(board);
				player1 = null;
				processScore(board,true);
				return;
			}
			board = (Board) o;
			printBoard();
			player2.sendBoard(board);
			while((o = player2.waitBoard()) != null && o.getClass() != board.getClass()){
				if(o instanceof Integer){
					sendScoreBoard(board,player2);
				}
				player1.writeObject(o);
			}
			if(o == null){
				board.gameOver = true;
				System.out.println("hey");
				player1.writeObject("The Player has left the game!");
				player1.sendBoard(board);
				player2 = null;
				processScore(board,true);
				return;
			}
			board = (Board) o;
			//board = (Board) player2.waitBoard();
			printBoard();
			player1.sendBoard(board);
			//cpu.min_Max(board,0,false);//calculate cpu move
			//board.setBoard(cpu.getResponse());//set cpu move
			//board.setScore(cpu.getnewScore());
			System.out.println("end of turn score: "+board.getScore());
			printBoard();
		}
		processScore(board,true);
	}
	
	//process score and store high scores
	public void processScore(Board b,boolean status) throws IOException, ClassNotFoundException{
		System.out.println("Processing score with scoreboard");
		InputStream file = new FileInputStream("Scoreboard.ser");
		InputStream buffer = new BufferedInputStream(file);
		ObjectInput fileinput = new ObjectInputStream (buffer);
		scoreboard = (Scoreboard) fileinput.readObject();
		int i = 0;
		if(status){
			for(Board record : scoreboard.highscores){
				if(Math.abs(record.getScore()) < Math.abs(b.getScore())){
					scoreboard.highscores.add(i,b);
					FileOutputStream fout = new FileOutputStream("Scoreboard.ser");
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(scoreboard);
					oos.close();
					break;
				}
				i++;
			}
		}
		System.out.println("Sending scoreboard to players");
		for(int j=1;j<=5;j++){
			Board score = scoreboard.highscores.get(j-1);
			String line = "["+j+"] "+score.title+": " + Math.abs(score.getScore()) + "\n";
			System.out.println(line);
			if (player1 != null)player1.writeObject(line);
			if (player2 != null) player2.writeObject(line);
		}
		if (player1 != null)player1.writeObject(board);
		if(player2 != null) player2.writeObject(board);
	}
	
	public void sendScoreBoard(Board b,Player plyr) throws IOException, ClassNotFoundException{
		System.out.println("Processing score with scoreboard");
		InputStream file = new FileInputStream("Scoreboard.ser");
		InputStream buffer = new BufferedInputStream(file);
		ObjectInput fileinput = new ObjectInputStream (buffer);
		scoreboard = (Scoreboard) fileinput.readObject();
		int i = 0;
		System.out.println("Sending scoreboard to players");
		for(int j=1;j<=5;j++){
			Board score = scoreboard.highscores.get(j-1);
			String line = "["+j+"] "+score.title+": " + Math.abs(score.getScore()) + "\n";
			System.out.println(line);
			plyr.writeObject(line);
		}
		plyr.writeObject(board);
	}
	//close player sockets
	public void closeSockets() throws IOException{
		player1.reader.close();
		player2.reader.close();
		player1.output.close();
		player2.output.close();
	}
	//save board to saved games folder 
	public void writeBoard(Board b) throws IOException{
		FileOutputStream fout = new FileOutputStream("SavedGames/"+board.title+".ser");
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(b);
		oos.close();
	}
	
	//print he current board 
	public void printBoard(){
		for(int i=0;i<9;i++){
			for(int j=0; j<9;j++)
				System.out.print(board.getBoard()[i][j]);
			System.out.println("");
		}
		System.out.println("");
	}
	
	//Handles if game is PVP or pvai
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Tread start");
		System.out.println(ai);
		if(ai){
			try {
				playAI();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else{
			try {
				playPlayer();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
