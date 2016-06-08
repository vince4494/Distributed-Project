package application;
import java.awt.Point;
import java.util.ArrayList;
//Author: Erik Barns
// WRITTEN PREVIOUSLY 
public class minMax {
	private int maxDepth;
	private Board response;
	public minMax(int depth){
		maxDepth = depth;
	}
	public minMax(){
	
	}	

	public void min_Max(Board current, int depth, boolean min_or_max){
		//if leaf node return score
		if(depth == maxDepth)
			return;
		ArrayList<Board> success = new ArrayList<Board>();
		success = getSuccessors(current,min_or_max);
		for(Board state : success){
			min_Max(state,state.getDepth(),!min_or_max);//recursively traverse the tree
		}
		if(min_or_max){//compute min score of nodes
			int val = Integer.MAX_VALUE;
			for(Board state : success){
				if(state.getVal() <= val){
						current.setChoice(state);
						val = state.getVal();
				}
			}
			current.setVal(val);//min val of successors
		}
		else{//compute max score of nodes
			int val = Integer.MIN_VALUE;
			for(Board state : success){
				if(state.getVal() > val){
					current.setChoice(state);//max val of succesors
					val = state.getVal();
				}
			}
			current.setVal(val);
		}
		
		
		if(current.getDepth() == 0){//if root node return choice
			response = current.getChoice();
		}
	}

	//returns response
	public char[][] getResponse(){
		return response.getBoard();
	}
	public int getnewScore(){
		return response.getScore();
	}
	//resturns list of boards. all valid moves
	public ArrayList<Board> getSuccessors(Board current, boolean minmax){
		ArrayList<Board> succs = new ArrayList<Board>();
		Board successor;
		for(int i=0;i<9;i++){
			char[][] s = deepCopy(current.getBoard());
			if(!minmax && (successor = makeMove(current,s, i, 'X')) != null)//if cpu
				succs.add(successor);
			else if((successor = makeMove(current,s, i, 'O')) != null){//if player
				succs.add(successor);
			}
		}
		return succs;
	}
	//finds next open space in col and creates new board from move
	public Board makeMove(Board curr, char[][] temp, int col,char player){
		if(temp[0][col] != '-')
			return null;
		
		for(int i=temp.length-1;i>=0;i--){//for each column, attempt to make move
			if(temp[i][col] == '-'){
				temp[i][col] = player;
				int score = calcScore(temp,i,col,player);
				if(player == 'O') return new Board(temp,curr.getDepth()+1,curr.getScore()-score,curr.getScore()-score);
				else return new Board(temp,curr.getDepth()+1,curr.getScore()+score,curr.getScore()+score);
			}
		}
		return null;
	}
	public int getMaxDepth(){
		return maxDepth;
	}
	//sets max depth of min max search
	public void setMaxDepth(int d){
		maxDepth = d;
	}
	//calculates the score of the move made
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
	//prints board
	public void printBoard(char[][] board){
		for(int i=0;i<9;i++){
			for(int j=0; j<9;j++)
				System.out.print(board[i][j]);
			System.out.println("");
		}
		System.out.println("");
	}
	//creates a deep copy of the char[][]
	public char[][] deepCopy(char[][] original){
		char[][] newboard = new char[9][9];
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				newboard[i][j] = original[i][j];
			}
		}
		return newboard;
	}
}
