package services;

import objects.Word;
import objects.WordVector;

import java.util.ArrayList;

public class Distance {

    private static double dotProduct(WordVector a){
        double sum =0;
        for(Word w:a.getWordList()){
            sum+= (w.getTF_IDF() * w.getTF_IDF());
        }
        return sum;
    }

    private static double dotProduct(WordVector a, WordVector b){
        double sum=0;
        int i =0;
        while(i < a.getWordList().size()){
            Word aVector = a.getWordList().get(i);
            Word bVector = b.getWordList().get(i);
            sum+=(aVector.getTF_IDF() * bVector.getTF_IDF());
            i++;
        }
        return sum;

    }

    private static WordVector difference(WordVector a, WordVector b){
        WordVector diffVect = new WordVector();
        int i =0;
        while(i < a.getWordList().size()){
            Word w = new Word(a.getWordList().get(i).getWord());
            w.setTF_IDF(a.getWordList().get(i).getWordFreq()-b.getWordList().get(i).getTF_IDF());
            diffVect.getWordList().add(w);
            i++;
        }

        return diffVect;
    }

    public static double euclid(WordVector a, WordVector b){
        WordVector diffVect = difference(a,b);
        return Math.sqrt(dotProduct(diffVect));
    }

    public static double cosign(WordVector a, WordVector b){
        double num = dotProduct(a,b);
        double denom1 = Math.sqrt(dotProduct(a));
        double denom2 = Math.sqrt(dotProduct(b));
        return num/(denom1*denom2);
    }

    public static ArrayList<WordVector> calculateDistances(ArrayList<ArrayList<Word>> wordMatrix){
        ArrayList<Word> allWords = new ArrayList<>();
        for(ArrayList<Word> wordVector: wordMatrix) {
            allWords.addAll(wordVector);
        }

        ArrayList<WordVector> wordVectorList = new ArrayList<>();
        int index =1;
        System.out.println("Converting to matrix");
        for(ArrayList<Word> wordVector: wordMatrix){
            for(Word currentWord: allWords){
                if(!wordVector.contains(currentWord))
                    wordVector.add(new Word(currentWord.getWord()));
            }
            wordVector.sort(new Word.CompareWord());
            WordVector w = new WordVector(wordVector,index++);
            wordVectorList.add(w);
        }

        return wordVectorList;

    }
}
