package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	public Server(int p) throws IOException{
		port = p;
		server_socket = new ServerSocket(port);
	}
	public void listen() throws IOException, ClassNotFoundException{
		int i=0;
		while (true){
			client_socket = server_socket.accept();
			Player p = new Player(i,client_socket);
			Players.add(p);
			System.out.println("client connected");
			if(p.selection == 3){
				System.out.println("Loading game");
				String[] files;
				File temp = new File("SavedGames");
				files = temp.list();
				p.sendGames(files);
				int option = p.waitForOption();
				System.out.println("Choosing: " + files[option]);
		      InputStream file = new FileInputStream("SavedGames/"+files[option]);
		      InputStream buffer = new BufferedInputStream(file);
		      ObjectInput fileinput = new ObjectInputStream (buffer);
		      board = (Board)fileinput.readObject();
		      board.saveGame = false;
		      Game g = new Game(p,board,true);
		      p.sendBoard(board);
		      Thread thread = new Thread(g);
		      thread.start();
		      fileinput.close();
			}
			else if(p.playingAI()){
				Game g = new Game(p,new Board(),true);
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
				Game g = new Game(p,p2,new Board(),false);
				Thread thread = new Thread(g);
				thread.start();
				i++;
				//g.playPlayer();
			}
		}

//		output = new ObjectOutputStream(client_socket.getOutputStream());
//		output.flush();
		//reader = new ObjectInputStream(client_socket.getInputStream());
//		if(Players.get(0).playingAI()){
//			while(!board.gameOver()){
//				board = Players.get(0).waitBoard();
//				printBoard();
//				cpu.min_Max(board,0,false);//calculate cpu move
//				board.setBoard(cpu.getResponse());//set cpu move
//				board.setScore(cpu.getnewScore());
//				printBoard();
//				Players.get(0).sendBoard(board);
//			}
//		}
//		else{
//			while(!board.gameOver()){
//				board = Players.get(0).waitBoard();
//				printBoard();
//				Players.get(1).sendBoard(board);
//				board = Players.get(1).waitBoard();
//				printBoard();
//				Players.get(0).sendBoard(board);
//				//cpu.min_Max(board,0,false);//calculate cpu move
//				//board.setBoard(cpu.getResponse());//set cpu move
//				//board.setScore(cpu.getnewScore());
//				System.out.println("end of turn score: "+board.getScore());
//				printBoard();
//			}			
//		}

	}
	public void printBoard(){
		for(int i=0;i<9;i++){
			for(int j=0; j<9;j++)
				System.out.print(board.getBoard()[i][j]);
			System.out.println("");
		}
		System.out.println("");
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		if(args.length != 1){
			return;
		}
		else{
			int port = Integer.valueOf(args[0]);
			System.out.println("Listening on port: "+ port);
			Server b = new Server(port);
			b.listen();
		}
	}
}
