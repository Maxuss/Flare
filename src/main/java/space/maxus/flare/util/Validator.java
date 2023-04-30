package space.maxus.flare.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@FunctionalInterface
public interface Validator {
    boolean isValid(String input);

    default Validator and(Validator other) {
        List<Validator> passes = new ArrayList<>();
        passes.add(this);
        passes.add(other);
        return new CompoundValidator(passes);
    }

    default Validator andMatching(@RegEx String regex) {
        return and(Validator.matching(regex));
    }

    default Validator andMatching(Pattern pattern) {
        return and(Validator.matching(pattern));
    }

    @Contract("_ -> new")
    static @NotNull Validator matching(@RegEx String regex) {
        return new RegexValidator(Pattern.compile(regex));
    }

    @Contract("_ -> new")
    static @NotNull Validator matching(Pattern compiled) {
        return new RegexValidator(compiled);
    }

    static @NotNull Validator of(@NotNull Checker checker) {
        return checker::valid;
    }

    @FunctionalInterface
    interface Checker {
        boolean valid(String input);
    }
}