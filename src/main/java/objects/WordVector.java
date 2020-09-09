package objects;

import java.util.ArrayList;

public class WordVector {

    private int index;
    private final ArrayList<Word> wordList;

    public WordVector(ArrayList<Word> wordList, int index){
        this.index = index;
        this.wordList = wordList;
    }

    public WordVector(){
        this.index = -1;
        this.wordList = new ArrayList<>();
    }

    public WordVector(int index){
        this.index = index;
        this.wordList = new ArrayList<>();
    }

    public int size(){
        return this.wordList.size();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof WordVector))
            return false;
        WordVector other = (WordVector) o;
        return this.index == other.index;
    }

    @Override
    public String toString(){ return ""+this.index; }

    public ArrayList<Word> getWordList(){
        return wordList;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
