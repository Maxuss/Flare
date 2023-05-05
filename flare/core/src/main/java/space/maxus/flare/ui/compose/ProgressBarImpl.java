package space.maxus.flare.ui.compose;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;

import java.util.List;

@EqualsAndHashCode(callSuper = true) @ToString
final class ProgressBarImpl extends RootReferencing implements ProgressBar {
    private final ReactiveState<Float> progress;
    private final ItemProvider filledProvider;
    private final ItemProvider emptyProvider;

    private List<Slot> allocatedSpace = List.of();
    private int totalSize = 1;

    ProgressBarImpl(float progress, ItemProvider filledProvider, ItemProvider emptyProvider) {
        this.progress = new ComposableReactiveState<>(progress, this);
        this.filledProvider = filledProvider;
        this.emptyProvider = emptyProvider;
    }

    @Override
    public ItemStack renderAt(Slot slot) {
        int idx = this.allocatedSpace.indexOf(slot) + 1;
        float requiredProgress = (float) idx / (float) totalSize;
        if(getProgress() < requiredProgress) {
            return emptyProvider.provide();
        } else {
            return filledProvider.provide();
        }
    }

    @Override
    public ProgressBar configure(Configurator<ProgressBar> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public ReactiveState<Float> progressState() {
        return progress;
    }

    @Override
    public @NotNull PackedComposable inside(@NotNull ComposableSpace space) {
        this.allocatedSpace = space.slots().stream().sorted().toList();
        this.totalSize = allocatedSpace.size();
        return super.inside(space);
    }
}
