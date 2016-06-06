package application;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.InputEvent;
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
	Socket txt_socket;
	int port;
	Board board = new Board();
	char[][] oldBoard;
	int oldscore;
	minMax cpu = new minMax(5);
	char marker;
	boolean user_turn = false;
	Label label_turn;
	Label label_score;
	TextField gameName;
	TextArea usrChat;
	ObjectInputStream input;
	ObjectOutputStream output;
	Alert alert;
	char piece;
	int difficulty;
	boolean player;
	TextArea chat;
	boolean play_ai = false;
	List<Integer> highScores = new ArrayList<Integer>();

	public Main() throws IOException{
		selection = -1;
		grid = new int[9][9];
		//Connect();
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			root = new AnchorPane();
			scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			DrawMap();
			drawTurnLabel();
			primaryStage.setScene(scene);
			primaryStage.show();
			resetButton();
			playGame();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}



	private void resetButton(){



		//reset button handler
		Button res = new Button("Play AI");//offer button to reset
        res.setLayoutY(460);
        res.setLayoutX(10);
        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {//closes window and creates a new one
            		try {
            			play_ai = true;
            			Alert alert = new Alert(AlertType.CONFIRMATION);
            			alert.setTitle("Play AI");
            			alert.setHeaderText("Difficulty");
            			alert.setContentText("Choose the difficulty of the opponent");

            			ButtonType buttonTypeOne = new ButtonType("Easy");
            			ButtonType buttonTypeTwo = new ButtonType("Medium");
            			ButtonType buttonTypeThree = new ButtonType("Hard");
            			ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeCancel);

            			Optional<ButtonType> result = alert.showAndWait();
            			if (result.get() == buttonTypeOne){
            			    difficulty = 3;
            			} else if (result.get() == buttonTypeTwo) {
            			    difficulty = 5;
            			} else if (result.get() == buttonTypeThree) {
            			    difficulty = 7;
            			} else {
            			    // ... user chose CANCEL or closed the dialog
            			}
						Connect(1);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}

        };
        res.setOnAction(buttonHandler);
       root.getChildren().addAll(res);//add button to scene

		Button play = new Button("Connect to Player");//offer button to reset

        play.setLayoutY(490);
        play.setLayoutX(10);
        EventHandler<ActionEvent> connectHandler = new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {//closes window and creates a new one

            			try {
            				play_ai = false;
							Connect(0);
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

       Label label1 = new Label("Game Name: ");
       gameName = new TextField ("");
       label1.setLayoutY(555);
       label1.setLayoutX(10);
       gameName.setLayoutX(80);
       gameName.setLayoutY(550);
       root.getChildren().addAll(label1,gameName);
		Button save = new Button("Save Game");//offer button to reset
        save.setLayoutY(520);
        save.setLayoutX(10);
        EventHandler<ActionEvent> SaveHandler = new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {//closes window and creates a new one

            			try {
            				if(gameName.getText().length() == 0){
            					alert = new Alert(AlertType.INFORMATION);
            					alert.setTitle(" Save Error ");
            					alert.setHeaderText("ERROR");
            					alert.setContentText("Please enter a name to save the game");
            					alert.showAndWait();
            				}
            				else
            					SaveGame(gameName.getText());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}


        };
        save.setOnAction(SaveHandler);
       root.getChildren().addAll(save);//add button to scene

       Button load = new Button("Load Game");//offer button to reset
       load.setLayoutY(520);
       load.setLayoutX(90);
       EventHandler<ActionEvent> LoadHandler = new EventHandler<ActionEvent>() {
			@Override
           public void handle(ActionEvent event) {//closes window and creates a new one

           			try {
							loadGame();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
           		}


       };
       load.setOnAction(LoadHandler);
      root.getChildren().addAll(load);//add button to scene

      Button end = new Button("End Game");//offer button to reset
      end.setLayoutY(520);
      end.setLayoutX(170);
      EventHandler<ActionEvent> EndHandler = new EventHandler<ActionEvent>() {
			@Override
          public void handle(ActionEvent event) {//closes window and creates a new one

          			try {
							endGame();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
          		}


      };
      end.setOnAction(EndHandler);
     root.getChildren().addAll(end);//add button to scene

     Button submit = new Button("Submit");//offer button to reset
     submit.setLayoutY(9.1*scale);
     submit.setLayoutX(6.3*scale);
     EventHandler<ActionEvent> SubmitHandler = new EventHandler<ActionEvent>() {
			@Override
         public void handle(ActionEvent event) {//closes window and creates a new one

         			try {
							submitMove();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
         		}


     };
    submit.setOnAction(SubmitHandler);
    root.getChildren().addAll(submit);//add button to scene

    Button undo = new Button("Undo");//offer button to reset
    undo.setLayoutY(9.1*scale);
    undo.setLayoutX(5.3*scale);
    EventHandler<ActionEvent> UndoHandler = new EventHandler<ActionEvent>() {
			@Override
        public void handle(ActionEvent event) {//closes window and creates a new one
							System.out.println("Undoing move");
							undoMove();
        		}


    };


    undo.setOnAction(UndoHandler);
   root.getChildren().addAll(undo);//add button to scene

   //high score button handler
   //once the high score button is clicked it displays in the all the high scores in the array
	Button highScore = new Button("High Scores");
	highScore.setLayoutY(520);
	highScore.setLayoutX(250);
	EventHandler<ActionEvent> highScoreHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event)
		{
			chat.appendText(highScores.toString() + "\n");

		}

	};
	highScore.setOnAction(highScoreHandler);
    root.getChildren().add(highScore);//add button to scene
     Button send = new Button("Send");//offer button to reset
     send.setLayoutY(520);
     send.setLayoutX(8 * scale);
     EventHandler<ActionEvent> SendHandler = new EventHandler<ActionEvent>() {
			@Override
         public void handle(ActionEvent event) {//closes window and creates a new one
         			try {
         				if(play_ai){
         					chat.appendText("Chat function is disabled when playing the CPU\n");
         					return;
         				}
						sendMessage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

         		}


     };
     send.setOnAction(SendHandler);
    root.getChildren().addAll(send);//add button to scene
	}
	public void undoMove(){
		board.setBoard(oldBoard);
		board.setScore(oldscore);
		drawNewBoard(oldBoard);
		user_turn = true;
		updateTurnLabel(user_turn);
	//	root.getChildren().remove(root.getChildren().size()-1);
	}
	public void submitMove() throws IOException, ClassNotFoundException
	{
		if(user_turn)
		{

			output.writeObject(board);
			output.flush();
		}
		else
		{

			output.writeObject(board);
			output.flush();
			cpuMove();
		}



	}

	//function to get high scores from when a game ends
	public void getHighScore()
	{
		Integer score = board.getScore();
		highScores.add(score);
		Collections.sort(highScores,Collections.reverseOrder());

		//condition for when game ends
		if(board.gameOver())
		{
			//if list does not equal 5 then still loop through and print
			//since we do not want to get an index out of bonds error
			if(highScores.size() < 5)
			{
				for(int i =0; i < highScores.size(); i++)
				{
					chat.appendText(i + ":" + highScores.get(i));
				}

			}
			else
			{
				for(int i = 0; i < 5; i++)
				{
					chat.appendText(i + ":" + highScores.get(i));
				}
			}
		}
		//condition for if just the button is pressed
		else
		{
			if(highScores.size() < 5)
			{
				for(int i =0; i < highScores.size(); i++)
				{
					chat.appendText(i + ":" + highScores.get(i));
				}

			}
			else
			{
				for(int i = 0; i < 5; i++)
				{
					chat.appendText(i + ":" + highScores.get(i));
				}
			}
		}

	}

	public void sendMessage() throws IOException{
		String message = usrChat.getText();
		output.writeObject(message);
		output.flush();
		usrChat.setText("");
		chat.appendText("You: " + message + "\n");

	}
	public void endGame() throws IOException{
		chat.appendText("Leaving Game: You Forfeit!");
		board = new Board();
		root.getChildren().remove(0, root.getChildren().size());
		DrawMap();
		resetButton();
		input.close();
		output.close();
		client_socket.close();
		client_socket = null;
		user_turn = false;
		chat.appendText("Leaving Game: You Forfeit!\n");

	}
	public void loadGame() throws IOException, ClassNotFoundException{
		if(client_socket == null){
			Connect(3);
		}
	}
	public void SaveGame(String title) throws IOException{
		if(output != null){
			board.player1_turn = user_turn;
			board.saveGame = true;
			board.setTitle(title);
			output.writeObject(board);
			System.out.println("Saving Game");
		}
	}
	public void Connect(int option) throws IOException, ClassNotFoundException{
		int port = 8084;
		if(client_socket != null){
			System.out.println("Game in progress!");
			return;
		}
		client_socket = new Socket(InetAddress.getLoopbackAddress(),port);
		input = new ObjectInputStream(client_socket.getInputStream());
		int turn = (int)input.readObject();
		System.out.println(turn);
		output = new ObjectOutputStream(client_socket.getOutputStream());
		output.writeInt(option);
		output.flush();
		if(option == 1){
			System.out.println("Difficulty: " + difficulty);
			output.writeInt(difficulty);
			output.flush();
		}
		if(option == 3){
			String[] files = (String[]) input.readObject();
			String answer = writeLoadGames(files);
			output.writeObject(Integer.valueOf(answer));
			Object j = input.readObject();
			System.out.println(j);
			board = (Board) j;
			piece = 'O';
			user_turn = board.player1_turn;
			System.out.println(user_turn);
			System.out.println("Loaded Board:");
			printBoard();
			drawNewBoard(board.getBoard());
		}
		else{
			if(turn % 2 == 0){
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
		updateTurnLabel(user_turn);
	}
	public void playGame() throws ClassNotFoundException, IOException{
		EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent mevent) {
				// TODO Auto-generated method stub
				Point2D click = new Point2D(mevent.getX(),mevent.getY());
				String eventname = mevent.getEventType().getName();
				System.out.println(user_turn);
				if(!user_turn){
					printBoard();
					//drawNewBoard(board.getBoard());
					return;
				}
				switch(eventname){
				case "MOUSE_RELEASED":
						if((click.getX() > (9 * scale)) || (click.getY() > (9*scale)))
							return;
						selection = (int)(click.getX()/50);
						System.out.println(selection);
						oldBoard = boardCopy(board.getBoard());
						oldscore = new Integer(board.getScore());
						if(board.makeMove(board.getBoard(),selection,piece)){
							user_turn = false;
							updateTurnLabel(false);
							printBoard();
//							try {
								drawNewBoard(board.getBoard());
								//output.writeObject(board);
								//output.flush();
								//cpuMove();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							 catch (ClassNotFoundException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}

							printBoard();
						}
						if(board.gameOver()){
							Label over = new Label();
							over.setLayoutX(500);
							over.setLayoutY(70);
							if(board.getScore() < 0)
							{
								over.setText("YOU WON!");;
								getHighScore();
							}
							else if (board.getScore() == 0)
							{
								over.setText("TIE!");
								getHighScore();
							}
							else
							{
								over.setText("YOU LOST!");
								getHighScore();

							}
							root.getChildren().add(over);
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
		Object o;
		while((o = input.readObject()).getClass() != board.getClass()){
			String message = "Opponent: ";
			if(o.getClass() == message.getClass()) chat.appendText(message + ((String)o) + "\n");
		}
		oldBoard = boardCopy(board.getBoard());
		board = (Board) o;
		printBoard();
		//cpu.min_Max(board,0,false);//calculate cpu move
		//board.setBoard(cpu.getResponse());//set cpu move
		//board.setScore(cpu.getnewScore());
		System.out.println("end of turn score: "+board.getScore());
		drawNewBoard(board.getBoard());
		user_turn = true;
		updateTurnLabel(user_turn);
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
				else drawWhite(i,j);

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
		else{
			move.setFill(Color.BLUE);
		}
		root.getChildren().add(move);
	}
	public void drawWhite(int i,int j){
//		while(row >= 0 && board.getBoard()[row][col] != '-'){
//			row--;
//		}
		Rectangle move = new Rectangle((j)*scale,(i)*scale,scale,scale);
		move.setFill(Color.WHITE);
		move.setStroke(Color.BLACK);
		root.getChildren().add(move);
	}

	public String writeLoadGames(String[] files){
		chat.appendText("Choose a game to load by index:\n");
		for(int i=0; i<files.length; i++){
			chat.appendText("["+i+"] " + files[i] + "\n");
		}
		TextInputDialog dialog = new TextInputDialog("Load a Game");
		dialog.setTitle("Load a game");
		dialog.setHeaderText("Choose the index of the game you want to load.");
		dialog.setContentText("Please enter your number:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    return result.get();
		}
		else return null;
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
		chat = new TextArea();
		chat.setLayoutX(9.2 * scale);
		chat.setLayoutY(10);
		chat.setMaxWidth(300);
		chat.setMinHeight(500);
		root.getChildren().add(chat);
		usrChat = new TextArea();
		usrChat.setLayoutX(9.2 * scale);
		usrChat.setLayoutY(10.3*scale);
		usrChat.setMaxWidth(300);
		usrChat.setMaxHeight(40);
		root.getChildren().add(usrChat);

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

	public void drawTurnLabel(){
		label_turn = new Label("Turn: ");
		label_turn.setLayoutX(380);
		label_turn.setLayoutY(9.3 * scale);
		root.getChildren().add(label_turn);

		label_score = new Label("Score: ");
		label_score.setLayoutX(380);
		label_score.setLayoutY(9*scale);
		root.getChildren().add(label_score);
	}
	public void updateTurnLabel(boolean turn){
		System.out.println(turn);
		if(turn){
			label_turn.setText("Turn: Your Turn");
		}
		else
			label_turn.setText("Turn: Opponents Turn");

		label_score.setText("Score: "+board.getScore());
	}
	public char[][] boardCopy(char[][] currboard){
		char[][] temp = new char[9][9];
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				temp[i][j] = currboard[i][j];

		return temp;

	}
	public static void main(String[] args) {
		launch(args);
	}
}
