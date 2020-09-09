package services;

import objects.WordVector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DimentionalityReduction {

    static ArrayList<WordVector> lowVarianceFilter(ArrayList<WordVector> wordList){
        System.out.println("Reducing dimensions using Low Variance Filter");
        int numDimensions = wordList.get(0).size();
        ArrayList<Double> variances = new ArrayList<>();
        for(int i =0; i < numDimensions; i++){
            ArrayList<Double> values = new ArrayList<>();
            for (WordVector wordVector : wordList)
                values.add(wordVector.getWordList().get(i).getTF_IDF());
            variances.add(var(values));
        }

        double thresh = mean(variances);
        ArrayList<WordVector> newList = new ArrayList<>();
        for(int i = 0; i < wordList.size(); i++)
            newList.add(new WordVector(i));

        for(int i =0; i < numDimensions; i++){
            if(variances.get(i) >=thresh) { //important dimension
                for(int j =0; j < wordList.size(); j++)
                    newList.get(j).getWordList().add(wordList.get(j).getWordList().get(i));

            }
        }

        return newList;
    }

    static ArrayList<WordVector> highCorrelationFilter(ArrayList<WordVector> wordList){
        System.out.println("Reducing Dimensions using High Correlation Filter");
        ArrayList<ArrayList<Double>> corrs = covarianceMatrix(wordList);
        int numVectors = wordList.size();
        int numDimensions = wordList.get(0).size();

        for(int currentDimension = 0; currentDimension < numDimensions; currentDimension++){
            if(corrs.get(currentDimension) == null)
                continue;

            for(int otherDimension = 0; otherDimension < numDimensions; otherDimension++){
                if(currentDimension!=otherDimension &&
                    Math.abs(corrs.get(currentDimension).get(otherDimension)) > .75) {//non equal dimensions have high correlation
                    corrs.set(otherDimension,null);
                }
            }
        }

        ArrayList<WordVector> filteredList = new ArrayList<>();
        for(int i =0; i < numVectors; i++)
            filteredList.add(new WordVector(i));

        for(int currentDimension = 0; currentDimension < numDimensions; currentDimension++){
            if(corrs.get(currentDimension) == null)
                continue;
            for(int currentVector = 0; currentVector < numVectors; currentVector++)
                filteredList.get(currentVector).getWordList().add(wordList.get(currentVector).getWordList().get(currentDimension));
        }


        return filteredList;
    }


    private static ArrayList<ArrayList<Double>> covarianceMatrix(ArrayList<WordVector> wordList){
        int numDimensions = wordList.get(0).size();
        ArrayList<ArrayList<Double>> correlations =  new ArrayList<>();

        for(int i =0; i < numDimensions; i++){
            ArrayList<Double> currentCorrelations = new ArrayList<>();
            final int currentDimension = i;
            List<Double> currentVals = wordList.stream().map(ele->ele.getWordList().get(currentDimension).getTF_IDF()).collect(Collectors.toList());
            double currentSigma = Math.sqrt(var(currentVals));

            for(int j=0; j < numDimensions; j++){
                final int otherDimension = j;
                List<Double> otherVals = wordList.stream().map(ele->ele.getWordList().get(otherDimension).getTF_IDF()).collect(Collectors.toList());
                double otherSigma = Math.sqrt(var(otherVals));
                double corr = coVar(currentVals,otherVals);
                corr = corr/(currentSigma*otherSigma);
                currentCorrelations.add(corr);
            }
            correlations.add(currentCorrelations);
        }

        return correlations;
    }

    private static double mean(List<Double> arr){
        double sum =0;
        for(double val: arr)
            sum+=val;
        return sum/arr.size();
    }

    private static double var(List<Double> arr){
        double mean = mean(arr);
        double sum=0;
        for(double val: arr)
            sum+= Math.pow(mean-val,2);

        return sum;
    }

    private static double coVar(List<Double> a, List<Double> b){
        double aBar = mean(a);
        double bBar = mean(b);
        double sum=0;

        for(int i = 0; i < a.size(); i++)
            sum+= (a.get(i)-aBar)*(b.get(i)-bBar);

        return sum;
    }
}
