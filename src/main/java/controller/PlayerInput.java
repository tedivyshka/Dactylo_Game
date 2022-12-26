package controller;

import javafx.scene.input.KeyEvent;

import model.Game;


public class PlayerInput {
	private Game game;

	public PlayerInput(Game game) {
		this.game = game;
	}

	public void keyPressed(KeyEvent e){
		char k = e.getCharacter().charAt(0);
		game.keyInput(k);
	}
}


