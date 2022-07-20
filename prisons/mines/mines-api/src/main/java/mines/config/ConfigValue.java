package mines.config;

import lombok.Getter;
import me.lucko.helper.text3.Text;
import mines.Mines;

public class ConfigValue {
    protected final Mines instance;
    private final String regionCommandUsageMessage;
    private final String regionRegisteredCommandMessage;
    private final String blockNotFoundMessage;
    private final String worldGuardRegionNotFoundMessage;
    public static ConfigValue valueInstance;

    public ConfigValue() {
        this.instance = Mines.getPlugin(Mines.class);
        this.regionCommandUsageMessage = instance.getConfig().getString("Messages.regionCommandUsage");
        this.regionRegisteredCommandMessage = instance.getConfig().getString("Messages.regionRegisteredCommand");
        this.blockNotFoundMessage = instance.getConfig().getString("Messages.blockNotFound");
        this.worldGuardRegionNotFoundMessage = instance.getConfig().getString("Messages.worldGuardRegionNotFound");
    }

    public static ConfigValue get() {
        if (ConfigValue.valueInstance == null) {
            ConfigValue.valueInstance = new ConfigValue();
        }

        return ConfigValue.valueInstance;
    }

    public String getRegionCommandUsageMessage() {
        return Text.colorize(regionCommandUsageMessage);
    }

    public String getRegionRegisteredCommandMessage() {
        return Text.colorize(regionRegisteredCommandMessage);
    }

    public String getBlockNotFoundMessage() {
        return Text.colorize(blockNotFoundMessage);
    }

    public String getWorldGuardRegionNotFoundMessage() {
        return Text.colorize(worldGuardRegionNotFoundMessage);
    }
}
