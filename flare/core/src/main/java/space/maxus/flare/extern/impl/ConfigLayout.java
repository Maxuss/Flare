package space.maxus.flare.extern.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;

import java.lang.invoke.MethodHandle;
import java.util.List;

@RequiredArgsConstructor @Getter
public class ConfigLayout {
    protected ConfigMetadata metadata;
    protected Dimensions dimensions;
    protected String title;
    protected MethodHandle genericClick;
    protected MethodHandle leftClick;
    protected MethodHandle rightClick;
    protected MethodHandle shiftFrom;
    protected MethodHandle shiftInside;

    protected List<PackedComposable> composed;

    public Frame build() {
        return new ConfigFrame(this);
    }
}
