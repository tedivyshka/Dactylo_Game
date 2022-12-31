package model;

import controller.Controller;

import javax.tools.DocumentationTool;
import java.util.*;
import java.util.concurrent.Future;

public class GameCompetitiveSolo extends Game{
    private int lives;
    private int level;
    private int timeBetweenWords; //Ajouter un mot après chaque timeBetweenWords en secondes
    private List<Integer> blueWordsPos; //Les positions des mots bleu qui ajoutent des vies
    private static final int maxWordsInList = 18;
    private static int bonusRate = 20; // % to get a blue word
    private Timer timer;
    private Controller controller;

    public void init(Controller c) {
        super.mode = Mode.COMPETITIVE;

        WordList.generateList();
        this.controller = c;
        this.currentList = WordList.startingList();
        this.currentPos = 0;
        this.correctCharacters = 0;
        this.typedCharacters = 0;
        this.lives = 20;
        this.level = 1;
        this.timeBetweenWords = 3;
        this.gameRunning = true;
        this.blueWordsPos = new ArrayList<Integer>();
        this.timer = new Timer();
        timerStart(true);
        this.startTime = System.nanoTime();
    }

    public void cancelTimer() {
        try{
            this.timer.cancel();
        }
        catch (IllegalStateException ex){}
    }


    public boolean keyInput(int k) {
        this.typedCharacters++;
        if(k == ' ') {
            String word = this.currentList.get(0);
            if(word.length() == this.currentPos) {
                //Word done -> move to next one
                this.correctCharacters++;
                this.currentPos = 0;
                this.score++;
                if(score % 100 == 0) this.levelUp();
                if(this.blueWordsPos.size() > 0 && this.blueWordsPos.get(0) == 0){
                    this.lives+= word.length();
                    this.blueWordsPos.remove(0);
                }
                System.out.println("Finished word: " + word + " , lives left = " + this.lives);
                this.updateList();
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
                if(this.lives <= 0) this.gameRunning = false;
                return false; //Wrong input, waiting for space
            }
            else if (k == word.charAt(this.currentPos)) {
                this.currentPos++;
                this.correctCharacters++;
                if(this.previousCorrectCharTime == 0){
                    this.previousCorrectCharTime = System.nanoTime();
                }else{
                    this.regularitySum += (System.nanoTime() - this.previousCorrectCharTime);
                    this.previousCorrectCharTime = System.nanoTime();
                }
                return true;
            }else {
                System.out.println("Expected " + word.charAt(this.currentPos) + " ; got " + (char)k);
                this.lives--;
                if(this.lives == 0) this.gameRunning = false;
                return false;
            }
        }
    }

    void levelUp() {
        this.level++;
        this.score = 0;
        this.timeBetweenWords = (int) (3 * Math.pow(0.9,level));
        try{
            this.timer.cancel();
        }
        catch (IllegalStateException ex){}
        timerStart(false);
    }

    void timerStart(boolean startOfGame) {
        int delay = (startOfGame) ? 2500 : 0; // In the beginning of the game we give the player 2.5 seconds before starting the timer
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                WordList.addWord(currentList);
                if(currentList.size() > maxWordsInList) validateCurrentWord();
                //Update blueWordsPos
                Random rand = new Random();
                int randEntry = rand.nextInt(101);
                if(randEntry < bonusRate){
                    blueWordsPos.add(currentList.size() - 1);
                }
                controller.update();
            }
        };
        timer.schedule(task,delay,this.timeBetweenWords * 1000L);
    }

    private void validateCurrentWord() {
        String word = this.currentList.get(0);
        if(word.length() == this.currentPos) {
            this.score++;
            if (score % 100 == 0) this.levelUp();
            if (this.blueWordsPos.size() > 0 && this.blueWordsPos.get(0) == 0) {
                this.lives+= word.length();
                this.blueWordsPos.remove(0);
            }
        }else{
            this.lives--;
            if(this.lives == 0) this.gameRunning = false;
        }
        this.currentPos = 0;
        this.currentList.remove(0);
        for(int i = 0; i < this.blueWordsPos.size() ; i++) this.blueWordsPos.set(i, this.blueWordsPos.get(i) - 1);
    }

    public void updateList(){
        //If the list is half or less full we add a new word
        boolean addNew = this.currentList.size() < (maxWordsInList / 2);
        WordList.update(this.currentList,addNew);

        //Update blueWordsPos
        this.blueWordsPos.replaceAll(integer -> integer - 1);
        if(!addNew) return;

        Random rand = new Random();
        int randEntry = rand.nextInt(101);
        if(randEntry < bonusRate){
            this.blueWordsPos.add(this.currentList.size() - 1);
        }

        controller.update();
    }

    public List<Integer> getBlueWordsPos() {
        return this.blueWordsPos;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLevel(int i) {
        this.level = i;
    }

    public int getLevel() {
        return this.level;
    }

    public double getTimeBetweenWords() {
        return this.timeBetweenWords;
    }

    public void setLives(int i) {
        this.lives = i;
    }


    @Override
    public void stop(){
        if(this.timer != null) {
            this.timer.cancel();
        }
    }
}
