package application;

import java.io.EOFException;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


//Author: Erik Barns
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
	
	//is playing cpu
	public boolean playingAI(){
		return play_ai;
	}
	
	//send board out socket
	public void sendBoard(Board b) throws IOException{
		try{
			output.writeObject(b);
			output.flush();
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	//wait for board and handle if socket connection error. 
	public Object waitBoard() throws ClassNotFoundException, IOException{
		try{
			Object o = reader.readObject();
			System.out.println(o);
			return o;
		}
		catch (SocketException e){
			System.out.println("Player has left");
			return null;
		} catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	//send integer 
	public void sendTurn(int i) throws IOException{
		output.writeObject(i);
		output.flush();
	}
	
	//write object out socket 
	public void writeObject(Object o ) throws IOException, SocketException{
		try{
			output.writeObject(o);
			output.flush();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	//send list of saved games 
	public void sendGames(String[] files) throws IOException{
		output.writeObject(files);
		output.flush();
	}
	
	//wait for selection 
	public int waitForOption() throws ClassNotFoundException, IOException{
		try{
			return (int) reader.readObject();
		} catch(EOFException e){
			return -1;
		}
	}
	
}
