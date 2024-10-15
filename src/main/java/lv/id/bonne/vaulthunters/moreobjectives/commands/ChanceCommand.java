//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectivesMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;


public class ChanceCommand
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

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("setCakeFruitChance").
            then(Commands.argument("chance", FloatArgumentType.floatArg(0f, 1f)).
                executes(ctx ->
                {
                    try
                    {
                        float value = FloatArgumentType.getFloat(ctx, "chance");
                        MoreObjectivesMod.CONFIGURATION.getFruitCakeSettings().setChance(value);
                        MoreObjectivesMod.CONFIGURATION.writeConfig();
                        ctx.getSource().sendSuccess(new TextComponent("Updated cake chance to " + value), true);
                        return 1;
                    }
                    catch (Exception e)
                    {
                        ctx.getSource().sendFailure(new TextComponent("Failed to save changes: " + e.getMessage()));
                        return 0;
                    }
                })
            );

        dispatcher.register(baseLiteral.then(command));
    }
}
