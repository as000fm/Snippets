Understood — then keep it **blocking** and simple: use the `writeLock().lock()` drain (no timeout), and `readLock().lock()` for adds. That guarantees the drain is exclusive and every client request will wait its turn.

Here’s the final “must wait” version (same as before, just no tryLock path):

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ClientBuckets {
  private final ReadWriteLock rw = new ReentrantReadWriteLock();
  private final HashMap<String, ConcurrentLinkedQueue<String>> map = new HashMap<>();

  public void add(String clientId, String value) {
    rw.readLock().lock();
    try {
      map.computeIfAbsent(clientId, k -> new ConcurrentLinkedQueue<>()).add(value);
    } finally {
      rw.readLock().unlock();
    }
  }

  /** Exclusive drain: blocks all adds until done. */
  public Map<String, List<String>> drain() {
    rw.writeLock().lock();
    try {
      Map<String, List<String>> snapshot = new HashMap<>(map.size());
      for (Map.Entry<String, ConcurrentLinkedQueue<String>> e : map.entrySet()) {
        snapshot.put(e.getKey(), new ArrayList<>(e.getValue()));
      }
      map.clear();
      return snapshot;
    } finally {
      rw.writeLock().unlock();
    }
  }
}
```

### Two practical notes

1. **Fairness (optional):** if you’re worried about a constant stream of `add()` calls starving the drain, you can make the lock “fair”:

```java
private final ReadWriteLock rw = new ReentrantReadWriteLock(true);
```

Fair locks reduce starvation risk but can lower throughput a bit.

2. **Keep drain work minimal while holding the write lock.** In this version, `drain()` *copies* while holding the write lock, which is correct for exclusivity. If the map/queues can get large, this will pause all adds during the copy. (Still correct — just something to be aware of.)

If you want, paste your current `add()` logic (especially if you do more than one `add` per call) and I’ll help ensure it stays atomic and fast under this locking scheme.
