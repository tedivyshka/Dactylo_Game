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
    private boolean gameRunning;
    private List<Integer> blueWordsPos; //Les positions des mots bleu qui ajoutent des vies
    private static int bonusRate = 20; // % possibilite d'avoir un mot bonus
    private static int maxWordsInList = 16;
    private Timer timer;
    private Controller controller;


    public void init(Controller c) {
        WordList.generateList();
        this.controller = c;
        this.currentList = WordList.startingList();
        this.currentPos = 0;
        this.lives = 20;
        this.level = 1;
        this.timeBetweenWords = 3;
        this.gameRunning = true;
        this.blueWordsPos = new ArrayList<Integer>();
        this.timer = new Timer();
        timerStart();
    }

    @Override
    public void cancelTimer() {
        this.timer.cancel();
    }

    @Override
    public List<Integer> getBlueWordsPos() {
        return this.blueWordsPos;
    }


    public boolean keyInput(int k) {
        if(k == ' ') {
            String word = this.currentList.get(0);
            if(word.length() == this.currentPos) {
                //Word done -> move to next one
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
                this.lives++;
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
