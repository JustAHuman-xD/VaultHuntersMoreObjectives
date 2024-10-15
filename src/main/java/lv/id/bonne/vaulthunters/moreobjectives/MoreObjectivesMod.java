package lv.id.bonne.vaulthunters.moreobjectives;


import com.mojang.logging.LogUtils;

import lv.id.bonne.vaulthunters.moreobjectives.commands.ChanceCommand;
import lv.id.bonne.vaulthunters.moreobjectives.commands.ReloadCommand;
import lv.id.bonne.vaulthunters.moreobjectives.configs.Configuration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.slf4j.Logger;


@Mod(MoreObjectivesMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MoreObjectivesMod
{
    public MoreObjectivesMod()
    {
        MinecraftForge.EVENT_BUS.register(this);
        CONFIGURATION = new Configuration().readConfig();
    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents
    {
        /**
         * Registers the mod's commands
         * @param event The event holding the command dispatcher
         */
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event)
        {
            ChanceCommand.register(event.getDispatcher());
            ReloadCommand.register(event.getDispatcher());
        }
    }


    public static ResourceLocation of(String name)
    {
        return new ResourceLocation(MODID, name);
    }


    public static final String MODID = "moreobjectives";


    public static Configuration CONFIGURATION;

    public static final Logger LOGGER = LogUtils.getLogger();
}
