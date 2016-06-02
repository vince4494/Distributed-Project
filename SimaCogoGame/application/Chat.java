package application;

import java.io.IOException;

public class Chat implements Runnable{
	Player player1;
	Player player2;
	
	public Chat(Player p1, Player p2){
		player1 = p1;
		player2 = p2;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
