package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import objects.Document;
import objects.ThreadReader;
import objects.Word;
import org.apache.log4j.BasicConfigurator;

public class FileManager {

    private static StanfordCoreNLP pipeline;
    private static Document[] allDocs;
    private static ArrayList<ArrayList<Word>> wordMatrix;

    public static void init() throws IOException{
        Set<String> stopwords = loadStopwords();
        BasicConfigurator.configure();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner"); //set annotations
        pipeline = new StanfordCoreNLP(props);
        wordMatrix = new ArrayList<>();

        final int DOCS = 24;
        allDocs = new Document[DOCS];
        ThreadReader[] threadReaders = new ThreadReader[3];
        for(int i =0; i < threadReaders.length; i++) {
            threadReaders[i] = new ThreadReader(i * 8, stopwords, wordMatrix);
            threadReaders[i].start();
        }

//        for(int i=1; i <= DOCS; i++){

//
//            File f = new File("src/main/resources/articles/article" + i +".txt");
//
//            System.out.println("Reading article " + i);
//
//            BufferedReader reader = new BufferedReader(new FileReader(f));
//
//            String line;
//            StringBuilder doc = new StringBuilder();
//            while ((line = reader.readLine()) != null) //read each document
//                doc.append(line.toLowerCase().replaceAll("\\W", " ")).append(" "); //remove special characters
//
//            String[] rawWords = doc.toString().split("\\s+");
//            doc = new StringBuilder();
//            for (String rawWord : rawWords)
//                if (!stopwords.contains(rawWord))
//                    doc.append(rawWord).append(" "); //remove stop words characters
//
//            ArrayList<Word> wordList = oneGram(doc.toString(),i);
//            wordList.addAll(twoGram(doc.toString(),i));
//            wordMatrix.add(wordList);
//
//        }

        try {
            for (ThreadReader t : threadReaders)
                t.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(wordMatrix.size() + " \n" + wordMatrix);
    }


    private static Set<String> loadStopwords() throws IOException {
        System.out.println(new File("").getAbsolutePath());
        BufferedReader reader = new BufferedReader(new FileReader(new File("src/main/resources/stopwords.txt")));
        HashSet<String> stopwords = new HashSet<>();
        String line;
        while ((line = reader.readLine()) != null)
            stopwords.add(line);

        return stopwords;
    }

    public synchronized static ArrayList<Word> twoGram(String text, int i){
        HashMap<String,Word> termFreq = new HashMap<>();

        int docLength = allDocs[i-1].getWords().size();
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document); //annotate words (stem, tokenize, etc)

        ArrayList<CoreLabel> labelList = new ArrayList<>(document.tokens());
        for(int j=1; j < labelList.size(); j++){
            if(labelList.get(j).ner().equals(labelList.get(j-1).ner())
                    && !labelList.get(j).ner().equals("O")){ // NER match with 2 elements
                String twoGram = labelList.get(j-1).lemma() + " " + labelList.get(j).lemma();
                if(termFreq.containsKey(twoGram))
                    termFreq.get(twoGram).incrementWordFreq();
                else
                    termFreq.put(twoGram,new Word(twoGram));
                allDocs[i-1].getWords().add(twoGram);
            }
        }

        for(Word w: termFreq.values())
            w.normalizeWordFreq(docLength);

        return new ArrayList<>(termFreq.values());
    }

    public synchronized static ArrayList<Word> oneGram(String text, int i){
        HashMap<String,Word> termFreq = new HashMap<>();
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document); //annotate words (stem, tokenize, etc)

        List<String> tempWordList = document.tokens().stream().map(CoreLabel::lemma).collect(Collectors.toList()); //add all the words to the doc for IDF
        allDocs[i-1] = new Document();
        allDocs[i-1].getWords().addAll(tempWordList);

        for(CoreLabel label : document.tokens()){
            if(termFreq.containsKey(label.lemma()))
                termFreq.get(label.lemma()).incrementWordFreq();
            else
                termFreq.put(label.lemma(),new Word(label.lemma()));
        }

        int docLength = tempWordList.size();
        for(Word w: termFreq.values())
            w.normalizeWordFreq(docLength);

        return new ArrayList<>(termFreq.values());
    }

    public static Document[] getAllDocs(){
        return allDocs;
    }

    public static ArrayList<ArrayList<Word>> getWordMatrix(){
        return wordMatrix;
    }


}
