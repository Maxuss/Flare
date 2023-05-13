package space.maxus.flare.react;

import com.google.common.base.MoreObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ReactiveState<V> implements ReactiveNotifier<V> {
    private final SubscriberList<V> subscriberList;
    private final AtomicReference<@Nullable V> value;

    public ReactiveState(@Nullable V origin) {
        this(new AtomicReference<>(origin), new SubscriberList<>());
    }

    public ReactiveState() {
        this(new AtomicReference<>(), new SubscriberList<>());
    }

    private ReactiveState(AtomicReference<@Nullable V> value, SubscriberList<V> subscriberList) {
        this.value = value;
        this.subscriberList = subscriberList;
    }

    @Override
    public @NotNull SubscriberList<V> getSubscriberList() {
        return subscriberList;
    }

    /**
     * Connects this state to another state, meaning that whenever the other state changes,
     * this state will be updated with its new value.
     *
     * @param other The other state to connect to
     */
    public void connect(@NotNull ReactiveState<V> other) {
        other.subscribe(val -> this.setOpt(Optional.ofNullable(val)));
    }

    public <S extends ReactiveSubscriber<V>> void subscribeUpdate(S subscriber) {
        getSubscriberList().subscribe(subscriber);
        try {
            subscriber.onStateChange(this.value.get());
        } catch (ReactiveException e) {
            Flare.logger().error("Error while populating a subscriber", e);
        }
    }

    public void set(@Nullable V newValue) {
        this.value.setRelease(newValue);
        getSubscriberList().notify(newValue);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void setOpt(@NotNull Optional<V> optValue) {
        this.value.setRelease(optValue.orElse(null));
    }

    public @NotNull V get() {
        return Objects.requireNonNull(getOrNull(), "State was null");
    }

    public @Nullable V getOrNull() {
        return this.value.get();
    }

    public @NotNull Optional<V> getOptional() {
        return Optional.ofNullable(getOrNull());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("subscriberList", subscriberList)
                .add("value", value.getPlain())
                .toString();
    }
}
