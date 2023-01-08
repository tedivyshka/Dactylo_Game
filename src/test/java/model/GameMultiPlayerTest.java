package model;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.Controller;

import java.util.List;


class GameMultiPlayerTest {

    private GameMultiPlayer game;

    @BeforeEach
    public void setUp() {
        game = new GameMultiPlayer();
        Controller c = new Controller();
        game.setUpHost(2,20,18,35,20);
        game.init(c);
        game.getList().set(0,"test");

    }

    @Test
    public void testSetUp() {
        String hostAddress = "localhost";
        game.setUp(hostAddress, false);
        assertEquals(hostAddress, GameMultiPlayer.SERVER_HOST);
    }

    @Test
    public void testSetUpHost() {
        game.setUpHost(2,20,18,35,20);
        assertEquals(2, game.nbPlayers);
        assertEquals(20,game.lives);
        assertEquals(18,game.maxWordsInList);
        assertEquals(35,game.redWordRate);
        assertTrue(GameMultiPlayer.isHost);

    }

    @Test
    public void testInit() {
        assertEquals(Mode.MULTI, game.mode);
        assertNotNull(game.currentList);
        assertEquals(0, game.currentPos);
        assertEquals(0, game.correctCharacters);
        assertEquals(0, game.typedCharacters);
        assertEquals(20, game.lives);
        assertTrue(game.gameRunning);
        assertNotNull(game.redWordsPos);
        assertNotNull(game.regularityList);
    }

    @Test
    public void testInitRedWords() {
        game.currentList = WordList.startingList(18);
        game.initRedBlueWords();
        assertNotNull(game.redWordsPos);
    }

    @Test
    public void testKeyInputCorrectCharacter() {
        assertTrue(game.keyInput('t'));
        assertEquals(1, game.getCurrentPos());
        assertEquals(1, game.getCorrectCharacters());
        assertEquals(1, game.getTypedCharacters());
    }

    @Test
    public void testKeyInputLivesExpired() {
        game.lives = 1;
        game.currentList = List.of("test");
        game.currentPos = 0;
        game.keyInput('x');
        assertEquals(0, game.lives);
    }

    @Test
    public void testUpdateList() {
        /*
         * This is the same function as in the other Game(...) classes
         * with server implications, which is difficult to test here.
         */
    }




}