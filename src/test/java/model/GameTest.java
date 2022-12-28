package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private static final int GAMEMODE_NORMAL_SOLO = 0;
    private static final int GAMEMODE_COMPETITIVE_SOLO = 1;

    @Test
    void testOf() {
        // Vérifie que les différents modes de jeu sont bien créés
        assertTrue(Game.of(GAMEMODE_NORMAL_SOLO) instanceof GameNormalSolo);
        assertTrue(Game.of(GAMEMODE_COMPETITIVE_SOLO) instanceof GameCompetitiveSolo);
        assertNull(Game.of(2));
    }

    @Test
    void testAbstractMethods() {
        // Création d'un objet de test
        Game game = Game.of(GAMEMODE_NORMAL_SOLO);

        // Vérifie que les méthodes abstraites sont bien implémentées et retournent des valeurs valides
        assert game != null;
        assertFalse(game.keyInput(0));
        assertNotNull(game.getWord());
        assertNotNull(game.getList());
        assertTrue(game.getPrecision() >= 0);
        assertTrue(game.getSpeed() >= 0);
        assertTrue(game.getPos() >= 0);
        assertTrue(game.isRunning());
        assertTrue(game.getRegularity() >= 0);

        // Vérifie que la méthode init() est bien implémentée
        game.init(null);
    }

    @Test
    void testGetMode() {
        // Création d'un objet de test
        Game game = Game.of(GAMEMODE_NORMAL_SOLO);

        // Vérifie que la méthode getMode() retourne un objet non-null
        assert game != null;
        assertNotNull(game.getMode());
    }

}
