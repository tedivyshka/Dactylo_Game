package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// This class represents a client thread that communicates with the server
class ClientThread extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientThread(Socket socket) {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // Read data from the client and process it
        while (true) {
            // Read data from the client
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            try {
                bytesRead = inputStream.read(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Process the data received from the client
            String data = new String(buffer, 0, bytesRead);
            // TODO: Update the game state based on the data received from the client

            /*
            // Send data back to the client
            try {
                outputStream.write(buffer, 0, bytesRead);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            */
        }
    }

    public void sendData(String data) {
        try {
            byte[] buffer = data.getBytes();
            outputStream.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Server {
    public void runServer(int desiredNumClients) throws IOException {
        // Create a server socket and start listening for incoming connections
        ServerSocket serverSocket = new ServerSocket(13000);
        List<ClientThread> clients = new ArrayList<>();

        // Accept all clients
        while (true) {
            // Accept an incoming connection from a client
            Socket socket = serverSocket.accept();

            // Create a new thread for the client and start it
            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();
            clients.add(clientThread);

            // Check if the desired number of clients has been reached
            if (clients.size() == desiredNumClients) break;
        }


        // Start the game
        Executor executor = Executors.newFixedThreadPool(clients.size());
        for(ClientThread clientThread : clients){
            executor.execute(clientThread);
        }
    }
}