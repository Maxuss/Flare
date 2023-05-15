package space.maxus.flare.util;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;

@ApiStatus.Internal
@RequiredArgsConstructor
public class ValidatingInvBoundPrompt extends ValidatingPrompt {
    private final Validator validator;
    private final String prompt;
    private final ReactiveState<String> textState;
    private final Inventory inventory;

    @Override
    protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
        return validator.isValid(input);
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
        textState.set(input);
        ((Player) context.getForWhom()).openInventory(inventory);
        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return LegacyComponentSerializer.legacySection().serialize(FlareUtil.MINI_MESSAGE.deserialize(prompt));
    }
}
