package space.maxus.flare.util;

import lombok.Data;

import java.util.regex.Pattern;

@Data
final class RegexValidator implements Validator {
    private final Pattern regex;

    @Override
    public boolean isValid(String input) {
        return regex.matcher(input).matches();
    }
}
