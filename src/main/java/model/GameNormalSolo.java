package model;

import controller.Controller;

import java.util.ArrayList;

public class GameNormalSolo extends Game {
	private int wordsToWin = 20;

	/**
	 * Initialize the game by starting all the variables
	 * @param c
	 */
	@Override
	public void init(Controller c) {
		super.mode = Mode.SOLO;
		WordList.generateList();
		this.currentList = WordList.startingList(Math.min(this.wordsToWin, 16));
		this.currentPos = 0;
		this.score = 0;
		this.correctCharacters = 0;
		this.typedCharacters = 0;
		this.gameRunning = true;
		this.startTime = System.nanoTime();
		this.previousCorrectCharTime = 0;
		this.regularityList = new ArrayList<>();
	}

	/**
	 * Handle the character typed by the player
	 * @param k character number
	 * @return boolean if the typed character was correct or not
	 */
	@Override
	public boolean keyInput(int k) {
		this.typedCharacters++;
		if(k == ' ') {
			String word = this.currentList.get(0);
			if(word.length() == this.currentPos) {
				//Word done -> move to next one
				this.correctCharacters++;
				this.score++;
				this.currentPos = 0;

				//Update list, remove head and add new word
				WordList.update(currentList, wordsToWin - this.score >= this.currentList.size());
				gameRunning = !(wordsToWin == this.score);
				return true;
			}
			return false;
		}
		else {
			String word = this.currentList.get(0);
			if(word.length() == this.currentPos) {
				//Word done but did not receive space -> error by the player
				return false;
			}
			else if (k == word.charAt(this.currentPos)) {
				//Character typed is the correct one
				this.currentPos++;
				this.correctCharacters++;
				if(this.previousCorrectCharTime == 0){
					this.previousCorrectCharTime = System.nanoTime();
				}else{
					this.regularityList.add((System.nanoTime() - this.previousCorrectCharTime));
					this.previousCorrectCharTime = System.nanoTime();
				}
				return true;
			}else {
				//Character typed is the wrong one
				return false;
			}
		}
	}

	public void setNbWord(int nbWord) {
		this.wordsToWin = nbWord;
	}
}
