package space.maxus.flare;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@UtilityClass
public class Util {
    private final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public Component text(String miniMessage) {
        return MINI_MESSAGE.deserialize(miniMessage);
    }
}
