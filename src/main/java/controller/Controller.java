package controller;

import javafx.application.Platform;
import model.*;
import view.View;

import javafx.scene.input.KeyEvent;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class Controller {
    private Game game;
    private View view;

    /**
     * when a key is pressed, it calls the corresponding function in game
     * @param e
     */
    public void keyPressed(KeyEvent e){
        char k = e.getCharacter().charAt(0);
        game.keyInput(k);
    }

    /**
     * Update the view by loading data from the model via getters.
     * call view function to change the word list, colors and secondary displays such as levels and lives.
     */
    public void update() {
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
                this.view.resetAdditionalInfo();
                this.view.printLevel(((GameCompetitiveSolo)game).getLevel());
                this.view.printLives(((GameCompetitiveSolo)game).getLives());

            }else if(game.getMode().equals(Mode.MULTI)){
                this.view.resetAdditionalInfo();
                this.view.printLives(((GameMultiPlayer)game).getLives());
            }
        });
    }


    /**
     * calls the function of the view that displays the end of game statistics
     */
    public void getStats() {
        String s = "Speed: " + game.getSpeed() + " MPM\n"
                + "Precision: " + game.getPrecision() + "%\n"
                + "Regularity: " + game.getRegularity() + " second(s)\n"
                + "Score: " + game.getScore() + " word(s)\n"
                + ((this.game instanceof  GameMultiPlayer) ? "Rank: " + ((GameMultiPlayer)game).getRank() + "\n" : "");

        Platform.runLater(() -> view.setEndScreen(s));
    }

    /**
     * Calls the game display function and calls the NormalSolo mode initialization function.
     * Then update the display
     */
    public void setMode0(int nbWord) {
        Platform.runLater(() -> this.view.startGame());
        ((GameNormalSolo)this.game).setNbWord(nbWord);
        this.game.init(this);
        this.update();
    }

    public void parametersMode0() {
        this.game = Game.of(0);
        assert this.game != null;
        Platform.runLater(() -> this.view.paramMode0());
    }

    /**
     * Calls the game display function and calls the CompetitiveSolo mode initialization function.
     * Then update the display
     */
    public void setMode1(int lives, int level, int timeBetweenWords, int bonusRate, int maxWordsInList) {
        Platform.runLater(() -> this.view.startGame());
        ((GameCompetitiveSolo)this.game).setParams(lives, level, timeBetweenWords, bonusRate, maxWordsInList);
        this.game.init(this);
        this.update();
    }

    public void parametersMode1() {
        this.game = Game.of(1);
        assert this.game != null;
        Platform.runLater(() -> this.view.paramMode1());
    }

    /**
     * Calls the game display function and set the game as MultiPlayer mode.
     */
    public void setMode2() {
        Platform.runLater(() -> this.view.menuMultiplayer());
    }

    public void parametersMode2() {
        this.game = Game.of(2);
        assert this.game != null;
        Platform.runLater(() -> this.view.paramMode2());
    }

    /**
     * set up the host for the multiplayer game mode.
     * @param nbPlayers the number of players selected.
     */
    public void setUpHost(int nbPlayers, int lives, int maxWordsInList, int redWordRate, int bonusRate) {
        String ip = ((GameMultiPlayer)this.game).setUpHost(nbPlayers, lives, maxWordsInList, redWordRate, bonusRate);
        Platform.runLater(() -> this.view.waitAsHostPage(ip));
        this.game.init(this);
    }

    /**
     * set up the player who join a game for the multiplayer game mode.
     * @param ip the IP of the host.
     */
    public void setUpJoin(String ip) {
        this.game = Game.of(2);
        ((GameMultiPlayer)this.game).setUp(ip,false);
        Platform.runLater(() -> this.view.waitAsJoinerPage(ip));
        this.game.init(this);
    }

    /**
     * reset the game and calls up the menu view function.
     */
    public void resetAndGoMenu() {
        this.game.stop();
        this.game = null;
        Platform.runLater(() -> this.view.startingMenuGui());
    }

    /**
     * start multiplayer game mode in the view.
     */
    public void startMultiPlayer() {
        Platform.runLater(() -> {
            this.view.startGame();
            this.update();
        });
    }

    /**
     * calls the error display function.
     * @param e error obtained (in the model)
     */
    public void error(Exception e) {
        Platform.runLater(() -> this.view.printError(e));
    }

    public void setView(View v1) {
        this.view = v1;
    }

    public Game getGame() {
        return this.game;
    }


    public boolean isGameRunning() {
        return game != null && game.isRunning();
    }


}
