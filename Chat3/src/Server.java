import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server extends Connection
{
	private int port;

	//calls constructor of the super class
	public Server(int port, Consumer<Serializable> onReceiveCallBack) {
		super(onReceiveCallBack);

		this.port= port;

	}

	@Override
	protected boolean isServer() {
		return true;
	}



	@Override
	protected String getIP() {
		return null;
	}

	@Override
	protected int getPort() {
		return port;
	}

}
