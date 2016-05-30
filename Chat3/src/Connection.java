import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public abstract class Connection
{
	private Consumer<Serializable> onReceiveCallBack;
	private ConnectionThread connThread = new ConnectionThread();

	public Connection(Consumer<Serializable> onReceiveCallBack)
	{
		this.onReceiveCallBack = onReceiveCallBack;
		connThread.setDaemon(true);
	}

	public void startConnection() throws Exception
	{
		connThread.start();
	}

	public void send(Serializable data) throws Exception
	{
		//sending object
		connThread.out.writeObject(data);

	}

	public void closeConnection() throws Exception
	{
		//closes the connection and if there is an exception it will be handled
		//by the application
		connThread.socket.close();

	}

	protected abstract boolean isServer();
	protected abstract String getIP();
	protected abstract int getPort();

	private class ConnectionThread extends Thread
	{
		private Socket socket;
		private ObjectOutputStream out;

		public void run()
		{
			// creates socket and listens for connection
			//creates object streams since we are sending and receiving objects
			try(ServerSocket server = isServer() ? new ServerSocket(getPort()) : null;
					Socket socket = isServer() ? server.accept(): new Socket(getIP(), getPort());
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
			{
				this.socket = socket;
				this.out = out;
				socket.setTcpNoDelay(true);

				while(true)
				{
					Serializable data = (Serializable) in.readObject();
					onReceiveCallBack.accept(data);
				}
			}

			//exception for when connection is closed
			catch(Exception e)
			{
				onReceiveCallBack.accept("Connection Closed");
			}

		}
	}
}
