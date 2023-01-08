package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordListTest {

    @Test
    void testGenerateList() {
        WordList.wordList.clear();
        WordList.generateList();
        assertFalse(WordList.wordList.isEmpty());
    }

    @Test
    void testStartingList() {
        int wordNb = 18;
        // Appel de la méthode à tester
        List<String> startingList = WordList.startingList(wordNb);

        // Vérifie que la liste retournée contient le nombre de mots attendu
        assertEquals(wordNb, startingList.size());

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
        assertEquals(2, currentList.size());
        assertEquals("mot3", currentList.get(0));
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