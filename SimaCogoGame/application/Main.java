package application;
	
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	int[][] grid;
	final int scale = 50;
	Pane root;
	int selection;
	Scene scene;
	Simacogo go;
	Socket client_socket;
	int port;
	Board board = new Board();
	minMax cpu = new minMax(5);
	boolean user_turn = true;
	DataInputStream input;
	DataOutputStream output;
	
	public Main(){
		selection = -1;
		grid = new int[9][9];
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			Connect();
			root = new AnchorPane();
			scene = new Scene(root,450,450);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			DrawMap();
			primaryStage.setScene(scene);
			primaryStage.show();
			playGame();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void Connect() throws IOException{
		int port = 8080;
		client_socket = new Socket(InetAddress.getLoopbackAddress(),port);
		input = new DataInputStream(client_socket.getInputStream());
		output = new DataOutputStream(client_socket.getOutputStream());
	}
	public void playGame(){
		EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mevent) {
				// TODO Auto-generated method stub
				Point2D click = new Point2D(mevent.getX(),mevent.getY());
				String eventname = mevent.getEventType().getName();
				if(!user_turn)
					return;
				switch(eventname){
				case "MOUSE_RELEASED":
						selection = (int)(click.getX()/50);
						System.out.println(selection);
						if(board.makeMove(board.getBoard(),selection,'O')){
							user_turn = false;
							drawMove(selection,true);
							cpuMove();
							printBoard();
						}
						if(board.gameOver()){
							if(board.getScore() < 0)
								System.out.println("YOU WON!");
							else if (board.getScore() == 0)
								System.out.println("TIE!");
							else
								System.out.println("YOU LOST!");
						}
				break;
				}
			}
		
		};
		scene.setOnMouseReleased(mouseHandler);
		//printBoard();//print board
	}

	public void cpuMove(){
		cpu.min_Max(board,0,false);//calculate cpu move
		board.setBoard(cpu.getResponse());//set cpu move
		board.setScore(cpu.getnewScore());
		System.out.println("end of turn score: "+cpu.getnewScore());
		drawNewBoard(board.getBoard());
		user_turn = true;
	}
	public void printBoard(){
		for(int i=0;i<9;i++){
			for(int j=0; j<9;j++)
				System.out.print(board.getBoard()[i][j]);
			System.out.println("");
		}
		System.out.println("");
	}
	public void drawNewBoard(char[][] b){
		for(int i=8;i>=0;i--){
			for(int j=8;j>=0;j--){
				if(b[i][j] == 'O')
					drawMove(j,true);
				else if(b[i][j] == 'X')
					drawMove(j,false);

			}
		}
	}
	public void drawMove(int selection,boolean player){
		int col = selection;
		int row = 8;
		while(row >= 0 && board.getBoard()[row][col] != '-'){
			row--;
		}
		Rectangle move = new Rectangle((col)*scale,(row+1)*scale,scale,scale);
		if(player)move.setFill(Color.GREEN);
		else {
			move.setFill(Color.BLUE);
		}
		root.getChildren().add(move);
	}
	public void DrawMap(){
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				 Rectangle rect = new Rectangle(i*scale,j*scale,scale,scale);
				 rect.setFill(Color.WHITE);
				 rect.setStroke(Color.BLACK);
				 root.getChildren().add(rect);
				 
			}
		}
	}

	public void DrawMove(int player, Point p){
		Circle move  = new Circle();
		move.setCenterX(p.getX()*scale);
		move.setCenterY(p.getY()*scale);
		if (player == 0)
			move.setFill(Color.GREEN);
		else 
			move.setFill(Color.BLUE);
		root.getChildren().add(move);
		
				
	}
	public void emptygrid(){
		for(int i=0;i<grid.length;i++)
			for(int j=0;j<grid.length;j++)
				grid[i][j] = 0;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
