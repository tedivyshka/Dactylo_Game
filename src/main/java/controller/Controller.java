package controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import model.Game;
import model.GameNormalSolo;
import view.View;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private static Game game = new GameNormalSolo();;
    private View view;

    public void changeMode(int i) {
        game = Game.of(i);
    }


    @Override
    public void keyPressed(KeyEvent e){
        System.out.println("key pressed\n");
        int k = e.getKeyCode();
        boolean res = game.keyInput(k);

        if(res){
            view.resetText();
            for(String s : game.getList()){
                view.printText(s);
            }
        }
    }


    public static void main(String[] args) {
        //Controller controller = new Controller();
        //controller.game = Game.of(0);
        Platform.runLater(() -> {
            try {
                View v1 = new View();
                Stage stage = new Stage();
                v1.start(stage);

                //v1.printText("test1 ");
                //v1.printText("test2 ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void setView(View v1) {
        this.view = v1;
        Platform.runLater(() ->{
            view.resetText();
            for(String s : game.getList()){
                view.printText(s);
            }
        });
    }

    public View getView() {
        return this.view;
    }

    public Game getGame() {
        return this.game;
    }
}
