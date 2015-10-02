package com.vip.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread extends Thread {

	Server server;
	Socket socket;

	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		start();
	}

	public void run() {
		
		DataInputStream dataIn = null;
		Scanner sc = null;
		
		try {
			server.welcomeUser(socket);
			System.out.println("Name Assigned to the user: (thread) " + Thread.currentThread().getName());
			
			dataIn = new DataInputStream(
					socket.getInputStream());
			
			while (true) {
				
				sc = new Scanner(dataIn);
				String message = sc.nextLine(); //dataIn.readUTF();
				
				if (!message.equals("")) {
					System.out.println("Sending message to all connected : " + message);
					server.sendToAll(socket, Thread.currentThread().getName() + ": " + message);
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
