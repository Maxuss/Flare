package space.maxus.flare.ui.compose;

import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;

import java.util.List;

/**
 * A slideshow is a periodically changing component.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#slideshow">Slideshow</a>
 */
public interface Slideshow extends Disable, Composable, Configurable<Slideshow> {
    /**
     * Constructs a new slideshow out of a list of items
     * @param items Items to be used
     * @param period Period between switching in ticks
     * @return A new slideshow
     */
    static @NotNull Slideshow of(List<ItemProvider> items, int period) {
        return new SlideshowImpl(items, period, false);
    }

    /**
     * Constructs a new slideshow out of a list of items
     * @param items Items to be used
     * @param period Period between switching in ticks
     * @param disabled Whether the slideshow should be disabled
     * @return A new slideshow
     */
    static @NotNull Slideshow of(List<ItemProvider> items, int period, boolean disabled) {
        return new SlideshowImpl(items, period, disabled);
    }

    /**
     * Returns all possible slides on this slideshow
     * @return All possible slides on this slideshow
     */
    List<ItemProvider> getSlides();

    /**
     * Returns the reactive state of the currently selected item provider
     * @return The reactive state of the currently selected item provider
     */
    ReactiveState<ItemProvider> itemState();

    /**
     * Returns the period between the slides switching
     * @return The period between the slides switching
     */
    int getPeriod();
}
