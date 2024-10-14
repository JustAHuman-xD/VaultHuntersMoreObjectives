//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.CakeObjective;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.time.modifier.ModifierExtension;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectivesMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.Configuration;
import lv.id.bonne.vaulthunters.moreobjectives.configs.FruitCakeSettings;
import lv.id.bonne.vaulthunters.moreobjectives.data.CakeDataForDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;


@Mixin(value = CakeObjective.class, remap = false)
public abstract class MixinCakeObjective
{
    @Shadow
    @Final
    public static FieldKey<BlockPos> CAKE_POS;


    @Inject(method = "initServer", at = @At("TAIL"))
    private void injectModifiers(VirtualWorld world, Vault vault, CallbackInfo ci)
    {
        if (vault.getOptional(Vault.LISTENERS).
            flatMap(listeners -> listeners.getOptional(Listeners.LOGIC)).
            map(logic -> logic.has(ClassicListenersLogic.ADDED_BONUS_TIME)).
            orElse(false))
        {
            return;
        }

        if (MoreObjectivesMod.CONFIGURATION.getFruitCakeSettings().getChance() > new Random().nextFloat())
        {
            MoreObjectivesMod.LOGGER.debug("Fruit Cake Vault Triggered.");

            CakeDataForDimension cakeDataForDimension = CakeDataForDimension.get(world);
            cakeDataForDimension.setFruitCake(true);
            cakeDataForDimension.setCakeType("");

            FruitCakeSettings cakeVault = MoreObjectivesMod.CONFIGURATION.getFruitCakeSettings();

            if (cakeVault != null)
            {
                vault.ifPresent(Vault.MODIFIERS, modifiers ->
                {
                    for (Configuration.ModifierCounter startModifier : cakeVault.getStartModifiers())
                    {
                        Optional<VaultModifier<?>> opt = VaultModifierRegistry.getOpt(startModifier.modifier());

                        opt.ifPresent(mod -> {
                            modifiers.addModifier(mod, startModifier.count(), true, ChunkRandom.any());
                            MoreObjectivesMod.LOGGER.debug("Add modifier: " +
                                mod.getDisplayName() +
                                " x " + startModifier.count() +
                                " to fruit cake vault.");
                        });
                    }
                });
            }
        }
    }


    @ModifyArg(method = "addModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextComponent;<init>(Ljava/lang/String;)V"))
    private String aa(String value, @Local VirtualWorld world)
    {
        if (!value.equals("cake"))
        {
            return value;
        }

        CakeDataForDimension cakeDataForDimension = CakeDataForDimension.get(world);

        if (cakeDataForDimension.isFruitCake() &&
            !cakeDataForDimension.getLastCakeType().isBlank())
        {
            // replace text with fruit + cake
            return cakeDataForDimension.getLastCakeType() + " cake";
        }
        else
        {
            return value;
        }
    }


    @Inject(method = "addModifier", at = @At("TAIL"))
    private void injectTimeAdder(VirtualWorld world,
        Vault vault,
        Player player,
        RandomSource random,
        CallbackInfo ci)
    {
        CakeDataForDimension cakeDataForDimension = CakeDataForDimension.get(world);

        if (cakeDataForDimension.isFruitCake())
        {
            String value = cakeDataForDimension.getLastCakeType();

            MoreObjectivesMod.CONFIGURATION.getFruitCakeSettings().getFruits().stream().
                filter(fruit -> fruit.getName().equals(value)).
                findAny().
                ifPresent(fruit ->
                    vault.ifPresent(Vault.CLOCK,
                        clock -> clock.addModifier(new ModifierExtension(fruit.getIncrement()))));
        }
    }


    @Inject(method = "generateCake", at = @At(value = "TAIL"))
    private void injectItemSpawning(VirtualWorld world, RegionPos region, RandomSource random, CallbackInfo ci)
    {
        CakeDataForDimension cakeDataForDimension = CakeDataForDimension.get(world);

        if (cakeDataForDimension.isFruitCake())
        {
            int memberCount = ServerVaults.get(world).
                flatMap(vault -> vault.getOptional(Vault.STATS)).
                map(stats -> stats.getMap().size()).
                orElse(0);

            Map.Entry<Float, FruitCakeSettings.Fruit> floatFruitEntry =
                MoreObjectivesMod.CONFIGURATION.getFruitCakeSettings().getFruitChances(memberCount).
                    higherEntry(random.nextFloat());

            if (floatFruitEntry != null)
            {
                cakeDataForDimension.setCakeType(floatFruitEntry.getValue().getName());
                MoreObjectivesMod.LOGGER.debug("Next cake " + floatFruitEntry.getValue().getName());
            }
            else
            {
                MoreObjectivesMod.LOGGER.debug("Failed to get next cake. Check your fruit list.");
            }

            String value = floatFruitEntry != null ? floatFruitEntry.getValue().getName() : "";

            MoreObjectivesMod.CONFIGURATION.getFruitCakeSettings().getFruits().stream().
                filter(fruit -> fruit.getName().equals(value)).
                findAny().
                ifPresent(fruit ->
                {
                    Item item = ForgeRegistries.ITEMS.getValue(fruit.getIcon());

                    if (item != null)
                    {
                        BlockPos blockPos = ((CakeObjective) (Object) this).get(CAKE_POS);

                        if (blockPos != null)
                        {
                            this.spawnItemParticles(world, blockPos, new ItemStack(item, 1));
                        }
                    }
                });
        }
        else
        {
            MoreObjectivesMod.LOGGER.debug("Not a fruit cake!");
        }
    }


    @Inject(method = "doEatingEffects", at = @At("TAIL"))
    private void injectItemRemoving(VirtualWorld world, BlockPos pos, CallbackInfo ci)
    {
        List<ItemEntity> itemStacks = world.getEntitiesOfClass(ItemEntity.class,
            AABB.ofSize(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 1, 2, 1),
            item -> item.getTags().contains("PreventMagnetMovement") && item.isNoGravity() && item.isInvulnerable());

        if (itemStacks.size() == 1)
        {
            itemStacks.get(0).discard();
        }
    }


    @Unique
    public void spawnItemParticles(ServerLevel world, BlockPos blockPos, ItemStack itemStack)
    {
        ItemEntity displayEntity =
            new ItemEntity(world, blockPos.getX() + 0.5, blockPos.getY() + 0.6, blockPos.getZ() + 0.5, itemStack);

        // Set it to never despawn and no pickup delay (players can't pick it up)
        displayEntity.setNeverPickUp();
        displayEntity.setUnlimitedLifetime();

        displayEntity.setNoGravity(true); // No gravity (make it float)
        displayEntity.setInvulnerable(true); // Make sure the entity can't be destroyed

        displayEntity.addTag("PreventMagnetMovement");
        displayEntity.getPersistentData().putBoolean("PreventRemoteMovement", true);

        // Optionally, disable collision
        displayEntity.setDeltaMovement(0, 0, 0); // No movement

        // Add the item entity to the world
        world.addFreshEntity(displayEntity);
    }
}
