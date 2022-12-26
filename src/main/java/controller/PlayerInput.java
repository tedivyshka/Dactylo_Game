package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import model.Game;


public class PlayerInput extends KeyAdapter{
	private Game game;

	public PlayerInput(Game game) {
		this.game = game;
	}
	
	@Override
	public void keyPressed(KeyEvent e){
		int k = e.getKeyCode();
		game.keyInput(k);
	}
}


