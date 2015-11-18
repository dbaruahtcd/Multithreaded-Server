
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Server implements Runnable {

	private Socket socket = null;
	private static boolean listening = true; 
	private String serverReply, clientMsg, serverReply1, serverReply2, serverReply3;
	private static ServerSocket socketServer = null;
	private static ExecutorService executor = Executors.newFixedThreadPool(2);


	public Server(Socket socket) {
		this.socket = socket;
	}


	
	public void run() {

		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream out  = new PrintStream(socket.getOutputStream());
			while((clientMsg = in.readLine()) != null)
			{
				if  (clientMsg.startsWith("HELO"))
				{
					
					serverReply = clientMsg;
					serverReply1 = "IP:" + socket.getLocalAddress();
					serverReply2 = "Port:" + socket.getLocalPort();
					serverReply3 = "StudentID:1234";
					out.println(serverReply + "\n" + serverReply1.replace("/", "") + "\n" + serverReply2 + "\n" + serverReply3);
					out.flush();
					
				}
				else if(clientMsg.matches("KILL_SERVICE"))
				{

					System.out.println("Closing down the server...");
					listening = false;
					in.close();
					out.close();
					executor.shutdown();
					socketServer.close();
					System.exit(0);
				}
				else 
					continue;
				
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
