package model;

import static org.junit.jupiter.api.Assertions.*;

import controller.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GameCompetitiveSoloTest {
    private GameCompetitiveSolo game;

    @BeforeEach
    public void setUp() {
        game = new GameCompetitiveSolo();
        game.setParams(20,1,3,20,18);
        game.init(new Controller());
        game.getList().set(0,"test");
    }

    @Test
    public void testInit() {
        assertEquals(Mode.COMPETITIVE, game.getMode());
        assertEquals(20, game.getLives());
        assertEquals(1, game.getLevel());
        assertEquals(3, game.getTimeBetweenWords());
        assertTrue(game.isRunning());
    }

    @Test
    public void testKeyInput_CorrectCharacter() {
        assertTrue(game.keyInput('t'));
        assertEquals(1, game.getCurrentPos());
        assertEquals(1, game.getCorrectCharacters());
        assertEquals(1, game.getTypedCharacters());
    }

    @Test
    public void testKeyInput_WrongCharacter() {
        assertFalse(game.keyInput('z'));
        assertEquals(0, game.getCurrentPos());
        assertEquals(0, game.getCorrectCharacters());
        assertEquals(1, game.getTypedCharacters());
        assertEquals(19, game.getLives());
    }

    @Test
    public void testKeyInput_Space() {
        assertFalse(game.keyInput(' '));
        assertEquals(0, game.getCurrentPos());
        assertEquals(0, game.getCorrectCharacters());
        assertEquals(1, game.getTypedCharacters());
        assertEquals(19, game.getLives());

        // Type the entire first word
        game.keyInput('t');
        game.keyInput('e');
        game.keyInput('s');
        game.keyInput('t');

        assertTrue(game.keyInput(' '));
        assertEquals(0, game.getCurrentPos());
        assertEquals(5, game.getCorrectCharacters());
        assertEquals(6, game.getTypedCharacters());
    }

    @Test
    public void testKeyInput_LoseLife() {
        game.setLives(1);
        assertFalse(game.keyInput('z'));
        assertFalse(game.isRunning());
    }


}


