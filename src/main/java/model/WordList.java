package model;

import java.io.*;  // Import the File class
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordList{

    public static ArrayList<String> wordList = new ArrayList<>(); // wordlist generated from txt file
    private static int startingWordCount = 16; // The number of words in the beginning of the game
    public static void generateList(){
        BufferedReader bf;
        try {
            bf = new BufferedReader(new FileReader("src/main/resources/wordlist.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println("Can't find file \"resources/wordlist.txt\".");
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

    public static List<String> startingList(){
        List<String> list = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < startingWordCount; i++){
            int randEntry = rand.nextInt(wordList.size() + 1);
            list.add(wordList.get(randEntry));
        }
        return list;
    }

    public static  void main(String [] args){
        generateList();
        System.out.println(startingList().toString());
    }

	public static void update(List<String> currentList, boolean addNew) {
		currentList.remove(0);
        if(addNew) {
            Random rand = new Random();
            int randEntry = rand.nextInt(wordList.size() + 1);
            currentList.add(wordList.get(randEntry));
        }
	}
}