package application;

import java.io.IOException;

public class Game implements Runnable {
	Player player1;
	Player player2;
	Board board;
	minMax cpu = new minMax(5);
	boolean ai;
	public Game(Player p1, Player p2,Board b,boolean playai){
		player1 = p1;
		player2 = p2;
		board = b;
		ai = playai;
		System.out.println("game");
		System.out.println(ai);
	}
	public Game(Player p1,Board b,boolean playai){
		player1 = p1;
		board = b;
		ai = playai;
	}

	public void playAI() throws IOException, ClassNotFoundException{
		while(!board.gameOver()){
			board = player1.waitBoard();
			printBoard();
			cpu.min_Max(board,0,false);//calculate cpu move
			board.setBoard(cpu.getResponse());//set cpu move
			board.setScore(cpu.getnewScore());
			printBoard();
			player1.sendBoard(board);
		}
	}
	
	public void playPlayer() throws IOException, ClassNotFoundException{
		while(!board.gameOver()){
			board = player1.waitBoard();
			printBoard();
			player2.sendBoard(board);
			board = player2.waitBoard();
			printBoard();
			player1.sendBoard(board);
			//cpu.min_Max(board,0,false);//calculate cpu move
			//board.setBoard(cpu.getResponse());//set cpu move
			//board.setScore(cpu.getnewScore());
			System.out.println("end of turn score: "+board.getScore());
			printBoard();
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
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Tread start");
		System.out.println(ai);
		if(ai){
			try {
				playAI();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try {
				playPlayer();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
