package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameCompetitiveSolo extends Game{
    private List<String> currentList;
    private int currentPos;
    private int lives;
    private int score;
    private int level;
    private int timeBetweenWords; //Ajouter un mot après chaque timeBetweenWords en secondes
    private boolean gameRunning;
    private List<Integer> blueWordsPos; //Les positions des mots bleu qui ajoutent des vies
    private static int bonusRate; // % possibilite d'avoir un mot bonus

    @Override
    public void init() {
        WordList.generateList();
        this.currentList = WordList.startingList();
        this.currentPos = 0;
        this.lives = 0;
        this.level = 1;
        this.timeBetweenWords = 3;
        this.gameRunning = true;
        this.blueWordsPos = new ArrayList<Integer>();
    }



    public boolean keyInput(int k) {
        System.out.println("Got input\n");
        if(k == ' ') {
            String word = this.currentList.get(0);
            if(word.length() == this.currentPos) {
                //Word done -> move to next one
                this.currentPos = 0;
                this.score++;
                if(score == 100) this.levelUp();
                System.out.println("Finished word: " + word + " , lives left = " + this.lives);
                this.currentList.remove(0);
                return true;
            }
            return false;
        }
        else {
            System.out.println("Adding character: " + ((char)k));
            String word = this.currentList.get(0);
            if(word.length() == this.currentPos) {
                System.out.println("Wrong character");
                this.lives--;
                if(this.lives == 0) this.gameRunning = false;
                return false; //Wrong input, waiting for space
            }
            else if (k == word.charAt(this.currentPos)) {
                this.currentPos++;
                return true;
            }else {
                System.out.println("Expected " + word.charAt(this.currentPos) + " ; got " + (char)k);
                this.lives--;
                if(this.lives == 0) this.gameRunning = false;
                return false;
            }
        }
    }

    private void levelUp() {
        this.level++;
        this.score = 0;
        this.timeBetweenWords = (int) (3 * Math.pow(0.9,level));
    }

    @Override
    public String getWord() {
        return this.currentList.get(0);
    }

    @Override
    public List<String> getList() {
        return this.currentList;
    }

    public void updateList(){
        WordList.update(this.currentList,true);
        for(int i = 0; i < this.blueWordsPos.size() ; i++) this.blueWordsPos.set(i, this.blueWordsPos.get(i) - 1);
        Random rand = new Random();
        int randEntry = rand.nextInt(101);
        if(randEntry < bonusRate){
            this.blueWordsPos.add(this.currentList.size() - 1);
        }
    }


    @Override
    public int getPos() {
        return this.currentPos;
    }

    @Override
    public boolean isRunning() {
        return this.gameRunning;
    }

    @Override
    public double getPrecision() {
        return 0;
    }

    @Override
    public double getSpeed() {
        return 0;
    }
    @Override
    public double getRegularity() {
        return 0;
    }
}
