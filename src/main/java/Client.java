import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;
import java.util.function.Consumer;


/*
Client class
 */
public class Client extends Thread{

	
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;

	
	private Consumer<Serializable> callback;

	// Default constructor
	Client(Consumer<Serializable> call){
	
		callback = call;
	}
	
	public void run() {
		
		try {
		socketClient= new Socket("127.0.0.1",5555);
	    out = new ObjectOutputStream(socketClient.getOutputStream());
	    in = new ObjectInputStream(socketClient.getInputStream());
	    socketClient.setTcpNoDelay(true);


		}
		catch(Exception e) {}

		// Continuously receive input
		while(true) {
			 
			try {
				Message msg = (Message) in.readObject();
				callback.accept(msg);
			}
			catch(Exception e) {}
		}
	
    }


	/*
	Helper Functions
	 */

	// Send output message
	public void send(Message newMessage) {
		
		try {
			out.writeObject(newMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
