package model;

public abstract class Game {
	public static Game of(int gamemode) {
		switch(gamemode) {
			case 0 : return new GameNormalSolo();
			case 1 : return null; //TODO Game Normal Competitive
			case 2 : return null; //TODO Game MultiPlayer
		}
		return null;
	}

	public abstract void keyInput(int k);
}
