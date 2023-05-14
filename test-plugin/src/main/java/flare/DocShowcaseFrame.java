package flare;

import space.maxus.flare.item.Items;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.compose.complex.Modals;
import space.maxus.flare.ui.frames.SimpleFrame;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

public class DocShowcaseFrame extends SimpleFrame {
    public DocShowcaseFrame() {
        super(Dimensions.SIX_BY_NINE);
    }

    @Override
    public void init() {
        compose(Placeholder.of(Items.empty()).inside(Slot.ALL));

        compose(
                Modals.YesNoModal.builder()
                        .name("Yes/No Modal")
                        .onAccept(() -> getViewer().sendMessage(FlareUtil.text("<gold>Accepted!")))
                        .onDecline(() -> getViewer().sendMessage(FlareUtil.text("<gold>Declined!")))
                        .declineName("Decline This")
                        .acceptName("Accept This")
                        .extraInformation("Click button to the right to accept, or click the button on the left to decline.")
                        .description("Try testing yes/no stuff")
                        .build()
                        .inside(Slot.ROW_TWO_SLOT_FIVE)
        );
    }
}
