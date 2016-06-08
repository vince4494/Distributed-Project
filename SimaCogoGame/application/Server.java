package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	int port;
	ServerSocket server_socket;
	Socket client_socket;
	ObjectInputStream reader;
	ObjectOutputStream output;
	Board board = new Board();
	minMax cpu = new minMax(5);
	ArrayList<Player> Players = new ArrayList<Player>();
	Scoreboard scoreb;
	public Server(int p) throws IOException{
		port = p;
		server_socket = new ServerSocket(port);
	}
	
	//Author: Erik Barns
	//listens to socket connection and processes options to connect to
	// another player or initiate a game with the AI. 
	// Once game has been determined creates thread and restarts loop.
	public void listen() throws IOException, ClassNotFoundException{
		System.out.println("===Simacogo Host===");
		System.out.println("Listening on port: "+ port);
		int i=0;
		while (true){
			client_socket = server_socket.accept();
			Player p = new Player(i,client_socket);
			Players.add(p);
			System.out.println("client connected");
			if(p.selection == 3)
			{
				
				System.out.println("Loading game");
				String[] files;
				File temp = new File("SavedGames");
				files = temp.list();
				p.sendGames(files);
				int option = p.waitForOption();
				if(option > 0 && option < files.length){
					System.out.println("Choosing: " + files[option]);
			      InputStream file = new FileInputStream("SavedGames/"+files[option]);
			      InputStream buffer = new BufferedInputStream(file);
			      ObjectInput fileinput = new ObjectInputStream (buffer);
			      board = (Board)fileinput.readObject();
			      board.saveGame = false;
			      Game g = new Game(p,board,true,scoreb);
			      g.cpu.setMaxDepth(board.ai_lvl);
			      p.sendBoard(board);
			      Thread thread = new Thread(g);
			      thread.start();
			      fileinput.close();
				}
				else{
					client_socket.close();
				}
			}
			else if(p.playingAI()){
				int difficulty = p.reader.readInt();
				System.out.println("Difficulty: "+difficulty);
				board = new Board();
				board.ai_lvl = difficulty;
				board.chat = false;
				Game g = new Game(p,board,true,scoreb);
				g.cpu.setMaxDepth(difficulty);
				Thread thread = new Thread(g);
				thread.start();
			}
			else{
				i++;
				client_socket = server_socket.accept();
				Player p2 = new Player(i,client_socket);
				//p.sendTurn(i);
				Players.add(p2);
				System.out.println("client connected");
				//Chat game_chat = new Chat(p,p2);
				Game g = new Game(p,p2,new Board(),false,scoreb);
				Thread thread = new Thread(g);
				thread.start();
//				Thread chatthread = new Thread(game_chat);
//				chatthread.start();
				i++;
				//g.playPlayer();
			}
		}

	}
	
	//print current board 
	public void printBoard(){
		for(int i=0;i<9;i++){
			for(int j=0; j<9;j++)
				System.out.print(board.getBoard()[i][j]);
			System.out.println("");
		}
		System.out.println("");
	}
	
	//Vince Zipparro: wrote printing method to print highscores
	//print to the console
	public void printHighScores(){
		for(Board b : scoreb.highscores){
			System.out.println(b.title +": " + b.getScore());
		}
	}
	
	//Author: Erik Barns, Vince Zipparo
	//create server and listen for connections.
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		if(args.length != 1){
			return;
		}
		else{
			int port = Integer.valueOf(args[0]);
			System.out.println("Listening on port: "+ port);
			Server b = new Server(port);
			
			//Vince Zipparro: wrote input stream and output stream for scoreboard file
			//creates new scoreboard file and outputs to the client
			try{
		      InputStream file = new FileInputStream("Scoreboard.ser");
		      InputStream buffer = new BufferedInputStream(file);
		      ObjectInput fileinput = new ObjectInputStream (buffer);
		      b.scoreb = (Scoreboard) fileinput.readObject();
			}catch (FileNotFoundException e){
				System.out.println(e.getMessage());
				System.out.println("Creating new scoreboard");
				b.scoreb = new Scoreboard();
				FileOutputStream fout = new FileOutputStream("Scoreboard.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(b.scoreb);
				oos.close();
			} catch (InvalidClassException e){
				System.out.println(e.getMessage());
				System.out.println("Creating new scoreboard");
				b.scoreb = new Scoreboard();
				FileOutputStream fout = new FileOutputStream("Scoreboard.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(b.scoreb);
				oos.close();
			}
			b.listen();
			
		}
	}
}
