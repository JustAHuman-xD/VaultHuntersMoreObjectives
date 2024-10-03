//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectivesMod;
import lv.id.bonne.vaulthunters.moreobjectives.logic.CowMobLogic;
import net.minecraft.resources.ResourceLocation;


/**
 * This mixin is used to mark vault as cow vault.
 */
@Mixin(value = WorldManager.class, remap = false)
public abstract class MixinCowVault
{
    /**
     * Injects if vault is cow vault.
     * @param world Virtual world.
     * @param ci Callback info.
     */
    @Inject(method = "initServer", at = @At(value = "INVOKE",
        target = "Liskallia/vault/core/vault/WorldManager;ifPresent(Liskallia/vault/core/data/key/FieldKey;Ljava/util/function/Consumer;)V",
        ordinal = 3),
        remap = false)
    private void injectInitServer(VirtualWorld world, Vault vault, CallbackInfo ci)
    {
        // Check the theme section. Only in chaos vaults.
        if (!((WorldManager) (Object) this).getOptional(WorldManager.THEME).
            orElse(VaultMod.id("empty")).
            equals(VaultMod.id("classic_vault_chaos")))
        {
            // Only on chaos vaults.
            return;
        }

        // Now count modifiers.
        vault.ifPresent(Vault.MODIFIERS, modifiers ->
        {
            List<VaultModifier<?>> modifierList = modifiers.getModifiers();

            Map<ResourceLocation, AtomicInteger> requiredModifiers =
                MoreObjectivesMod.CONFIGURATION.getCowVaultsRequirements();

            // Count trigger modifiers.
            for (Iterator<VaultModifier<?>> iterator = modifierList.iterator();
                iterator.hasNext() && !requiredModifiers.isEmpty(); )
            {
                VaultModifier<?> modifier = iterator.next();

                if (requiredModifiers.containsKey(modifier.getId()))
                {
                    AtomicInteger atomicInteger = requiredModifiers.get(modifier.getId());

                    if (atomicInteger.decrementAndGet() == 0)
                    {
                        requiredModifiers.remove(modifier.getId());
                    }
                }
            }

            // Mark vault as cow vault
            if (requiredModifiers.isEmpty())
            {
                // Change to CowMobLogic
                ((WorldManager) (Object) this).
                    setIfPresent(WorldManager.MOB_LOGIC, new CowMobLogic());
            }
        });
    }
}
