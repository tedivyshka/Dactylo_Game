package model;

import controller.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMultiPlayer extends Game {

    private int lives;
    private int level;
    private List<Integer> redWordsPos; //Les positions des mots rouges qu'on envoie aux adversaires
    private static final int maxWordsInList = 18;
    private Controller controller;

    private static String SERVER_HOST;

    private static boolean isHost;

    private int nbPlayers;

    public void setUp(String hostAddress, boolean isHost){
        SERVER_HOST = hostAddress;
        GameMultiPlayer.isHost = isHost;
    }

    public String setUpHost(int nbPlayers){
        this.nbPlayers = nbPlayers;
        try{
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            int port = 13000;
            this.setUp(ipAddress,true);
            return ipAddress;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(Controller c) {
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
        this.redWordsPos = new ArrayList<Integer>();

        if(isHost){
            try { hostGame(); }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            try { joinGame(); }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

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
                    //TODO send word to server
                }
                System.out.println("Finished word: " + word + " , lives left = " + this.lives);
                this.updateList();
                return true;
            }
            return false;
        } else {
            System.out.println("Adding character: " + ((char) k));
            String word = this.currentList.get(0);
            if (word.length() == this.currentPos) {
                System.out.println("Wrong character");
                this.lives--;
                if (this.lives <= 0) this.gameRunning = false;
                return false; //Wrong input, waiting for space
            } else if (k == word.charAt(this.currentPos)) {
                this.currentPos++;
                this.correctCharacters++;
                if (this.previousCorrectCharTime == 0) {
                    this.previousCorrectCharTime = System.nanoTime();
                } else {
                    this.regularitySum += (System.nanoTime() - this.previousCorrectCharTime);
                    this.previousCorrectCharTime = System.nanoTime();
                }
                return true;
            } else {
                System.out.println("Expected " + word.charAt(this.currentPos) + " ; got " + (char) k);
                this.lives--;
                if (this.lives == 0) this.gameRunning = false;
                return false;
            }
        }
    }

    public void updateList() {
        //If the list is half or less full we add a new word
        boolean addNew = this.currentList.size() < (maxWordsInList / 2);
        WordList.update(this.currentList, addNew);

        //Update redWordsPos
        this.redWordsPos.replaceAll(integer -> integer - 1);
        Random rand = new Random();
        int randEntry = rand.nextInt(101);
        // % possibilite d'avoir un mot bonus
        int redWordRate = 20;
        if (randEntry < redWordRate) {
            this.redWordsPos.add(this.currentList.size() - 1);
            System.out.println("Added red word, current red word count: \n" + this.redWordsPos.size());
        }
    }

    private void hostGame() throws IOException{
        Server server = new Server();
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

    public void joinGame() throws IOException {
        int SERVER_PORT = 13000;
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            // Create a reader and a writer for the socket
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Start a thread to receive messages from the server
            Thread receiverThread = new Thread(() -> {
                try {
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            // The server has closed the connection, so we exit the loop
                            break;
                        }
                        // Handle the message received from the server
                        handleServerMessage(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();
        }
    }

    private void handleServerMessage(String line) {
        System.out.println("Recieved line: " + line);
    }

    public void setIsHost(boolean isHost){ this.isHost = isHost; }


    public List<Integer> getRedWordsPos() { return this.redWordsPos; }
}