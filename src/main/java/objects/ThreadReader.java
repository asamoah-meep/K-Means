package objects;

import services.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class ThreadReader extends Thread{

    private final int fileIndex;
    private int offset;
    private final Set<String> stopwords;
    private final ArrayList<ArrayList<Word>> wordMatrix;

    private File f;
    public ThreadReader(int index, Set<String> stopwords, ArrayList<ArrayList<Word>> wordMatrix){
        this.fileIndex = index;
        this.stopwords = stopwords;
        this.wordMatrix = wordMatrix;
        System.out.println(index);
    }

    public void setFileIndex(int offset){
        this.offset = fileIndex + offset;
        this.f = new File("src/main/resources/articles/article" + (this.offset) +".txt");
    }

    @Override
    public void run(){

        for(int i =1; i<=8; i++){
            System.out.println("T= " + (fileIndex +i) );
            setFileIndex(i);
            readFile();
        }
    }

    private void readFile(){
        String line;
        StringBuilder doc = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            while ((line = reader.readLine()) != null) //read each document
                doc.append(line.toLowerCase().replaceAll("\\W", " ")).append(" "); //remove special characters
        }catch (IOException e){
            e.printStackTrace();
        }

        String[] rawWords = doc.toString().split("\\s+");
        doc = new StringBuilder();
        for (String rawWord : rawWords)
            if (!stopwords.contains(rawWord))
                doc.append(rawWord).append(" "); //remove stop words characters

        ArrayList<Word> wordList = FileManager.oneGram(doc.toString(), offset);
        wordList.addAll(FileManager.twoGram(doc.toString(), offset));
        wordMatrix.add(wordList);
    }


}
