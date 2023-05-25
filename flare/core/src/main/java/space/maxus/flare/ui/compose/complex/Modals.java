package space.maxus.flare.ui.compose.complex;

import lombok.Builder;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.*;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.space.Slot;

/**
 * Contains certain utility modals
 */
@UtilityClass
public class Modals {
    /**
     * A Yes/No modal
     */
    @Builder
    public static final class YesNoModal implements ComposableLike {
        /**
         * The name of the modal
         */
        @Builder.Default
        private final String name = "Modal";
        /**
         * The description of the modal button
         */
        @Builder.Default
        private final String description = "Opens a modal";
        /**
         * The name of the decline button
         */
        @Builder.Default
        private final String declineName = "Decline";
        /**
         * The name of the accept button
         */
        @Builder.Default
        private final String acceptName = "Accept";
        /**
         * Handler for when the accept button is clicked
         */
        @Builder.Default
        private final Runnable onAccept = () -> {
        };
        /**
         * Handler for when the decline button
         */
        @Builder.Default
        private final Runnable onDecline = () -> {
        };
        /**
         * Extra information in the modal
         */
        @Builder.Default
        private final @Nullable String extraInformation = null;

        /**
         * Converts this modal into a modal builder
         * @return The modal builder
         */
        @SuppressWarnings("ConstantValue")
        public Modal.@NotNull Builder asBuilder() {
            return yesOrNoModalBuilder(
                    name,
                    description,
                    declineName,
                    acceptName,
                    onAccept,
                    onDecline,
                    extraInformation
            );
        }

        @Override
        public @NotNull Composable asComposable() {
            return asBuilder().build();
        }

        private @NotNull Modal.Builder yesOrNoModalBuilder(
                @NotNull String name,
                @NotNull String description,
                String declineName,
                String acceptName,
                Runnable accept,
                Runnable decline,
                @Nullable String extraInformation
        ) {
            return Modal.builder(
                            ItemProvider.still(Items.builder(Material.PAPER)
                                    .hideAllFlags()
                                    .name("<gray>Open %s <dark_gray>[◘]".formatted(name))
                                    .lore(description)
                                    .padLore()
                                    .addLoreLine("<dark_gray>Click to open")
                                    .build()
                            )
                    )
                    .dimensions(Dimensions.THREE_BY_NINE)
                    .title("Yes/No")
                    .initializer(modal -> {
                        modal.compose(Placeholder.of(ItemProvider.still(Items.empty())).inside(Slot.ALL));
                        modal.compose(Button.builder(ItemProvider.still(
                                                Items
                                                        .builder(Material.BARRIER)
                                                        .name("<red>%s".formatted(declineName))
                                                        .addLoreLine("<dark_gray>Click to decline")
                                                        .build()
                                        ))
                                        .onClick(Button.ClickHandler.cancelling((btn, e) -> {
                                            decline.run();
                                            Frame root = modal.getParent().root();
                                            modal.switchFrame(root);
                                            PlayerFrameStateManager.restoreSnapshot(e.getWhoClicked());
                                            PlayerFrameStateManager.restoreSnapshot(e.getWhoClicked()); // voiding the snapshot
                                        }))
                                        .build().inside(Slot.ROW_TWO_SLOT_THREE)
                        );
                        modal.compose(Button.builder(ItemProvider.still(
                                                Items
                                                        .builder(Material.EMERALD)
                                                        .name("<green>%s".formatted(acceptName))
                                                        .addLoreLine("<dark_gray>Click to accept")
                                                        .build()
                                        ))
                                        .onClick(Button.ClickHandler.cancelling((btn, e) -> {
                                            accept.run();
                                            Frame root = modal.getParent().root();
                                            modal.switchFrame(root);
                                            PlayerFrameStateManager.restoreSnapshot(e.getWhoClicked());
                                            PlayerFrameStateManager.restoreSnapshot(e.getWhoClicked()); // voiding the snapshot
                                        }))
                                        .build().inside(Slot.ROW_TWO_SLOT_SEVEN)
                        );
                        if (extraInformation != null) {
                            modal.compose(
                                    Placeholder.of(ItemProvider.still(
                                            Items.builder(Material.OAK_SIGN)
                                                    .hideAllFlags()
                                                    .name("<green>Info")
                                                    .lore(extraInformation)
                                                    .build()
                                    )).inside(Slot.ROW_TWO_SLOT_FIVE)
                            );
                        }
                    });
        }
    }
}
