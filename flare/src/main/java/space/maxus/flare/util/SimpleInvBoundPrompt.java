package space.maxus.flare.util;

import lombok.Data;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;

@Data
public class SimpleInvBoundPrompt implements Prompt {
    private final String prompt;
    private final ReactiveState<String> textState;
    private final Inventory inventory;

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return LegacyComponentSerializer.legacySection().serialize(FlareUtil.MINI_MESSAGE.deserialize(prompt));
    }

    @Override
    public boolean blocksForInput(@NotNull ConversationContext context) {
        return true;
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        textState.set(input);
        ((Player) context.getForWhom()).openInventory(inventory);
        return Prompt.END_OF_CONVERSATION;
    }
}
