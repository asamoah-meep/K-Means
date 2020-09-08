package objects;

import java.util.Comparator;

public class Word {

    private double wordFreq;
    private double tf_idf;
    private final String word;

    public void incrementWordFreq(){
        this.wordFreq++;
    }

    public void normalizeWordFreq(int docLength){
        this.wordFreq= (this.wordFreq*1000)/docLength;
    }

    public Word(String word) {
        this.word = word;
        this.wordFreq = 1;
        this.tf_idf = 0;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Word))
            return false;
        return ((Word) o).word.equals(this.word);
    }
    @Override
    public String toString(){
        return String.format("%s(%.4f)",this.word,this.tf_idf);
    }

    public static class CompareWord implements Comparator <Word> {

        @Override
        public int compare(Word w1, Word w2) {
            return w1.word.compareTo(w2.word);
        }
    }

    public String getWord(){
        return word;
    }

    public double getWordFreq(){
        return wordFreq;
    }

    public double getTF_IDF(){
        return tf_idf;
    }
    public void setTF_IDF(double tf_idf){
        this.tf_idf = tf_idf;
    }
}
