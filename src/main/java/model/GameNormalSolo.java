package model;

import controller.Controller;

import java.util.List;

public class GameNormalSolo extends Game {
	private static int wordsToWin = 20;
	private static int charactersForWord = 5;
	private List<String> currentList;
	private int currentPos;
	private int score;
	private int correctCharacters;
	private int typedCharacters;
	private boolean gameRunning;
	private long startTime;
	private long regularitySum;
	private long previousCorrectCharTime;


	@Override
	public void init(Controller c) {
		WordList.generateList();
		this.currentList = WordList.startingList();
		this.currentPos = 0;
		this.score = 0;
		this.correctCharacters = 0;
		this.typedCharacters = 0;
		this.gameRunning = true;
		this.startTime = System.nanoTime();
		this.previousCorrectCharTime = 0;
	}

	@Override
	public void cancelTimer() {}

	@Override
	public List<Integer> getBlueWordsPos() {
		return null;
	}


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

				if(wordsToWin - this.score >= this.currentList.size()){
					WordList.update(currentList,true); //Update list, remove head and add new word
				}else{
					WordList.update(currentList,false);
				}
				if(this.wordsToWin == this.score) {
					System.out.println("Game finished");
					gameFinished();
				}

				System.out.println("Finished word: " + word + " , score = " + this.score);
				return true;
			}
			return false;
		}
		else {
			System.out.println("Adding character: " + ((char)k));
			String word = this.currentList.get(0);
			if(word.length() == this.currentPos) {
				System.out.println("Wrong character");
				return false; //Wrong input, waiting for space
			}
			else if (k == word.charAt(this.currentPos)) {
				this.currentPos++;
				this.correctCharacters++;
				if(this.previousCorrectCharTime == 0){
					this.previousCorrectCharTime = System.nanoTime();
				}else{
					this.regularitySum += (System.nanoTime() - this.previousCorrectCharTime);
					this.previousCorrectCharTime = System.nanoTime();
				}
				return true;
			}else {
				System.out.println("Expected " + word.charAt(this.currentPos) + " ; got " + (char)k);
				return false;
			}
		}
	}

	private void gameFinished() {
		double precision = this.getPrecision();
		double speed = this.getSpeed(); //vitesse
		double regularity = this.getRegularity();
		System.out.println("Speed: " + speed + " MPM");
		System.out.println("Precision: " + precision + "%");
		System.out.println("Regularity: " + regularity + "ms");
		this.gameRunning = false;
	}

	@Override
	public String getWord() {
		return this.currentList.get(0);
	}

	@Override
	public List<String> getList() {
		return this.currentList;
	}

	@Override
	public double getPrecision(){
		double result = ( (float) this.correctCharacters / (float) this.typedCharacters ) * 100;
		result = Math.round(result * 10);
		return result / 10;
	}

	@Override
	public double getSpeed(){
		long timeToFinishMillisecond = (System.nanoTime() - this.startTime) / 1000000;
		double timeToFinish = ((double) timeToFinishMillisecond) / 1000;
		double timeInMinutes = timeToFinish / 60;
		double result = this.correctCharacters / (timeInMinutes * charactersForWord) ;
		result = result * 1000;
		long tmp = Math.round(result);
		return (double) tmp / 1000;
	}

	@Override
	public double getRegularity(){
		double result = (double) this.regularitySum / (double) (1000000 * (this.correctCharacters-1)) ;
		result = result * 1000;
		long tmp = Math.round(result);
		return (double) tmp / 1000;
	}

	@Override
	public int getPos() {
		return this.currentPos;
	}

	@Override
	public boolean isRunning() {
		return this.gameRunning;
	}
}
