
import java.util.*;
        import java.util.concurrent.*;

class TokenBucket {
    int tokens;
    final int maxTokens;
    final int refillRatePerHour;
    long lastRefillTime;

    TokenBucket(int maxTokens, int refillRatePerHour) {
        this.maxTokens = maxTokens;
        this.refillRatePerHour = refillRatePerHour;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - lastRefillTime;

        double tokensToAdd = (elapsedMillis / 3600000.0) * refillRatePerHour;

        if (tokensToAdd >= 1) {
            tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
            lastRefillTime = now;
        }
    }

    synchronized int getRemainingTokens() {
        refill();
        return tokens;
    }

    synchronized long getRetryAfterSeconds() {
        if (tokens > 0) return 0;

        double millisPerToken = 3600000.0 / refillRatePerHour;
        return (long) (millisPerToken / 1000);
    }
}

public class DistributedRateLimiter {

    private final Map<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();
    private final int LIMIT = 1000;

    public String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(clientId, new TokenBucket(LIMIT, LIMIT));

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after "
                    + bucket.getRetryAfterSeconds() + "s)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            return "(used: 0, limit: " + LIMIT + ")";
        }

        int remaining = bucket.getRemainingTokens();
        int used = LIMIT - remaining;

        return "(used: " + used + ", limit: " + LIMIT + ")";
    }

    public static void main(String[] args) {

        DistributedRateLimiter limiter = new DistributedRateLimiter();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}