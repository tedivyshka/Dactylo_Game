package model;

import com.google.gson.Gson;
import controller.Controller;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMultiPlayer extends Game {

    private int lives;
    private int level;
    private List<Integer> redWordsPos; // List of positions of red (bonus) words
    private static final int redWordRate = 35; // % to get a red word
    private static final int maxWordsInList = 18;
    private Controller controller;
    private static String SERVER_HOST; // Address of the server to join
    private static boolean isHost; // Whether the current client is hosting a server
    private int nbPlayers;
    private static Socket socket;
    private Server server;
    private int rank;

    /**
     * Set up the server address and isHost boolean
     * @param hostAddress
     * @param isHost
     */
    public void setUp(String hostAddress, boolean isHost){
        SERVER_HOST = hostAddress;
        GameMultiPlayer.isHost = isHost;
    }

    /**
     * Set up the host: call setUp with current address
     * @param nbPlayers
     * @return
     */
    public String setUpHost(int nbPlayers){
        this.nbPlayers = nbPlayers;
        try{
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            this.setUp(ipAddress,true);
            return ipAddress;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Initialize the game by starting all the variables
     * If this is the host, start the server
     * At the end join the server
     * @param c
     */
    public void init(Controller c) {
        System.out.println("Init game\n");
        super.mode = Mode.MULTI;

        WordList.generateList();
        this.controller = c;
        this.currentList = WordList.startingList();
        this.currentPos = 0;
        this.correctCharacters = 0;
        this.typedCharacters = 0;
        this.lives = 20;
        this.level = 1;
        this.gameRunning = true;
        this.redWordsPos = new ArrayList<>();
        this.initRedWords();
        this.startTime = System.nanoTime();
        this.regularityList = new ArrayList<>();

        if(isHost){
            try { hostGame(); }
            catch (IOException e) {
                this.controller.error(e);
                System.out.println("error !!!\n");
            }
        }else{
            try { joinGame(); }
            catch (IOException e) {
                this.controller.error(e);
                System.out.println("error !!!\n");
            }
        }

    }

    private void initRedWords() {
        for(int i = 0; i < this.currentList.size(); i++){
            Random rand = new Random();
            int randEntry = rand.nextInt(101);
            if (randEntry < redWordRate) this.redWordsPos.add(i);
        }
    }

    /**
     * Handle the character typed by the player
     * @param k character number
     * @return boolean if the typed character was correct or not
     */
    public boolean keyInput(int k) {
        this.typedCharacters++;
        if (k == ' ') {
            String word = this.currentList.get(0);
            if (word.length() == this.currentPos) {
                //Word done -> move to next one
                this.correctCharacters++;
                this.currentPos = 0;
                this.score++;
                if (this.redWordsPos.size() > 0 && this.redWordsPos.get(0) == 0) {
                    //Send word to server
                    Gson gson = new Gson();
                    Request message = new Request("WORD",word);
                    String json = gson.toJson(message);

                    PrintWriter out;
                    try {
                        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.println(json);
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    this.redWordsPos.remove(0);
                }
                this.updateList();
                return true;
            }
            if (this.redWordsPos.size() > 0 && this.redWordsPos.get(0) == 0) {
                // Red words only get sent if they are typed with no errors
                // If current word is red -> turn it into a normal word
                this.redWordsPos.remove(0);
            }
            return false;
        } else {
            String word = this.currentList.get(0);
            if (word.length() == this.currentPos) {
                //Word done but did not receive space -> error by the player
                if (this.redWordsPos.size() > 0 && this.redWordsPos.get(0) == 0) {
                    // Red words only get sent if they are typed with no errors
                    // If current word is red -> turn it into a normal word
                    this.redWordsPos.remove(0);
                }
                this.lives--;
                if (this.lives <= 0) endGame();

                return false;
            } else if (k == word.charAt(this.currentPos)) {
                //Character typed is the correct one
                this.currentPos++;
                this.correctCharacters++;
                if (this.previousCorrectCharTime == 0) {
                    this.previousCorrectCharTime = System.nanoTime();
                } else {
                    this.regularityList.add((System.nanoTime() - this.previousCorrectCharTime));
                    this.previousCorrectCharTime = System.nanoTime();
                }
                return true;
            } else {
                //Character typed is the wrong one
                if (this.redWordsPos.size() > 0 && this.redWordsPos.get(0) == 0) {
                    // Red words only get sent if they are typed with no errors
                    // If current word is red -> turn it into a normal word
                    this.redWordsPos.remove(0);
                }
                this.lives--;
                if (this.lives <= 0) endGame();
                return false;
            }
        }
    }

    /**
     * Update the list when a word was correctly typed:
     * remove the word in position 0 and if the list is halfway or less full
     * add a new word, randomly decide if it's a red word
     */
    public void updateList() {
        //If the list is half or less full we add a new word
        boolean addNew = this.currentList.size() < (maxWordsInList / 2);
        WordList.update(this.currentList, addNew);
        this.redWordsPos.replaceAll(integer -> integer - 1);
        
        if(!addNew) return;
        //Update redWordsPos
        Random rand = new Random();
        int randEntry = rand.nextInt(101);
        if (randEntry < redWordRate) this.redWordsPos.add(this.currentList.size() - 1);

    }

    /**
     * Validate current word when a new word has been added and the limit of
     * words has been reached
     */
    private void validateCurrentWord() {
        String word = this.currentList.get(0);
        if(word.length() == this.currentPos) {
            //The current word has been completed
            this.score++;
            if (this.redWordsPos.size() > 0 && this.redWordsPos.get(0) == 0) {
                // Validated word is red, send it to the server
                Gson gson = new Gson();
                Request message = new Request("WORD",word);
                String json = gson.toJson(message);

                PrintWriter out;
                try {
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.println(json);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.redWordsPos.remove(0);
            }
        }else{
            //The current word has not been completed
            this.lives--;
            if (this.lives <= 0) endGame();
        }
        this.currentPos = 0;
        this.currentList.remove(0);
        this.redWordsPos.replaceAll(integer -> integer - 1);
    }

    /**
     * Notify the server that the game has ended
     */
    private void endGame() {
        //Notify the server that the game has ended
        Gson gson = new Gson();
        Request message = new Request("END","");
        String json = gson.toJson(message);

        PrintWriter out;
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.println(json);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to host the game, start the server and then join it
     * @throws IOException
     */
    private void hostGame() throws IOException{
        this.server = new Server();
        Thread serverThread = new Thread(() -> {
            try {
                server.runServer(this.nbPlayers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        joinGame();
    }

    /**
     * Join the game and start the thread to listen to it
     * Handle requests sent from the server in the thread
     * @throws IOException
     */
    public void joinGame() throws IOException {
        int SERVER_PORT = 13000;
        System.out.println("Trying to join " + SERVER_HOST + "\n");

        Socket sock = new Socket(SERVER_HOST, SERVER_PORT);
        this.socket = sock;
        BufferedReader sock_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connection established");

        // Start a thread to receive messages from the server
        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    if(this.socket.isClosed()) break;

                    String line;
                    try {
                        line = sock_br.readLine();
                    }
                    catch(SocketException ex){
                        break; //Socket is closed
                    }

                    if (line == null) {
                        // The server has closed the connection, so we exit the loop
                        break;
                    }

                    // Handle the message received from the server
                    Gson gson = new Gson();
                    Request message = gson.fromJson(line, Request.class);
                    if(message.getType().equals("START")){
                        this.controller.startMultiPlayer();
                    }
                    else if(message.getType().equals("WORD")){
                        System.out.println("Got new word");
                        String word = message.getWord();
                        handleServerMessage(word);
                    }
                    else if(message.getType().equals("RANK")){
                        // Game has ended, server has sent the rank. Update the view
                        String ranking = message.getWord();
                        this.rank = Integer.parseInt(ranking);
                        this.gameRunning = false;
                        this.controller.getStats();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        receiverThread.start();
    }

    /**
     * Handle the request of type WORD sent from the server:
     * add the word to the current list
     * @param word to add to the list
     */
    private void handleServerMessage(String word) {
        this.currentList.add(word);
        if(currentList.size() > maxWordsInList) validateCurrentWord();
        this.controller.update();
    }

    /**
     * Close the socket and if this is the host close the server
     */
    @Override
    public void stop(){
        try {
            if(this.socket != null) this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(this.isHost) this.server.closeServer();
    }

    public List<Integer> getRedWordsPos() { return this.redWordsPos; }

    public int getRank(){ return this.rank; }
}