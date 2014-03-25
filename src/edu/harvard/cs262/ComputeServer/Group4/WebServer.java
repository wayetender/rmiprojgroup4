package edu.harvard.cs262.ComputeServer.Group4;

/**
 * A local embedded webserver, will run from a .jar file, and serve files
 * from the root.
 * 
 * @author Joseph Lewis <joehms22@gmail.com>
 */

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class WebServer extends Thread {
	private static final FileNotFoundException FILE_NOT_FOUND = new FileNotFoundException();
	private static final byte[] ERROR_404 = "HTTP/1.0 404 Not Found\r\nContent-type: text/plain\r\n\r\n404 Error, page not found."
			.getBytes();
	private static final int PORT_NUM = 8000;
	public static WebServer SERVER = null;
	private ServerSocket serverSocket;

	public static WebServer getInstance() {
		// Make sure we only ever have one server running.
		if (SERVER == null)
			SERVER = new WebServer();

		return SERVER;
	}

	private WebServer() {
		try {
			serverSocket = new ServerSocket(PORT_NUM);

			System.err.println("Starting server on port: " + PORT_NUM);

			// If you're running this embedded, you will probably want to
			// make the thread a daemon so it quits when your main program
			// quits rather than serves forever.
			// setDaemon(true);

			start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Starts a new thread for every request we get.
		while (true)
			try {
				new RequestHandler(serverSocket.accept());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static void main(String[] args) {
		WebServer.getInstance();
	}

	private class RequestHandler extends Thread {
		private final Socket socket;
		private final ClassLoader m_loader;
		private static final String DEFAULT_INDEX_PAGE = "index.html";

		// Start the thread in the constructor
		public RequestHandler(Socket s) {
			socket = s;
			m_loader = WebServer.getInstance().getClass().getClassLoader();
			start();
		}

		@Override
		public void run() {
			try {
				DataInputStream in = new DataInputStream(
						socket.getInputStream());
				PrintStream out = new PrintStream(new BufferedOutputStream(
						socket.getOutputStream()));

				// Parse the HTML request
				StringTokenizer st = new StringTokenizer(in.readLine());

				try {
					String requestType = st.nextToken();
					String filename = normalizeFilePath(st.nextToken());

					System.err.println("Request: " + requestType + " "
							+ filename);

					InputStream f = m_loader.getResourceAsStream(filename);

					if (f == null)
						throw FILE_NOT_FOUND;

					out.print("HTTP/1.0 200 OK\r\nContent-type: "
							+ getMimeType(filename) + "\r\n\r\n");

					// Send file contents to client, then close the connection
					final byte[] a = new byte[2048];
					int n;
					while ((n = f.read(a)) > 0)
						out.write(a, 0, n);

				} catch (NoSuchElementException e) {
					out.print(ERROR_404);
				} catch (FileNotFoundException e) {
					out.print(ERROR_404);
				}

				out.close();
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		public String getMimeType(String filename) {
			String ext = filename.substring(filename.indexOf(".") + 1);

			if (ext.equals("html")) {
				return "text/html";
			} else if (ext.equalsIgnoreCase("java")) {
				return "text/x-java";
			} else if (ext.equalsIgnoreCase("class")) {
				return "application/x-java";
			} else {
				return "application/octet-stream";
			}
		}

		public String normalizeFilePath(String filename)
				throws FileNotFoundException {
			// Remove GET params.
			if (filename.contains("?"))
				filename = filename.split("?", 2)[0];

			// Make requests to a folder show the index.html file.
			if (filename.endsWith("/"))
				filename += DEFAULT_INDEX_PAGE;

			// Remove leading /
			while (filename.indexOf("/") == 0)
				filename = filename.substring(1);

			// Check for illegal characters to prevent access to
			// superdirectories
			if (filename.contains("..") || filename.contains(":")
					|| filename.contains("|"))
				throw FILE_NOT_FOUND;

			return filename;
		}
	}
}