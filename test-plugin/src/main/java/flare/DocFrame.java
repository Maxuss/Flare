package flare;

import org.bukkit.Material;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.compose.Button;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.frames.SimpleFrame;
import space.maxus.flare.ui.space.Slot;

public class DocFrame extends SimpleFrame {
    public DocFrame() {
        super(Dimensions.THREE_BY_NINE);
    }

    @Override
    public String getTitle() {
        return "<gradient:gold:black>Hello, <dark_red>%player_name%</dark_red>!";
    }

    @Override
    public void init() {
        this.compose(
                Placeholder
                        .of(ItemProvider.still(Items.empty()))
                        .inside(Slot.ALL)
        );
        var clickCount = useState(0);
        this.compose(
                Button
                        .builder(Reactive.item(clickCount, newCount ->
                            Items.builder(Material.BLAZE_POWDER)
                                    .name("<gold>I am a button!")
                                    .lore("<yellow>You clicked me <green>%s</green> times!".formatted(newCount))
                                    .hideAllFlags()
                                    .glint()
                                    .build()
                        ))
                        .onClickCancelling((btn, e) ->
                                clickCount.set(clickCount.get() + 1)
                        )
                        .build()
                        .bind(clickCount)
                        .inside(Slot.ROW_TWO_SLOT_FIVE)
        );
        this.compose(
                new DocFC(new DocFC.Props("Hello!", "World!"))
                        .inside(Slot.ROW_THREE_SLOT_FIVE)
        );
    }
}
