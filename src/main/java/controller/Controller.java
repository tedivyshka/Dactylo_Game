package controller;

import javafx.application.Platform;
import model.Game;
import view.View;

import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.util.List;

public class Controller {
    private static Game game = Game.of(0);
    private View view;

    public void changeMode(int i) {
        game = Game.of(i);
        //todo changer la vue
    }


    public void keyPressed(KeyEvent e){
        char k = e.getCharacter().charAt(0);
        game.keyInput(k);

    }
    public void update() {
        // on récupère ces données car on ne sait pas quand les instructions
        // du runLater seront effectuées. Cela évite que la liste ou
        // la position soit modifiée entre temps.
        int x = game.getPos();
        List<String> l = game.getList();

        Platform.runLater(() -> {
            view.resetText();
            for(String s : l){
                view.printText(s);
            }
            view.colorWord(x);
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


    public boolean isGameRunning() {
        return game.isRunning();
    }

    public void getStats() {
        Platform.runLater(() -> {
            String s = "";
            s += "Speed: " + game.getSpeed() + " MPM\n";
            s += "Precision: " + game.getPrecision() + "%\n";
            s += "Regularity: " + game.getRegularity() + "ms\n";
            view.setEndScreen(s);
        });

    }
}
