package services;

import objects.Document;
import objects.Word;
import objects.WordVector;

import java.util.ArrayList;

public class WordAnalysis {

    public static void init(ArrayList<ArrayList<Word>> wordMatrix, Document[] allDocs){
        System.out.println("Calculating tf-idf");
        for(ArrayList<Word> wordVector: wordMatrix)
            for(Word w: wordVector)
                w.setTF_IDF(w.getWordFreq() * calculateIDF(w.getWord(), allDocs));

        ArrayList<WordVector> wordVectors = Distance.calculateDistances(wordMatrix);

        System.out.println(wordVectors);
    }

    private static double calculateIDF(String word, Document[] docs){
        int docFreq = 0;
        for (Document currentDoc : docs) {
            if (currentDoc.getWords().contains(word))
                docFreq++;
        }
        return Math.log10(docs.length / (double) docFreq);

    }
}
