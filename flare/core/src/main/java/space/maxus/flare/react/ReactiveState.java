package space.maxus.flare.react;

import com.google.common.base.MoreObjects;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A ReactiveState is a thread-safe wrapper over a value, that can be hooked to, so you can listen to value updates.
 * @param <V> The type of the value this state holds
 */
@ThreadSafe
@Debug.Renderer(text = "this.value.get()")
public class ReactiveState<V> implements ReactiveNotifier<V> {
    private final SubscriberList<V> subscriberList;
    private final AtomicReference<@Nullable V> value;

    /**
     * Constructs a reactive state with the given value
     * @param origin Initial value of this state. May be null.
     */
    public ReactiveState(@Nullable V origin) {
        this(new AtomicReference<>(origin), new SubscriberList<>());
    }

    /**
     * Constructs a reactive state with no value
     */
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

    /**
     * Subscribes a listener to this state, additionally populating it with current value
     * @param subscriber The subscriber to subscribe to this state
     * @param <S> The type of the subscriber
     */
    public <S extends ReactiveSubscriber<V>> void subscribeUpdate(S subscriber) {
        getSubscriberList().subscribe(subscriber);
        try {
            subscriber.onStateChange(this.value.get());
        } catch (ReactiveException e) {
            Flare.logger().error("Error while populating a subscriber", e);
        }
    }

    /**
     * Sets the value of this state
     * @param newValue New value to be set
     */
    public void set(@Nullable V newValue) {
        this.value.setRelease(newValue);
        getSubscriberList().notify(newValue);
    }

    /**
     * Sets the value of this state to the given optional value or null
     * @param optValue Value to be set
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void setOpt(@NotNull Optional<V> optValue) {
        this.value.setRelease(optValue.orElse(null));
    }

    /**
     * Gets the value of this state.
     * @return The value of this state
     * @throws NullPointerException if value is null
     */
    public @NotNull V get() {
        return Objects.requireNonNull(getOrNull(), "State was null");
    }

    /**
     * Gets the value of this state or null.
     * @return The value of this state or null
     */
    public @Nullable V getOrNull() {
        return this.value.get();
    }

    /**
     * Gets the value of this state as an optional.
     * @return The value of this state as an optional
     */
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
