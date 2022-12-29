package model;

import controller.Controller;

import java.util.List;

public abstract class Game {
	protected Mode mode;
	protected int currentPos;
	protected int score;
	protected boolean gameRunning;
	protected long regularitySum;
	protected long previousCorrectCharTime;
	protected long startTime;
	protected int correctCharacters;
	protected int typedCharacters;
	protected List<String> currentList;
	protected static int charactersForWord = 5;


	public static Game of(int gamemode) {
		switch(gamemode) {
			case 0 : return new GameNormalSolo();
			case 1 : return new GameCompetitiveSolo();
			case 2 : return new GameMultiPlayer();
		}
		return null;
	}

	public double getPrecision(){
		double result = ( (float) this.correctCharacters / (float) this.typedCharacters ) * 100;
		result = Math.round(result * 10);
		return result / 10;
	}

	public double getSpeed(){
		long timeToFinishMillisecond = (System.nanoTime() - this.startTime) / 1000000;
		double timeToFinish = ((double) timeToFinishMillisecond) / 1000;
		double timeInMinutes = timeToFinish / 60;
		double result = this.correctCharacters / (timeInMinutes * charactersForWord) ;
		result = result * 1000;
		long tmp = Math.round(result);
		return (double) tmp / 1000;
	}

	public double getRegularity(){
		double result = (double) this.regularitySum / (double) (1000000 * (this.correctCharacters-1)) ;
		result = result * 1000;
		long tmp = Math.round(result);
		return (double) tmp / 1000;
	}

	public abstract boolean keyInput(int k);

	public List<String> getList(){
		return this.currentList;
	}


	public String getWord() {
		return this.currentList.get(0);
	}

	public int getPos(){
		return this.currentPos;
	}

	public boolean isRunning(){
		return this.gameRunning;
	}

	public abstract void init(Controller controller);



	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public int getCurrentPos() {
		return currentPos;
	}

	public void setCurrentPos(int currentPos) {
		this.currentPos = currentPos;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isGameRunning() {
		return gameRunning;
	}

	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}

	public long getRegularitySum() {
		return regularitySum;
	}

	public void setRegularitySum(long regularitySum) {
		this.regularitySum = regularitySum;
	}

	public long getPreviousCorrectCharTime() {
		return previousCorrectCharTime;
	}

	public void setPreviousCorrectCharTime(long previousCorrectCharTime) {
		this.previousCorrectCharTime = previousCorrectCharTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getCorrectCharacters() {
		return correctCharacters;
	}

	public void setCorrectCharacters(int correctCharacters) {
		this.correctCharacters = correctCharacters;
	}

	public int getTypedCharacters() {
		return typedCharacters;
	}

	public void setTypedCharacters(int typedCharacters) {
		this.typedCharacters = typedCharacters;
	}

	public List<String> getCurrentList() {
		return currentList;
	}

	public void setCurrentList(List<String> currentList) {
		this.currentList = currentList;
	}

	public static int getCharactersForWord() {
		return charactersForWord;
	}

	public static void setCharactersForWord(int charactersForWord) {
		Game.charactersForWord = charactersForWord;
	}

}
