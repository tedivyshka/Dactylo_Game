package view;

import com.sun.javafx.sg.prism.NGShape;
import controller.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.scene.layout.Border;

import javafx.scene.control.*;

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




    @Override
    public void start(Stage primaryStage) {
        try {


            VBox menu = new VBox();
            menu.setSpacing(20);

            root.setCenter(menu);
            menu.setAlignment(Pos.CENTER);


            // Créez les boutons pour chaque mode de jeu
            Button btnMode1 = new Button("Normal");
            Button btnMode2 = new Button("Competitive");
            Button btnMode3 = new Button("Multiplayer");

            // Ajoutez les boutons au VBox
            menu.getChildren().addAll(btnMode1, btnMode2, btnMode3);

            // Ajoutez un gestionnaire d'événements pour chaque bouton
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


            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

            primaryStage.setTitle("Dactylo-Game");
            primaryStage.setScene(scene);

            root.requestFocus();
            primaryStage.show();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void waitAsHostPage(int nbPlayers) {
        //model part
        String ip = controller.setUpHost(nbPlayers);

        // gui part
        root.getChildren().clear();
        root.requestFocus();

        Label message = new Label("Waiting for player to join.\nYour adresse is " + ip + ".");
        message.setWrapText(true);

        HBox messageBox = new HBox(message);
        messageBox.setAlignment(Pos.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox vbox = new VBox(messageBox,progressIndicator);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        root.setCenter(vbox);
    }


    public void menuMultiplayer() {
        root.getChildren().clear();
        root.requestFocus();

        //host button
        MenuButton host = new MenuButton("Host (select the number of players)");

        MenuItem two = new MenuItem("2");
        two.setOnAction(e -> waitAsHostPage(2));

        MenuItem three = new MenuItem("3");
        three.setOnAction(e -> waitAsHostPage(3));

        MenuItem four = new MenuItem("4");
        four.setOnAction(e -> waitAsHostPage(4));

        host.getItems().addAll(two,three,four);

        // join button + ip text
        TextField joinText = new TextField();
        Button join = new Button("Join with the ip :");
        join.setOnAction(e -> this.waitAsJoinerPage(joinText.getCharacters().toString()));

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

    private void waitAsJoinerPage(String ip) {
        //model part
        controller.setUpJoin(ip);

        // gui part
        root.getChildren().clear();
        root.requestFocus();

        Label message = new Label("Waiting for player to join.\nThe adresse joined is " + ip + ".");
        message.setWrapText(true);

        HBox messageBox = new HBox(message);
        messageBox.setAlignment(Pos.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox vbox = new VBox(messageBox,progressIndicator);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        root.setCenter(vbox);

    }

    public void startGame(){

        try{

            root.getChildren().clear();
            root.requestFocus();

            MenuBar menuBar = new MenuBar();

            Menu newGame = new Menu("New game");

            MenuItem soloMode = new MenuItem("Single-player");
            soloMode.setOnAction(e -> this.controller.changeMode(0)); // appel controller

            MenuItem multiMode = new MenuItem("Multiplayer");
            multiMode.setOnAction(e -> this.controller.changeMode(2)); // appel controller

            MenuItem competitiveMode = new MenuItem("Normal Competitive");
            competitiveMode.setOnAction(e -> this.controller.changeMode(1)); // appel controller

            newGame.getItems().addAll(soloMode, multiMode, competitiveMode);
            menuBar.getMenus().addAll(newGame);

            HBox topBar = new HBox(menuBar);
            HBox.setHgrow(menuBar, Priority.ALWAYS);
            root.setTop(topBar);


            this.text = new StyleClassedTextArea();
            text.setEditable(false);
            text.setWrapText(true);

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
                            System.out.println(this);
                            //on affiche les statistiques
                            controller.getStats();
                        }
                    }

                }


            });

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void printText(String s) {
        this.text.appendText(s + " ");
    }


    public void resetText() {
        System.out.println(this);
        this.text.replaceText("");
    }

    public void colorWord(int pos){
        this.text.setStyleClass(0, this.text.getLength(), "black");
        this.text.setStyleClass(0, pos, "green");

        if(this.controller.getGame().getMode().equals( Mode.COMPETITIVE)) {
            System.out.println("COLORING BLUE");
            this.text.setStyleClass(pos, this.text.getLength(), "black");
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
            System.out.println("COLORING RED");
            this.text.setStyleClass(pos, this.text.getLength(), "black");
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



    public static void main(String[] args) {
        Controller controller = new Controller();
        //controller.init();
        //controller.getGame().init(controller);
        // on commence par start la GUI
        Platform.runLater(() -> {
            try {
                View v1 = new View(controller);
                controller.setView(v1);
                Stage stage = new Stage();
                v1.start(stage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //controller.update();
    }

    public void setEndScreen(String stats) {
        this.text.replaceText(stats);
        this.text.setStyleClass(0, this.text.getLength(), "black");
    }


    public void printLivesAndLevel(int lives,int level) {
        this.additionnalInfo.replaceText("lives : " + lives + "\n" + "level : " + level);
    }



}


