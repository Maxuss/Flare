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

@UtilityClass
public class Modals {
    @Builder
    public static final class YesNoModal implements ComposableLike {
        @Builder.Default
        private final String name = "Modal";
        @Builder.Default
        private final String description = "Opens a modal";
        @Builder.Default
        private final String declineName = "Decline";
        @Builder.Default
        private final String acceptName = "Accept";
        @Builder.Default
        private final Runnable onAccept = () -> {
        };
        @Builder.Default
        private final Runnable onDecline = () -> {
        };
        @Builder.Default
        private final @Nullable String extraInformation = null;

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
