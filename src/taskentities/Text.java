package taskentities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Dmitry Chueshov
 * @project TaskTwo
 */

public class Text {

    final static EnumSet<Punctuation> sentencePunctuation = EnumSet.range(Punctuation.EXCLAMATION, Punctuation.QUESTION);
    final static EnumSet<Punctuation> nonSentencePunctuation = EnumSet.complementOf(sentencePunctuation);  //Created EnumSet with all param of sent.Punctuation
    final static EnumSet<Punctuation> allPunctuation = EnumSet.allOf(Punctuation.class);

    /** String param for constructor Text */
    private String fileAddress;

    /** all text*/
    private String wholeText;

    /** collection sentences*/
    private List<Sentence> sentences;

    /** set of words*/
    private Set<Word> words;

    public Text(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    public String getWholeText() {
        return wholeText;
    }


    public void createtext() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileAddress))){
            StringBuilder wholeTextBuilder = new StringBuilder();
            while (reader.ready()) {
                String buffer = reader.readLine();
                buffer = buffer.replaceAll("\\s+", " ");
                wholeTextBuilder.append(buffer);                                        //row update
                wholeTextBuilder.append('\r');
            }
            wholeText = wholeTextBuilder.toString();
        }catch (Exception e){
            throw e;
        }
    }

    public List<Sentence> getSentences(){
        if (sentences != null) return sentences;
        else {
            sentences = new ArrayList<>();
            if (wholeText != null && wholeText.length() > 0) {
                int startPosition = 0;
                int index;
                for (index = startPosition; index < wholeText.length(); index++) {
                    for (Punctuation punctuation : Text.sentencePunctuation) {
                        if (wholeText.charAt(index) == punctuation.getSign()) {
                            if (startPosition < index) {
                                Sentence buffer = new Sentence(getWholeText().substring(startPosition, index));
                                sentences.add(buffer);
                            }
                            startPosition = index + 1;
                        }
                    }
                }
                if (startPosition < index) {
                    Sentence buffer = new Sentence(getWholeText().substring(startPosition, index));
                    sentences.add(buffer);
                }
            }
            return sentences;
        }
    }

    public Set<Word> getWords(){
        if (words != null ) return words;
        else {
            words = new HashSet<>();
            if (sentences == null) {
                sentences = getSentences();
            }
            if (sentences.size() >0) {
                for (Sentence sentence : sentences){
                    words.addAll(sentence.getWords());
                }
            }
            return words;
        }
    }
}
