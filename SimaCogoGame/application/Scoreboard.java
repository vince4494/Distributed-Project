package application;

import java.io.Serializable;
import java.util.ArrayList;

//Vince Zipparro: wrote scoreboard class
@SuppressWarnings("serial")
public class Scoreboard implements Serializable{

	/**
	 * 
	 */
	//scores are stored in arraylist
	ArrayList<Board> highscores = new ArrayList<Board>();
	public Scoreboard(){
		for(int i=4;i>=0;i--){
			Board b = new Board();
			b.setScore(i*5);
			b.setTitle("Game"+(i+1));
			highscores.add(b);
		}
	}
	//get scores method
	public ArrayList<Board> getScoreboard(){
		return highscores;
	}


}
