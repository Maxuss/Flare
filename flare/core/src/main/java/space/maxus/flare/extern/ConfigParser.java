package space.maxus.flare.extern;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValue;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.util.FlareUtil;

import java.util.Optional;

public abstract class ConfigParser {
    protected abstract Frame parseSingleConfigVersioned(String fileName, Config config);

    protected <E extends Enum<E>> @Nullable E parseEnum(String value) {
        Class<E> enumClass = FlareUtil.genericClass();
        try {
            return Enum.valueOf(enumClass, value.replace(" ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected <V extends ConfigValue> Optional<V> getOpt(Config cfg, String path) {
        try {
            ConfigValue value = cfg.getValue(path);
            return Optional.of((V) value);
        } catch (ConfigException.Missing | ClassCastException e) {
            return Optional.empty();
        }
    }
}
