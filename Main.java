//
 // Bare-bones Java web server
//

import java.awt.Desktop;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.*;
import java.net.URI;

class Main {
	static File file;
	static String mime;
	//String name = new String(file.toString());

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

/*
		if(Desktop.isDesktopSupported())
			Desktop.getDesktop().browse(new URI("http://localhost:1234"));
		else
			System.out.println("Please direct your browser to http://localhost:1234.");
*/

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

				//StringTokenizer st = new StringTokenizer(inputLine, " ");
				
				if(inputLine.length() >= 4 && inputLine.substring(0, 4).equals("POST")) {
					post = true;
					String s = inputLine;
					s = inputLine.substring(6, inputLine.length());
					String[] parts = s.split(" ");
					s = parts[0];
					file = new File(s);
					mime = Files.probeContentType(file.toPath());
				} else if(!post){
					file = new File("stuff.html");
					mime = Files.probeContentType(file.toPath());
				}
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
				System.out.println(postedContent);
			}

			// Whatever data we want to send
			System.out.println(file.toString());
			String payload = readFile(file.toString());
			

			// Send HTTP headers
			System.out.println("Sending a response...");
			out.print("HTTP/1.1 200 OK\r\n");
			out.print("Content-Type: " + "text/html " + "\r\n");
			out.print("Content-Length: " + Integer.toString(payload.length()) + "\r\n");
			out.print("Set-Cookie: " + "some cookie " + "\r\n");
			out.print("Connection: close\r\n");
			out.print("\r\n");

			// Send the payload
			out.println(payload);
			clientSocket.close();
		}
	}
}

/*
 * 				while(st.hasMoreTokens()) {
					String s = st.nextToken();
					if(s.contains("/")) {
						System.out.println(s);
						s = s.substring(1);
						if(s.equals("")) {
							file = new File("stuff.html");
							mime = Files.probeContentType(file.toPath());
						} else {
							file = new File(s);
							System.out.println(s);
							mime = Files.probeContentType(file.toPath());
						}
					}
						
				}
*/
