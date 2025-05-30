//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin.cow_vault;

import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.world.storage.VirtualWorld;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectives;
import lv.id.bonne.vaulthunters.moreobjectives.configs.Configs;
import lv.id.bonne.vaulthunters.moreobjectives.configs.data.ModifierCounter;
import lv.id.bonne.vaulthunters.moreobjectives.core.vault.CowMobLogic;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(value = WorldManager.class, remap = false)
public abstract class MixinWorldManager {
    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/WorldManager;ifPresent(Liskallia/vault/core/data/key/FieldKey;Ljava/util/function/Consumer;)V", ordinal = 3), remap = false)
    private void injectInitServer(VirtualWorld world, Vault vault, CallbackInfo ci) {
        if (!Configs.COW_VAULT.themeMatches(cast().get(WorldManager.THEME))) {
            return;
        } else if (!Configs.COW_VAULT.objectiveMatches(vault.getOptional(Vault.OBJECTIVES).flatMap(objectives -> objectives.getOptional(Objectives.KEY)).orElse(null))) {
            return;
        }

        vault.ifPresent(Vault.MODIFIERS, modifiers -> {
            List<VaultModifier<?>> modifierList = modifiers.getModifiers();
            Map<ResourceLocation, Integer> requirements = Configs.COW_VAULT.getTrigger();
            for (VaultModifier<?> modifier : modifierList) {
                requirements.computeIfPresent(modifier.getId(), (id, count) -> --count <= 0 ? null : count);
            }

            if (requirements.isEmpty()) {
                MoreObjectives.LOGGER.debug("Cow Vault Triggered. Replace Mobs with cows.");
                cast().setIfPresent(WorldManager.MOB_LOGIC, new CowMobLogic());
                for (ModifierCounter extra : Configs.COW_VAULT.getModifiers()) {
                    modifiers.addModifier(extra.modifier(), extra.count(), false, ChunkRandom.ofInternal(vault.get(Vault.SEED)));
                    MoreObjectives.LOGGER.debug("Adding extra modifier: {}", extra.modifier());
                }
            } else {
                MoreObjectives.LOGGER.debug("Failed to trigger Cow vault. Missing:");
                for (Map.Entry<ResourceLocation, Integer> entry : requirements.entrySet()) {
                    MoreObjectives.LOGGER.debug(" - {} -> {}", entry.getKey(), entry.getValue());
                }
            }
        });
    }

    @Unique
    private WorldManager cast() {
        return (WorldManager) (Object) this;
    }
}
