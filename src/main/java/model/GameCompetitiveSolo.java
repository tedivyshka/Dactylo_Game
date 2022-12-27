package model;

import controller.Controller;

import java.util.*;

public class GameCompetitiveSolo extends Game{
    private List<String> currentList;
    private int currentPos;
    private int lives;
    private int score;
    private int level;
    private int timeBetweenWords; //Ajouter un mot après chaque timeBetweenWords en secondes

    private static int charactersForWord = 5;
    private int correctCharacters;
    private int typedCharacters;
    private boolean gameRunning;
    private long startTime;
    private long regularitySum;
    private long previousCorrectCharTime;
    private List<Integer> blueWordsPos; //Les positions des mots bleu qui ajoutent des vies
    private static final int maxWordsInList = 16;
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
        timerStart();
        this.startTime = System.nanoTime();
    }

    public void cancelTimer() {
        this.timer.cancel();
    }

    public List<Integer> getBlueWordsPos() {
        return this.blueWordsPos;
    }

    public int getLives() {
        return this.lives;
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
                if(score == 100) this.levelUp();
                if(this.blueWordsPos.size() > 0 && this.blueWordsPos.get(0) == 0){
                    this.lives++;
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
                if(this.lives == 0) this.gameRunning = false;
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

    private void levelUp() {
        this.level++;
        this.score = 0;
        this.timeBetweenWords = (int) (3 * Math.pow(0.9,level));
        timer.cancel();
        timerStart();
    }

    private void timerStart() {
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                System.out.println("Adding new word\n");
                WordList.addWord(currentList);
                if(currentList.size() > maxWordsInList) validateCurrentWord();
                controller.update();
            }
        };
        timer.schedule(task,0,this.timeBetweenWords * 1000);
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
        }
        System.out.print(this.lives + "\n");
        this.currentPos = 0;
        this.currentList.remove(0);
        for(int i = 0; i < this.blueWordsPos.size() ; i++) this.blueWordsPos.set(i, this.blueWordsPos.get(i) - 1);
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
        boolean addNew = this.currentList.size() < 8;
        WordList.update(this.currentList,addNew);
        for(int i = 0; i < this.blueWordsPos.size() ; i++) this.blueWordsPos.set(i, this.blueWordsPos.get(i) - 1);
        Random rand = new Random();
        int randEntry = rand.nextInt(101);
        // % possibilite d'avoir un mot bonus
        int bonusRate = 20;
        if(randEntry < bonusRate){
            this.blueWordsPos.add(this.currentList.size() - 1);
            System.out.println("Added blue word, current blue word count: \n" + this.blueWordsPos.size());
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
    public double getPrecision(){
        double result = ( (float) this.correctCharacters / (float) this.typedCharacters ) * 100;
        result = Math.round(result * 10);
        return result / 10;
    }

    @Override
    public double getSpeed(){
        long timeToFinishMillisecond = (System.nanoTime() - this.startTime) / 1000000;
        double timeToFinish = ((double) timeToFinishMillisecond) / 1000;
        double timeInMinutes = timeToFinish / 60;
        double result = this.correctCharacters / (timeInMinutes * charactersForWord) ;
        result = result * 1000;
        long tmp = Math.round(result);
        return (double) tmp / 1000;
    }

    @Override
    public double getRegularity(){
        double result = (double) this.regularitySum / (double) (1000000 * (this.correctCharacters-1)) ;
        result = result * 1000;
        long tmp = Math.round(result);
        return (double) tmp / 1000;
    }
}
