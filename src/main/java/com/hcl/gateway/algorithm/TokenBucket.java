package com.hcl.gateway.algorithm;

import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {
    private final long capacity;      
    private final long refillRate;     
    private double currentTokens;     
    private long lastRefillTimestamp;  
    private final ReentrantLock lock = new ReentrantLock();

    public TokenBucket(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.currentTokens = capacity; 
        this.lastRefillTimestamp = System.currentTimeMillis(); 
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastRefillTimestamp;
        double tokensToAdd = (elapsedTime / 1000.0) * refillRate;

        if (tokensToAdd > 0) {
            currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
            lastRefillTimestamp = now; 
        }
    }

    public boolean allowRequest() {
        lock.lock(); 
        try {
            refill(); 
            if (currentTokens >= 1) {
                currentTokens--; 
                return true;     
            }
            return false;        
        } finally {
            lock.unlock(); 
        }
    }
}
