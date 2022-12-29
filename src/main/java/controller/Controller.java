package controller;

import javafx.application.Platform;
import model.Game;
import model.GameCompetitiveSolo;
import model.GameMultiPlayer;
import model.Mode;
import view.View;

import javafx.scene.input.KeyEvent;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class Controller {
    private Game game;
    private View view;


    public void init() {
        this.game = Game.of(0);
        this.game.init(this);
        this.update();
    }
    public void changeMode(int i) {
        if(this.game != null) {
            if (this.game.getMode().equals(Mode.COMPETITIVE)) ((GameCompetitiveSolo) this.game).cancelTimer();
        }
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
                    String s = null;
                    try {
                        s = it.next();
                    } catch (ConcurrentModificationException ignored) {
                    } finally {
                        view.printText(s);
                    }
                }
                view.colorWord(x);
            }

            if(game.getMode().equals(Mode.COMPETITIVE)){
                view.printLivesAndLevel(
                        ((GameCompetitiveSolo)game).getLives(),
                        ((GameCompetitiveSolo)game).getLevel()
                );
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
        String s = "Speed: " + game.getSpeed() + " MPM\n"
                + "Precision: " + game.getPrecision()
                + "%\n"
                + "Regularity: " + game.getRegularity() + "ms\n";

        Platform.runLater(() -> {
            view.setEndScreen(s);
        });

    }


    public void setMode1() {
        this.view.startGame();
        this.game = Game.of(0);
        this.game.init(this);
        this.update();
    }

    public void setMode2() {
        this.view.startGame();
        this.game = Game.of(1);
        this.game.init(this);
        this.update();
    }

    public void setMode3() {
        this.view.menuMultiplayer();
        this.game = Game.of(2);
    }

    public void setUpHost(int nbPlayers) {
        ((GameMultiPlayer)this.game).setUpHost(nbPlayers);
    }
}
