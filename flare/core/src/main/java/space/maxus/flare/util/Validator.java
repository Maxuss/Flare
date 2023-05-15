package space.maxus.flare.util;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An interface that is used to validate text input
 */
@FunctionalInterface
public interface Validator {
    /**
     * Constructs a validator that matches based on a Regular Expression
     * @param regex Regex to match against
     * @return A new validator that matches based on the regex
     */
    @Contract("_ -> new")
    static @NotNull Validator matching(@Language("RegExp") @RegEx String regex) {
        return new RegexValidator(Pattern.compile(regex));
    }

    /**
     * Constructs a validator that matches based on a compiled Regular Expression
     * @param compiled Compiled regex to match against
     * @return A new validator that matches based on the regex
     */
    @Contract("_ -> new")
    static @NotNull Validator matching(Pattern compiled) {
        return new RegexValidator(compiled);
    }

    /**
     * Constructs a validator based on a manual checker
     * @param checker Lambda checker to use
     * @return A new validator
     */
    static @NotNull Validator of(@NotNull Checker checker) {
        return checker::valid;
    }

    /**
     * Ensures that input is valid
     * @param input Input to be checked
     * @return True if input is valid, false otherwise
     */
    boolean isValid(String input);

    /**
     * Combines two validators into one, that ensures that both output true
     * @param other Other validator to be added
     * @return A new compound validator
     */
    default Validator and(Validator other) {
        List<Validator> passes = new ArrayList<>();
        passes.add(this);
        passes.add(other);
        return new CompoundValidator(passes);
    }

    /**
     * Combines this validator with a new regex-based validator
     * @param regex Regex for new validator to match against
     * @return A new compound validator
     */
    default Validator andMatching(@RegEx @Language("RegExp") String regex) {
        return and(Validator.matching(regex));
    }


    /**
     * Combines this validator with a new regex-based validator
     * @param pattern Compiled regex for new validator to match against
     * @return A new compound validator
     */
    default Validator andMatching(Pattern pattern) {
        return and(Validator.matching(pattern));
    }

    /**
     * A functional interface that is used to check if a string is valid
     */
    @FunctionalInterface
    interface Checker {
        /**
         * Checks if a string is valid
         * @param input String to check
         * @return True if the string is valid, false otherwise
         */
        boolean valid(String input);
    }
}