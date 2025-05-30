package lv.id.bonne.vaulthunters.moreobjectives.mixin;

import iskallia.vault.init.ModConfigs;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectives;
import lv.id.bonne.vaulthunters.moreobjectives.configs.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModConfigs.class, remap = false)
public class MixinModConfigs {
    @Inject(method = "register", at = @At("TAIL"))
    private static void injectConfigs(CallbackInfo ci) {
        Configs.registerConfigs();
        MoreObjectives.LOGGER.info("Successfully loaded custom Vault Configs.");
    }
}
