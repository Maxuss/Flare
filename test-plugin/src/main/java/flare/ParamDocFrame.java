package flare;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.Items;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.compose.Placeholder;
import space.maxus.flare.ui.frames.ParamFrame;
import space.maxus.flare.ui.space.Slot;

public class ParamDocFrame extends ParamFrame<ParamDocFrame.MyProps> {

    protected ParamDocFrame(@NotNull MyProps params) {
        super(params, Dimensions.THREE_BY_NINE);
    }

    @Override
    public String getTitle() {
        return "Hello, %s!".formatted(props.name);
    }

    @Override
    public void init() {
        this.compose(
                Placeholder
                        .of(
                                Items.builder(Material.BLAZE_ROD)
                                        .name("<gold>%s".formatted(props.message))
                        )
                        .inside(Slot.ALL)
        );
    }

    public record MyProps(String name, String message) { }
}
