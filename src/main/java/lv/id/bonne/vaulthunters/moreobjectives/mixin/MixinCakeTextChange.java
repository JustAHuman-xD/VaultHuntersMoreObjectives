//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lv.id.bonne.vaulthunters.moreobjectives.data.CakeDataForDimension;


/**
 * This mixin adds `Fruit` to cake enter and exit messages, if cake vault has fruit mode.
 */
@Mixin(value = ClassicListenersLogic.class, remap = false)
public class MixinCakeTextChange
{
    @Redirect(method = "printJoinMessage",
        at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/Objectives;get(Liskallia/vault/core/data/key/FieldKey;)Ljava/lang/Object;"))
    private Object replaceEnterCakeText(Objectives instance, FieldKey<String> fieldKey, VirtualWorld world)
    {
        return this.appendCakeText(instance, fieldKey, Optional.of(world));
    }


    @Redirect(method = "printJoinMessage",
        at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectIterator;hasNext()Z", ordinal = 0))
    private boolean hasNext(ObjectIterator<Object2IntMap.Entry<VaultModifier<?>>> instance, VirtualWorld world, Vault vault)
    {
        if (!instance.hasNext())
        {
            // Fast check and exit
            return false;
        }

        Objectives objectives = vault.get(Vault.OBJECTIVES);

        if (!objectives.get(Objectives.KEY).equals("cake"))
        {
            // Return default value as objective is not cake vault
            return instance.hasNext();
        }

        CakeDataForDimension cakeDataForDimension = CakeDataForDimension.get(world);

        if (cakeDataForDimension.isFruitCake())
        {
            // This is fruit cake vault. No modifiers displayed.
            return false;
        }

        // Return default value as it is not fruit cake.
        return instance.hasNext();
    }


    @Redirect(method = "lambda$initServer$1", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/Objectives;get(Liskallia/vault/core/data/key/FieldKey;)Ljava/lang/Object;"))
    private Object replaceExitCakeText(Objectives instance, FieldKey<String> fieldKey, Vault vault)
    {
        return this.appendCakeText(instance, fieldKey, ServerVaults.getWorld(vault));
    }


    /**
     * This method ckecks if objective is cake objective and if objective has custom field key
     * that triggers fruit cake vaults. Then it adds `Fruit` before `Cake.
     * @param instance Objectives instance.
     * @param fieldKey FieldKey.KEY
     * @return Objective name.
     */
    @Unique
    private String appendCakeText(Objectives instance, FieldKey<String> fieldKey, Optional<VirtualWorld> worldOptional)
    {
        if (!instance.get(fieldKey).equals("cake") || worldOptional.isEmpty())
        {
            return instance.get(fieldKey);
        }

        CakeDataForDimension cakeDataForDimension = CakeDataForDimension.get(worldOptional.get());

        if (cakeDataForDimension.isFruitCake())
        {
            // This is fruit cake vault. No modifiers displayed.
            return "Fruit Cake";
        }

        return instance.get(fieldKey);
    }
}
