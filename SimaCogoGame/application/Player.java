package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Player {
	int port;

	Socket client;
	ObjectInputStream reader;
	ObjectOutputStream output;
	boolean play_ai;
	int selection;
	int id;
	public Player(int i,Socket c) throws IOException{
		id = i;
		client = c;
		//System.out.println("client connected");
		output = new ObjectOutputStream(client.getOutputStream());
		
		output.flush();
		sendTurn(i);
		reader = new ObjectInputStream(client.getInputStream());
		selection = reader.readInt();
		if(selection == 1)
			play_ai = true;
		else
			play_ai = false;
		System.out.println(play_ai);
	}
	public boolean playingAI(){
		return play_ai;
	}
	public void sendBoard(Board b) throws IOException{
		output.writeObject(b);
		output.flush();
	}
	public Board waitBoard() throws ClassNotFoundException, IOException{
		return (Board) reader.readObject();
	}
	public void sendTurn(int i) throws IOException{
		output.writeObject(i);
		output.flush();
	}
	public void sendGames(String[] files) throws IOException{
		output.writeObject(files);
		output.flush();
	}
	public int waitForOption() throws ClassNotFoundException, IOException{
		return (int) reader.readObject();
	}
	
}
