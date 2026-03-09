import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCacheSystem {

    private final int L1_CAPACITY = 10000;
    private final int L2_CAPACITY = 100000;

    // L1 Cache (Memory, LRU)
    private LinkedHashMap<String, VideoData> L1 =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > L1_CAPACITY;
                }
            };

    // L2 Cache (SSD simulated)
    private LinkedHashMap<String, VideoData> L2 =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > L2_CAPACITY;
                }
            };

    // L3 Database
    private HashMap<String, VideoData> L3 = new HashMap<>();

    // Access counts
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // Stats
    private int L1_hits = 0;
    private int L2_hits = 0;
    private int L3_hits = 0;
    private int totalRequests = 0;

    public MultiLevelCacheSystem() {

        for (int i = 1; i <= 1000; i++) {
            String id = "video_" + i;
            L3.put(id, new VideoData(id, "VideoContent_" + i));
        }
    }

    public VideoData getVideo(String videoId) {

        totalRequests++;

        if (L1.containsKey(videoId)) {
            L1_hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            incrementAccess(videoId);
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        if (L2.containsKey(videoId)) {
            L2_hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);
            promoteToL1(video);

            incrementAccess(videoId);
            return video;
        }

        System.out.println("L2 Cache MISS");

        VideoData video = L3.get(videoId);

        if (video != null) {
            L3_hits++;
            System.out.println("L3 Database HIT (150ms)");

            L2.put(videoId, video);
            accessCount.put(videoId, 1);

            return video;
        }

        System.out.println("Video not found");
        return null;
    }

    private void incrementAccess(String videoId) {

        int count = accessCount.getOrDefault(videoId, 0) + 1;
        accessCount.put(videoId, count);

        if (count > 5 && L2.containsKey(videoId)) {
            promoteToL1(L2.get(videoId));
        }
    }

    private void promoteToL1(VideoData video) {
        L1.put(video.videoId, video);
    }

    public void invalidate(String videoId) {

        L1.remove(videoId);
        L2.remove(videoId);
        L3.remove(videoId);
        accessCount.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    public void getStatistics() {

        double L1_rate = (L1_hits * 100.0) / totalRequests;
        double L2_rate = (L2_hits * 100.0) / totalRequests;
        double L3_rate = (L3_hits * 100.0) / totalRequests;
        double overall = ((L1_hits + L2_hits) * 100.0) / totalRequests;

        System.out.println("\nCache Statistics:");
        System.out.printf("L1 Hit Rate: %.2f%%\n", L1_rate);
        System.out.printf("L2 Hit Rate: %.2f%%\n", L2_rate);
        System.out.printf("L3 Hit Rate: %.2f%%\n", L3_rate);
        System.out.printf("Overall Cache Hit Rate: %.2f%%\n", overall);
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}