package space.maxus.flare.ui.page;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.frames.ParamFrame;

import java.util.function.Consumer;
@ToString @EqualsAndHashCode(callSuper = true)
public class PageFrame extends ParamFrame<PageFrame.Props> {
    protected PageFrame(@NotNull Props params) {
        super(params);
    }

    @Override
    public void init() {
        // initializer should be called lazily, on-demand, and not eagerly in `init`
    }

    void load() {
        this.props.initializer.accept(this);
    }

    record Props(int page, Consumer<PageFrame> initializer) { }
}
