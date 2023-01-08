package model;

import java.io.*;  // Import the File class
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
    Class to handle creating and updating a list of Strings representing
    the list of words in the game
 */
public class WordList{

    public static ArrayList<String> wordList = new ArrayList<>(); // wordlist generated from txt file
    static String sourceFile = "src/main/resources/wordlist.txt"; // File name of the word list

    /**
     * Generates the static variable wordList from a txt file.
     */
    public static void generateList(){
        BufferedReader bf;
        try {
            bf = new BufferedReader(new FileReader(sourceFile));
        }
        catch (FileNotFoundException e) {
            System.out.println("Can't find file " + sourceFile + ".");
            return;
        }
        String line = "";
        while (line != null) {
            wordList.add(line);
            try {
                line = bf.readLine();
            } catch (IOException e) { break; };
        }
        try {
			bf.close();
		} catch (IOException e) {}
    }

    /**
     * Create starting list with random words from wordList
     * @return List<String> of size startingWordCount
     */
    public static List<String> startingList(int startingWordCount){
        List<String> list = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < startingWordCount; i++){
            int randEntry = rand.nextInt(wordList.size() );
            list.add(wordList.get(randEntry));
        }
        return list;
    }

    /**
     * Update the list given by removing the first word and adding a random
     * word from wordList at the end
     * @param currentList
     * @param addNew specifies if we need to add a new word or not
     */
	public static void update(List<String> currentList, boolean addNew) {
		currentList.remove(0);
        if(addNew) {
            Random rand = new Random();
            int randEntry = rand.nextInt(wordList.size() + 1);
            currentList.add(wordList.get(randEntry));
        }
	}

    /**
     * Update the list given by adding a new random word from wordList
     * @param currentList
     */
    public static void addWord(List<String> currentList){
        Random rand = new Random();
        int randEntry = rand.nextInt(wordList.size() + 1);
        currentList.add(wordList.get(randEntry));
    }
}