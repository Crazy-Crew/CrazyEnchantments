package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyItems;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.Server;

@Command(value = "crazyenchantments", alias = {"ce", "enchanter"})
public class BaseEnchantCommand {

    protected final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    protected final CrazyPlatform platform = this.plugin.getPlatform();

    protected final CrazyItems items = this.platform.getCrazyItems();

    protected final CrazyManager crazyManager = this.plugin.getCrazyManager();

    protected final ShopManager shopManager = this.crazyManager.getShopManager();

    protected final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    protected final FusionPaper fusion = this.platform.getFusion();

    protected final FileManager fileManager = this.platform.getFileManager();

    protected final Server server = this.plugin.getServer();

    protected final ProtectionCrystalSettings crystal = this.crazyManager.getProtectionCrystalSettings();

}