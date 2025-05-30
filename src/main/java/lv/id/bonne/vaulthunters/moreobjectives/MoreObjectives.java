package lv.id.bonne.vaulthunters.moreobjectives;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MoreObjectives.MOD_ID)
public class MoreObjectives {
    public static final String MOD_ID = "moreobjectives";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MoreObjectives() {}

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
