package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Map<Integer, Integer> indexMap = new HashMap<>() {{
        put(1, 6);
        put(2, 7);
        put(3, 9);
        put(4, 10);
        put(5, 11);
        put(6, 12);
        put(7, 13);
        put(8, 14);
        put(9, 15);
        put(10, 16);
        put(11, 17);
        put(12, 18);
        put(13, 24);
        put(14, 24);
    }};

    public static void main(String[] args) throws IOException {
        String text = Files.readString(Paths.get(args[0]));
        Details details = getDetails(text);
        Double scoreARI = null;
        Double scoreFK = null;
        Double scoreSMOG = null;
        Double scoreCL = null;

        System.out.println("The text is:");
        System.out.println(text);
        System.out.println();
        System.out.println("Words: " + details.getWords());
        System.out.println("Sentences: " + details.getSentences());
        System.out.println("Characters: " + details.getCharacters());
        System.out.println("Syllables: " + details.getSyllables());
        System.out.println("Polysyllables: " + details.getPolysyllables());

        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        Scanner scanner = new Scanner(System.in);
        String scoreType = scanner.next();
        System.out.println();
        int oldnessSum = 0;
        int count = 1;
        switch (scoreType) {
            case "all":
                scoreARI = getScoreARI(details);
                oldnessSum += printMessageAndReturnOldness("ARI", scoreARI);

                scoreFK = getScoreFK(details);
                oldnessSum += printMessageAndReturnOldness("FK", scoreFK);

                scoreSMOG = getScoreSMOG(details);
                oldnessSum += printMessageAndReturnOldness("SMOG", scoreSMOG);

                scoreCL = getScoreCL(details);
                oldnessSum += printMessageAndReturnOldness("CL", scoreCL);
                count = 4;
                break;
            case "ARI":
                scoreARI = getScoreARI(details);
                oldnessSum = printMessageAndReturnOldness(scoreType, scoreARI);
                break;
            case "FK":
                scoreFK = getScoreFK(details);
                oldnessSum = printMessageAndReturnOldness(scoreType, scoreFK);
                break;
            case "SMOG":
                scoreSMOG = getScoreSMOG(details);
                oldnessSum = printMessageAndReturnOldness(scoreType, scoreSMOG);
        }
        System.out.println();
        System.out.println("This text should be understood in average by " + (double) oldnessSum / count + " year olds");
    }

    private static int printMessageAndReturnOldness(String scoreType, double score) {
        String message = null;
        int oldness = score >= 14 ? 24 : indexMap.get((int) Math.round(score));
        switch (scoreType) {
            case "ARI":
                message = "Automated Readability Index: %.2f (about %d year olds).";
                break;
            case "FK":
                message = "Flesch–Kincaid readability tests: %.2f (about %d year olds).";
                break;
            case "SMOG":
                message = "Simple Measure of Gobbledygook: %.2f (about %d year olds).";
                break;
            case "CL":
                message = "Coleman–Liau index: %.2f (about %d year olds).";
        }
        if (message != null) {
            System.out.printf(message, score, oldness);
            System.out.println();
        }

        return oldness;
    }

    private static double getScoreARI(Details details) {
        return 4.71 * ((double) details.getCharacters() / details.getWords()) + 0.5 * ((double) details.getWords() / details.getSentences()) - 21.43;
    }

    private static double getScoreFK(Details details) {
        return 0.39 * ((double) details.getWords() / details.getSentences()) + 11.8 * ((double) details.getSyllables() / details.getWords()) - 15.59;
    }

    private static double getScoreSMOG(Details details) {
        return 1.043 * Math.sqrt(details.getPolysyllables() * (double) 30 / details.getSentences()) + 3.1291;
    }

    private static double getScoreCL(Details details) {
        return 0.0588 * details.getL() - 0.296 * details.getS() - 15.8;
    }

    private static Details getDetails(String text) {
        String[] words = text.split(" |\n|\t");
        int wordsCount = words.length;
        int sentencesCount = text.split("\\.|\\!|\\?").length;
        int charactersCount = text.replaceAll(" |\n|\t", "").split("").length;
        int syllablesCount = 0;
        int polysyllablesCount = 0;
        for (String word : words) {
            word = word.toLowerCase();
            word = word.replaceAll("-|\\.|!|,|\\?", "");
            word = word.replaceAll("e\\b", "");
            word = word.replaceAll("[aeiouy]{2,}", "a");
            word = word.replaceAll("[^aeiouy]+", "");
            int length = Math.max(word.length(), 1);
            syllablesCount += length;
            if (length > 2) {
                polysyllablesCount++;
            }
        }

        return new Details(
                wordsCount,
                sentencesCount,
                charactersCount,
                syllablesCount,
                polysyllablesCount,
                ((double) charactersCount / wordsCount) * 100.0,
                ((double) sentencesCount / wordsCount) * 100.0
        );
    }

    private static double getAvg(Double... scores) {
        return Arrays.stream(scores).reduce(0.0, Double::sum) / scores.length;
    }

    private static class Details {
        private int words;
        private int sentences;
        private int characters;
        private int syllables;
        private int polysyllables;
        private double l;
        private double s;

        public Details(int words, int sentences, int characters, int syllables, int polysyllables, double l, double s) {
            this.words = words;
            this.sentences = sentences;
            this.characters = characters;
            this.syllables = syllables;
            this.polysyllables = polysyllables;
            this.l = l;
            this.s = s;
        }

        public int getWords() {
            return words;
        }

        public int getSentences() {
            return sentences;
        }

        public int getCharacters() {
            return characters;
        }

        public int getSyllables() {
            return syllables;
        }

        public int getPolysyllables() {
            return polysyllables;
        }

        public double getL() {
            return l;
        }

        public double getS() {
            return s;
        }
    }
}