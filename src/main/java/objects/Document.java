package objects;

import java.util.ArrayList;

public class Document {

    public ArrayList<String> getWords() {
        return words;
    }

    private final ArrayList<String> words;
    public Document(){
        words = new ArrayList<>();
    }

    @Override
    public String toString(){
        return "!!!" + words.toString();
    }

}
