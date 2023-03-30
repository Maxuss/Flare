package space.maxus.flare.react;

import com.google.common.base.MoreObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class ReactiveState<V> implements ReactiveNotifier<V> {
    private final SubscriberList<V> subscriberList = new SubscriberList<>();
    private final AtomicReference<@Nullable V> value;

    public ReactiveState(@Nullable V origin) {
        this.value = new AtomicReference<>(origin);
    }

    public ReactiveState() {
        this.value = new AtomicReference<>();
    }

    @Override
    public @NotNull SubscriberList<V> getSubscriberList() {
        return subscriberList;
    }

    public void set(@Nullable V newValue) {
        this.value.setRelease(newValue);
        getSubscriberList().notify(newValue);
    }

    public @Nullable V get() {
        return this.value.get();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("subscriberList", subscriberList)
                .add("value", value.getPlain())
                .toString();
    }
}
