package space.maxus.flare.ui.compose.extra;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.compose.RootReferencing;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.PausingTask;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@ToString @EqualsAndHashCode(callSuper = true)
final class SlideshowImpl extends RootReferencing implements Slideshow {
    private final ReactiveState<Boolean> disabledState;
    @Getter
    private final List<ItemProvider> slides;
    @Getter
    private final int period;
    private final AtomicInteger currentIdx;
    private final ReactiveState<ItemProvider> currentSlide;
    private PausingTask task;

    public SlideshowImpl(List<ItemProvider> slides, int period, boolean isDisabled) {
        this.slides = slides;
        this.period = period;
        this.disabledState = new ComposableReactiveState<>(isDisabled, this);
        this.currentIdx = new AtomicInteger(0);
        this.currentSlide = new ComposableReactiveState<>(slides.get(0), this);
        this.task = new PausingTask(() -> {
            int cur = this.currentIdx.get();
            int newIdx = cur + 1 >= slides.size() ? 0 : cur + 1;
            this.currentIdx.setRelease(newIdx);
            this.currentSlide.set(slides.get(newIdx));
        });
        this.disabledState.subscribe(disabled -> {
            if(Objects.requireNonNullElse(disabled, false))
                this.task.pause();
            else
                this.task.resume();
        });
    }

    @Override
    public void injectRoot(Frame root) {
        super.injectRoot(root);
        // starting task only on root injection
        this.task.runTaskTimerAsynchronously(Flare.getHook(), period, period);
    }

    @Override
    public Slideshow configure(Configurator<Slideshow> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    @Override
    public ItemStack renderAt(Slot slot) {
        return currentSlide.get().provide();
    }

    @Override
    public ReactiveState<ItemProvider> itemState() {
        return currentSlide;
    }

    @Override
    public void destroy() {
        this.task.cancel();
    }

    @Override
    public void restore() {
        this.task = this.task.copy();
        this.task.runTaskTimerAsynchronously(Flare.getHook(), period, period);
    }
}
