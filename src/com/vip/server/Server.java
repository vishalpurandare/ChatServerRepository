/**
 * 
 */
package com.vip.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Vishal
 *
 */
public class Server {

	Map<Thread, Socket> allThreadsMap = new HashMap<Thread, Socket>();
	ServerSocket serverSocket = null;

	public Server(int port) throws Exception {
		listen(port);
	}

	private void listen(int port) {
		try {
			// create serverSocket to listen at the port
			serverSocket = new ServerSocket(port);
			System.out.println("Server Listening on: " + serverSocket);

			// Server listening to request in infinite loop
			while (true) {
				Socket socket;
				socket = serverSocket.accept();
				System.out.println("Conncted to: " + socket);

				// create new server thread for this connection, as our server
				// is multi-threaded
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
		int port = 8000;
		try {

			Server server = new Server(port);
			System.out.println("Created server: " + server);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendToAll(Socket fromSocket, String message) throws IOException {

		Iterator<Map.Entry<Thread, Socket>> iter = allThreadsMap.entrySet()
				.iterator();

		Thread thisThread = Thread.currentThread();

		while (iter.hasNext()) {
			Entry<Thread, Socket> entry = iter.next();

			if (!thisThread.equals(entry.getKey())) {
				Socket outSoc = entry.getValue();
				PrintWriter pwThis = new PrintWriter(outSoc.getOutputStream());
				writeMessage(pwThis, Constants.outputSymbol + message, true);
			}

		}

	}

	public void removeConnection(Socket socket) {
		try {
			Iterator<Map.Entry<Thread, Socket>> iter = allThreadsMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<Thread, Socket> entry = iter.next();
				if (socket.equals(entry.getValue())) {
					entry.getValue().getOutputStream().close();
					iter.remove();
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void welcomeUser(Socket socket) throws IOException {

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		DataInputStream in = new DataInputStream(socket.getInputStream());
		PrintWriter pw = new PrintWriter(out);
		Thread thisThread = Thread.currentThread();

		writeMessage(pw, Constants.outputSymbol + Constants.welcomeMessage,
				true);
		writeMessage(pw, Constants.outputSymbol + Constants.enterNameLabel,
				true);
		writeMessage(pw, Constants.inputSymbol, false);

		Scanner sc = new Scanner(in);
		String userName = sc.nextLine();

		while (userName.equals("") || checkThreads(socket, userName)) {
			if (userName.equals("")) {
				writeMessage(pw, Constants.outputSymbol
						+ Constants.blankUserNameMessage, true);
			} else {
				writeMessage(pw, Constants.outputSymbol
						+ Constants.reenterUserNameMessage, true);
			}
			writeMessage(pw, Constants.inputSymbol, false);
			userName = sc.nextLine();
		}

		thisThread.setName(userName);
		// Intimate other users of this user connected
		writeMessage(pw, Constants.outputSymbol + Constants.welcomeThisUser,
				true);
		sendToAll(socket, "User: " + userName + " is connected !");

		if (allThreadsMap.size() == 0) {

		} else {
			Iterator<Map.Entry<Thread, Socket>> iter = allThreadsMap.entrySet()
					.iterator();

			while (iter.hasNext()) {
				Entry<Thread, Socket> entry = iter.next();
				writeMessage(pw, entry.getKey().getName(), true);
			}

		}

		allThreadsMap.put(thisThread, socket);

	}

	private boolean checkThreads(Socket socket, String userName) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread thread : threadSet) {
			if (thread.getName().equalsIgnoreCase(userName)) {
				return true;
			}
		}
		return false;
	}

	public void writeMessage(PrintWriter pw, String message, boolean newLine) {
		pw.write(message);
		if (newLine) {
			pw.write(Constants.lineSeparator);
		}
		pw.flush();
	}

}
