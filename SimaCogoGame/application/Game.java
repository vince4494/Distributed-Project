package application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Game implements Runnable {
	Player player1;
	Player player2;
	Board board;
	minMax cpu = new minMax(5);
	boolean ai;
	public Game(Player p1, Player p2,Board b,boolean playai){
		player1 = p1;
		player2 = p2;
		board = b;
		ai = playai;
		System.out.println("game");
		System.out.println(ai);
	}
	public Game(Player p1,Board b,boolean playai){
		player1 = p1;
		board = b;
		ai = playai;
	}

	public void playAI() throws IOException, ClassNotFoundException{
		System.out.println(board.gameOver());
		
		while(!board.gameOver()){
			System.out.println("waiting for board");
			board = (Board) player1.waitBoard();
			if(board.saveGame){
				System.out.println("Writing board");
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
	}
	
	public void playPlayer() throws IOException, ClassNotFoundException{
		while(!board.gameOver()){
			Object o;
			while((o = player1.waitBoard()).getClass() != board.getClass()){
				player2.writeObject(o);
			}
			board = (Board) o;
			printBoard();
			player2.sendBoard(board);
			while((o = player2.waitBoard()).getClass() != board.getClass()){
				player1.writeObject(o);
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
		
	}
	
	public void writeBoard(Board b) throws IOException{
		FileOutputStream fout = new FileOutputStream("SavedGames/"+board.title+".ser");
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(b);
		oos.close();
	}
	public void printBoard(){
		for(int i=0;i<9;i++){
			for(int j=0; j<9;j++)
				System.out.print(board.getBoard()[i][j]);
			System.out.println("");
		}
		System.out.println("");
	}
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
