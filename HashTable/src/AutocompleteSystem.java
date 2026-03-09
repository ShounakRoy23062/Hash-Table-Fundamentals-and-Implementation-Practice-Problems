
import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queryFrequency = new HashMap<>();
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> globalFrequency = new HashMap<>();

    // Insert or update query
    public void addQuery(String query) {

        int freq = globalFrequency.getOrDefault(query, 0) + 1;
        globalFrequency.put(query, freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queryFrequency.put(query, freq);
        }
    }

    // Get top 10 suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : node.queryFrequency.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10)
                pq.poll();
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll().getKey() + " (" + pq.poll().getValue() + ")");
        }

        Collections.reverse(result);

        return result;
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");

        List<String> suggestions = system.search("jav");

        for (String s : suggestions)
            System.out.println(s);
    }
}