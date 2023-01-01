package model;

import controller.Controller;

import java.util.Collections;
import java.util.List;

public abstract class Game {
	protected Mode mode;
	protected int currentPos;
	protected int score;
	protected boolean gameRunning;
	protected List<Long> regularityList;
	protected long previousCorrectCharTime;
	protected long startTime;
	protected int correctCharacters;
	protected int typedCharacters;
	protected List<String> currentList;
	protected static int charactersForWord = 5;

	/**
	 * Static factory pattern to create a game
	 * @param gameMode the mode of the game
	 * @return Game object according to the game mode
	 */
	public static Game of(int gameMode) {
		switch(gameMode) {
			case 0 : return new GameNormalSolo();
			case 1 : return new GameCompetitiveSolo();
			case 2 : return new GameMultiPlayer();
			default : return null;
		}
	}

	/**
	 * Get the precision statistic of the game:
	 * Number of correct characters divided by the number of total characters typed
	 * Multiplied by 100 in order to display as percentage
	 * @return precision
	 */
	public double getPrecision(){
		double result = ( (float) this.correctCharacters / (float) this.typedCharacters ) * 100;
		result = Math.round(result * 10);
		return result / 10;
	}

	/**
	 * Get the speed statistic of the game:
	 * The number of correct characters divided by the time the game took in minutes,
	 * divided by the number of characters per word in average (here: 5)
	 * @return speed
	 */
	public double getSpeed(){
		long timeToFinishMillisecond = (System.nanoTime() - this.startTime) / 1000000;
		double timeToFinish = ((double) timeToFinishMillisecond) / 1000;
		double timeInMinutes = timeToFinish / 60;
		double result = this.correctCharacters / (timeInMinutes * charactersForWord) ;
		result = result * 1000;
		long tmp = Math.round(result);
		return (double) tmp / 1000;
	}

	/**
	 * The series under study is the regularityList
	 * which contains the duration between 2 consecutive useful characters
	 *
	 * To calculate the regularity, we proceed as follows:
	 * 1 - Calculate the arithmetic average of the series.
	 * 2 - Calculate the square of the deviation from the average of each value in the series.
	 * 3 - Calculate the sum of the values obtained.
	 * 4 - We divide by the size of the series.
	 * 5 - Calculate the square root of the result.
	 *
	 * @return regularity
	 */
	public double getRegularity(){
		// conversion in seconds
		List<Double> reg = this.regularityList.stream().mapToDouble(x -> x / 1000000000f).boxed().toList();
		// calcul of average
		double average = reg.stream().reduce(0D ,Double::sum) / reg.size();
		// distance from the average of each of the values in the series.
		double sum = reg.stream().map(x -> Math.abs(average - x))
				// to square // sum of list
				.map(x -> x * x).reduce(0D, Double::sum);
		// divided by the size of the series
		sum /= reg.size();
		// square root
		return Math.sqrt(sum);
	}

	public abstract boolean keyInput(int k);

	public List<String> getList(){
		return this.currentList;
	}

	public abstract void init(Controller controller);

	public String getWord() {
		return this.currentList.get(0);
	}

	public void stop() {}

	public int getPos(){
		return this.currentPos;
	}

	public boolean isRunning(){
		return this.gameRunning;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public int getCurrentPos() {
		return currentPos;
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
}
