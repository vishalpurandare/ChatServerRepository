package com.vip.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {

	Server server;
	Socket socket;

	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		start();
	}

	public void run() {
		server.welcomeUser(socket);
		System.out.println("Name Assigned to the user: (thread) " + Thread.currentThread().getName());
		
		DataInputStream dataIn = null;
		BufferedReader br = null;
		
		try {
			dataIn = new DataInputStream(
					socket.getInputStream());
			
			while (true) {
				
				br = new BufferedReader(new InputStreamReader(dataIn));
				String message = br.readLine(); //dataIn.readUTF();
				
				if (!message.equals("")) {
					System.out.println("Sending message to all connected : " + message);
					server.sendToAll(message);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("In Here !");
			server.removeConnection(socket);
		}
	}

}
