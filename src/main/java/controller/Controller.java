package controller;

import javafx.application.Platform;
import model.Game;
import view.View;

import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;

public class Controller {
    private Game game = Game.of(0);
    private View view;

    public void changeMode(int i) {
        this.game.cancelTimer();
        this.game = Game.of(i);
        this.game.init(this);
        this.update();
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
            if(view != null) {
                view.resetText();
                for (Iterator<String> it = l.iterator(); it.hasNext(); ) {
                    String s = it.next();
                    view.printText(s);
                }
                view.colorWord(x);
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
        return this.game;
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
