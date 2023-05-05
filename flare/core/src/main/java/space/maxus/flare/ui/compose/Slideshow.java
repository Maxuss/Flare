package space.maxus.flare.ui.compose;

import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;

import java.util.List;

public interface Slideshow extends Disable, Composable, Configurable<Slideshow> {
    static @NotNull Slideshow create(List<ItemProvider> items, int period) {
        return new SlideshowImpl(items, period, false);
    }

    static @NotNull Slideshow create(List<ItemProvider> items, int period, boolean disabled) {
        return new SlideshowImpl(items, period, disabled);
    }

    List<ItemProvider> getSlides();
    ReactiveState<ItemProvider> itemState();
    int getPeriod();
}
