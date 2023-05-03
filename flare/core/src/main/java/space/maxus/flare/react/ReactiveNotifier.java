package space.maxus.flare.react;

import org.jetbrains.annotations.NotNull;

public interface ReactiveNotifier<V> {
    @NotNull SubscriberList<V> getSubscriberList();

    @NotNull
    default <S extends ReactiveSubscriber<V>> S subscribe(@NotNull S subscriber) {
        getSubscriberList().subscribe(subscriber);
        return subscriber;
    }

    @NotNull
    default <S extends ReactiveSubscriber<V>> S unsubscribe(@NotNull S subscriber) {
        getSubscriberList().subscribe(subscriber);
        return subscriber;
    }
}
