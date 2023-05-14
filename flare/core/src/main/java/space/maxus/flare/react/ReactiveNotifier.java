package space.maxus.flare.react;

import org.jetbrains.annotations.NotNull;

/**
 * An interface for objects that can notify subscribers of changes.
 * @param <V> The type of value to notify subscribers
 */
public interface ReactiveNotifier<V> {
    /**
     * Returns this notifier's subscriber list
     * @return This notifier's subscriber list
     */
    @NotNull SubscriberList<V> getSubscriberList();

    /**
     * Subscribes a listener to this notifier
     * @param subscriber The listener to subscribe
     * @return The subscriber
     * @param <S> The type of subscriber
     */
    @NotNull
    default <S extends ReactiveSubscriber<V>> S subscribe(@NotNull S subscriber) {
        getSubscriberList().subscribe(subscriber);
        return subscriber;
    }

    /**
     * Unsubscribes a listener from this notifier
     * @param subscriber The listener to unsubscribe
     * @return The subscriber
     * @param <S> The type of subscriber
     */
    @NotNull
    default <S extends ReactiveSubscriber<V>> S unsubscribe(@NotNull S subscriber) {
        getSubscriberList().subscribe(subscriber);
        return subscriber;
    }
}
