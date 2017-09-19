package flash843;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Flash843Server implements Runnable {

	private ServerSocket server;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String xml;

	public Flash843Server() {
		xml = "<cross-domain-policy> "
				+ "<allow-access-from domain=\"*\" to-ports=\"1025-9999\"/>"
				+ "</cross-domain-policy> ";

		try {
			server = new ServerSocket(843);
			// System.out.println(xml);
			System.out.println("flash sandbox: 843 listening");
			new Thread(this).start();
		} catch (IOException e) {
			System.out.println("flash sandbox: 843 listender failed:\n" + e.getMessage());
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
				// System.out.println("request: " + info);

				if (info.indexOf("<policy-file-request/>") >= 0) {
					writer.write(xml + "\0");
					writer.flush();
					// System.out.println("send to: " +
					// client.getInetAddress());
				} else {
					writer.write("unknown request\0");
					writer.flush();
					System.out.println("unknown request: "
							+ client.getInetAddress());
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

	public static void main(String[] args) {
		new Flash843Server();
	}
}
