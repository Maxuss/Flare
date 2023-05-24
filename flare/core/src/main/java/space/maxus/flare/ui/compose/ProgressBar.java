package space.maxus.flare.ui.compose;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.util.FlareUtil;

import java.util.Objects;

/**
 * A progress bar is a dynamic component that renders progress inside itself.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#progressbar">Progress Bar</a>
 */
public interface ProgressBar extends Composable, Configurable<ProgressBar> {
    /**
     * Returns an item builder for a full progress bar part
     * @param material Base material
     * @param progress Bar progress
     * @param dotted Whether the display should be dotted.
     * @return An item builder for a full progress bar part
     * @see FlareUtil#renderBarText(float, int, boolean)
     */
    static ItemStackBuilder fullBuilder(Material material, float progress, boolean dotted) {
        return Items.builder(material)
                .name("<gray>Progress: <green>%s%% <dark_gray>[%%]".formatted(Math.round(progress * 100f)))
                .addLoreLine(FlareUtil.renderBarText(progress, dotted ? 24 : 10, dotted))
                .hideAllFlags();
    }

    /**
     * Returns an item builder for an empty progress bar part
     * @param material Base material
     * @param progress Bar progress
     * @param dotted Whether the display should be dotted.
     * @return An item builder for an empty progress bar part
     * @see FlareUtil#renderBarText(float, int, boolean)
     */
    static ItemStackBuilder emptyBuilder(Material material, float progress, boolean dotted) {
        return Items.builder(material)
                .name("<gray>Progress: <red>%s%% <dark_gray>[%%]".formatted(Math.round(progress * 100f)))
                .addLoreLine(FlareUtil.renderBarText(progress, dotted ? 24 : 10, dotted))
                .hideAllFlags();
    }

    /**
     * Returns an item builder for a full progress bar part
     * @param material Base material
     * @param state The bar progress reactive state
     * @param dotted Whether the display should be dotted.
     * @return An item builder for a full progress bar part
     * @see FlareUtil#renderBarText(float, int, boolean)
     */
    static @NotNull ItemProvider fullProvider(ReactiveState<Float> state, Material material, boolean dotted) {
        return Reactive.item(state, progress -> fullBuilder(material, Objects.requireNonNullElse(progress, .0f), dotted).build());
    }

    /**
     * Returns an item builder for an empty progress bar part
     * @param material Base material
     * @param state The bar progress reactive state
     * @param dotted Whether the display should be dotted.
     * @return An item builder for an empty progress bar part
     * @see FlareUtil#renderBarText(float, int, boolean)
     */
    static @NotNull ItemProvider emptyProvider(ReactiveState<Float> state, Material material, boolean dotted) {
        return Reactive.item(state, progress -> emptyBuilder(material, Objects.requireNonNullElse(progress, .0f), dotted).build());
    }

    /**
     * Constructs a progress bar with full and empty item providers
     * @param full Item provider for filled part
     * @param empty Item provider for empty part
     * @return A progress bar with full and empty item providers
     */
    static @NotNull ProgressBar of(ItemProvider full, ItemProvider empty) {
        return of(full, empty, 0f);
    }

    /**
     * Constructs a progress bar with full and empty item providers and base progress
     * @param full Item provider for filled part
     * @param empty Item provider for empty part
     * @param progress Starting progress of the bar.
     * @return A progress bar with full and empty item providers
     */
    static @NotNull ProgressBar of(ItemProvider full, ItemProvider empty, @Range(from = 0, to = 1) float progress) {
        return new ProgressBarImpl(progress, full, empty);
    }

    /**
     * Returns the current progress state
     * @return Current progress state.
     */
    ReactiveState<Float> progressState();

    /**
     * Gets current bar progress
     * @return Current bar progress
     */
    default @Range(from = 0, to = 1) float getProgress() {
        return progressState().get();
    }

    /**
     * Sets the bar progress
     * @param newProgress New progress
     */
    default void setProgress(@Range(from = 0, to = 1) float newProgress) {
        progressState().set(newProgress);
    }
}
