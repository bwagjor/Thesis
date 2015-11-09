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


    public CommsWindow(final ScriptInterface app_) {

        super("Remote Access Server - Commands History");
  
        JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields,BoxLayout.X_AXIS));

        //here we will have the text messages screen
        messagesArea = new JTextArea();
       // messagesArea.setColumns(30);
       // messagesArea.setRows(10);
        messagesArea.setEditable(false);
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
					    //this method declared in the interface from TCPServer class is implemented here
					    //this method is actually a callback method, because it will run every time when it will be called from
					    //TCPServer class (at while)
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