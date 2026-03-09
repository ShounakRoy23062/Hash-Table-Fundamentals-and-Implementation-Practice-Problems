
import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long timestamp;

    Transaction(int id, int amount, String merchant, String account, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.timestamp = timestamp;
    }
}

public class FinancialTransactions {

    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public List<int[]> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum within 1 hour window
    public List<int[]> findTwoSumTimeWindow(int target, long windowMillis) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction prev = map.get(complement);

                if (Math.abs(t.timestamp - prev.timestamp) <= windowMillis) {
                    result.add(new int[]{prev.id, t.id});
                }
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // K-Sum using recursion
    public List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        kSumHelper(0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void kSumHelper(int start, int k, int target,
                            List<Integer> current,
                            List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0 || start >= transactions.size())
            return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            current.add(t.id);

            kSumHelper(i + 1, k - 1, target - t.amount, current, result);

            current.remove(current.size() - 1);
        }
    }

    // Duplicate detection
    public void detectDuplicates() {

        Map<String, Set<String>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(t.account);
        }

        for (String key : map.keySet()) {

            Set<String> accounts = map.get(key);

            if (accounts.size() > 1) {

                String[] parts = key.split("_");

                System.out.println(
                        "{amount:" + parts[0] +
                                ", merchant:\"" + parts[1] +
                                "\", accounts:" + accounts + "}"
                );
            }
        }
    }

    public static void main(String[] args) {

        FinancialTransactions system = new FinancialTransactions();

        long now = System.currentTimeMillis();

        system.addTransaction(new Transaction(1, 500, "StoreA", "acc1", now));
        system.addTransaction(new Transaction(2, 300, "StoreB", "acc2", now + 1000));
        system.addTransaction(new Transaction(3, 200, "StoreC", "acc3", now + 2000));
        system.addTransaction(new Transaction(4, 500, "StoreA", "acc4", now + 3000));

        System.out.println("TwoSum target=500:");
        for (int[] pair : system.findTwoSum(500)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("\nTwoSum within 1 hour:");
        for (int[] pair : system.findTwoSumTimeWindow(500, 3600000)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("\nKSum k=3 target=1000:");
        System.out.println(system.findKSum(3, 1000));

        System.out.println("\nDuplicate Detection:");
        system.detectDuplicates();
    }
}