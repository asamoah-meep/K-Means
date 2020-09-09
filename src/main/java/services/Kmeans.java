package services;

import objects.Parameters;
import objects.Word;
import objects.WordVector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Kmeans {

    Cluster[] clusters;
    private final ArrayList<WordVector> wordMatrix;
    int k;

    Kmeans(ArrayList<WordVector> wordMatrix, Parameters params) throws IOException {
        System.out.println("Creating Clusters");
        this.wordMatrix = wordMatrix;

        String part = params.getCreation();
        boolean useEuclid = params.getDistance().equals("euclid");

        this.k = params.getCount();
        if(part.equals("random")){
            this.clusters = new Cluster[this.k];
            ArrayList<WordVector> copyMatrix = new ArrayList<>(wordMatrix);
            for (int i = 0; i < this.k; i++) {
                int randIndex = (int) Math.floor(Math.random() * copyMatrix.size());
                clusters[i] = new Cluster(wordMatrix.get(randIndex));
                copyMatrix.remove(randIndex);
            }
        }else if(part.equals("optimized")){
            this.clusters = new Cluster[this.k];
            KMeansEnhanced(useEuclid);
        }

        findNearestNeighbors(useEuclid);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("output/Clusters.txt")));
        for(Cluster c: this.clusters) {
            String subject;
            if(c.nearestWords.get(0).getIndex()<8)
                subject = "Airline Safety";
            else if(c.nearestWords.get(0).getIndex() < 16)
                subject = "Mouth Disease";
            else
                subject = "Japan Mortgage Rates";
            writer.write(subject + ":" + c.toString());
        }
        writer.close();
    }


    private void KMeansEnhanced(boolean d){ //if d, euclidian distance: else, cosign similarity
        ArrayList<WordVector> copyMatrix = new ArrayList<>(this.wordMatrix);
        clusters[0] = new Cluster((copyMatrix.get(5))); // can pick any initial value, 5 works well for the 1st cluster
        copyMatrix.remove(0   );

        for(int i =1; i < this.k; i++){
            double[][] distances = new double[copyMatrix.size()][i];
            for(int currentMeanIndex =0; currentMeanIndex < i; currentMeanIndex++) {
                WordVector m = this.clusters[currentMeanIndex].mean;
                for (int currentWordIndex = 0; currentWordIndex < copyMatrix.size(); currentWordIndex++) {
                    if (d) {
                        double dist = 1/(Distance.euclid(m,copyMatrix.get(currentWordIndex)));
                        distances[currentWordIndex][currentMeanIndex] = dist*dist;

                    }
                    else {
                        double dist = Distance.cosign(m,copyMatrix.get(currentWordIndex));
                        double recip;
                        if(dist==0)
                            recip = 100000; // so that distance doesn't blow up to infinity
                        else
                            recip = 1/dist;
                        distances[currentWordIndex][currentMeanIndex] = recip*recip;
                    }
                }
            }

            double[] cmf = d?
                    maxDistances(distances) : minDistances(distances);

            double randVal = Math.random();
            for(int j =0; j < cmf.length; j++){
                if(randVal < cmf[j]) {
                    clusters[i] = new Cluster(copyMatrix.get(j));
                    copyMatrix.remove(j);
                    break;
                }
            }

        }
    }
    private static double[] maxDistances(double[][] arr){
        double[] sol = new double[arr.length];
        for(int i =0; i < arr.length; i++){
            double currentMax = Double.NEGATIVE_INFINITY;
            for(int j=0; j < arr[i].length; j++){
                if(arr[i][j] > currentMax)
                    currentMax = arr[i][j];
            }
            sol[i] = currentMax;
        }

        return normalize(sol);
    }

    private static double[] minDistances(double[][]arr){
        double[] sol = new double[arr.length];
        for(int i =0; i < arr.length; i++){
            double currentMin = Double.POSITIVE_INFINITY;
            for(int j=0; j < arr[i].length; j++){
                if(arr[i][j] < currentMin)
                    currentMin = arr[i][j];
            }
            sol[i] = currentMin;
        }

        return normalize(sol);
    }


    private void findNearestNeighbors(boolean d){
        Cluster[] tempClusters = new Cluster[this.k];
        int numVectors = wordMatrix.size();
        double[][] distances = new double[this.k][numVectors];
        do {
            for(int i =0; i < this.k; i++){ //iterate through mean of each cluster
                WordVector m = this.clusters[i].mean;
                tempClusters[i] = new Cluster(this.clusters[i]); // make shallow copies
                this.clusters[i].nearestWords = new ArrayList<>();
                for(int j =0; j < wordMatrix.size(); j++){ //store distances from mean to each cluster
                    distances[i][j] = d?
                            Distance.euclid(m,wordMatrix.get(j)):
                            Distance.cosign(m,wordMatrix.get(j));
                }
            }

            for(int i = 0; i < numVectors; i++){
                int index = d?
                        minDistance(distances,i):
                        maxDistance(distances,i);

                this.clusters[index].nearestWords.add(wordMatrix.get(i)); //assign smallest distances
            }
            for (Cluster cluster : this.clusters)
                cluster.updateMean();


        }while(!this.clustersMatch(tempClusters));
    }

    private static double[] normalize(double[] sol){
        double sum =0;
        for(double val: sol)
            sum+=val;
        for(int i=0; i < sol.length; i++)
            sol[i]/=sum;

        for(int i =1; i < sol.length; i++)
            sol[i]+=sol[i-1];

        return sol;
    }

    private static int minDistance(double[][] arr, int j){
        double minVal = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for(int i =0; i < arr.length; i++){
            if(arr[i][j] < minVal){
                minIndex = i;
                minVal = arr[i][j];
            }
        }

        return minIndex;
    }

    private static int maxDistance(double[][] arr, int j){
        double maxVal = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;
        for(int i =0; i < arr.length; i++){
            if(arr[i][j] > maxVal){
                maxIndex = i;
                maxVal = arr[i][j];
            }
        }

        return maxIndex;
    }

    private boolean clustersMatch(Cluster[] oldClusters){
        for(int i =0; i < oldClusters.length; i++){
            if(!(this.clusters[i].equals(oldClusters[i])))
                return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return Arrays.toString(clusters);
    }

    static class Cluster implements Comparable<Cluster>{
        WordVector mean;
        ArrayList<WordVector> nearestWords;

        Cluster(WordVector mean){
            this.mean = mean;
            this.nearestWords = new ArrayList<>();
        }

        Cluster(Cluster copy){
            this.mean = new WordVector();
            this.mean.setIndex(copy.mean.getIndex());
            this.mean.getWordList().addAll(copy.mean.getWordList());

            this.nearestWords = new ArrayList<>();
            this.nearestWords.addAll(copy.nearestWords);

        }

        private int minIndex(){
            int minVal = Integer.MAX_VALUE;
            for (WordVector nearestWord : this.nearestWords)
                if (nearestWord.getIndex() < minVal)
                    minVal = nearestWord.getIndex();

            return minVal;
        }

        @Override
        public int compareTo(Cluster t){
            int currentMin = this.minIndex();
            int otherMin = t.minIndex();
            return Integer.compare(currentMin,otherMin);
        }
        private void updateMean(){
            WordVector meanVector = new WordVector();
            int numWords = this.mean.size();
            for(int i =0; i < numWords; i++){
                Word w = new Word(this.mean.getWordList().get(i).getWord());
                double tempTF_IDF = w.getTF_IDF();
                for (WordVector nearestWord : nearestWords)
                    tempTF_IDF += nearestWord.getWordList().get(i).getTF_IDF();
                w.setTF_IDF(tempTF_IDF/nearestWords.size());
                meanVector.getWordList().add(w);
            }
            this.mean = meanVector;

        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Cluster))
                return false;
            Cluster other = (Cluster) o;
            if(this.nearestWords.size()!=other.nearestWords.size())
                return false;
            for(WordVector wordVector: this.nearestWords){
                if(!other.nearestWords.contains(wordVector))
                    return false;
            }

            return true;
        }

        @Override
        public String toString(){
            return String.format("Cluster:%s\n",nearestWords);
        }

    }
}
