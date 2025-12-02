package com.cinema.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis Distributed Lock Service for managing seat locks
 * Uses SETNX (SET if Not eXists) pattern for distributed locking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SEAT_LOCK_PREFIX = "seat:lock:";
    private static final String BOOKING_CACHE_PREFIX = "booking:";

    /**
     * Try to acquire lock for a seat
     * @param showSeatId The show seat ID to lock
     * @param userId The user ID acquiring the lock
     * @param timeoutSeconds Lock timeout in seconds
     * @return true if lock acquired, false if seat is already locked
     */
    public boolean tryLockSeat(Long showSeatId, Long userId, long timeoutSeconds) {
        String lockKey = SEAT_LOCK_PREFIX + showSeatId;
        String lockValue = String.valueOf(userId);

        try {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, timeoutSeconds, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(success)) {
                log.debug("Acquired lock for seat {} by user {}", showSeatId, userId);
                return true;
            } else {
                log.debug("Failed to acquire lock for seat {} - already locked", showSeatId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error acquiring lock for seat {}: {}", showSeatId, e.getMessage());
            // Fallback: allow operation to proceed (database will handle consistency)
            return true;
        }
    }

    /**
     * Try to acquire locks for multiple seats atomically
     * @param showSeatIds List of show seat IDs to lock
     * @param userId The user ID acquiring the locks
     * @param timeoutSeconds Lock timeout in seconds
     * @return true if all locks acquired, false if any seat is already locked
     */
    public boolean tryLockSeats(List<Long> showSeatIds, Long userId, long timeoutSeconds) {
        // First, try to acquire all locks
        for (Long seatId : showSeatIds) {
            if (!tryLockSeat(seatId, userId, timeoutSeconds)) {
                // Rollback: release any locks we've acquired
                for (Long acquiredSeatId : showSeatIds) {
                    if (acquiredSeatId.equals(seatId)) {
                        break; // Stop at the failed seat
                    }
                    unlockSeat(acquiredSeatId, userId);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Release lock for a seat
     * @param showSeatId The show seat ID to unlock
     * @param userId The user ID that owns the lock
     * @return true if unlocked, false otherwise
     */
    public boolean unlockSeat(Long showSeatId, Long userId) {
        String lockKey = SEAT_LOCK_PREFIX + showSeatId;

        try {
            // Only unlock if we own the lock
            Object currentLockHolder = redisTemplate.opsForValue().get(lockKey);
            if (currentLockHolder != null && currentLockHolder.toString().equals(String.valueOf(userId))) {
                Boolean deleted = redisTemplate.delete(lockKey);
                log.debug("Released lock for seat {} by user {}", showSeatId, userId);
                return Boolean.TRUE.equals(deleted);
            } else {
                log.warn("Cannot unlock seat {} - lock not owned by user {}", showSeatId, userId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error releasing lock for seat {}: {}", showSeatId, e.getMessage());
            return false;
        }
    }

    /**
     * Release locks for multiple seats
     */
    public void unlockSeats(List<Long> showSeatIds, Long userId) {
        for (Long seatId : showSeatIds) {
            unlockSeat(seatId, userId);
        }
    }

    /**
     * Force release a seat lock (for admin/system use)
     */
    public boolean forceUnlockSeat(Long showSeatId) {
        String lockKey = SEAT_LOCK_PREFIX + showSeatId;
        try {
            Boolean deleted = redisTemplate.delete(lockKey);
            log.info("Force released lock for seat {}", showSeatId);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            log.error("Error force releasing lock for seat {}: {}", showSeatId, e.getMessage());
            return false;
        }
    }

    /**
     * Check if a seat is locked
     */
    public boolean isSeatLocked(Long showSeatId) {
        String lockKey = SEAT_LOCK_PREFIX + showSeatId;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        } catch (Exception e) {
            log.error("Error checking lock for seat {}: {}", showSeatId, e.getMessage());
            return false;
        }
    }

    /**
     * Get the user ID that holds the lock for a seat
     */
    public Long getSeatLockHolder(Long showSeatId) {
        String lockKey = SEAT_LOCK_PREFIX + showSeatId;
        try {
            Object value = redisTemplate.opsForValue().get(lockKey);
            if (value != null) {
                return Long.parseLong(value.toString());
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting lock holder for seat {}: {}", showSeatId, e.getMessage());
            return null;
        }
    }

    /**
     * Extend lock timeout for a seat
     */
    public boolean extendLock(Long showSeatId, Long userId, long additionalSeconds) {
        String lockKey = SEAT_LOCK_PREFIX + showSeatId;
        try {
            Object currentLockHolder = redisTemplate.opsForValue().get(lockKey);
            if (currentLockHolder != null && currentLockHolder.toString().equals(String.valueOf(userId))) {
                Long currentTtl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
                if (currentTtl != null && currentTtl > 0) {
                    redisTemplate.expire(lockKey, currentTtl + additionalSeconds, TimeUnit.SECONDS);
                    log.debug("Extended lock for seat {} by {} seconds", showSeatId, additionalSeconds);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error extending lock for seat {}: {}", showSeatId, e.getMessage());
            return false;
        }
    }

    // ==================== Generic Cache Operations ====================

    /**
     * Set a value in cache with expiration
     */
    public void setCache(String key, Object value, long timeoutSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error setting cache for key {}: {}", key, e.getMessage());
        }
    }

    /**
     * Get a value from cache
     */
    @SuppressWarnings("unchecked")
    public <T> T getCache(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && type.isInstance(value)) {
                return (T) value;
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting cache for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Delete a cache entry
     */
    public void deleteCache(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting cache for key {}: {}", key, e.getMessage());
        }
    }

    /**
     * Get or compute value with caching
     */
    public <T> T getOrCompute(String key, Class<T> type, long timeoutSeconds, Supplier<T> supplier) {
        T cached = getCache(key, type);
        if (cached != null) {
            return cached;
        }

        T computed = supplier.get();
        if (computed != null) {
            setCache(key, computed, timeoutSeconds);
        }
        return computed;
    }
}
