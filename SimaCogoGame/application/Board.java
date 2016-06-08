package application;
import java.awt.Point;
import java.io.Serializable;
//Author: Erik Barns
//CODE IS DEVELOPED FROM PREVIOUS CODE
public class Board implements Serializable {
	private char[][] board;
	private int score;
	private int val;
	private Board choice;
	private int depth;
	boolean player1_turn;
	boolean player2_turn;
	int ai_lvl = 3;
	boolean gameOver = false;
	boolean saveGame = false;
	String title = "Gameboy";
	boolean chat = true;
	public Board(){
		board = createBoard();
		score = 0;//game score
		depth = 0;
	}
	
	public Board(char[][] b, int d, int s, int v){
		board = b;
		depth = d;
		score = s;//game score
		val = v;//optimal successor score
	}
	public Board getChoice(){
		return choice;
	}
	public void setChoice(Board c){
		choice = c;
	}
	public int getVal(){
		return val;
	}
	public void setVal(int v){
		val = v;
	}
	public void setTitle(String t){
		title = t;
	}
	public char[][] getBoard(){
		return board;
	}
	public void setBoard(char[][] newboard){
		board = newboard;
	}
	public int getScore(){
		return score;
	}
	public void setScore(int s){
		score = s;
	}
	public int getDepth(){
		return depth;
	}
	public void setDepth(int d){
		depth = d;
	}
	public boolean gameOver(){
		if(gameOver)
			return true;
		for(int i=0;i<9;i++){
			if(board[0][i] == '-'){
				return false;
			}
		}
		gameOver = true;
		return true;
	}
	//creates char[][] of '-' characters. empty board
	public char[][] createBoard(){
		char[][] b = new char[9][9];
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				b[i][j] = '-';
		return b;
			
	}
	
	//make user move on board
	public boolean makeMove(char[][] temp, int col,char player){
		if(col < 0 || col > 8)//if move not valid
			return false;
		if(temp[0][col] != '-')//if column full
			return false;
		
		for(int i=temp.length-1;i>=0;i--){//look for top of tile stack in col
			if(temp[i][col] == '-'){
				temp[i][col] = player;
				int score;
				if(player == 'O')score = getScore()-calcScore(temp,i,col,player);
				else score = getScore()+calcScore(temp,i,col,player);
				setScore(score);//user is max
				//System.out.println(getScore());
				return true;
			}
		}
		return false;
	}
	
	//calculates how many points a tile scores for the given player
	public int calcScore(char[][] board,int r, int c, char player){
		int score = 0;
		if((c+1) <= 8){
			if(r+1 <= 8)//bottom right
				if(board[r+1][c+1] == player)
					score++;
			if(r-1 >= 0)//top right
				if(board[r-1][c+1] == player)
					score++;
			if(board[r][c+1] == player)//right
				score += 2;
		}
		if((c-1) >= 0){
			if(r+1 <= 8)//bottom left
				if(board[r+1][c-1] == player)
					score++;
			if(r-1 >= 0)//top left
				if(board[r-1][c-1] == player)
					score++;
			if(board[r][c-1] == player)//left
				score += 2;
		}
		if(r+1 <= 8)
			if(board[r+1][c] == player)//down
				score += 2;
		if(r-1 >= 0)
			if(board[r-1][c] == player)//up
				score += 2;
		//System.out.println(score);
		return score;
	}

}
