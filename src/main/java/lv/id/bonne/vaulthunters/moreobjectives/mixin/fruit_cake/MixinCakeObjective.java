//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin.fruit_cake;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.CakeObjective;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.core.vault.time.modifier.ModifierExtension;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectives;
import lv.id.bonne.vaulthunters.moreobjectives.configs.Configs;
import lv.id.bonne.vaulthunters.moreobjectives.configs.data.Fruit;
import lv.id.bonne.vaulthunters.moreobjectives.configs.data.ModifierCounter;
import lv.id.bonne.vaulthunters.moreobjectives.data.CakeWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(value = CakeObjective.class, remap = false)
public abstract class MixinCakeObjective {
    @Inject(method = "initServer", at = @At("TAIL"))
    private void injectModifiers(VirtualWorld world, Vault vault, CallbackInfo ci) {
        if (vault.getOptional(Vault.LISTENERS).flatMap(listeners -> listeners.getOptional(Listeners.LOGIC)).
                map(logic -> logic.has(ClassicListenersLogic.ADDED_BONUS_TIME)).orElse(false)) {
            return;
        }

        RandomSource random = ChunkRandom.ofInternal(vault.get(Vault.SEED));
        if (!(Configs.FRUIT_CAKE.getChance() > random.nextFloat())) {
            return;
        }

        MoreObjectives.LOGGER.debug("Fruit Cake Vault Triggered.");
        CakeWorldData cakeData = CakeWorldData.get(world);
        cakeData.setFruitCake(true);
        cakeData.setCakeType("");
        vault.ifPresent(Vault.MODIFIERS, modifiers -> {
            for (ModifierCounter startModifier : Configs.FRUIT_CAKE.getModifiers()) {
                if (startModifier.modifier() != null) {
                    modifiers.addModifier(startModifier.modifier(), startModifier.count(), true, random);
                    MoreObjectives.LOGGER.debug("Add modifier: {} x {} to fruit cake vault.", startModifier.modifier().getDisplayName(), startModifier.count());
                }
            }
        });
    }

    @ModifyArg(method = "addModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextComponent;<init>(Ljava/lang/String;)V"))
    private String fruitCakeModifierDisplay(String value, @Local(argsOnly = true) VirtualWorld world) {
        if (value.equals("cake")) {
            CakeWorldData cakeData = CakeWorldData.get(world);
            Fruit fruit = Configs.FRUIT_CAKE.getFruit(cakeData.getLastCakeType());
            return cakeData.isFruitCake() && fruit != null ? fruit.icon().getDefaultInstance().getDisplayName().getString() + " cake" : value;
        }
        return value;
    }

    @Inject(method = "addModifier", at = @At("TAIL"))
    private void injectTimeAdder(VirtualWorld world, Vault vault, Player player, RandomSource random, CallbackInfo ci) {
        CakeWorldData cakeData = CakeWorldData.get(world);
        if (!cakeData.isFruitCake()) {
            return;
        }

        Fruit fruit = Configs.FRUIT_CAKE.getFruit(cakeData.getLastCakeType());
        if (fruit != null) {
            vault.ifPresent(Vault.CLOCK, clock -> clock.addModifier(new ModifierExtension(fruit.ticks())));
        }
    }

    @Inject(method = "generateCake", at = @At(value = "TAIL"))
    private void injectItemSpawning(VirtualWorld world, RegionPos region, RandomSource random, CallbackInfo ci) {
        CakeWorldData cakeData = CakeWorldData.get(world);
        if (!cakeData.isFruitCake()) {
            MoreObjectives.LOGGER.debug("Not a fruit cake!");
            return;
        }

        int memberCount = ServerVaults.get(world).flatMap(vault -> vault.getOptional(Vault.STATS)).map(stats -> stats.getMap().size()).orElse(0);
        Map.Entry<Float, Fruit> floatFruitEntry = Configs.FRUIT_CAKE.getFruitChances(memberCount).higherEntry(random.nextFloat());

        if (floatFruitEntry != null) {
            Fruit fruit = floatFruitEntry.getValue();
            cakeData.setCakeType(fruit.id());
            this.moreObjectives$spawnItemParticles(world, ((CakeObjective) (Object) this).get(CakeObjective.CAKE_POS), new ItemStack(fruit.icon(), 1));
            MoreObjectives.LOGGER.debug("Next cake {}", cakeData.getLastCakeType());
        } else {
            cakeData.setCakeType("");
            MoreObjectives.LOGGER.debug("Failed to get next cake. Check your fruit list.");
        }
    }

    @Inject(method = "doEatingEffects", at = @At("TAIL"))
    private void injectItemRemoving(VirtualWorld world, BlockPos pos, CallbackInfo ci) {
        List<ItemEntity> itemStacks = world.getEntitiesOfClass(ItemEntity.class,
                AABB.ofSize(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 1, 2, 1),
                item -> item.getTags().contains("PreventMagnetMovement") && item.isNoGravity() && item.isInvulnerable());

        if (itemStacks.size() == 1) {
            itemStacks.get(0).discard();
        }
    }

    @Unique
    public void moreObjectives$spawnItemParticles(ServerLevel world, BlockPos pos, ItemStack itemStack) {
        ItemEntity displayEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, itemStack);
        displayEntity.setNeverPickUp();
        displayEntity.setUnlimitedLifetime();
        displayEntity.setNoGravity(true);
        displayEntity.setInvulnerable(true);
        displayEntity.addTag("PreventMagnetMovement");
        displayEntity.getPersistentData().putBoolean("PreventRemoteMovement", true);
        displayEntity.setDeltaMovement(0, 0, 0);
        world.addFreshEntity(displayEntity);
    }
}
