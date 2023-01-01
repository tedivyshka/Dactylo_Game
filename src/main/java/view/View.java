package view;

import controller.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import model.GameCompetitiveSolo;
import model.GameMultiPlayer;
import model.Mode;
import org.fxmisc.richtext.StyleClassedTextArea;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;


public class View extends Application {

    private final Controller controller;

    private final BorderPane root = new BorderPane();

    private final Scene scene = new Scene(root, 1080, 180);

    private StyleClassedTextArea text = null;

    private StyleClassedTextArea additionnalInfo = null;

    public View(Controller c) {
        this.controller = c;
    }

    /**
     * the start menu where you choose the game mode.
     */
    public void startingMenuGui(){
        root.getChildren().clear();

        VBox menu = new VBox();
        menu.setSpacing(20);

        root.setCenter(menu);
        menu.setAlignment(Pos.CENTER);


        Button btnMode1 = new Button("Normal");
        Button btnMode2 = new Button("Competitive");
        Button btnMode3 = new Button("Multiplayer");

        menu.getChildren().addAll(btnMode1, btnMode2, btnMode3);

        btnMode1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.setMode1();
            }
        });
        btnMode2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.setMode2();
            }
        });
        btnMode3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.setMode3();
            }
        });
    }

    /**
     * start function for JavaFX
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        startingMenuGui();

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        primaryStage.setTitle("Dactylo-Game");
        primaryStage.setScene(scene);

        root.requestFocus();
        primaryStage.show();
    }

    /**
     * the multiplayer menu where you can choose if you want to host a game or join one.
     */
    public void menuMultiplayer() {
        root.getChildren().clear();
        root.requestFocus();

        returnMenuButton();

        //host button
        MenuButton host = new MenuButton("Host (select the number of players)");

        MenuItem two = new MenuItem("2");
        two.setOnAction(e -> controller.setUpHost(2));

        MenuItem three = new MenuItem("3");
        three.setOnAction(e -> controller.setUpHost(3));

        MenuItem four = new MenuItem("4");
        four.setOnAction(e -> controller.setUpHost(4));

        host.getItems().addAll(two,three,four);

        // join button + ip text
        TextField joinText = new TextField();
        Button join = new Button("Join with the ip :");
        join.setOnAction(e -> controller.setUpJoin(joinText.getCharacters().toString()));

        HBox joinBox = new HBox(join,joinText);
        joinBox.setAlignment(Pos.CENTER);

        //or text
        Label or = new Label("or");

        //buttons box
        VBox hostJoin = new VBox(host, or,joinBox);
        hostJoin.setAlignment(Pos.CENTER);
        hostJoin.setSpacing(10);

        root.setCenter(hostJoin);
    }

    /**
     * waiting page for the host.
     * @param ip the IP of the host
     */
    public void waitAsHostPage(String ip) {
        root.getChildren().clear();

        returnMenuButton();
        String ipAddress = "127.0.1.1";
        URL url = null;
        try {
            url = new URL("https://api.ipify.org");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            ipAddress = reader.readLine();
        } catch (Exception ignored) {}

        Label message = new Label("Waiting for player(s) to join.\nYour address is " + ipAddress + ".\n" +
                "If this game is hosted locally, use 127.0.1.1 as the address.\n");
        message.setWrapText(true);

        HBox messageBox = new HBox(message);
        messageBox.setAlignment(Pos.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox vbox = new VBox(messageBox,progressIndicator);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        root.setCenter(vbox);

    }

    /**
     * waiting page for the player who joined a game.
     * @param ip the IP of the host
     */
    public void waitAsJoinerPage(String ip) {
        root.getChildren().clear();
        root.requestFocus();

        returnMenuButton();

        Label message = new Label("Waiting for player to join.\nThe address joined is " + ip + ".");
        message.setWrapText(true);

        HBox messageBox = new HBox(message);
        messageBox.setAlignment(Pos.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox vbox = new VBox(messageBox,progressIndicator);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        root.setCenter(vbox);

    }

    /**
     * the button to return to the menu.
     */
    private void returnMenuButton(){
        MenuBar menuBar = new MenuBar();

        Button newGame = new Button("Return menu");
        newGame.setOnAction(e -> controller.resetAndGoMenu());


        HBox topBar = new HBox(newGame);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        root.setTop(topBar);
    }

    /**
     * the display of a game, regardless of the game mode.
     */
    public void startGame(){
        root.getChildren().clear();
        root.requestFocus();

        returnMenuButton();

        this.text = new StyleClassedTextArea();
        text.setEditable(false);
        text.setWrapText(true);
        text.setStyleClass(0,text.getLength(),"black");

        this.additionnalInfo = new StyleClassedTextArea();
        additionnalInfo.setEditable(false);
        additionnalInfo.setWrapText(false);

        VBox vbox = new VBox(text,additionnalInfo);

        root.setCenter(vbox);

        scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(controller.isGameRunning()) {
                    controller.keyPressed(event);
                    controller.update();
                    if(!controller.isGameRunning()){
                        if(controller.getGame().getMode().equals(Mode.COMPETITIVE)) ((GameCompetitiveSolo) controller.getGame()).cancelTimer();

                        System.out.println("game no more running\n");
                        //on affiche les statistiques
                        controller.getStats();
                    }
                }
            }
        });
    }

    /**
     * text display
     * @param s text to print
     */
    public void printText(String s) {
        this.text.appendText(s + " ");
    }

    /**
     * reset the displayed text.
     */
    public void resetText() {
        this.text.replaceText("");
    }

    /**
     * color the words being written.
     * Also color the words in blue and red for some game modes.
     * @param pos the location of the writing cursor in the first word.
     */
    public void colorWord(int pos){
        this.text.setStyleClass(0, this.text.getLength(), "black");
        this.text.setStyleClass(0, pos, "green");

        if(this.controller.getGame().getMode().equals( Mode.COMPETITIVE)) {
            //this.text.setStyleClass(pos, this.text.getLength(), "black");
            List<Integer> blueWordList = ((GameCompetitiveSolo) this.controller.getGame()).getBlueWordsPos();
            List<String> currentList = this.controller.getGame().getList();
            if (blueWordList != null) {
                for (Integer i : blueWordList) {
                    if (i < 0) continue;
                    int position = 0;
                    for (int j = 0; j < i; j++) {
                        position += currentList.get(j).length();
                        position++;
                    }
                    this.text.setStyleClass(position, position + currentList.get(i).length(), "blue");
                }
            }
            this.text.setStyleClass(0, pos, "green");
        }
        else if(this.controller.getGame().getMode().equals( Mode.MULTI)) {
            //this.text.setStyleClass(pos, this.text.getLength(), "black");
            List<Integer> redWordList = ((GameMultiPlayer) this.controller.getGame()).getRedWordsPos();
            List<String> currentList = this.controller.getGame().getList();
            if (redWordList != null) {
                for (Integer i : redWordList) {
                    if (i < 0) continue;
                    int position = 0;
                    for (int j = 0; j < i; j++) {
                        position += currentList.get(j).length();
                        position++;
                    }
                    this.text.setStyleClass(position, position + currentList.get(i).length(), "red");
                }
            }
            this.text.setStyleClass(0, pos, "green");
        }
    }


    /**
     * main function to start our dactylo game.
     * @param args
     */
    public static void main(String[] args) {
        Controller controller = new Controller();
        Platform.runLater(() -> {
            View v1 = new View(controller);
            controller.setView(v1);
            Stage stage = new Stage();
            v1.start(stage);
        });
    }

    /**
     * display the end screen with the stats of the game.
     * @param stats statistics of the game
     */
    public void setEndScreen(String stats) {
        this.resetAdditionalInfo();
        this.text.replaceText(stats);
        this.text.setStyleClass(0, this.text.getLength(), "black");
    }


    /**
     * print lives.
     * @param lives
     */
    public void printLives(int lives) {
        this.additionnalInfo.appendText("lives : " + lives + "\n");
        this.additionnalInfo.setStyleClass(0,this.additionnalInfo.getLength(),"black");
    }

    /**
     * print the level
     * @param level
     */
    public void printLevel(int level){
        this.additionnalInfo.appendText("level : " + level + "\n");
        this.additionnalInfo.setStyleClass(0,this.additionnalInfo.getLength(),"black");
    }

    /**
     * reset the additional information text (level and lives).
     */
    public void resetAdditionalInfo(){
        this.additionnalInfo.replaceText("");
    }

    /**
     * error page.
     * @param e error
     */
    public void printError(Exception e) {
        root.getChildren().clear();
        root.requestFocus();

        returnMenuButton();

        Label error = new Label("Error :" + e.toString());
        this.root.setCenter(error);
    }
}


