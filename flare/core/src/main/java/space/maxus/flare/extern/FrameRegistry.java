package space.maxus.flare.extern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Frame;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Frame registry can be used for storing your frames so they can later be accessed by end-user via commands like /flare open
 */
public class FrameRegistry {
    private final ConcurrentHashMap<String, Frame> allFrames = new ConcurrentHashMap<>();

    /**
     * Gets all registered frame ids
     * @return All registered frame IDs
     */
    public @NotNull Set<String> getFrameIds() {
        return allFrames.keySet();
    }

    /**
     * Registers a frame with the given id
     * @param id Identifier of a frame. Must be unique.
     * @param frame Frame to register
     */
    public void addFrame(@NotNull String id, @NotNull Frame frame) {
        allFrames.put(id, frame);
    }

    /**
     * Gets a frame by its id
     * @param id ID of the frame
     * @return Frame if found, otherwise null
     */
    public @Nullable Frame getFrame(String id) {
        return allFrames.get(id);
    }
}
