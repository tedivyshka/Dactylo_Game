package model;

import controller.Controller;

import javax.tools.DocumentationTool;
import java.util.*;
import java.util.concurrent.Future;

public class GameCompetitiveSolo extends Game{
    private int lives;
    private int level;
    private int timeBetweenWords; // Time in seconds between two words added by the game
    private List<Integer> blueWordsPos; // List of positions of blue (bonus) words
    private int bonusRate; // % to get a blue word
    private int maxWordsInList;
    private Timer timer;
    private Controller controller;

    public void setParams(int lives, int level, int timeBetweenWords, int bonusRate, int maxWordsInList){
        this.lives = lives;
        this.level = level;
        this.timeBetweenWords = timeBetweenWords;
        this.bonusRate = bonusRate;
        this.maxWordsInList = maxWordsInList;
    }
    /**
     * Initialize the game by starting all the variables and starting the timer
     * @param c controller
     */
    public void init(Controller c) {
        super.mode = Mode.COMPETITIVE;

        WordList.generateList();
        this.controller = c;
        this.currentList = WordList.startingList();
        this.currentPos = 0;
        this.correctCharacters = 0;
        this.typedCharacters = 0;
        this.gameRunning = true;
        this.initBlueWords();
        this.regularityList = new ArrayList<>();
        this.timer = new Timer();
        timerStart(true);
        this.startTime = System.nanoTime();
    }

    /**
     * Function that cancels the current timer
     */
    public void cancelTimer() {
        try{
            this.timer.cancel();
        }
        catch (IllegalStateException ex){}
    }

    /**
     * Handle the character typed by the player
     * @param k character number
     * @return boolean if the typed character was correct or not
     */
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
                this.updateList();
                return true;
            }
            this.lives--;
            this.gameRunning = (this.lives > 0);
            return false;
        }
        else {
            String word = this.currentList.get(0);
            if(word.length() == this.currentPos) {
                //Word done but did not receive space -> error by the player
                this.lives--;
                this.gameRunning = (this.lives > 0);
                return false;
            }
            else if (k == word.charAt(this.currentPos)) {
                //Character typed is the correct one
                this.currentPos++;
                this.correctCharacters++;
                if(this.previousCorrectCharTime == 0){
                    this.previousCorrectCharTime = System.nanoTime();
                }else{
                    this.regularityList.add((System.nanoTime() - this.previousCorrectCharTime));
                    this.previousCorrectCharTime = System.nanoTime();
                }
                return true;
            }else {
                //Character typed is the wrong one
                this.lives--;
                this.gameRunning = (this.lives > 0);
                return false;
            }
        }
    }

    /**
     * Method to level up when the score has increased by 100 since the last level
     * Update the timeBetweenWords and restart the timer
     */
    void levelUp() {
        this.level++;
        this.score = 0;
        this.timeBetweenWords = (int) (3 * Math.pow(0.9,level));
        try{
            this.timer.cancel();
        }
        catch (IllegalStateException ex){}
        this.timer = new Timer();
        timerStart(false);
    }

    /**
     * Start the timer which runs repeatedly after timeBetweenWords seconds
     * If this is the beginning of the game, delay by 2.5 seconds to give the player a chance to start easily
     * @param startOfGame beginning of the game or not
     */
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

    /**
     * Update the list when a word was correctly typed:
     * remove the word in position 0 and if the list is halfway or less full
     * add a new word, randomly decide if it's a blue word
     */
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

    /**
     * Validate current word when a new word has been added and the limit of
     * words has been reached
     */
    private void validateCurrentWord() {
        String word = this.currentList.get(0);

        if(word.length() == this.currentPos) {
            //The current word has been completed
            this.score++;
            if (score % 100 == 0) this.levelUp();
            if (this.blueWordsPos.size() > 0 && this.blueWordsPos.get(0) == 0) {
                this.lives+= word.length();
                this.blueWordsPos.remove(0);
            }
        }else{
            //The current word has not been completed
            this.lives--;
            if(this.lives == 0) this.gameRunning = false;
        }

        this.currentPos = 0;
        this.currentList.remove(0);
        this.blueWordsPos.replaceAll(integer -> integer - 1);
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

    public void initBlueWords(){
        this.blueWordsPos = new ArrayList<>();
        for(int i = 0; i < this.currentList.size(); i++){
            Random rand = new Random();
            int randEntry = rand.nextInt(101);
            if(randEntry < bonusRate){
                this.blueWordsPos.add(i);
            }
        }
    }

    /**
     * Stop the timer
     */
    @Override
    public void stop(){
        if(this.timer != null) {
            this.timer.cancel();
        }
    }
}