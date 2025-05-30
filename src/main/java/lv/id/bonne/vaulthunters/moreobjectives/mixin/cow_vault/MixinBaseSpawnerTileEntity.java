//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin.cow_vault;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.block.entity.BaseSpawnerTileEntity;
import lv.id.bonne.vaulthunters.moreobjectives.events.ExtraCommonEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BaseSpawnerTileEntity.class, remap = false)
public abstract class MixinBaseSpawnerTileEntity {
    @ModifyVariable(method = "spawnEntity", at = @At("STORE"))
    private static EntityType<?> changeEntityType(EntityType<?> entityType, @Local(argsOnly = true) ServerLevel serverLevel) {
        return ExtraCommonEvents.SPAWNER_ENTITY_CREATE.invoke(serverLevel, entityType).getEntityType();
    }
}
