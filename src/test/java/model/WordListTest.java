package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordListTest {

    @Test
    void testGenerateList() {
        // Vérifie que la liste de mots est vide avant l'appel de la méthode
        assertTrue(WordList.wordList.isEmpty());

        // Appel de la méthode à tester
        WordList.generateList();

        // Vérifie que la liste de mots n'est plus vide après l'appel de la méthode
        assertFalse(WordList.wordList.isEmpty());

        // Vérifie que la liste contient bien le nombre de mots attendu
        assertEquals(WordList.startingWordCount, WordList.wordList.size());
    }

    @Test
    void testStartingList() {
        // Appel de la méthode à tester
        List<String> startingList = WordList.startingList();

        // Vérifie que la liste retournée contient le nombre de mots attendu
        assertEquals(WordList.startingWordCount, startingList.size());

        // Vérifie que chaque élément de la liste fait partie de la liste générale de mots
        for (String word : startingList) {
            assertTrue(WordList.wordList.contains(word));
        }
    }

    @Test
    void testUpdate() {
        // Création d'une liste de mots de test
        List<String> currentList = new ArrayList<>();
        currentList.add("mot1");
        currentList.add("mot2");
        currentList.add("mot3");

        // Appel de la méthode à tester avec l'option "addNew" à false
        WordList.update(currentList, false);

        // Vérifie que la liste a été mise à jour comme prévu
        assertEquals(2, currentList.size());
        assertEquals("mot2", currentList.get(0));
        assertEquals("mot3", currentList.get(1));

        // Appel de la méthode à tester avec l'option "addNew" à true
        WordList.update(currentList, true);

        // Vérifie que la liste a été mise à jour comme prévu
        assertEquals(3, currentList.size());
        assertEquals("mot3", currentList.get(0));
        assertNotNull(currentList.get(2));
    }

    @Test
    void testAddWord() {
        // Création d'une liste de mots de test
        List<String> currentList = new ArrayList<>();
        currentList.add("mot1");
        currentList.add("mot2");

        // Appel de la méthode à tester
        WordList.addWord(currentList);

        // Vérifie que la liste a été mise à jour comme prévu
        assertEquals(3, currentList.size());
        assertNotNull(currentList.get(2));
    }

}