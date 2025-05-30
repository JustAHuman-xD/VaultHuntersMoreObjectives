//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin.cow_vault;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.ispawner.world.spawner.SpawnerAction;
import lv.id.bonne.vaulthunters.moreobjectives.events.ExtraCommonEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = SpawnerAction.class, remap = false)
public class MixinSpawnerAction {
    @ModifyVariable(method = "applyEggOverride", at = @At(value = "STORE"), name = "type")
    private EntityType<?> modifyVar10000(EntityType<?> value, @Local(argsOnly = true) Level world) {
        return ExtraCommonEvents.SPAWNER_ENTITY_CREATE.invoke(world, value).getEntityType();
    }
}