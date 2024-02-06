package rs.ac.uns.ftn.nwt.simulator_server.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
// TODO UPGRADE THIS
@Service
public class RunnableManager {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ExecutorService donkey = Executors.newVirtualThreadPerTaskExecutor();

    // <deviceId <runnableId, runnable>>
    private final Map<Long, Map<Long, ScheduledFuture<?>>> runnables = new ConcurrentHashMap<>(); // <runnableId, runnable>

    public long startRunnable(long deviceId, Runnable runnable, int delay, int periodInSeconds) {
        long runnableId = Thread.currentThread().threadId();

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> donkey.execute(runnable), delay,periodInSeconds, TimeUnit.SECONDS);

        var tasks = runnables.get(deviceId);
        if(tasks == null) {
            tasks = new ConcurrentHashMap<>();
            tasks.put(runnableId, future);
        } else {
            tasks.put(runnableId, future);
        }
        runnables.put(deviceId, tasks);

        return runnableId;
    }

    public void cancelRunnable(long deviceId, long runnableId) {

        var tasks = runnables.get(deviceId);

        ScheduledFuture<?> task = tasks.get(runnableId);
        if (task != null) {
            task.cancel(true);
            tasks.remove(runnableId);
        }
        runnables.put(deviceId, tasks);
    }


    public void startNonEndingRunnable(Runnable runnable, int delay, int periodInSeconds) {
        scheduler.scheduleAtFixedRate(() -> donkey.execute(runnable), delay,periodInSeconds, TimeUnit.SECONDS);
    }
}
