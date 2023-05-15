package space.maxus.flare.util;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a bukkit runnable that can be paused and resumed
 */
@RequiredArgsConstructor
@ToString
public class PausingTask extends BukkitRunnable {
    private final Runnable executor;
    private final AtomicBoolean paused = new AtomicBoolean(false);

    /**
     * Pauses this task with memory effects of {@link java.lang.invoke.VarHandle#setRelease}
     */
    public void pause() {
        paused.setRelease(true);
    }

    /**
     * Resumes this task with memory effects of {@link java.lang.invoke.VarHandle#setRelease}
     */
    public void resume() {
        paused.setRelease(false);
    }

    @Override
    public void run() {
        if (!paused.get())
            executor.run();
    }

    /**
     * Copies this task without copying its pause state
     * @return Copy of this task
     */
    public PausingTask copy() {
        return new PausingTask(executor);
    }
}
