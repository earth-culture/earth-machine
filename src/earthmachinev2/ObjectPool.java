/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachinev2;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Christopher Brett
 * @param <T>
 */
public abstract class ObjectPool<T> {

    private final long expirationTime;

    private final ConcurrentHashMap<T, Long> locked;
    private final ConcurrentHashMap<T, Long> unlocked;

    public ObjectPool() {
        expirationTime = 30000; // 30 seconds
        locked = new ConcurrentHashMap<>();
        unlocked = new ConcurrentHashMap<>();
    }

    protected abstract T create();

    public abstract boolean validate(T o);

    public abstract void expire(T o);

    public synchronized T checkOut() {
        long now = System.currentTimeMillis();
        T t;
        if (!unlocked.isEmpty()) {
            for (T element : unlocked.keySet()) {
                t = element;
                if ((now - unlocked.get(t)) > expirationTime) {
                    // object has expired
                    unlocked.remove(t);
                    expire(t);
                } else {
                    if (validate(t)) {
                        unlocked.remove(t);
                        locked.put(t, now);
                        return (t);
                    } else {
                        // object failed validation
                        unlocked.remove(t);
                        expire(t);
                    }
                }
            }
        }
        // no objects available, create a new one
        t = create();
        locked.put(t, now);
        return (t);
    }

    public synchronized void checkIn(T t) {
        locked.remove(t);
        unlocked.put(t, System.currentTimeMillis());
    }
}

