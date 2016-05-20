package application;
import java.util.Scanner;

public class Simacogo {
	Board board;
	minMax cpu;
	public Simacogo(){
		board = new Board();
		cpu = new minMax();
	}
	public static void main(String[] args){
		Simacogo game = new Simacogo();
		Scanner in = new Scanner(System.in);
		System.out.println("=== WELCOME TO SIMACOGO ===");
		System.out.println("Choose a difficulty");
		System.out.println("EASY\nMEDIUM\nHARD");
		String choice = in.next().toLowerCase();
		if(choice.equals("easy"))
			game.cpu.setMaxDepth(3);
		else if(choice.equals("medium"))
			game.cpu.setMaxDepth(5);
		else{
			game.cpu.setMaxDepth(3);
		}
		while(!game.board.gameOver()){
			game.printBoard();//print current board
			System.out.println("ENTER A COLUMN TO PLACE YOUR TILE");//accept user input
			int selection = in.nextInt()-1;
			while(!game.board.makeMove(game.board.getBoard(),selection,'O')){//prompt for move until valid move
				System.out.println("Oops! Try Again!");
				System.out.println("ENTER A COLUMN TO PLACE YOUR TILE");
				selection = in.nextInt()-1;
			}
			game.printBoard();//print board
			game.cpu.min_Max(game.board,0,false);//calculate cpu move
			game.board.setBoard(game.cpu.getResponse());//set cpu move
			game.board.setScore(game.cpu.getnewScore());
			System.out.println("end of turn score: "+game.cpu.getnewScore());
		}
		game.printBoard();
		if(game.board.getScore() < 0)
			System.out.println("YOU WON!");
		else if (game.board.getScore() == 0)
			System.out.println("TIE!");
		else
			System.out.println("YOU LOST!");
	}
	
	//print current board
	public void printBoard(){
		for(int i=0;i<9;i++){
			for(int j=0; j<9;j++)
				System.out.print(board.getBoard()[i][j]);
			System.out.println("");
		}
		System.out.println("");
	}
}
