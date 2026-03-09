import java.util.*;

public class Problem4 {

    private int N = 5; // size of n-gram

    // ngram -> set of documents containing it
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    // document -> list of its ngrams
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    // Add a document to the system
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> grams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }

            grams.add(sb.toString().trim());
        }

        return grams;
    }

    // Analyze a document for plagiarism
    public void analyzeDocument(String docId, String text) {

        List<String> grams = generateNgrams(text);

        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String gram : grams) {

            if (ngramIndex.containsKey(gram)) {

                for (String doc : ngramIndex.get(gram)) {

                    matchCounts.put(doc,
                            matchCounts.getOrDefault(doc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + grams.size() + " n-grams");

        for (String doc : matchCounts.keySet()) {

            int matches = matchCounts.get(doc);

            double similarity = (matches * 100.0) / grams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + doc + "\"");

            System.out.printf("Similarity: %.1f%% ", similarity);

            if (similarity > 60)
                System.out.println("(PLAGIARISM DETECTED)");
            else if (similarity > 15)
                System.out.println("(Suspicious)");
            else
                System.out.println();
        }
    }

    public static void main(String[] args) {

        Problem4 detector = new Problem4();

        detector.addDocument("essay_089.txt",
                "artificial intelligence is transforming the world of technology and innovation");

        detector.addDocument("essay_092.txt",
                "artificial intelligence is transforming the world of technology and innovation rapidly");

        String newEssay =
                "artificial intelligence is transforming the world of technology and innovation in modern society";

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}