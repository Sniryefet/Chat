package Server;



import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Other.messages;

/**
 * Server class using Sockets to listen to port 1234, handles incoming clients requests by initiating new thread for each client. <br>
 * @author Ofek Bader
 *
 */
public class Server extends Thread{

    private int PORT = 1234; // port to listen to.
    public ServerGUI GUI; // the server GUI.

    java.util.concurrent.CopyOnWriteArrayList<Client_handler> clients; // thread-safe List of connected clients
    
    private boolean terminate = false; // a boolean to terminate the thread when requested.
    private ServerSocket ss; // Socket

    /**
     * Run the server thread and start listening to new incoming connections.
     */
    @Override
    public void run() { 
        System.out.println("Starting Server");
        
        try {
            ss = new ServerSocket(PORT); // listen to PORT
            System.out.println("Listening on port "+PORT);
        
            while(!terminate) { // run until terminated.
                Socket clientSocket = ss.accept(); // accept new incoming connection.
                Client_handler clientHandler = new Client_handler(clientSocket, GUI, this); //create new client handler to handle communciations
                clientHandler.start(); // start handling.
                this.clients.add(clientHandler); // add client to list.
                
                System.out.println("HELLO NEW CLIENT");
                logGUI("HELLO");
            }
        } catch (Exception e) {
        	System.out.println("ERROR WHILE RUNNING SERVER:" + e.toString());
            try {
            	clients.clear();
            	clients = null;
            	
                ss.close(); //try close resources
            } catch (Exception e2) {
            	e2.printStackTrace();
            }
            
        }
        System.out.println("TERMINATED SERVER");
    }

    /**
     * Send a broadcast message to all connected clients.
     * @param msg the message to send.
     */
    public void broadcastMessage(String msg) {
        try { 
            for(Client_handler c : new ArrayList<Client_handler>(clients)) {
            	if(c.isConnected()) {
                c.sendMessage(msg);
                System.out.println("sent to : " + c.getClientName() + " | " + c.getId());
            }
           }
        } catch(Exception e) {

        }
    }

    /**
     * Sending a private message.
     * @param msg - the message to send
     * @param name - who to send to
     * @param from - from who the message is sent
     */
    public void sendMessage(String msg, String name, Client_handler client) {
    	String from = client.getClientName();
        try { 
            for(Client_handler c : new ArrayList<Client_handler>(clients)) {
                if(c.getClientName().equals(name)) {
                    c.sendMessage("<Whisper> "+from+": "+msg);
                    client.sendMessage("<Whisper: "+name+"> "+from+": "+msg);
                    System.out.println("sent to : " + c.getClientName());
                    return;
                }

            }
        } catch(Exception e) {
        	System.out.println("ERROR IN Server.sendMessage, errorstack: "+ e.getMessage());
        }
    }
    
    /**
     * Sending a client list.
     * @param client
     */
    public void sendClientList(Client_handler client) {
        StringBuilder sb = new StringBuilder();
        sb.append("<SERVER> Connected clients: ");

        for(Client_handler c : new ArrayList<Client_handler>(clients)) {
            sb.append("("+c.getClientName()+") ");
        }

        client.sendMessage(sb.toString());
    }

    /**
     * sending a broadcast message to all connected clients.
     * @param msg - the message to send.
     * @param from - from who the message is sent.
     */
    public void sendBroadcastMessage(String msg, String from) {
    	broadcastMessage(from +": "+ msg);
    }
    
    /**
     * Sending a broadcast message about a new client connection.
     * @param client
     * @param name as the name chosen by the client
     */
    public void connectMessage(Client_handler client, String name) {
        for(Client_handler c : new ArrayList<Client_handler>(clients)) {
            if(c.isConnected() && c.getClientName().equals(name)){
            	client.sendMessage(messages.nameTaken+" Name already in use, please choose a different name.");
            	return;
            } 
        }
        
       	client.setClientName(name);
        broadcastMessage("<SERVER> "+client.getClientName()+" has connected!");
    }

    /**
     * sending broadcast message about a new client disconnection
     * @param client
     */
    public void disconnectMessage(Client_handler client) {
        broadcastMessage("<SERVER> " + client.getClientName() +" has disconnected!");
    }
    
    /**
     * Handles the disconnection of a client, removing the client from the list and sending a broadcast message about disconnection.
     * @param client
     */
    public void clientDisconnect(Client_handler client) {
    	clients.remove(client); //TODO: NEED TO MAKE IT THREAD SAFE
    	if(client.isConnected())
    		disconnectMessage(client);
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
     * Terminate this thread and all client thread connected.
     */
    public void terminate() {
    	this.terminate = true;
    	
    	 try { 
             for(Client_handler c : new ArrayList<Client_handler>(clients)) {
            	 c.terminate();
             }
             
             this.ss.close();
         } catch(Exception e) {
        	 System.out.println("Error while terminating 'Server':" + e.toString());
         }
    	 
     	System.out.println("Server ["+this.getId()+"] is terminating");
    	
    }

    /**
     * [Constructor] creates new server instance to listen to connections and handles chat IO.
     * @param PORT - port to listen
     * @param GUI - reference to GUI.
     */
    public Server(int PORT, ServerGUI GUI) {
        clients = new java.util.concurrent.CopyOnWriteArrayList<Client_handler>();

        setDaemon(true);
        this.PORT = PORT;
        this.GUI = GUI;

        this.start();
    }
}

