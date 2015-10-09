
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server implements Runnable {

	private Socket socket = null;
	private static boolean listening = true; 
	private String serverReply, clientMsg;
	private static ServerSocket socketServer = null;
	private static ExecutorService executor = Executors.newFixedThreadPool(2);


	public Server(Socket socket) {
		this.socket = socket;
	}


	
	public void run() {

		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream out  = new DataOutputStream(socket.getOutputStream());
			while((clientMsg = in.readLine()) != null)
			{
				if(clientMsg.matches("KILL_SERVICE"))
				{

					System.out.println("Closing down the server...");
					listening = false;
					in.close();
					out.close();
					executor.shutdown();
					socketServer.close();
					System.exit(0);
				}
				serverReply = clientMsg + " IP : " + socket.getLocalAddress() + " Port :" + socket.getLocalPort()+ '\n' ;
				out.writeChars("SERVER :" + serverReply);
			}
		}
		catch(IOException e)
		{
			System.out.println("IO error has occured");
		}


	}

	public static void main(String args[]) throws IOException 
	{

		if(args.length != 1)
		{
			System.err.println("Usage : Server <portnumber>");
			System.exit(1);
		}

		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println( sdf.format(cal.getTime()) );
		socketServer = new ServerSocket(Integer.parseInt(args[0]));
		System.out.println("SERVER : Waiting for client request on port : " + socketServer.getLocalPort());
		
		try
		{	
			while(listening)
			{
				Socket conSocket = socketServer.accept();
				Server sr = new Server(conSocket);
				executor.execute(sr);
			}

		}
		catch(SocketException e)
		{
			System.err.println("Disconnected");
		}

	}

}

