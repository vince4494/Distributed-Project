package application;

import java.io.DataOutputStream;
import java.io.IOException;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Chat implements Runnable{

	DataOutputStream out;
	public Chat(DataOutputStream output){
		
		out = output;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
