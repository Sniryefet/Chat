package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import Other.*;

/**
 * Client Handler class to handle clients socket input and output streams
 * and perform actions on server. EXTRENDS THREAD
 * @author Ofek Bader
 *
 */
public class Client_handler extends Thread {
    
    private Socket socket; // Client's socket
    private ServerGUI GUI; // Server GUI
    private Server server; // Server Thread with SocketServer
    private PrintWriter wrStream; // Writing Stream to client
    private String clientName = ""; // Client's name

    private boolean terminate = false; // a boolean to terminate the thread when requested
    
    /**
     * [Constructor] creates new Client thread to handle I/O.
     * @param s Client Socket
     * @param GUI Graphical User Interface
     * @param ser Server instance which is tied to
     */
    public Client_handler(Socket s, ServerGUI GUI, Server ser) {
        this.socket = s;
        this.GUI = GUI;
        this.server = ser;
        setDaemon(true);
    }

    /**
     * Run the Thread to handle client's communication IO
     */
    @Override
    public void run() {
        if(socket != null)
            System.out.println("started running");
        else {
            System.out.println("client crashed");
            return;
        }
        
        try {
            wrStream = new PrintWriter(socket.getOutputStream(), true); // get writing stream
            BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream())); // get reading stream

            int index; // the interpreter
            String data, msg, type, name;// data = raw clients packet, msg = parsed client's packet, type = type of message, name = clients name to send to
            while(!terminate){ // as long as not terminated then keep running.
                try {
                    while((data = in.readLine()) != null) { // if there is data to read then read it

                        index = data.indexOf(">");
                        msg = data.substring(index+1); // get 'msg' from string
                        type = data.substring(0, index+1); // get 'type' from string
                    	//System.out.println(type + " : " +msg); //debug
                        
                    	if(!isConnected()) { // if not connected and the message is a connection request then connect it.
                    		if(type.equals(messages.connect)) {
	                           	server.connectMessage(this, msg);
                    		} else continue;	
                    	}
                    	
                        System.out.println("data recieved: " + data);
                        logGUI(data);
                        
                        switch(type) {
                        case messages.broadcast: //if Broadcast message to all
                        	server.sendBroadcastMessage(msg, clientName);
                        	break;
                        case messages.getClients: // if requesting logged in users
                        	  sendClientList();
                        	break;
                        case messages.toName: // if sending a private message
                        	index = msg.indexOf(">");
                        	name = msg.substring(0, index);
                        	msg = msg.substring(index+1);
                        	System.out.println(name + " || " + msg);
                        	server.sendMessage(msg, name, this);
                        	break;
                        }
                    }
                    
                } catch (Exception e) {
                	System.out.println("Socket closed or socket error occoured: "+ e.toString());
                } finally {
                	server.clientDisconnect(this);
                	this.clientName = "";
                	terminate();

                	
                	try {
                        
                        wrStream.close();
                        wrStream = null;
                        
                        socket.close();
                        socket = null;
                	} catch (Exception closeEx) {
                		closeEx.printStackTrace();
                	}
                }
            }
        } catch (Exception error) {
        	error.printStackTrace();
        }

    }
    
    /**
     * Log data to textArea in GUI
     * @param log - text
     */
    private void logGUI(String log) {
    	if(GUI!=null)
    		GUI.addLog(log);
    }
    
    /**
     * send message via the writing stream of the socket
     * @param msg the message to send
     */
    public void sendMessage(String msg) {
        wrStream.println(msg);
    }

    /**
     * Encapsulate the message with a "Client List request" type header,
     * and then send it.
     */
    public void sendClientList() {
    	server.sendClientList(this);
    }
    
    /**
     * Set the client name property
     * @param name
     */
    public void setClientName(String name) {
    	this.clientName = name;
    	System.out.println("SET CLIENTNAME TO: "+name); //debug
    }
    
    /**
     * Get the client name property
     * @return Clients name
     */
    public String getClientName() {
    	return this.clientName;
    }
    
    /**
     * if connected once then the ClientName property shouldn't be empty, returns true if clientName property is not zero, false otherwise.
     * @return
     */
    public boolean isConnected() {
    	return (this.clientName.length() > 0);
    }
    
    /**
     * Send a request to the thread to terminate itself, and stop communicating with client.
     */
    public void terminate() {
    	this.terminate = true;
    	
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("Client_Handler ["+this.getId()+"] is terminating");
    }
}