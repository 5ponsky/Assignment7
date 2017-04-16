//
// Bare-bones Java web server
//

import java.awt.Desktop;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.*;
import java.net.URI;
import java.util.Date;

class Main {

	// Load the page from the webserver
	static String readFile(String path) throws IOException {
		try {
			return new String(String.join("\n", Files.readAllLines(Paths.get(path))));
		} catch (IOException ioe) {
			return new String("Oops, we got: " + ioe.getMessage() + " instead.");
		}
	}

	public static void main(String[] args) throws Exception {

		// Listen for a connection from a client
		ServerSocket serverSocket = new ServerSocket(1234);


		if(Desktop.isDesktopSupported())
			Desktop.getDesktop().browse(new URI("http://localhost:1234"));
		else
			System.out.println("Please direct your browser to http://localhost:1234.");


		while(true)
		{
			Socket clientSocket = serverSocket.accept();
			System.out.println("Got a connection!");
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// Receive the request from the client
			String inputLine;
			boolean post = false;
			int contentLength = 0;
			while ((inputLine = in.readLine()) != null) {

				if(inputLine.length() >= 4 && inputLine.substring(0, 4).equals("POST"))
					post = true;
				if(inputLine.length() >= 14 && inputLine.substring(0, 14).equals("Content-Length"))
					contentLength = Integer.parseInt(inputLine.substring(16));
				System.out.println("The client said: " + inputLine);
				if(inputLine.length() < 2)
					break;
			}
			char[] postedContent = null;
			if(post && contentLength > 0)
			{
				// Read the POST content
				postedContent = new char[contentLength];
				in.read(postedContent, 0, contentLength);
			}

			// Whatever data we want to send
			String payload = readFile("index.html");

			// Send HTTP headers
			System.out.println("Sending a response...");
			out.print("HTTP/1.1 200 OK\r\n");
			out.print("Content-Type: text/html\r\n");
			out.print("Content-Length: " + Integer.toString(payload.length()) + "\r\n");
			out.print("Connection: close\r\n");
			out.print("\r\n");

			// Send the payload
			out.println(payload);
			clientSocket.close();
		}
	}
}