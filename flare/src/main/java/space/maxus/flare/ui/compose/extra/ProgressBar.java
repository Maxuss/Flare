package space.maxus.flare.ui.compose.extra;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.util.FlareUtil;

import java.util.Objects;

public interface ProgressBar extends Composable, Configurable<ProgressBar> {
    static ItemStackBuilder fullBuilder(Material material, float progress, boolean dotted) {
        return Items.builder(material)
                .name("<gray>Progress: <green>%s%% <dark_gray>[%%]".formatted(FlareUtil.formatFloat(progress * 100f)))
                .addLoreLine(FlareUtil.renderBarText(progress, dotted ? 24 : 10, dotted))
                .hideAllFlags();
    }

    static ItemStackBuilder emptyBuilder(Material material, float progress, boolean dotted) {
        return Items.builder(material)
                .name("<gray>Progress: <red>%s%% <dark_gray>[%%]".formatted(FlareUtil.formatFloat(progress * 100f)))
                .addLoreLine(FlareUtil.renderBarText(progress, dotted ? 24 : 10, dotted))
                .hideAllFlags();
    }

    static @NotNull ItemProvider fullProvider(ReactiveState<Float> state, Material material, boolean dotted) {
        return Reactive.item(state, progress -> fullBuilder(material, Objects.requireNonNullElse(progress, .0f), dotted).build());
    }

    static @NotNull ItemProvider emptyProvider(ReactiveState<Float> state, Material material, boolean dotted) {
        return Reactive.item(state, progress -> emptyBuilder(material, Objects.requireNonNullElse(progress, .0f), dotted).build());
    }

    static @NotNull ProgressBar create(ItemProvider full, ItemProvider empty) {
        return create(full, empty, 0f);
    }

    static @NotNull ProgressBar create(ItemProvider full, ItemProvider empty, float progress) {
        return new ProgressBarImpl(progress, full, empty);
    }

    ReactiveState<Float> progressState();

    default float getProgress() {
        return progressState().get();
    }
    default float setProgress(float newProgress) {
        return progressState().get();
    }
}
