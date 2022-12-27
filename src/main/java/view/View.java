package view;

import controller.Controller;
import controller.PlayerInput;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.fxmisc.richtext.StyleClassedTextArea;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.Objects;


public class View extends Application {

    private final Controller controller;

    private StyleClassedTextArea text = null;


    public View(Controller c) {
        this.controller = c;
    }




    @Override
    public void start(Stage primaryStage){
        try {

            BorderPane root = new BorderPane();
            MenuBar menuBar = new MenuBar();
            Button exit = new Button("exit");
            exit.setOnAction(e -> System.exit(0));
            Menu newGame = new Menu("New game");
            MenuItem soloMode = new MenuItem("Single-player");
            soloMode.setOnAction(e -> this.controller.changeMode(0)); // appel controller

            MenuItem multiMode = new MenuItem("Multiplayer");
            soloMode.setOnAction(e -> this.controller.changeMode(1)); // appel controller

            MenuItem competitiveMode = new MenuItem("Normal Competitive");
            soloMode.setOnAction(e -> this.controller.changeMode(2)); // appel controller

            newGame.getItems().addAll(soloMode, multiMode, competitiveMode);
            menuBar.getMenus().addAll(newGame);

            HBox topBar = new HBox(menuBar, exit);
            HBox.setHgrow(menuBar, Priority.ALWAYS);
            HBox.setHgrow(exit, Priority.NEVER);
            root.setTop(topBar);

            this.text = new StyleClassedTextArea();
            text.setEditable(false);

            VBox vbox = new VBox(text);
            vbox.setPadding(new Insets(10));
            root.setCenter(vbox);

            Scene scene = new Scene(root, 1080, 720);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            primaryStage.setTitle("Dactylo-Game");
            primaryStage.setScene(scene);



            scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    controller.keyPressed(event);
                    controller.update();

                }
            });

            root.requestFocus();
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void printText(String s) {
        this.text.appendText(s + " ");
    }


    public void resetText() {
        this.text.replaceText("");
    }

    public void colorWord(int pos){
        this.text.setStyleClass(0, this.text.getLength(), "black");
        this.text.setStyleClass(0, pos, "green");
    }


    public static void main(String[] args) {
        Platform.setImplicitExit(false);
        Controller controller = new Controller();
        controller.changeMode(0);
        controller.getGame().init();
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
        controller.update();
    }

}


