package space.maxus.flare.extern.impl;

import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.Experimental
public class ConfigParserExperimental extends ConfigParserV1 {
    @Getter
    private static final ConfigParserExperimental instance = new ConfigParserExperimental();
}
