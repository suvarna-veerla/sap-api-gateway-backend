package com.hcl.gateway.breaker;

public class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int failureThreshold = 3; 
    private long lastStateChangeTime = System.currentTimeMillis();
    private final long cooldownPeriod = 5000; 

    public boolean allowRequest() {
        long now = System.currentTimeMillis();

        if (state == State.OPEN && (now - lastStateChangeTime > cooldownPeriod)) {
            state = State.HALF_OPEN;
            lastStateChangeTime = now;
            System.out.println("🔄 Circuit Breaker: HALF-OPEN (Testing server status...)");
        }

        return state != State.OPEN;
    }

    public void recordFailure() {
        failureCount++;
        System.out.println("⚠️ Server Failure Recorded. Count: " + failureCount);
        if (state == State.CLOSED && failureCount >= failureThreshold) {
            state = State.OPEN;
            lastStateChangeTime = System.currentTimeMillis();
            System.out.println("🚨 Circuit Breaker: OPENED! (Blocking all requests)");
        }
    }

    public void recordSuccess() {
        failureCount = 0;
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            System.out.println("🟢 Circuit Breaker: CLOSED! (Server is healthy again)");
        }
    }

    public State getCurrentState() {
        return state;
    }
}
