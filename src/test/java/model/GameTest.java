package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private final Game game = new GameNormalSolo();

    private static final int GAMEMODE_NORMAL_SOLO = 0;
    private static final int GAMEMODE_COMPETITIVE_SOLO = 1;
    //private static final int MULTI



    @Test
    void testOf() {
        // Vérifie que les différents modes de jeu sont bien créés
        assertTrue(Game.of(GAMEMODE_NORMAL_SOLO) instanceof GameNormalSolo);
        assertTrue(Game.of(GAMEMODE_COMPETITIVE_SOLO) instanceof GameCompetitiveSolo);
        //assertTrue(Game.of( "MULTI" instanceof Multi);
    }


    @Test
    public void testGetPrecision() {
        game.setCorrectCharacters(5);
        game.setTypedCharacters(10);
        assertEquals(50, game.getPrecision(), 0.1);

        game.setCorrectCharacters(5);
        game.setTypedCharacters(15);
        assertEquals(33.3, game.getPrecision(), 0.1);

        game.setCorrectCharacters(5);
        game.setTypedCharacters(5);
        assertEquals(100, game.getPrecision(), 0.1);
    }

    @Test
    public void testGetSpeed() {
        game.setStartTime(System.nanoTime() - 1000000000); // 1 second elapsed
        game.setCorrectCharacters(5);
        assertEquals(60, game.getSpeed(), 0.1);

        game.setStartTime(System.nanoTime() - 2000000000); // 2 seconds elapsed
        game.setCorrectCharacters(10);
        assertEquals(60, game.getSpeed(), 0.1);
    }

    @Test
    public void testGetRegularity() {
        game.setRegularitySum(5000000); // 5ms
        game.setCorrectCharacters(2);
        assertEquals(5, game.getRegularity(), 0.1);
    }

}
