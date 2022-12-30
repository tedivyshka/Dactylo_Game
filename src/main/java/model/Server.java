package model;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// This class represents a client thread that communicates with the server
class ClientThread extends Thread {
    private Socket socket;

    private final int id;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientThread(Socket socket, int id) {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.id = id;
    }

    @Override
    public void run() {
        //notify clients that game has started
        Gson gson = new Gson();
        Message startMessage = new Message("START","");
        String json = gson.toJson(startMessage);

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            out.println(json);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        // Read data from the client and process it
        while (true) {
            // Read data from the client
            byte[] buffer = new byte[1024];
            int bytesRead;

            try {
                bytesRead = inputStream.read(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(bytesRead < 0) continue;
            // Process the data received from the client
            String data = new String(buffer, 0, bytesRead);

            // Update the game state based on the data received from the client
            System.out.println(this.id + " Got message: " + data);
            Message message = gson.fromJson(data, Message.class);
            String word = message.getWord();

            // Send data back to the client
            Server.sendWord(word,this.id);
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

    public Socket getSocket() { return this.socket; }
}

public class Server {
    private static List<ClientThread> clients;
    private static ServerSocket server;

    public void runServer(int desiredNumClients) throws IOException {
        // Create a server socket and start listening for incoming connections
        ServerSocket serverSocket = new ServerSocket(13000);
        server = serverSocket;
        clients = new ArrayList<>();

        // Accept all clients
        int idCounter = 0;
        while (clients.size() < desiredNumClients) {
            // Accept an incoming connection from a client
            Socket socket = serverSocket.accept();

            // Create a new thread for the client
            ClientThread clientThread = new ClientThread(socket, idCounter);
            idCounter++;
            clients.add(clientThread);
        }

        // Start the game -> execute all clientThreads in the ThreadPool
        Executor executor = Executors.newFixedThreadPool(clients.size());
        for(ClientThread clientThread : clients){
            executor.execute(clientThread);
        }
    }

    public static void sendWord(String word, int id) {
        for(int i = 0; i < clients.size(); i++){
            if(i == id) continue; //The word was sent by this client -> ignore
            Socket current = clients.get(i).getSocket();

            //Serialize the word and send it to the client
            Gson gson = new Gson();
            Message message = new Message("WORD",word);
            String json = gson.toJson(message);

            PrintWriter out = null;
            try {
                out = new PrintWriter(new OutputStreamWriter(current.getOutputStream()));
                out.println(json);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void closeServer(){
        try {
            if(this.server != null) server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}