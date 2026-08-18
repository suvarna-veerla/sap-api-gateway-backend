package com.hcl.gateway.manager;

import com.hcl.gateway.algorithm.TokenBucket;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterManager {
    private final ConcurrentHashMap<String, TokenBucket> userBuckets;
    private final long bucketCapacity; 
    private final long refillRate;    

    public RateLimiterManager(long bucketCapacity, long refillRate) {
        this.userBuckets = new ConcurrentHashMap<>();
        this.bucketCapacity = bucketCapacity;
        this.refillRate = refillRate;
    }

    public boolean isAllowed(String clientIp) {
        TokenBucket bucket = userBuckets.computeIfAbsent(clientIp, 
            ip -> new TokenBucket(bucketCapacity, refillRate)
        );
        return bucket.allowRequest();
    }

    public void resetBucket(String clientIp) {
        userBuckets.remove(clientIp);
    }
    
    public int getTotalActiveUsers() {
        return userBuckets.size();
    }
}
