package model;

import controller.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameNormalSoloTest {
    private GameNormalSolo game;

    @BeforeEach
    public void setUp() {
        game = new GameNormalSolo();
        Controller controller = new Controller();
        game.init(controller);
        game.getList().set(0,"test");
    }
    @Test
    void testInit() {
        game.init(new Controller());
        assertEquals(Mode.SOLO, game.mode);
        assertNotNull(game.currentList);
        assertEquals(0, game.currentPos);
        assertEquals(0, game.score);
        assertEquals(0, game.correctCharacters);
        assertEquals(0, game.typedCharacters);
        assertTrue(game.gameRunning);
        assertNotEquals(0, game.startTime);
        assertEquals(0, game.previousCorrectCharTime);
    }

    @Test
    void testKeyInput_space() {
        assertFalse(game.keyInput(' '));
        assertEquals(1, game.typedCharacters);

        // Type the entire first word
        game.keyInput('t');
        game.keyInput('e');
        game.keyInput('s');
        game.keyInput('t');

        assertTrue(game.keyInput(' '));
        assertEquals(1, game.score);
        assertEquals(5, game.correctCharacters);
        assertEquals(0, game.currentPos);
    }

    @Test
    void testKeyInput_correctCharacter() {
        game.init(new Controller());
        assertFalse(game.keyInput('a'));

        game.currentList.set(0, "abc");
        game.currentPos = 0;
        assertTrue(game.keyInput('a'));
        assertEquals(1, game.correctCharacters);
        assertEquals(1, game.currentPos);
        assertTrue(game.keyInput('b'));
        assertEquals(2, game.correctCharacters);
        assertEquals(2, game.currentPos);
    }

    @Test
    void testKeyInput_incorrectCharacter() {
        game.init(new Controller());
        game.currentList.set(0, "abc");
        game.currentPos = 0;
        assertFalse(game.keyInput('d'));
        assertEquals(1, game.typedCharacters);
        assertEquals(0, game.correctCharacters);
    }
}

