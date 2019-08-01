package Server;


import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



import javax.swing.*;

public class ServerGUI {


    private static JTextArea textAreaLOG;
	private JFrame frame;
	private boolean isRunning =false;
    
    public Server server;
    
    public ServerGUI() {
    	initialize();
    	frame.setVisible(true);
    }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Server GUI");
		frame.getContentPane().setForeground(Color.WHITE);
		//1.1 Override event listeners
        frame.addWindowListener(new WindowAdapter() 
        {
          public void windowClosed(WindowEvent e)
          {
            System.out.println("CLOSED"); // DOESNT GET CALLED SOMEHOW
          }
        
          public void windowClosing(WindowEvent e)
          {
        	  if(server != null)
        		  server.terminate();
        	  System.exit(0);
          }
        });
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        
		textAreaLOG = new JTextArea();
		textAreaLOG.setBackground(Color.gray);
		textAreaLOG.setFont(new Font("Arial",0, 20));
		scrollPane.setViewportView(textAreaLOG);
		
		JButton btnNewButton = new JButton("Start");
		frame.getContentPane().add(btnNewButton, BorderLayout.NORTH);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {


				if(isRunning)
				{
					btnNewButton.setText("Start");
					server.terminate();
				}
				else {
					btnNewButton.setText("Stop");
					server = new Server(1234, ServerGUI.this);
				}
	
				isRunning = !isRunning;						
			}
			
		});
		
		
	}
    
    public void setServer(Server server) {
    	this.server = server;
    }

    public void addLog(String log) {
    	textAreaLOG.append(log+"\n");
    }
}