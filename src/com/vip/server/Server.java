/**
 * 
 */
package com.vip.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Vishal
 *
 */
public class Server {

	Map<Socket, DataOutputStream> outputStreams = new HashMap<Socket, DataOutputStream>();
	ServerSocket serverSocket = null;

	public Server(int port) throws Exception {
		listen(port);
	}

	private void listen(int port) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Listening on: " + serverSocket);
			while (true) {
				Socket socket;
				socket = serverSocket.accept();
				System.out.println("Conncted to: " + socket);
				DataOutputStream dataOut = new DataOutputStream(
						socket.getOutputStream());
				outputStreams.put(socket, dataOut);
				// create new server thread for this connection
				ServerThread st = new ServerThread(this, socket);
				System.out.println("Created the server thread: " + st);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		try {
			Server server = new Server(port);
			System.out.println("Created server: " + server);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendToAll(String message) {
		try {
			Iterator<Map.Entry<Socket, DataOutputStream>> iter = outputStreams
					.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Socket, DataOutputStream> entry = iter.next();
				DataOutputStream out = entry.getValue();
				out.writeUTF(message);
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeConnection(Socket socket) {
		try {
			Iterator<Map.Entry<Socket, DataOutputStream>> iter = outputStreams
					.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Socket, DataOutputStream> entry = iter.next();
				if (socket.equals(entry.getKey())) {
					DataOutputStream out = entry.getValue();
					out.close();
					iter.remove();
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void welcomeUser(Socket socket) {
		BufferedReader br = null;
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			out.writeUTF("Welcome, to ViP Chat Server!");
			out.writeUTF("Enter Your Name: ");
			
			br = new BufferedReader(new InputStreamReader(in));
			String userName = br.readLine(); //dataIn.readUTF();
			
			Thread.currentThread().setName(userName);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
