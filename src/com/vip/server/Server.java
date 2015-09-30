/**
 * 
 */
package com.vip.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vishal
 *
 */
public class Server {

	public Server(int port) throws Exception {

		listen(port);

	}

	private void listen(int port) {

		Map<Socket, DataOutputStream> outputStreams = new HashMap<Socket, DataOutputStream>();
		ServerSocket serverSocket = null;
		
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
				
				//create new server thread for this connection
				ServerThread st = new ServerThread(this, socket);

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

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}
