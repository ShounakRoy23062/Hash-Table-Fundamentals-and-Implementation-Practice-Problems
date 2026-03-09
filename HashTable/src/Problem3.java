import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class Problem3 {

    private final int capacity;
    private Map<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public Problem3(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > Problem3.this.capacity;
            }
        };
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long time = (System.nanoTime() - start) / 1_000_000;
                return "Cache HIT → " + entry.ipAddress + " (" + time + " ms)";
            } else {
                cache.remove(domain);
            }
        }

        misses++;

        // Simulate upstream DNS query
        String newIP = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, newIP, 300));

        return "Cache MISS → Queried upstream → " + newIP;
    }

    // Simulated upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        Random r = new Random();

        return "172.217.14." + (200 + r.nextInt(50));
    }

    // Remove expired entries
    public void cleanupExpired() {

        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            if (it.next().getValue().isExpired()) {
                it.remove();
            }
        }
    }

    // Cache statistics
    public String getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        return "Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }

    public static void main(String[] args) throws Exception {

        Problem3 dnsCache = new Problem3(5);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));

        Thread.sleep(1000);

        System.out.println(dnsCache.resolve("openai.com"));
        System.out.println(dnsCache.resolve("google.com"));

        System.out.println(dnsCache.getCacheStats());
    }
}