package lv.id.bonne.vaulthunters.moreobjectives;


import com.mojang.logging.LogUtils;

import lv.id.bonne.vaulthunters.moreobjectives.configs.Configuration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

import org.slf4j.Logger;


@Mod(MoreObjectivesMod.MODID)
public class MoreObjectivesMod
{
    public MoreObjectivesMod()
    {
        CONFIGURATION = new Configuration().readConfig();
    }


    public static ResourceLocation of(String name)
    {
        return new ResourceLocation(MODID, name);
    }


    public static final String MODID = "moreobjectives";


    public static Configuration CONFIGURATION;

    public static final Logger LOGGER = LogUtils.getLogger();
}
