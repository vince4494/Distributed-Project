package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	int port;
	ServerSocket server_socket;
	Socket client_socket;
	ObjectInputStream reader;
	ObjectOutputStream output;
	Board board = new Board();
	minMax cpu = new minMax(5);
	public Server(int p) throws IOException{
		port = p;
		server_socket = new ServerSocket(port);
	}
	public void listen() throws IOException, ClassNotFoundException{
		client_socket = server_socket.accept();
		System.out.println("client connected");
		output = new ObjectOutputStream(client_socket.getOutputStream());
		output.flush();
		reader = new ObjectInputStream(client_socket.getInputStream());
		
		while(!board.gameOver()){
			board = (Board)reader.readObject();
			printBoard();
			cpu.min_Max(board,0,false);//calculate cpu move
			board.setBoard(cpu.getResponse());//set cpu move
			board.setScore(cpu.getnewScore());
			System.out.println("end of turn score: "+cpu.getnewScore());
			printBoard();
			output.writeObject(board);
		}
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
