package Client;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Other.messages;
import javax.swing.*;
import javax.swing.border.Border;
/**
 * Graphical User Interface for Client side application <br>
 * [1] can connect <br>
 * [2] send message by name <br>
 * [3] send message to all [broadcast] <br>
 * [4] receive from server new client connection messages and disconnect [broadcast]
 */
public class ClientGUI {
    
	private JFrame frame;
	private JTextField textFieldRecipient;
	private JTextField textFieldMessageChat;
	private JLabel lblName;
	private JTextField textFieldName;
	private JLabel lblAdress;
	private JTextField txtLadress;
	private JButton btnShowonline;
	private JButton btnClear;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JButton btnConnect;

    private Client client;

    public ClientGUI() {
    	initialize();
    	frame.setVisible(true);
    }
    
    /**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//1. create a frame
		frame = new JFrame("Client GUI");
		frame.setBounds(100, 100, 692, 428);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     //1.1 Override event listeners
        frame.addWindowListener(new WindowAdapter() 
        {
          @Override
		public void windowClosed(WindowEvent e)
          {
            System.out.println("CLOSED"); // DOESNT GET CALLED SOMEHOW
          }
        
          @Override
		public void windowClosing(WindowEvent e)
          {
        	  if(client!=null)
        		  client.terminate();
          }
        });
		
        //2. create a top panel
		JPanel panel_top = new JPanel();
		frame.getContentPane().add(panel_top, BorderLayout.NORTH);
		panel_top.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		//2.1 create connect button
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(client == null) {
					setClient(new Client(ClientGUI.this));
					btnConnect.setText("Disconnect");
				} else {
					client.disconnect();
					client.terminate();
					disconnected();
				}
	
			}
		});
		panel_top.add(btnConnect);
		
		//2.2.1 create name label
		lblName = new JLabel("name");
		panel_top.add(lblName);
		
		//2.2.2 create name text field
		textFieldName = new JTextField();
		textFieldName.setText("anna");
		panel_top.add(textFieldName);
		textFieldName.setColumns(10);

		//2.3.1 create address|ip label
		lblAdress = new JLabel("Address");
		panel_top.add(lblAdress);
		
		//2.3.2 create text field for address|ip
		txtLadress = new JTextField();
		txtLadress.setText("LocalHost");
		panel_top.add(txtLadress);
		txtLadress.setColumns(10);
		
		//2.4 create Show Whose Online button
		btnShowonline = new JButton("ShowOnline");
		panel_top.add(btnShowonline);
		
		btnShowonline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getClientList();
			}
		});
		
		//2.5 clear textarea | log panel
		btnClear = new JButton("Clear");
		panel_top.add(btnClear);
		
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearLog();
			}
			
		});
		
		//3. create lower panel
		JPanel panel_btn = new JPanel();
		frame.getContentPane().add(panel_btn, BorderLayout.SOUTH);
		
		//3.1 create textfield for recipent - for "whispering"
		textFieldRecipient = new JTextField();
		panel_btn.add(textFieldRecipient);
		textFieldRecipient.setColumns(10);
		
		//3.2 create textfield for chat messages.
		textFieldMessageChat = new JTextField();
		panel_btn.add(textFieldMessageChat);
		textFieldMessageChat.setColumns(40);
		
		//3.3 create send button to send messages
		JButton btnSendChatMessage = new JButton("Send");
		btnSendChatMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendChatMessage(textFieldMessageChat.getText());
			}
		});
		panel_btn.add(btnSendChatMessage);
		
		//3.4.1 create scroll pane for scrolling view.
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		//3.4.2 create a textArea for log, and put it inside the scrollPane.
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
        java.awt.Font trb = new java.awt.Font("TimesRoman", java.awt.Font.BOLD, 18);
        textArea.setFont(trb);
        textArea.setBorder(BorderFactory.createSoftBevelBorder(1)); 
        
        //3.4.3 create textArea for participants/chat members
        /*textAreaClients = new JTextArea();
        textAreaClients.setColumns(9);
        textAreaClients.setBackground(Color.LIGHT_GRAY);
        textAreaClients.setBorder(BorderFactory.createEtchedBorder()); 
        scrollPane.setRowHeaderView(textAreaClients);*/

	}

    
	/**
	 * Set a new client for this GUI for communicating with the chat server, using the name and address from GUI input.
	 * @param c as the new client object
	 */
    public void setClient(Client c) {
    	if(client != null)
    		this.client.terminate();
    	
        this.client = c;
        c.ipaddress = txtLadress.getText();
        c.clientName = textFieldName.getText();
        this.client.start();
        
    }

    /**
     * sending messages from text field via socket stream to chat server
     * @param msg the text to send
     */
    public void sendMessage(String msg) {
        if(client == null) return;
        if(msg.length() == 0) return;

        client.sendMessage(msg);
        clearText();
    }
    
    /**
     * sending a message to the server to broadcast to all of it's clients connected to it.
     * @param msg as the message
     */
    private void broadcastMessage(String msg) {
		msg = messages.broadcast+msg;
		sendMessage(msg);
    }
    
    /**
     * sending a message to the server to a single client/user connected to the server.
     * @param msg as the message
     */
    private void unicastMessage(String msg) {
		msg = messages.toName+textFieldRecipient.getText()+">"+msg;
		sendMessage(msg);
    }
    
    /**
     * sending a CHAT message to the server.
     * @param msg as the message
     */
    public void sendChatMessage(String msg) {
    	if(textFieldMessageChat.getText().length() == 0)
    		return;
    	
    	if(textFieldRecipient.getText().length() > 0)
    		unicastMessage(msg);
    	else 
    		broadcastMessage(msg);
    }
    
    /**
     * request from the server the client list connected to the chat server.
     */
    public void getClientList() {
    	sendMessage(messages.getClients);
    }
    
    /**
     * update "connect" button text.
     */
    public void disconnected() {
		client = null;
		btnConnect.setText("Connect");
    }
    
    /**
     * added text to the TextAreaLog on gui
     * @param text
     */
    public void addLog(String text) {
    	textArea.append(text + "\n");
    }

    /**
     * clear text of the Chat message field
     */
    private void clearText() {
        if(textFieldMessageChat == null) return;

        textFieldMessageChat.setText("");
    }
    
    /**
     * clear text of the TextAreaLog.
     */
    public void clearLog() {
        if(textArea == null) return;

        textArea.setText("");
    }
    

    
 
}