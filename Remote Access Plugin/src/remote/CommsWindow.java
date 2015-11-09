package remote;

import javax.swing.*;

import org.micromanager.api.ScriptInterface;

import mmcorej.CMMCore;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CommsWindow extends JFrame {
    private JTextArea messagesArea;
    private JButton startServer;
    private JScrollPane scroll;
    private ImageTCPServer mServer;

	//The simple GUI displayed to users upon opening the Remote Access plugin
	//Press start to begin Server

    public CommsWindow(final ScriptInterface app_) {

        super("Remote Access Server - Commands History");
  
        JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields,BoxLayout.X_AXIS));

        //here we will have the text messages screen
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        //Make history log scrollable
        scroll = new JScrollPane(messagesArea);
        startServer = new JButton("Start");
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // disable the start button
                startServer.setEnabled(false);

                //creates the object OnMessageReceived asked by the TCPServer constructor
                try {
					mServer = new ImageTCPServer(new ImageTCPServer.OnMessageReceived() {
					    @Override
						//Append received images to history log
					    public void messageReceived(String message) {
					        messagesArea.append("\n "+message);
					    }
					}, app_);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                mServer.start();
                
            }
        });

        //add the buttons and the text fields to the panel
        panelFields.add(scroll);
        panelFields.add(startServer);



        getContentPane().add(panelFields);

        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

        setSize(300, 170);
        setVisible(true);
    }
}
