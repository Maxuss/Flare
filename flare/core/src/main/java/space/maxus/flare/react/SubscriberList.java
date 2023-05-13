package space.maxus.flare.react;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class SubscriberList<V> {
    private final Set<ReactiveSubscriber<V>> subscribers = ConcurrentHashMap.newKeySet();

    public void subscribe(@NotNull ReactiveSubscriber<V> subscriber) {
        Validate.notNull(subscriber, "Tried to register a null subscriber!");

        this.subscribers.add(subscriber);
    }

    public void unsubscribe(@NotNull ReactiveSubscriber<V> subscriber) {
        Validate.notNull(subscriber, "Tried to unregister a null subscriber!");

        this.subscribers.remove(subscriber);
    }

    public void notify(@Nullable V changedState) {
        for (ReactiveSubscriber<V> subscriber : this.subscribers) {
            try {
                subscriber.onStateChange(changedState);
            } catch (ReactiveException e) {
                Flare.logger().error("Error while notifying subscriber", e);
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("subscriber-count", subscribers.size())
                .toString();
    }
}
