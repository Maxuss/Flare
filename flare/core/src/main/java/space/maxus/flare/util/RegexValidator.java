package space.maxus.flare.util;

import lombok.Data;

import java.util.regex.Pattern;

/**
 * A {@link Validator} that validates based on provided regex
 */
@Data
final class RegexValidator implements Validator {
    private final Pattern regex;

    @Override
    public boolean isValid(String input) {
        return regex.matcher(input).matches();
    }
}
