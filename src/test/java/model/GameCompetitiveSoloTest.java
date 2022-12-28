package model;

import static org.junit.jupiter.api.Assertions.*;

import controller.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.TimerTask;


public class GameCompetitiveSoloTest {
    private GameCompetitiveSolo game;

    private Controller controller;

    @BeforeEach
    public void setUp() {
        game = new GameCompetitiveSolo();
        game.init(controller);
        controller.getGame().getList().set(0,"test");
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
        assertEquals(20, game.getLives());

        // Type the entire first word
        game.keyInput('t');
        game.keyInput('e');
        game.keyInput('s');
        game.keyInput('t');
        game.keyInput(' ');

        assertEquals(0, game.getCurrentPos());
        assertEquals(1, game.getCorrectCharacters());
        assertEquals(6, game.getTypedCharacters());
        assertEquals(20, game.getLives());
    }

    @Test
    public void testKeyInput_LoseLife() {
        game.setLives(1);
        assertFalse(game.keyInput('z'));
        assertFalse(game.isRunning());
    }

    @Test
    public void testLevelUp() {
        game.setLevel(1);
        game.setScore(100);
        game.levelUp();
        assertEquals(2, game.getLevel());
        assertEquals(0, game.getScore());
        assertEquals(2.7, game.getTimeBetweenWords(), 0.1);
    }

    @Test
    public void testUpdateList() {
        int x = game.getList().size();
        game.updateList();
        assertEquals(x, game.getList().size());
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
        game.setTypedCharacters(0);
        assertEquals(0, game.getPrecision(), 0.1);

        game.setCorrectCharacters(5);
        game.setTypedCharacters(5);
        assertEquals(100, game.getPrecision(), 0.1);
    }

    @Test
    public void testGetSpeed() {
        game.setStartTime(System.nanoTime() - 1000000000); // 1 second elapsed
        game.setCorrectCharacters(5);
        assertEquals(5, game.getSpeed(), 0.1);

        game.setStartTime(System.nanoTime() - 2000000000); // 2 seconds elapsed
        game.setCorrectCharacters(10);
        assertEquals(5, game.getSpeed(), 0.1);
    }

    @Test
    public void testGetRegularity() {
        game.setRegularitySum(5000000); // 5ms
        game.setCorrectCharacters(2);
        assertEquals(2.5, game.getRegularity(), 0.1);

        game.setRegularitySum(10000000); // 10ms
        game.setCorrectCharacters(3);
        assertEquals(3.3, game.getRegularity(), 0.1);
    }

}


