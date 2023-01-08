package view;

import controller.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.WindowEvent;
import model.GameCompetitiveSolo;
import model.GameMultiPlayer;
import model.Mode;
import org.fxmisc.richtext.StyleClassedTextArea;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import javafx.scene.control.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class View extends Application {

    private final Controller controller;

    private final BorderPane root = new BorderPane();

    private final Scene scene = new Scene(root, 540, 360);

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


        Button btnMode0 = new Button("Normal");
        Button btnMode1 = new Button("Competitive");
        Button btnMode2 = new Button("Multiplayer");

        menu.getChildren().addAll(btnMode0, btnMode1, btnMode2);

        btnMode0.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.parametersMode0();
            }
        });
        btnMode1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.parametersMode1();
            }
        });
        btnMode2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.setMode2();
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
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                controller.stop();
            }
        });

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
        Button host = new Button("Host");
        host.setOnAction((e) -> controller.parametersMode2());

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
        /*
        URL url;
        try {
            url = new URL("https://api.ipify.org");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            ipAddress = reader.readLine();
        } catch (Exception ignored) {}
         */
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
     * Page where the player choose the parameters of his game.
     */
    public void paramMode0() {
        root.getChildren().clear();
        root.requestFocus();

        returnMenuButton();

        Label message = new Label("Select the number of word(s) to win.\n");
        message.setWrapText(true);

        AtomicInteger count = new AtomicInteger(20);
        Label countLab = new Label(count.toString());

        Button incrementButton = new Button("+");
        incrementButton.setOnAction(event -> {
            count.getAndIncrement();
            countLab.setText(count.toString());
        });

        Button decrementButton = new Button("-");
        decrementButton.setOnAction(event -> {
            if(count.get() > 1) {
                count.getAndDecrement();
                countLab.setText(count.toString());
            }
        });

        Button start = new Button("Start");
        start.setOnAction((e) -> controller.setMode0(count.get()));
        start.setAlignment(Pos.CENTER);

        HBox nbWordsButtons = new HBox(decrementButton,countLab,incrementButton);
        nbWordsButtons.setAlignment(Pos.CENTER);

        VBox choiceBox = new VBox(message,nbWordsButtons,start);
        choiceBox.setAlignment(Pos.CENTER);
        VBox.setMargin(start,new Insets(15,0,0,0));

        root.setCenter(choiceBox);
    }

    /**
     * Page where the player choose the parameters of his game.
     */
    public void paramMode1() {
        root.getChildren().clear();
        root.requestFocus();

        returnMenuButton();

// button for lives
        Label messageLives = new Label("Select the number of lives.\n");
        messageLives.setWrapText(true);

        AtomicInteger countLives = new AtomicInteger(20);
        Label countLabLives = new Label(countLives.toString());

        Button incrementButtonLives = new Button("+");
        incrementButtonLives.setOnAction(event -> {
            countLives.getAndIncrement();
            countLabLives.setText(countLives.toString());
        });

        Button decrementButtonLives = new Button("-");
        decrementButtonLives.setOnAction(event -> {
            if(countLives.get() > 1) {
                countLives.getAndDecrement();
                countLabLives.setText(countLives.toString());
            }
        });

// button for level
        Label messageLevel = new Label("Select the level.\n");
        messageLevel.setWrapText(true);

        AtomicInteger countLevel = new AtomicInteger(1);
        Label countLabLevel = new Label(countLevel.toString());

        Button incrementButtonLevel = new Button("+");
        incrementButtonLevel.setOnAction(event -> {
            countLevel.getAndIncrement();
            countLabLevel.setText(countLevel.toString());
        });

        Button decrementButtonLevel = new Button("-");
        decrementButtonLevel.setOnAction(event -> {
            if(countLevel.get() > 1) {
                countLevel.getAndDecrement();
                countLabLevel.setText(countLevel.toString());
            }
        });

// button for timeBetweenWord
        Label messageTimeBetweenWord = new Label("Select the time between words.\n");
        messageTimeBetweenWord.setWrapText(true);

        AtomicInteger countTimeBetweenWord = new AtomicInteger(3);
        Label countLabTimeBetweenWord = new Label(countTimeBetweenWord.toString());

        Button incrementButtonTimeBetweenWord = new Button("+");
        incrementButtonTimeBetweenWord.setOnAction(event -> {
            countTimeBetweenWord.getAndIncrement();
            countLabTimeBetweenWord.setText(countTimeBetweenWord.toString());
        });

        Button decrementButtonTimeBetweenWord = new Button("-");
        decrementButtonTimeBetweenWord.setOnAction(event -> {
            if(countTimeBetweenWord.get() > 1) {
                countTimeBetweenWord.getAndDecrement();
                countLabTimeBetweenWord.setText(countTimeBetweenWord.toString());
            }
        });

// button for bonusRate
        Label messageBonusRate = new Label("Select the bonus rate in %.\n");
        messageBonusRate.setWrapText(true);

        AtomicInteger countBonusRate = new AtomicInteger(20);
        Label countLabBonusRate = new Label(countBonusRate.toString());

        Button incrementButtonBonusRate = new Button("+");
        incrementButtonBonusRate.setOnAction(event -> {
            if(countBonusRate.get() < 100) {
                countBonusRate.getAndIncrement();
                countLabBonusRate.setText(countBonusRate.toString());
            }
        });

        Button decrementButtonBonusRate = new Button("-");
        decrementButtonBonusRate.setOnAction(event -> {
            if(countBonusRate.get() > 1) {
                countBonusRate.getAndDecrement();
                countLabBonusRate.setText(countBonusRate.toString());
            }
        });

// button for maxWordsInList
        Label messageMaxWordsInList = new Label("Select the maximum number of words in the list.\n");
        messageMaxWordsInList.setWrapText(true);

        AtomicInteger countMaxWordsInList = new AtomicInteger(18);
        Label countLabMaxWordsInList = new Label(countMaxWordsInList.toString());

        Button incrementButtonMaxWordsInList = new Button("+");
        incrementButtonMaxWordsInList.setOnAction(event -> {
            countMaxWordsInList.getAndIncrement();
            countLabMaxWordsInList.setText(countMaxWordsInList.toString());
        });

        Button decrementButtonMaxWordsInList = new Button("-");
        decrementButtonMaxWordsInList.setOnAction(event -> {
            if(countMaxWordsInList.get() > 16) {
                countMaxWordsInList.getAndDecrement();
                countLabMaxWordsInList.setText(countMaxWordsInList.toString());
            }
        });



// HBox for lives
        HBox livesButtons = new HBox(decrementButtonLives,countLabLives,incrementButtonLives);
        livesButtons.setAlignment(Pos.CENTER);

// HBox for level
        HBox levelButtons = new HBox(decrementButtonLevel,countLabLevel,incrementButtonLevel);
        levelButtons.setAlignment(Pos.CENTER);

// HBox for timeBetweenWord
        HBox timeBetweenWordButtons = new HBox(decrementButtonTimeBetweenWord,countLabTimeBetweenWord,incrementButtonTimeBetweenWord);
        timeBetweenWordButtons.setAlignment(Pos.CENTER);

// HBox for bonusRate
        HBox bonusRateButtons = new HBox(decrementButtonBonusRate,countLabBonusRate,incrementButtonBonusRate);
        bonusRateButtons.setAlignment(Pos.CENTER);

// HBox for maxWordsInList
        HBox maxWordsInListButtons = new HBox(decrementButtonMaxWordsInList,countLabMaxWordsInList,incrementButtonMaxWordsInList);
        maxWordsInListButtons.setAlignment(Pos.CENTER);


// start button
        Button start = new Button("Start");
        start.setOnAction((e) -> controller.setMode1(countLives.get(),
                countLevel.get(),
                countTimeBetweenWord.get(),
                countBonusRate.get(),
                countMaxWordsInList.get())
        );
        start.setAlignment(Pos.CENTER);


// VBox containing all the buttons
        VBox choiceBox = new VBox(messageLives, livesButtons,
                messageLevel, levelButtons,
                messageTimeBetweenWord, timeBetweenWordButtons,
                messageBonusRate, bonusRateButtons,
                messageMaxWordsInList, maxWordsInListButtons,
                start
        );
        choiceBox.setAlignment(Pos.CENTER);
        choiceBox.setAlignment(Pos.CENTER);
        VBox.setMargin(start,new Insets(15,0,0,0));
        root.setCenter(choiceBox);
    }


    /**
     * Page where the player choose the parameters of his game.
     */
    public void paramMode2() {
        root.getChildren().clear();
        root.requestFocus();

        returnMenuButton();




// button for number of players
        Label messageNbPlayer = new Label("Select the number of players.\n");
        messageNbPlayer.setWrapText(true);

        AtomicInteger countNbPlayer = new AtomicInteger(2);
        Label countLabNbPlayer = new Label(countNbPlayer.toString());

        Button incrementButtonNbPlayer = new Button("+");
        incrementButtonNbPlayer.setOnAction(event -> {
            if(countNbPlayer.get() < 4) {
                countNbPlayer.getAndIncrement();
                countLabNbPlayer.setText(countNbPlayer.toString());
            }
        });

        Button decrementButtonNbPlayer = new Button("-");
        decrementButtonNbPlayer.setOnAction(event -> {
            if(countNbPlayer.get() > 2) {
                countNbPlayer.getAndDecrement();
                countLabNbPlayer.setText(countNbPlayer.toString());
            }
        });

// button for lives
        Label messageLives = new Label("Select the number of lives.\n");
        messageLives.setWrapText(true);

        AtomicInteger countLives = new AtomicInteger(20);
        Label countLabLives = new Label(countLives.toString());

        Button incrementButtonLives = new Button("+");
        incrementButtonLives.setOnAction(event -> {
            countLives.getAndIncrement();
            countLabLives.setText(countLives.toString());
        });

        Button decrementButtonLives = new Button("-");
        decrementButtonLives.setOnAction(event -> {
            if(countLives.get() > 1) {
                countLives.getAndDecrement();
                countLabLives.setText(countLives.toString());
            }
        });

// button for maxWordsInList
        Label messageMaxWordsInList = new Label("Select the maximum number of words in the list.\n");
        messageMaxWordsInList.setWrapText(true);

        AtomicInteger countMaxWordsInList = new AtomicInteger(18);
        Label countLabMaxWordsInList = new Label(countMaxWordsInList.toString());

        Button incrementButtonMaxWordsInList = new Button("+");
        incrementButtonMaxWordsInList.setOnAction(event -> {
            countMaxWordsInList.getAndIncrement();
            countLabMaxWordsInList.setText(countMaxWordsInList.toString());
        });

        Button decrementButtonMaxWordsInList = new Button("-");
        decrementButtonMaxWordsInList.setOnAction(event -> {
            if(countMaxWordsInList.get() > 16) {
                countMaxWordsInList.getAndDecrement();
                countLabMaxWordsInList.setText(countMaxWordsInList.toString());
            }
        });

// for this two, we set the AtomicInteger before
// because each is defined in relation with the other
        AtomicInteger countBonusRate = new AtomicInteger(20);
        Label countLabBonusRate = new Label(countBonusRate.toString());

        AtomicInteger countRedWordRate = new AtomicInteger(20);
        Label countLabRedWordRate = new Label(countRedWordRate.toString());


// button for bonusRate
        Label messageBonusRate = new Label("Select the bonus rate in %.\n");
        messageBonusRate.setWrapText(true);

        Button incrementButtonBonusRate = new Button("+");
        incrementButtonBonusRate.setOnAction(event -> {
            if(countBonusRate.get() < 100 - countRedWordRate.get()) {
                countBonusRate.getAndIncrement();
                countLabBonusRate.setText(countBonusRate.toString());
            }
        });

        Button decrementButtonBonusRate = new Button("-");
        decrementButtonBonusRate.setOnAction(event -> {
            if(countBonusRate.get() > 1) {
                countBonusRate.getAndDecrement();
                countLabBonusRate.setText(countBonusRate.toString());
            }
        });


// button for red words rate
        Label messageRedWordRate = new Label("Select the red word rate in %.\n");
        messageRedWordRate.setWrapText(true);

        Button incrementButtonRedWordRate = new Button("+");
        incrementButtonRedWordRate.setOnAction(event -> {
            if(countRedWordRate.get() < 100 - countBonusRate.get()) {
                countRedWordRate.getAndIncrement();
                countLabRedWordRate.setText(countRedWordRate.toString());
            }
        });

        Button decrementButtonRedWordRate = new Button("-");
        decrementButtonRedWordRate.setOnAction(event -> {
            if(countRedWordRate.get() > 1) {
                countRedWordRate.getAndDecrement();
                countLabRedWordRate.setText(countRedWordRate.toString());
            }
        });



// HBox for lives
        HBox livesButtons = new HBox(decrementButtonLives,countLabLives,incrementButtonLives);
        livesButtons.setAlignment(Pos.CENTER);

// HBox for number of players
        HBox nbPlayerButtons = new HBox(decrementButtonNbPlayer,countLabNbPlayer,incrementButtonNbPlayer);
        nbPlayerButtons.setAlignment(Pos.CENTER);

// HBox for red word rate
        HBox redWordRateButtons = new HBox(decrementButtonRedWordRate,countLabRedWordRate,incrementButtonRedWordRate);
        redWordRateButtons.setAlignment(Pos.CENTER);

// HBox for bonusRate
        HBox bonusRateButtons = new HBox(decrementButtonBonusRate,countLabBonusRate,incrementButtonBonusRate);
        bonusRateButtons.setAlignment(Pos.CENTER);

// HBox for maxWordsInList
        HBox maxWordsInListButtons = new HBox(decrementButtonMaxWordsInList,countLabMaxWordsInList,incrementButtonMaxWordsInList);
        maxWordsInListButtons.setAlignment(Pos.CENTER);



// start button
        Button start = new Button("Start");
        start.setOnAction((e) -> controller.setUpHost(countNbPlayer.get(),
                countLives.get(),
                countMaxWordsInList.get(),
                countRedWordRate.get(),
                countBonusRate.get()
                ));
        start.setAlignment(Pos.CENTER);


// VBox containing all the buttons
        VBox choiceBox = new VBox(messageNbPlayer, nbPlayerButtons,
                messageLives, livesButtons,
                messageMaxWordsInList, maxWordsInListButtons,
                messageRedWordRate, redWordRateButtons,
                messageBonusRate, bonusRateButtons,
                start
        );
        choiceBox.setAlignment(Pos.CENTER);
        choiceBox.setAlignment(Pos.CENTER);
        VBox.setMargin(start,new Insets(15,0,0,0));
        root.setCenter(choiceBox);

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

        VBox vbox;
        if(this.controller.getGame().getMode().equals(Mode.SOLO)){
             vbox = new VBox(text);
        }else {
            this.additionnalInfo = new StyleClassedTextArea();
            additionnalInfo.setEditable(false);
            additionnalInfo.setWrapText(false);

            vbox = new VBox(text, additionnalInfo);
        }


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
            List<Integer> redWordList = ((GameMultiPlayer) this.controller.getGame()).getRedWordsPos();
            List<Integer> blueWordList = ((GameMultiPlayer) this.controller.getGame()).getBlueWordsPos();
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
        if (!controller.getGame().getMode().equals(Mode.SOLO)){
            this.resetAdditionalInfo();
        }
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