package application;

import java.io.Serializable;
import java.util.ArrayList;

public class Scoreboard implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6662959459727787407L;
	ArrayList<Board> highscores = new ArrayList<Board>();
	public Scoreboard(){
		for(int i=4;i>=0;i--){
			Board b = new Board();
			b.setScore(i*5);
			b.setTitle("Game"+(i+1));
			highscores.add(b);
		}
	}
	
	public ArrayList<Board> getScoreboard(){
		return highscores;
	}
	
	
}
