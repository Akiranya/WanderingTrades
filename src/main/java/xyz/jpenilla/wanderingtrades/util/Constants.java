package xyz.jpenilla.wanderingtrades.util;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextColor.color;

public final class Constants {
    private Constants() {
    }

    public static final NamespacedKey CONFIG_NAME = new NamespacedKey(WanderingTrades.getInstance(), "wtConfig");
    public static final NamespacedKey PROTECT = new NamespacedKey(WanderingTrades.getInstance(), "wtProtect");
    public static final NamespacedKey REFRESH_NATURAL = new NamespacedKey(WanderingTrades.getInstance(), "wtRefreshNatural");
    public static final NamespacedKey LAST_REFRESH = new NamespacedKey(WanderingTrades.getInstance(), "wt_last_refresh_time");

    public static final Component PREFIX_COMPONENT = text()
                    .append(text("[", WHITE))
                    .append(text("W", color(0x6B0BDE)))
                    .append(text("T", color(0xBA0DFA)))
                    .append(text("]", WHITE))
                    .append(space())
                    .clickEvent(runCommand("/wanderingtrades help"))
                    .build();

    public static final class Permissions {
        private Permissions() {
        }

        public static final String WANDERINGTRADES_HEADAVAILABLE = "wanderingtrades.headavailable";
    }
}
