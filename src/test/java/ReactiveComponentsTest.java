import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import space.maxus.flare.Util;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.text.ReactiveComponent;

import java.util.logging.Logger;

class ReactiveComponentsTest {
    static final Logger logger = Logger.getLogger(ReactiveComponentsTest.class.getName());

    @Test
    void general() {
        ReactiveState<String> state = new ReactiveState<>("Origin");
        ReactiveComponent<String> component = Reactive.text(state, (other) -> Util.text("<gold>The value is: " + other));

        Assertions.assertEquals(Util.text("<gold>The value is: Origin"), component.asComponent());

        state.set("New value");
        Assertions.assertEquals(Util.text("<gold>The value is: New value"), component.asComponent());
    }

    @Test
    void vanillaCompatibility() {
        ReactiveState<String> state = new ReactiveState<>("Origin");
        ReactiveComponent<String> component = Reactive.text(state, (other) -> Util.text("<gold>The value is: " + other));
        net.minecraft.network.chat.Component vanilla = PaperAdventure.asVanilla(component);
        state.set("new");
        Assertions.assertEquals(PaperAdventure.asVanilla(
                Component.empty()
                        .color(NamedTextColor.GOLD)
                        .append(Util.text("<gold>The value is: new")
                        )
        ).copy(), vanilla.copy());
    }
}
