package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	int port;
	ServerSocket server_socket;
	Socket client_socket;
	BufferedReader reader;
	PrintWriter output;
	public Server(int p) throws IOException{
		port = p;
		server_socket = new ServerSocket(port);
	}
	public void listen() throws IOException{
		client_socket = server_socket.accept();
		reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
		output = new PrintWriter(client_socket.getOutputStream(),true);
		String message = reader.readLine();
		
		System.out.println("CLient said: " + message);
		output.println("Received your message");
	}
	public static void main(String[] args) throws IOException{
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
