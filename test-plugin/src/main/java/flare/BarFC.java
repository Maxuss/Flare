package flare;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.FunctionComposable;
import space.maxus.flare.ui.compose.complex.Composition;
import space.maxus.flare.ui.compose.ProgressBar;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

public class BarFC extends FunctionComposable<Float> {
    private BukkitTask barTask;
    private final ReactiveState<Float> bar = useState(props);

    public BarFC(Float props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        setupBarTask();
        return Composition.of(
                ProgressBar
                        .of(
                                ProgressBar.fullProvider(bar, Material.GREEN_STAINED_GLASS_PANE, true),
                                ProgressBar.emptyProvider(bar, Material.RED_STAINED_GLASS_PANE, true)
                        )
                        .configure(b -> b.progressState().connect(bar))
                        .inside(Rect.of(Slot.ROW_TWO_SLOT_ONE, Slot.ROW_TWO_SLOT_NINE))
        );
    }

    private void setupBarTask() {
        this.barTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Flare.getInstance(), () -> bar.set(bar.get() >= 1f ? 0f : bar.get() + .05f), 5L, 5L);
    }

    @Override
    public void destroy() {
        if(barTask != null)
            barTask.cancel();
    }

    @Override
    public void restore() {
        setupBarTask();
    }
}
