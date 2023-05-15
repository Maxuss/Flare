package space.maxus.flare.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Contains several pre-made utility validators
 */
@UtilityClass
public final class Validators {
    /**
     * Validates that a string is a parsable integer
     */
    public final Validator INTEGER = NumberUtils::isParsable;
    /**
     * Validates that a string is a parsable hex code
     */
    public static Validator HEX = NumberUtils::isCreatable;
}
