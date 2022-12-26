package controller;

import javafx.application.Platform;
import model.Game;
import view.View;

import javafx.scene.input.KeyEvent;

import javax.swing.*;

public class Controller {
    private static Game game = Game.of(0);
    private View view;

    public void changeMode(int i) {
        game = Game.of(i);
        //todo changer la vue
    }


    public void keyPressed(KeyEvent e){
        System.out.println("key pressed");
        System.out.println("view = " + view);
        char k = e.getCharacter().charAt(0);
        game.keyInput(k);

    }
    public void update() {
        System.out.println("update view");
        view.resetText();
        for(String s : game.getList()){
            view.printText(s);
        }
    }

    public void updateFirst() {
        Platform.runLater(() -> {
            System.out.println("first update view");
            for(String s : game.getList()){
                view.printText(s);
            }
        });
    }

    public void setView(View v1) {
        this.view = v1;
    }

    public View getView() {
        return this.view;
    }

    public Game getGame() {
        return Controller.game;
    }


}
