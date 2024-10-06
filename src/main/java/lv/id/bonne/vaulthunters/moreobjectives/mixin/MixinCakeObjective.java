//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin;


import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
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
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectivesMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.Configuration;
import lv.id.bonne.vaulthunters.moreobjectives.configs.FruitCakeSettings;
import lv.id.bonne.vaulthunters.moreobjectives.utils.ICakeObjectiveAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import static iskallia.vault.core.data.ICompound.DISK;


@Mixin(value = CakeObjective.class, remap = false)
public abstract class MixinCakeObjective implements ICakeObjectiveAccessor
{
    @Shadow
    @Final
    public static FieldRegistry FIELDS;

    @Unique
    private static FieldKey<String> CAKE_TYPE;

    @Unique
    private static FieldKey<String> LAST_CAKE_TYPE;


    @Override
    @Unique
    public FieldKey<String> getCakeType()
    {
        return CAKE_TYPE;
    }


    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void injectCustomField(CallbackInfo ci)
    {
        CAKE_TYPE = FieldKey.of("cake_type", String.class).
            with(Version.v1_0,
            Adapters.UTF_8, DISK.all()).
            register(FIELDS);
        LAST_CAKE_TYPE = FieldKey.of("last_cake_type", String.class).
            with(Version.v1_0,
                Adapters.UTF_8, DISK.all()).
            register(FIELDS);
    }


    @Inject(method = "<init>(Lnet/minecraft/resources/ResourceLocation;)V", at = @At("TAIL"))
    private void injectValue(CallbackInfo ci)
    {
        if (MoreObjectivesMod.CONFIGURATION.fruitCakeSettings.getChance() > new Random().nextFloat())
        {
            // First cake never gives extra time.
            ((CakeObjective) (Object) this).set(CAKE_TYPE, "null");
        }
    }


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

        if (((CakeObjective) (Object) this).has(CAKE_TYPE))
        {
            FruitCakeSettings cakeVault = MoreObjectivesMod.CONFIGURATION.fruitCakeSettings;

            if (cakeVault != null)
            {
                vault.ifPresent(Vault.MODIFIERS, modifiers ->
                {
                    for (Configuration.ModifierCounter startModifier : cakeVault.getStartModifiers())
                    {
                        Optional<VaultModifier<?>> opt = VaultModifierRegistry.getOpt(startModifier.modifier());

                        opt.ifPresent(mod -> {
                            modifiers.addModifier(mod, startModifier.count(), true, ChunkRandom.any());
                        });
                    }
                });
            }
        }
    }


    @ModifyArg(method = "addModifier", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/network/chat/TextComponent;<init>(Ljava/lang/String;)V", ordinal = 4))
    private String modifyCakeTextOne(String original)
    {
        Optional<String> optional = ((CakeObjective) (Object) this).getOptional(LAST_CAKE_TYPE);

        return optional.filter(s -> !s.equals("null")).map(s -> s + " " + original).orElse(original);
    }


    @ModifyArg(method = "addModifier", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/network/chat/TextComponent;<init>(Ljava/lang/String;)V", ordinal = 7))
    private String modifyCakeTextTwo(String original)
    {
        Optional<String> optional = ((CakeObjective) (Object) this).getOptional(LAST_CAKE_TYPE);

        return optional.filter(s -> !s.equals("null")).map(s -> s + " " + original).orElse(original);
    }


    @Inject(method = "addModifier", at = @At("TAIL"))
    private void injectTimeAdder(VirtualWorld world,
        Vault vault,
        Player player,
        RandomSource random,
        CallbackInfo ci)
    {
        if (((CakeObjective) (Object) this).has(LAST_CAKE_TYPE))
        {
            String value = ((CakeObjective) (Object) this).get(LAST_CAKE_TYPE);

            MoreObjectivesMod.CONFIGURATION.fruitCakeSettings.getFruits().stream().
                filter(fruit -> fruit.getName().equals(value)).
                findAny().
                ifPresent(fruit ->
                    vault.ifPresent(Vault.CLOCK,
                        clock -> clock.addModifier(new ModifierExtension(fruit.getIncrement()))));
        }
    }


    @Inject(method = "generateCake",
        at = @At(value = "INVOKE",
            target = "Liskallia/vault/core/world/storage/VirtualWorld;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectItemSpawning(VirtualWorld world,
        RegionPos region,
        RandomSource random,
        CallbackInfo ci,
        int minX,
        int minZ,
        BlockPos.MutableBlockPos pos,
        int i,
        int x,
        int z,
        int y)
    {
        if (((CakeObjective) (Object) this).has(CAKE_TYPE))
        {
            ((CakeObjective) (Object) this).set(LAST_CAKE_TYPE, ((CakeObjective) (Object) this).get(CAKE_TYPE));

            Map.Entry<Float, FruitCakeSettings.Fruit> floatFruitEntry =
                MoreObjectivesMod.CONFIGURATION.fruitCakeSettings.getFruitChances().higherEntry(random.nextFloat());

            if (floatFruitEntry != null)
            {
                ((CakeObjective) (Object) this).set(CAKE_TYPE, floatFruitEntry.getValue().getName());
            }

            String value = floatFruitEntry != null ? floatFruitEntry.getValue().getName() : "null";

            MoreObjectivesMod.CONFIGURATION.fruitCakeSettings.getFruits().stream().
                filter(fruit -> fruit.getName().equals(value)).
                findAny().
                ifPresent(fruit ->
                {
                    Item item = ForgeRegistries.ITEMS.getValue(fruit.getIcon());

                    if (item != null)
                    {
                        this.spawnItemParticles(world, pos, new ItemStack(item, 1));
                    }
                });
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
