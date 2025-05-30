//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin.fruit_cake;

import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lv.id.bonne.vaulthunters.moreobjectives.data.CakeWorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin adds `Fruit` to cake enter and exit messages & hides modifiers if the cake vault has fruit mode.
 */
@Mixin(value = ClassicListenersLogic.class, remap = false)
public class MixinClassicListenersLogic {
    @Redirect(method = "printJoinMessage", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/Objectives;get(Liskallia/vault/core/data/key/FieldKey;)Ljava/lang/Object;"))
    private Object replaceEnterCakeText(Objectives instance, FieldKey<String> fieldKey, VirtualWorld world) {
        return this.moreObjectives$appendCakeText(instance, fieldKey, world);
    }

    @Redirect(method = "lambda$initServer$1", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/Objectives;get(Liskallia/vault/core/data/key/FieldKey;)Ljava/lang/Object;"))
    private Object replaceExitCakeText(Objectives instance, FieldKey<String> fieldKey, Vault vault) {
        return this.moreObjectives$appendCakeText(instance, fieldKey, ServerVaults.getWorld(vault).orElse(null));
    }

    @Redirect(method = "printJoinMessage", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectIterator;hasNext()Z", ordinal = 0))
    private boolean hasNext(ObjectIterator<Object2IntMap.Entry<VaultModifier<?>>> instance, VirtualWorld world, Vault vault) {
        if (!instance.hasNext()) {
            return false;
        } else if (!"cake".equals(vault.get(Vault.OBJECTIVES).get(Objectives.KEY)) || !CakeWorldData.isFruitCake(world)) {
            return instance.hasNext();
        }
        return false;
    }

    @Unique
    private String moreObjectives$appendCakeText(Objectives instance, FieldKey<String> fieldKey, VirtualWorld world) {
        String value = instance.get(fieldKey);
        if (!"cake".equals(value) || world == null) {
            return value;
        }
        return CakeWorldData.isFruitCake(world) ? "Fruit Cake" : value;
    }
}
