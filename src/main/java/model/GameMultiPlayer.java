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
    private List<Integer> redWordsPos; //Les positions des mots rouges qu'on envoie aux adversaires
    private static final int maxWordsInList = 18;
    private Controller controller;
    private static String SERVER_HOST;
    private static boolean isHost;
    private int nbPlayers;
    private static Socket socket;
    private Server server;
    private int rank;

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
        this.redWordsPos = new ArrayList<Integer>();
        this.startTime = System.nanoTime();

        if(isHost){
            try { hostGame(); }
            catch (IOException e) {
                this.controller.error(e);
                System.out.println("error !!!\n");
                //throw new RuntimeException(e);
            }
        }else{
            try { joinGame(); }
            catch (IOException e) {
                this.controller.error(e);
                System.out.println("error !!!\n");
                //throw new RuntimeException(e);
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
                    //Send word to server
                    Gson gson = new Gson();
                    Message message = new Message("WORD",word);
                    String json = gson.toJson(message);

                    PrintWriter out = null;
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
            return false;
        } else {
            String word = this.currentList.get(0);
            if (word.length() == this.currentPos) {
                this.lives--;
                if (this.lives <= 0) endGame();
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
                this.lives--;
                if (this.lives <= 0) endGame();
                return false;
            }
        }
    }

    public void updateList() {
        //If the list is half or less full we add a new word
        boolean addNew = this.currentList.size() < (maxWordsInList / 2);
        WordList.update(this.currentList, addNew);
        if(!addNew) return;

        //Update redWordsPos
        this.redWordsPos.replaceAll(integer -> integer - 1);
        Random rand = new Random();
        int randEntry = rand.nextInt(101);
        // % possibilite d'avoir un mot bonus
        int redWordRate = 35;
        if (randEntry < redWordRate) {
            this.redWordsPos.add(this.currentList.size() - 1);
            System.out.println("Added red word, current red word count: \n" + this.redWordsPos.size());
        }
    }

    private void validateCurrentWord() {
        String word = this.currentList.get(0);
        if(word.length() == this.currentPos) {
            this.score++;
            if (this.redWordsPos.size() > 0 && this.redWordsPos.get(0) == 0) {
                Gson gson = new Gson();
                Message message = new Message("WORD",word);
                String json = gson.toJson(message);

                PrintWriter out = null;
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
            this.lives--;
            if (this.lives <= 0) endGame();
        }
        this.currentPos = 0;
        this.currentList.remove(0);
        for(int i = 0; i < this.redWordsPos.size() ; i++) this.redWordsPos.set(i, this.redWordsPos.get(i) - 1);
    }

    private void endGame() {
        //Notify the server that the game has ended
        Gson gson = new Gson();
        Message message = new Message("END","");
        String json = gson.toJson(message);

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.println(json);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    public void joinGame() throws IOException {
        int SERVER_PORT = 13000;
        System.out.println("Trying to join " + SERVER_HOST + "\n");

        Socket sock = new Socket(SERVER_HOST, SERVER_PORT);
        this.socket = sock;
        BufferedReader sock_br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter sock_pw = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connection established");

        // Start a thread to receive messages from the server
        Thread receiverThread = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                while (true) {
                    if(this.socket.isClosed()) break;

                    String line;
                    try {
                        line = reader.readLine();
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
                    Message message = gson.fromJson(line, Message.class);
                    if(message.getType().equals("START")){
                        this.controller.startMultiPlayer();
                    }
                    else if(message.getType().equals("WORD")){
                        System.out.println("Got new word");
                        String word = message.getWord();
                        handleServerMessage(word);
                    }
                    else if(message.getType().equals("RANK")){
                        //TODO recieved rank from server -> print ranking in view
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

    private void handleServerMessage(String word) {
        System.out.println("Received word from server: " + word);
        this.currentList.add(word);
        if(currentList.size() > maxWordsInList) validateCurrentWord();
        this.controller.update();
    }

    public List<Integer> getRedWordsPos() { return this.redWordsPos; }

    public int getRank(){ return this.rank; }

    @Override
    public void stop(){
        try {
            if(this.socket != null) this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(this.isHost) this.server.closeServer();
    }
}

class Message {
    private String type; // START -> start game  ;  WORD -> add word

    private String word;

    public String getType(){ return this.type; }
    public String getWord(){ return this.word; }
    public Message(String type, String word) {
        this.type = type;
        this.word = word;
    }
}