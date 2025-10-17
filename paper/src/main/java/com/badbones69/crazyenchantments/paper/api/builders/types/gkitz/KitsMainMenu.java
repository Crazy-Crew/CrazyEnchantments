package com.badbones69.crazyenchantments.paper.api.builders.types.gkitz;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.builders.gui.types.StaticInventory;
import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.managers.configs.types.KitConfig;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public class KitsMainMenu extends StaticInventory {

    private final KitConfig config;

    public KitsMainMenu(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player, title, size);

        this.config = this.configManager.getKitConfig();
    }

    @Override
    public void open() {
        final Player player = getPlayer();

        if (!this.kitsManager.isRegistered()) {
            this.fusion.log("warn", "The kits main menu is not enabled, either because of an error, or g-kits are not enabled.");

            MessageKeys.GKIT_NOT_ENABLED.sendMessage(player);

            return;
        }

        final Gui gui = getGui();

//        final Player player = getPlayer();

        for (final Map.Entry<Integer, ItemBuilder> builder : this.config.getGuiCustomization().entrySet()) {
            final ItemBuilder itemBuilder = builder.getValue();
            final int slot = builder.getKey();

            gui.setItem(slot, new GuiItem(itemBuilder.build()));
        }

//        final CEPlayer cePlayer = this.crazyManager.getCEPlayer(player.getUniqueId());

        for (final GKitz kit : this.kitsManager.getKits()) {
//            final ItemStack displayItem = kit.getDisplayItem().clone();
//            final List<Component> lore = new ArrayList<>();
//            final GkitCoolDown gkitCooldown = !cePlayer.canUseGKit(kit) && cePlayer.hasGkitPermission(kit) ? cePlayer.getCoolDown(kit) : new GkitCoolDown();
//
//            final List<Component> currentLore = displayItem.lore();
//
//            if (currentLore != null) {
//                for (Component line : currentLore) {
//                    //String legacyLoreLine = ColorUtils.toLegacy(line); //todo() legacy trash
//                    //if (legacyLoreLine.toLowerCase().matches(".*%(day|hour|minute|second)%.*")) line = ColorUtils.legacyTranslateColourCodes(gkitCooldown.getCoolDownLeft(legacyLoreLine));
//                    //lore.add(line);
//                }
//            }
//
//            displayItem.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());

            gui.setItem(kit.getSlot() - 1, new GuiItem(kit.getDisplayItem(), event -> {
//                if (!(event.getInventory().getHolder(false) instanceof KitsMenu holder)) return;
//
//                event.setCancelled(true);
//
//                final Player player = holder.getPlayer();
//
//                final ItemStack itemStack = event.getCurrentItem();
//
//                if (itemStack == null || itemStack.isEmpty()) return;
//
//                final CEPlayer cePlayer = this.crazyManager.getCEPlayer(player.getUniqueId());
//
//                if (event.getClickedInventory() != holder.getInventoryView().getTopInventory()) return;
//
//                final PersistentDataContainerView container = itemStack.getPersistentDataContainer();
//
//                if (!container.has(DataKeys.gkit_type.getNamespacedKey())) return;
//
//                final String kitName = container.get(DataKeys.gkit_type.getNamespacedKey(), PersistentDataType.STRING);
//
//                if (kitName == null) return;
//
//                final GKitz kit = this.kitsManager.getKitByName(kitName);
//
//                if (event.getAction() == InventoryAction.PICKUP_HALF) {
//                    final int amountOfItems = kit.getPreviewItems().size() + 1; // Add 1 to account for the back button.
//                    final int slots = Math.min(((amountOfItems / 9) + (amountOfItems % 9 > 0 ? 1 : 0)) * 9, 54);
//
//                    MenuManager.openKitsPreviewMenu(player, slots, kit);
//
//                    return;
//                }
//
//                final Map<String, String> placeholders = new HashMap<>(1) {{
//                    put("%Kit%", kit.getName());
//                }};
//
//                if (cePlayer.hasGkitPermission(kit)) {
//                    if (cePlayer.canUseGKit(kit)) {
//                        cePlayer.giveGKit(kit);
//                        cePlayer.addCoolDown(kit);
//
//                        player.updateInventory();
//
//                        Messages.RECEIVED_GKIT.sendMessage(player, placeholders);
//                    } else {
//                        //player.sendMessage(ColorUtils.getPrefix() + cePlayer.getCoolDown(kit).getCoolDownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders))); //todo() legacy trash
//                    }
//                } else {
//                    Messages.NO_GKIT_PERMISSION.sendMessage(player, placeholders);
//                }
            }));
        }
    }
}