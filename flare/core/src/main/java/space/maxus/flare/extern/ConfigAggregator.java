package space.maxus.flare.extern;

import com.typesafe.config.Config;
import space.maxus.flare.Flare;
import space.maxus.flare.extern.impl.ConfigParserExperimental;
import space.maxus.flare.extern.impl.ConfigParserV1;
import space.maxus.flare.ui.Frame;

import java.nio.file.Path;

public class ConfigAggregator {
    public void prepareDefaultConfigs() {
        Path basePath = Flare.getInstance().getDataFolder().toPath();
        if(basePath.resolve("frames").toFile().exists())
            return;
        Flare.getInstance().saveResource("frames/example.conf", false);
    }

    public Frame parseSingleConfig(String fileName, Config config) {
        String version = config.getString("version");
        return switch (version) {
            case "1" -> ConfigParserV1.getInstance().parseSingleConfigVersioned(fileName, config);
            case "experimental" -> ConfigParserExperimental.getInstance().parseSingleConfigVersioned(fileName, config);
            default -> throw new IllegalArgumentException("Unsupported config version: " + version);
        };
    }
}
