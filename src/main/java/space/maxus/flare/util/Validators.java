package space.maxus.flare.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.math.NumberUtils;

@UtilityClass
public final class Validators {
    public final Validator INTEGER = NumberUtils::isParsable;
    public static Validator HEX = NumberUtils::isCreatable;
}
