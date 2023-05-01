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
import space.maxus.flare.ui.compose.extra.ProgressBar;
import space.maxus.flare.ui.space.Rect;
import space.maxus.flare.ui.space.Slot;

public class BarFC extends FunctionComposable<Float> {
    private BukkitTask barTask;

    public BarFC(Float props) {
        super(props);
    }

    @Override
    public @NotNull Composable compose() {
        ReactiveState<Float> bar = useState(props);
        barTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Flare.getHook(), () -> {
            Flare.LOGGER.info("RUNNING");
            bar.set(bar.get() >= 1f ? 0f : bar.get() + .05f);
        }, 5L, 5L);
        return Composition.list(
                ProgressBar
                        .create(
                                ProgressBar.fullProvider(bar, Material.GREEN_STAINED_GLASS_PANE, true),
                                ProgressBar.emptyProvider(bar, Material.RED_STAINED_GLASS_PANE, true)
                        )
                        .configure(b -> b.progressState().connect(bar))
                        .inside(Rect.of(Slot.ROW_TWO_SLOT_ONE, Slot.ROW_TWO_SLOT_NINE))
        );
    }

    @Override
    public void destroy() {
        if(barTask != null)
            barTask.cancel();
    }
}
