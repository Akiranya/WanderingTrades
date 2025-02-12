package xyz.jpenilla.wanderingtrades.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Components;

@DefaultQualifier(NonNull.class)
public final class ListTradeConfigsInterface extends BaseInterface {
    public ListTradeConfigsInterface(final WanderingTrades plugin) {
        super(plugin);
    }

    @Override
    protected ChestInterface buildInterface() {
        final int rows = 4;
        return ChestInterface.builder()
            .rows(rows)
            .title(Messages.GUI_TC_LIST_TITLE.asComponent())
            .addTransform(this.parts.fillBottomRow())
            .addTransform((pane, view) -> pane.element(this.newConfigElement(), 4, pane.rows() - 1))
            .addTransform(this.parts.closeButton())
            .addReactiveTransform(this.parts.paginationTransform(rows, this::listElements))
            .build();
    }

    private List<ItemStackElement<ChestPane>> listElements() {
        return this.plugin.configManager().tradeConfigs().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .map(this::configElement)
            .toList();
    }

    private ItemStackElement<ChestPane> configElement(final TradeConfig tradeConfig) {
        final Set<String> tradeKeys = new HashSet<>(tradeConfig.tradesByName().keySet());
        final List<Component> finalLores = new ArrayList<>();
        tradeKeys.stream()
            .sorted()
            .limit(10)
            .forEach(key -> finalLores.add(Component.text("  " + key, NamedTextColor.GRAY)));
        if (finalLores.size() == 10) {
            finalLores.add(Messages.GUI_TC_LIST_AND_MORE.withPlaceholders(Components.placeholder("value", tradeKeys.size() - 10)));
        }

        final ItemStack stack = ItemBuilder.create(Material.PAPER)
            .customName(Component.text(tradeConfig.configName()))
            .lore(finalLores)
            .build();

        return ItemStackElement.of(stack, context -> new ListTradesInterface(
            this.plugin,
            this.plugin.configManager().tradeConfigs().get(tradeConfig.configName())
        ).replaceActiveScreen(context));
    }

    private ItemStackElement<ChestPane> newConfigElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.WRITABLE_BOOK)
                .customName(Messages.GUI_TC_LIST_ADD_CONFIG)
                .lore(Messages.GUI_TC_LIST_ADD_CONFIG_LORE)
                .build(),
            this::newConfigClick
        );
    }

    private void newConfigClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().send(player, Messages.MESSAGE_CREATE_CONFIG_PROMPT);
                return "";
            })
            .onValidateInput((player, input) -> {
                if (input.contains(" ")) {
                    this.plugin.chat().send(player, Messages.MESSAGE_NO_SPACES);
                    return false;
                }
                if (TextUtil.containsCaseInsensitive(input, List.copyOf(this.plugin.configManager().tradeConfigs().keySet()))) {
                    this.plugin.chat().send(player, Messages.MESSAGE_CREATE_UNIQUE);
                    return false;
                }
                return true;
            })
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                try {
                    Files.copy(
                        Objects.requireNonNull(this.plugin.getResource("trades/blank.yml")),
                        new File(String.format("%s/trades/%s.yml", this.plugin.getDataFolder(), s)).toPath()
                    );

                    this.plugin.configManager().reload();
                    this.plugin.chat().send(player, Messages.MESSAGE_CREATE_CONFIG_SUCCESS);
                } catch (final IOException ex) {
                    ex.printStackTrace();
                    this.plugin.chat().sendParsed(player, "<red>Error");
                }
                this.open(player);
            })
            .onDenied((player, s) -> {
                this.plugin.chat().send(player, Messages.MESSAGE_CREATE_CONFIG_CANCEL);
                this.open(player);
            })
            .start(context.viewer().player());
    }
}
