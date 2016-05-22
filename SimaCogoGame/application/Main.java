package application;
	
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
	char marker;
	boolean user_turn = false;
	ObjectInputStream input;
	ObjectOutputStream output;
	char piece;
	boolean player;
	public Main() throws IOException{
		selection = -1;
		grid = new int[9][9];
		//Connect();
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			root = new AnchorPane();
			scene = new Scene(root,600,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			DrawMap();
			primaryStage.setScene(scene);
			primaryStage.show();
			resetButton(primaryStage);
			playGame();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void resetButton(Stage oceanStage){
		//reset button handler
		Button res = new Button("Play AI");//offer button to reset
        res.setLayoutY(500);
        res.setLayoutX(10);
        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {//closes window and creates a new one
            		try {
						Connect(true);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            
        };
        res.setOnAction(buttonHandler);
       root.getChildren().addAll(res);//add button to scene
       
		Button play = new Button("Connect to Player");//offer button to reset
        play.setLayoutY(530);
        play.setLayoutX(10);
        EventHandler<ActionEvent> connectHandler = new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {//closes window and creates a new one
            		
            			try {
							Connect(false);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            	
            
        };
        play.setOnAction(connectHandler);
       root.getChildren().addAll(play);//add button to scene
	}
	public void Connect(boolean play_ai) throws IOException, ClassNotFoundException{
		int port = 8080;
		client_socket = new Socket(InetAddress.getLoopbackAddress(),port);
		output = new ObjectOutputStream(client_socket.getOutputStream());
		output.writeBoolean(play_ai);
		output.flush();
		input = new ObjectInputStream(client_socket.getInputStream());
		int turn = (int)input.readObject();
		if(turn == 0){
			piece = 'O';
			user_turn = true;
			player = true;
		}
		else{
			piece = 'X';
			user_turn = false;
			player = false;
			cpuMove();
		}
	}
	public void playGame() throws ClassNotFoundException, IOException{
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
						if(board.makeMove(board.getBoard(),selection,piece)){
							user_turn = false;
							printBoard();
							try {
								
								output.writeObject(board);
								output.flush();
								cpuMove();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							
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

	public void cpuMove() throws ClassNotFoundException, IOException{
		//input = new ObjectInputStream(client_socket.getInputStream());
		drawNewBoard(board.getBoard());
		board = (Board)input.readObject();
		printBoard();
		//cpu.min_Max(board,0,false);//calculate cpu move
		//board.setBoard(cpu.getResponse());//set cpu move
		//board.setScore(cpu.getnewScore());
		System.out.println("end of turn score: "+board.getScore());
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
					drawMove(i,j,true);
				else if(b[i][j] == 'X')
					drawMove(i,j,false);

			}
		}
	}
	public void drawMove(int i,int j,boolean player){
		int col = selection;
		int row = 8;
//		while(row >= 0 && board.getBoard()[row][col] != '-'){
//			row--;
//		}
		Rectangle move = new Rectangle((j)*scale,(i)*scale,scale,scale);
		if(player){
			move.setFill(Color.GREEN);
			System.out.println("green block ");
		}
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
