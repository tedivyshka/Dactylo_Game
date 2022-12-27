package model;

import controller.Controller;

import java.util.List;

public abstract class Game {
	public static Game of(int gamemode) {
		switch(gamemode) {
			case 0 : return new GameNormalSolo();
			case 1 : return new GameCompetitiveSolo();
			case 2 : return null; //TODO Game MultiPlayer
		}
		return null;
	}

	public abstract boolean keyInput(int k);

	public abstract String getWord();

	public abstract List<String> getList();

	public abstract double getPrecision();

	public abstract double getSpeed();

	public abstract int getPos();

	public abstract boolean isRunning();

	public abstract double getRegularity();

	public abstract void init(Controller controller);

	public abstract void cancelTimer();
}
