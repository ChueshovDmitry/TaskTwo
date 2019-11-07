import taskentities.Sentence;
import taskentities.Text;
import taskentities.Word;
import java.io.IOException;
import java.util.*;

/**
 * @author Professional 31.10.2019
 * @project TaskTwo
 *
 * Создать программу обработки текста учебника по программированию
 * с использованием классов: Символ, Слово, Предложение, Знак препинания и др.
 * Найти наибольшее количество предложений текста,
 * в которых есть одинаковые слова.
 *
 * Заменена табуляция и последовательность пробелов одним пробелом.
 *  последовательность решения : поиск слова,
 * которое встречается максимальное число раз,
 *  достаём множество предложений,
 * проверяем все слова из собранных предложений,
 *  кроме максимального,
 *  проверяем все предложения, кроме уже собранных,
 * проверяем каждое предложение, можно ли его добавить во множество,
 *  чтобы не нарушить правило.
 *
 *  Вводим понятие ранга предложения = к-во уникальных предложений,
 *  в которых встречаются его слова. Для получения такого списка -
 *  карта слов на множество предложений.
 *  Из карты получаем счётчик уникальных предложений.
 */

public class Runner {
    public static void main(String[] args) {

        Text currentText = new Text("C:\\comptext.txt");

        try{
            currentText.createtext();
        }catch (IOException e) {
            System.out.println(e);
        }

        List<Sentence> sentenceList = currentText.getSentences();

        System.out.println(String.format("Number of sentences in a text (количество предлодений в тексте): %d", sentenceList.size()));

        Map<Word, Set<Sentence>> wordRange = calculateWordRange(sentenceList);

        Word wordWithMaxRange = getWordWithMaxRange(wordRange);

        System.out.println(String.format("Word with maximum range(слово с максимальным рангом,повторением): %s",wordWithMaxRange ));


        Set<Sentence> resultSentenceSet = new HashSet<>();
        Set<Sentence> sentenceSetToCheck = new HashSet<>();


        //  get sentences with word of Max Range, and make set of other sentences to check
        //  получаем предложения со словом Max Range и составляем множество других предложений для проверки
        for (Sentence sentence : wordRange.get(wordWithMaxRange)){
            resultSentenceSet.add(sentence);
            for (Word word : sentence.getWords()){
                sentenceSetToCheck.addAll(wordRange.get(word));
            }
        }

        sentenceSetToCheck.removeAll(resultSentenceSet);

        checkPotentialSentences(wordRange, resultSentenceSet, sentenceSetToCheck);


        System.out.println(String.format("Number of sentences with at least one similar word(Количество предложений хотя бы с одним похожим словом): %d", resultSentenceSet.size()));

        for (Sentence sentence : resultSentenceSet){
            System.out.println(sentence);
    }
    }

    // check other sentences if they contain necessary words
    // проверяем другие предложения, если они содержат необходимые слова

    private static void checkPotentialSentences(Map<Word, Set<Sentence>> wordRange, Set<Sentence> resultSentenceSet, Set<Sentence> sentenceSetToCheck) {
        for (Sentence sentenceToCheck : sentenceSetToCheck){
            boolean isAllOtherContainSimilarWord = true;
            for (Sentence resultSentence : resultSentenceSet){
                if (!sentenceToCheck.hasSimilarWords(resultSentence, wordRange)) {
                    isAllOtherContainSimilarWord = false;
                    break;
                }

            }
            if (isAllOtherContainSimilarWord) resultSentenceSet.add(sentenceToCheck);
        }
    }


    private static Word getWordWithMaxRange(Map<Word, Set<Sentence>> wordRange) {
        Word wordWithMaxRange = null;
        int maxRange = 0;

        for (Word word : wordRange.keySet()){
            if (wordWithMaxRange == null) {
                wordWithMaxRange = word;
                maxRange = wordRange.get(word).size();
            } else {
                if (maxRange < wordRange.get(word).size()){
                    wordWithMaxRange = word;
                    maxRange = wordRange.get(word).size();
                } else {
                    if (maxRange == wordRange.get(word).size()){
                        int countPotentialMaxWordSentence = getMaxSentenceLength(wordRange, word);;
                        int countCurrentMaxWordSentence = getMaxSentenceLength(wordRange, wordWithMaxRange);
                        if (countCurrentMaxWordSentence < countPotentialMaxWordSentence){
                            wordWithMaxRange = word;
                        }
                    }
                }
            }
        }
        return wordWithMaxRange;
    }

    private static int getMaxSentenceLength(Map<Word, Set<Sentence>> wordRange, Word wordWithMaxRange) {
        int countCurrentMaxWordSentence = 0;
        for (Sentence sentence : wordRange.get(wordWithMaxRange)){
            if (countCurrentMaxWordSentence < sentence.getWords().size())
                countCurrentMaxWordSentence = sentence.getWords().size();
        }
        return countCurrentMaxWordSentence;
    }

    private static Map<Word, Set<Sentence>> calculateWordRange(List<Sentence> sentenceList) {
        Map<Word, Set<Sentence>> wordRange = new HashMap<>();

        for (Sentence sentence: sentenceList) {
            Set<Word> wordSet = sentence.getWords();
            for (Word word: wordSet){
                if (wordRange.containsKey(word)){
                    wordRange.get(word).add(sentence);
                } else {
                    Set<Sentence> sentenceOfWord= new HashSet<>();
                    sentenceOfWord.add(sentence);
                    wordRange.put(word, sentenceOfWord);
                }
            }
        }
        return wordRange;
    }
}