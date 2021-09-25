package com.control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;


public class ChatHost extends Thread {

	public static boolean flag = true;
	private ServerSocket svSocket;

	public ChatHost() throws NumberFormatException, IOException {
		svSocket = new ServerSocket(4000);
	}

	public void run() {
		while (flag) {

			try {
				@SuppressWarnings("unused")
				Socket socket = svSocket.accept();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Falha ao receber conex�o", "Error chat",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}

	}

	public void closeSocket() {
		try {
			svSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


}
