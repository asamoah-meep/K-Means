package services;

import objects.Document;
import objects.Parameters;
import objects.Word;
import objects.WordVector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class WordAnalysis {

    public static void init(ArrayList<ArrayList<Word>> wordMatrix, Document[] allDocs, Parameters params) throws IOException {
        System.out.println("Calculating tf-idf");
        for(ArrayList<Word> wordVector: wordMatrix)
            for(Word w: wordVector)
                w.setTF_IDF(w.getWordFreq() * calculateIDF(w.getWord(), allDocs));

        ArrayList<WordVector> wordVectorList = Distance.calculateDistances(wordMatrix);

        System.out.println(wordVectorList);

        wordVectorList = DimentionalityReduction.lowVarianceFilter(wordVectorList);
        wordVectorList = DimentionalityReduction.highCorrelationFilter(wordVectorList);

        System.out.println(params);
        PCAReduction pca = new PCAReduction(wordVectorList);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("output/Coordinates.txt")));
        Kmeans kmeans = new Kmeans(wordVectorList, params);

       double[][] xy = params.getPCA().equals("SVD")?
               pca.SVD() : pca.eigenvalueDecomp();

        for(double[]v: xy)
            writer.write(String.format("%.2f,%.2f\n",v[0],v[1]));

        writer.close();
        System.out.println(kmeans.toString());
//        plotPoints(kmeans,xy);
        System.out.println("Done");
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
