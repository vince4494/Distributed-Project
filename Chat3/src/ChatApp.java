import java.net.InetAddress;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatApp extends Application
{
	// change boolean to true to launch server
	// change boolean to false to launch client
	private boolean isServer = true;



	private TextArea messages =  new TextArea();
	private Connection connection = isServer ? createServer() : createClient();


	//creates contents for the scene
	//establishes whether it is a client or server and gets messages for sending an receiving
	private Parent createContent()
	{
		messages.setPrefHeight(200);
		TextField input = new TextField();
		input.setOnAction(event ->{
			String message = isServer ? "Player 1: " : "Player 2: ";
			message += input.getText();
			input.clear();

			messages.appendText(message + "\n");
			try {
				connection.send(message);
			} catch (Exception e) {
				messages.appendText("Failed to send message\n");
			}


		});


		VBox root = new VBox(20,messages,input);
		root.setPrefSize(300,200);
		return root;

	}

	@Override
	public void init() throws Exception
	{
		connection.startConnection();
	}

	//creates scene
	@Override
	public void start(Stage scene) throws Exception
	{
		if(isServer == true)
		{
			scene.setTitle("Player 1");
		}
		else
		{

			scene.setTitle("Player 2");

		}

		scene.setScene(new Scene(createContent()));
		scene.show();

	}

	public void stop() throws Exception
	{
		connection.closeConnection();
	}

	//creates a server
	private Server createServer()
	{
		return new Server(55555, data -> {
			Platform.runLater(()-> {
				messages.appendText(data.toString() + "\n");
			});

		});
	}

	//creates a client
	private Client createClient()
	{
		return new Client("127.0.0.1",55555, data -> {
			Platform.runLater(()-> {
				messages.appendText(data.toString() + "\n");
			});

		});
	}


	public static void main(String[] args)
	{
		launch(args);
	}

}
