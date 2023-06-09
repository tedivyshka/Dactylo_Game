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
    private final Socket socket;
    private final Server server;
    private final int id;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * Constructor for ClientThread
     * @param socket of the client
     * @param id player id
     * @param server
     */
    public ClientThread(Socket socket, int id, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.id = id;
    }

    /**
     * Run method for the ClientThread
     * Listens to requests from the client and responds accordingly
     */
    @Override
    public void run() {
        //notify clients that game has started
        Gson gson = new Gson();
        Request startMessage = new Request("START","");
        String json = gson.toJson(startMessage);

        PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream));
        out.println(json);
        out.flush();

        // Read data from the client and process it
        while (!interrupted()) {
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
            Request message = gson.fromJson(data, Request.class);

            if(message.getType().equals("END")){
                break;
            }else if(message.getType().equals("PARAM")){
                String param = message.getWord();

                // Send data back to the client
                Server.sendParam(param, this.id);
            }
            else {
                String word = message.getWord();

                // Send data back to the client
                Server.sendWord(word, this.id);
            }
        }
        Request endMessage = new Request("RANK",Integer.toString(this.server.getRank()));
        String endJson = gson.toJson(endMessage);

        PrintWriter end = new PrintWriter(new OutputStreamWriter(outputStream));
        end.println(endJson);
        end.flush();
        try { this.socket.close(); } catch(Exception ex) {}
    }
    public Socket getSocket() { return this.socket; }
}

/**
 * This class represents the server
 * A ThreadPool is used for the clients, each client executed in one thread
 */
public class Server {
    private static List<ClientThread> clients;
    private static ServerSocket server;
    private static int stillPlaying; //Number of clients that are still playing

    private static int serverPort = 13000;

    /**
     * Function to run the server: wait for the clients to join
     * and then execute each ClientThread
     * @param desiredNumClients the number of clients we expect to join
     * @throws IOException
     */
    public void runServer(int desiredNumClients) throws IOException {
        // Create a server socket and start listening for incoming connections
        server = new ServerSocket(serverPort);
        clients = new ArrayList<>();

        // Accept all clients
        int idCounter = 0;
        while (clients.size() < desiredNumClients) {
            // Accept an incoming connection from a client
            Socket socket = server.accept();

            // Create a new thread for the client
            ClientThread clientThread = new ClientThread(socket, idCounter, this);
            idCounter++;
            clients.add(clientThread);
        }

        // Start the game -> execute all clientThreads in the ThreadPool
        Executor executor = Executors.newFixedThreadPool(clients.size());
        for(ClientThread clientThread : clients){
            executor.execute(clientThread);
        }
        stillPlaying = clients.size(); //Used for the ranking at the end of the game
    }

    /**
     * Send the red word to the clients except the client that has typed it
     * @param word
     * @param id of the client that correctly typed the red word
     */
    public static void sendWord(String word, int id) {
        for(int i = 0; i < clients.size(); i++){
            if(i == id) continue; //The word was sent by this client -> ignore
            Socket current = clients.get(i).getSocket();

            //Serialize the word and send it to the client
            Gson gson = new Gson();
            Request request = new Request("WORD",word);
            String json = gson.toJson(request);

            PrintWriter out;
            try {
                out = new PrintWriter(new OutputStreamWriter(current.getOutputStream()));
                out.println(json);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Send the parameters of the host
     * @param param
     * @param id of the host
     */
    public static void sendParam(String param, int id) {
        for(int i = 0; i < clients.size(); i++){
            if(i == id) continue; //The word was sent by this client -> ignore
            Socket current = clients.get(i).getSocket();

            //Serialize the word and send it to the client
            Gson gson = new Gson();
            Request request = new Request("PARAM",param);
            String json = gson.toJson(request);

            PrintWriter out;
            try {
                out = new PrintWriter(new OutputStreamWriter(current.getOutputStream()));
                out.println(json);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Close the server when the game is over
     */
    public void closeServer(){
    	for(ClientThread c : clients){
            c.interrupt();
        }
        try {
            if(server != null) server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get ranking of the player that just lost and reduce the
     * number of players still playing
     * @return rank
     */
    public int getRank(){
        stillPlaying -= 1;
        return stillPlaying + 1;
    }
}

/**
 * This class represents a request between the server and the clients
 */
class Request {
    /*  Possible request types:
        START -> start game  ; PARAM -> send parameters to clients ;  WORD -> add word
        END -> notify server of loss ; RANK -> give client their rank
     */
    private final String type;

    private final String word;

    public String getType(){ return this.type; }
    public String getWord(){ return this.word; }

    /**
     * Constructor for Request class
     * @param type
     * @param word
     */
    public Request(String type, String word) {
        this.type = type;
        this.word = word;
    }
}