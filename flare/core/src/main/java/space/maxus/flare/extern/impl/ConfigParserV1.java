package space.maxus.flare.extern.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.extern.ConfigParser;
import space.maxus.flare.ui.Frame;

@ApiStatus.Internal
public class ConfigParserV1 extends ConfigParser {
    @Getter
    private static final ConfigParserV1 instance = new ConfigParserV1();

    @Override
    public Frame parseSingleConfigVersioned(String fileName, Config config) {
        ConfigMetadata meta = parseMetadata(fileName, config);
        ConfigLayout layout = parseLayout(meta, config);
        return new ConfigFrame(layout);
    }

    protected @Nullable ConfigLayout parseLayout(ConfigMetadata meta, Config config) {
        // TODO: finish layout parsing
        return null;
    }

    protected ConfigMetadata parseMetadata(String fileName, Config config) {
        return this.<ConfigObject>getOpt(config, "meta").map(meta -> {
            Config metadata = meta.toConfig();
            String handlerPackage = this.getOpt(metadata, "handler package").map(it -> (String) it.unwrapped()).orElse("");
            String id = this.getOpt(metadata, "id").map(it -> (String) it.unwrapped()).orElse(fileName);
            return new ConfigMetadata(id, handlerPackage);
        }).orElseGet(() -> new ConfigMetadata(fileName, ""));
    }
}
