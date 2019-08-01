package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Other.messages;

/**
 * Client class, using threading to handle I/O for network socket streams.
 * handles the session and performs actions such as sending messages to server and recieving.
 */
public class Client extends Thread {

    private PrintWriter wrStream; // Client Socket's writing stream.
    private BufferedReader reStream; // Client Socket's reading stream
    private Socket s; // client's socket
    private ClientGUI GUI; // Client GUI
    public String ipaddress; // Server IP address to connect toa
    public String clientName = ""; // Client Name

    private boolean terminate = false; // a boolean to terminate the thread when requested.
    
    /**
     * [Constructor]
     * @param GUI ClientGUI object instance as the graphical interface who receives input and output
     */
    public Client(ClientGUI GUI) {
        this.GUI = GUI;
    }

    /**
     * From Thread class, connecting to specified ip address and specified port, handling I/O
     */
    public void run() {
       System.out.println("Starting Client");
        try{
            s = new Socket(ipaddress, 1234);  //Connecting to IPADDRESS:1234
            wrStream = new PrintWriter(s.getOutputStream(), true); // get writing stream
            reStream = new BufferedReader(new InputStreamReader(s.getInputStream())); // get reading stream
            
            if(clientName.length() < 2) return; //if the Client name is too short than abort, the Client name must have a certain length
            connect(clientName);
            
            String data;
            while(!terminate){ // READ / LISTEN LOOP
                try {
                    while((data = reStream.readLine()) != null) { //if there is some data waiting to be read
                        System.out.println("[this is client] data recieved: " + data);
                        GUI.addLog(data); 
                        
                        if(data.length() >= messages.nameTaken.length() && data.substring(0,messages.nameTaken.length()).equals(messages.nameTaken))
                        {
                        	terminate = true;
                        	GUI.disconnected();
                        	break;
                        	
                        }
                        	
                    }
                    
                } catch (Exception e) {
                   System.out.println("read stream is closed or unavailable: " + e.getMessage());
                   terminate();
                }
            }

        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
            	// close socket and free resources
                
                wrStream.close();
                wrStream = null;
                
                reStream.close();
                reStream = null;
                
                s.close();

                
            } catch (Exception e2) {
                System.out.println("ERR, Coudln't close Client's Socket: " + e2.getMessage());
            }
        }
        
        System.out.println("Client terminated.");
    }

    /**
     * Outputting data to output stream of the socket
     * @param msg
     */
    public void sendMessage(String msg) {
    	if(wrStream != null) // if there is a writing stream
    		wrStream.println(msg);
    }
    
    /**
     * Outputting "connect" and "name" data to output stream of the socket, used to first connect to the chat server. <br>
     * must set IP address to the client's instance beforehand.
     * @param name Client chosen name
     */
    public void connect(String name) { 
    	sendMessage(messages.connect + name);
    }
    
    
    /**
     * Outputting "connect" and "name" data to output stream of the socket, used to first connect to the chat server
     * @param name Client chosen name
     * @param ipa - the ip address to connect
     */
    public void connect(String name, String ipa) { 
    	this.ipaddress = ipa;
    	connect(name);
    }

    
    public void disconnect() {
    	sendMessage(messages.disconnect);
    	terminate();
    }

    /**
     * Terminating the thread.
     */
    public void terminate() {
    	this.terminate = true;
    		try {
				s.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    }
}