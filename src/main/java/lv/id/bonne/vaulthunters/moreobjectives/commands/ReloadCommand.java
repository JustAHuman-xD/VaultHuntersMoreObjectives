//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectivesMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.Configuration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;


public class ReloadCommand
{
    /**
     * Registers the command that toggles a pause for the vault.
     *
     * @param dispatcher The command dispatcher.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> baseLiteral = Commands.literal("moreobjectives").
            requires(stack -> stack.hasPermission(1));

        LiteralArgumentBuilder<CommandSourceStack> reload = Commands.literal("reload").
            executes(ctx ->
            {
                Configuration configuration = MoreObjectivesMod.CONFIGURATION.reloadConfig();

                if (configuration == null)
                {
                    ctx.getSource().sendFailure(new TextComponent("Failed to reload config."));
                    return 0;
                }

                MoreObjectivesMod.CONFIGURATION = configuration;
                ctx.getSource().sendSuccess(new TextComponent("Config file reloaded."), true);
                return 1;
            });

        LiteralArgumentBuilder<CommandSourceStack> reset = Commands.literal("reset").
            executes(ctx ->
            {
                MoreObjectivesMod.CONFIGURATION.generateConfig();
                ctx.getSource().sendSuccess(new TextComponent("Config file reset."), true);
                return 1;
            });

        dispatcher.register(baseLiteral.then(reset).then(reload));
    }
}
