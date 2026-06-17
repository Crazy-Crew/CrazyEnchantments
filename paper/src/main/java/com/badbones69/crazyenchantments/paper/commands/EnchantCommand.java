package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

@Command(value = "crazyenchantments", alias = {"ce", "enchanter"})
public class EnchantCommand {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final CrazyPlatform platform = this.plugin.getPlatform();

    protected final PaperFileManager fileManager = this.platform.getFileManager();

    protected final Starter starter = this.plugin.getStarter();

    protected final ScramblerListener scrambler = this.starter.getScramblerListener();

    protected final ProtectionCrystalSettings crystal = this.starter.getProtectionCrystalSettings();

    protected final SlotCrystalListener slotCrystal = this.starter.getSlotCrystalListener();

    protected final Methods methods = this.starter.getMethods();

    protected final EnchantmentBookSettings bookSettings = this.starter.getEnchantmentBookSettings();

    protected final Server server = this.plugin.getServer();

    protected final CrazyManager crazyManager = this.starter.getCrazyManager();

}