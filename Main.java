//
 // Bare-bones Java web server
//

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

class Main {
	static File file;
	static String mime;
	static Random random = new Random();
	static ArrayList<String> users = new ArrayList<String>();
	static int id = 1;
	static String cookie;
	//String name = new String(file.toString());

	// Load the page from the webserver
	static String readFile(String path) throws IOException {
		try {
			return new String(String.join("\n", Files.readAllLines(Paths.get(path))));
		} catch (IOException ioe) {
			return new String("Oops, we got: " + ioe.getMessage() + " instead.");
		}
	}
	
	static void writeFile(File f, char[] data) throws IOException {
		try {
			Writer out = new BufferedWriter(new FileWriter(f, true));
			out.append(new String(data));
			out.append("\r\n");
			out.close();
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	static boolean checkUsername() {
		for(int i = 0; i < users.length; ++i) {
			if()
		}
	}

	public static void main(String[] args) throws Exception {

		// Listen for a connection from a client
		ServerSocket serverSocket = new ServerSocket(1234);

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
				
				// Check if POST or GET request
				if(inputLine.length() >= 4 && inputLine.substring(0, 4).equals("POST")) {
					String s = inputLine;
					s = inputLine.substring(6, inputLine.length());
					String[] parts = s.split(" ");
					s = parts[0];
					
					if(s.equals("/?")) {
						System.out.println("asdfasdfasdf");
						checkUsername();
					}
					
					post = true;
					file = new File(s);
					mime = Files.probeContentType(file.toPath());
				} else if(!post) {
					file = new File("stuff.html");
					mime = Files.probeContentType(file.toPath());
				}
				
				// If we don't have a cookie, give the user a cookie/ID, 
				//and add them to the user list
				if(inputLine.length() >= 6 && inputLine.substring(0, 7).equals("Cookie:")) {
					String s = inputLine;
					s = inputLine.substring(8, inputLine.length());
					if(s.length() < 7) {
						System.out.println(id);
						cookie = new String("userID=" + id);
						users.add(cookie);
						++id;
					}
				}
				
				if(inputLine.length() >= 14 && inputLine.substring(0, 14).equals("Content-Length"))
					contentLength = Integer.parseInt(inputLine.substring(16));
				System.out.println("The client said: " + inputLine);
				if(inputLine.length() < 2) {
					break;
				}
			}
			
			char[] postedContent = null; 
			if(post && contentLength > 0)
			{
				// Read the POST content
				postedContent = new char[contentLength];
				in.read(postedContent, 0, contentLength);
				
				// Get the data to write
				writeFile(file, postedContent);
			}

			// Whatever data we want to send
			String payload = readFile(file.toString());
			

			// Send HTTP headers
			System.out.println("Sending a response...");
			out.print("HTTP/1.1 200 OK\r\n");
			out.print("Content-Type: " + "text/html " + "\r\n");
			out.print("Content-Length: " + Integer.toString(payload.length()) + "\r\n");
			out.print("Set-Cookie: " + cookie + "\r\n");
			out.print("Connection: close\r\n");
			out.print("\r\n");

			// Send the payload
			out.println(payload);
			clientSocket.close();
		}
	}
}
