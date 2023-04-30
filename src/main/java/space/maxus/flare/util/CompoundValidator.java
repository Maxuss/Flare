package space.maxus.flare.util;

import lombok.Data;

import java.util.List;

@Data
public class CompoundValidator implements Validator {
    private final List<Validator> passes;

    @Override
    public boolean isValid(String input) {
        return FlareUtil.reduceBoolStream(
                passes.stream().map(each -> each.isValid(input)),
                (a, b) -> a && b
        );
    }

    @Override
    public Validator and(Validator other) {
        // avoiding creating another CompoundValidator here
        this.passes.add(other);
        return this;
    }
}
