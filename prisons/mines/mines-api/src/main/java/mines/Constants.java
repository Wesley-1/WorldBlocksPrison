package mines;

import me.lucko.helper.bossbar.*;
import org.bukkit.Bukkit;

public class Constants {
    public static final BossBar progressBossBar = new BukkitBossBarFactory(Bukkit.getServer()).newBossBar().color(BossBarColor.PINK).style(BossBarStyle.SOLID);
}
