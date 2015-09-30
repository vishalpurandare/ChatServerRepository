package com.vip.server;

import java.io.DataInputStream;
import java.io.IOException;
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
		DataInputStream dataIn = null;
		try {
			dataIn = new DataInputStream(
					socket.getInputStream());
			while (true) {
				String message = dataIn.readUTF();
				System.out.println("Sending Message: " + message);
				server.sendToAll(message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			server.removeConnection(socket);
		}
	}

}
