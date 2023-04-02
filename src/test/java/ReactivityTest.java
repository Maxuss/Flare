import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import space.maxus.flare.util.FlareUtil;
import space.maxus.flare.react.ReactiveState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

class ReactivityTest {
    static final Logger logger = Logger.getLogger(ReactivityTest.class.getName());

    @Test
    void testReactiveStates() {
        ReactiveState<String> state1 = new ReactiveState<>("This is a start value!");
        ReactiveState<Integer> state2 = new ReactiveState<>();
        state1.subscribe((state) -> logger.info("State: " + state));
        state2.subscribe((state) -> logger.info("Other state: " + state));
        state1.set("New value!");
        state2.set(53);
        state2.set(null);
        state1.set("A");
        state1.set(null);
        Assertions.assertNull(state1.get());
        Assertions.assertNull(state2.get());
    }

    private final ReactiveState<String> globalReactive = new ReactiveState<>("Origin");
    @Test
    void testGlobalReactiveStates() {
        globalReactive.subscribe((state) -> logger.info("State changed: " + state));
        globalReactive.set("New");
        Assertions.assertEquals("New", globalReactive.get());
    }

    @Test
    void testAsyncReactiveStates() throws InterruptedException {
        ReactiveState<String> origin = new ReactiveState<>("Origin");
        AtomicInteger counter = new AtomicInteger();
        origin.subscribe((state) -> {
            counter.incrementAndGet();
            logger.info("Just changed state: " + state);
        });

        ExecutorService pool = FlareUtil.executor(10);
        for(int i = 0; i < 10; i ++) {
            int solid = i;
            pool.execute(() -> origin.set("Thread #" + solid));
        }

        pool.shutdown();
        boolean ignored = pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        Assertions.assertEquals(10, counter.get());
    }
}
