package space.maxus.flare.util;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@ToString
public class PausingTask extends BukkitRunnable {
    private final Runnable executor;
    private final AtomicBoolean paused = new AtomicBoolean(false);

    public void pause() {
        paused.setRelease(true);
    }

    public void resume() {
        paused.setRelease(false);
    }

    @Override
    public void run() {
        if (!paused.get())
            executor.run();
    }

    public PausingTask copy() {
        return new PausingTask(executor);
    }
}
