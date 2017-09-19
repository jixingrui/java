package common.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FlashPolicyService implements Runnable {
	private ServerSocket server;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String xml;
	private static int port = 8843;

	static class SingletonHolder {
		static FlashPolicyService instance = new FlashPolicyService();
	}

	public static FlashPolicyService start() {
		return SingletonHolder.instance;
	}

	private FlashPolicyService() {
		xml = "<cross-domain-policy>"
				+ "<site-control permitted-cross-domain-policies=\"master-only\"/>"
				+ "<allow-access-from domain=\"*\" to-ports=\"1025-9999\"/>"
				+ "</cross-domain-policy>";
		try {
			server = new ServerSocket(port);
			System.out.println("Flash policy service: " + port);
			new Thread(this).start();
		} catch (IOException e) {
			System.out.println("Flash policy service: " + port + " failed\n"
					+ e.getMessage());
		}
	}

	public void run() {
		while (true) {
			Socket client = null;
			try {
				client = server.accept();
				InputStreamReader input = new InputStreamReader(
						client.getInputStream(), "UTF-8");
				reader = new BufferedReader(input);
				OutputStreamWriter output = new OutputStreamWriter(
						client.getOutputStream(), "UTF-8");
				writer = new BufferedWriter(output);
				StringBuilder data = new StringBuilder();
				int c = 0;
				while ((c = reader.read()) != -1) {
					if (c != '\0')
						data.append((char) c);
					else
						break;
				}
				String info = data.toString();
				if (info.indexOf("<policy-file-request/>") >= 0) {
					writer.write(xml + "\0");
					writer.flush();
					System.out.println("Flash policy served for "+client.getRemoteSocketAddress());
				} else {
					writer.write("unknown request\0");
					writer.flush();
					System.out.println("unknown request: "
							+ client.getInetAddress() + " " + info);
				}
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (client != null) {
						client.close();
						client = null;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					System.gc();
				}
			}
		}
	}

//	public static void main(String[] args) {
//		FlashPolicyService.start();
//	}
}
