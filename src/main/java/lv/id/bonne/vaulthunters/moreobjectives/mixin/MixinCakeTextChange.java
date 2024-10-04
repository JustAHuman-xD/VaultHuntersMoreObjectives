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
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import lv.id.bonne.vaulthunters.moreobjectives.utils.ICakeObjectiveAccessor;


/**
 * This mixin adds `Fruit` to cake enter and exit messages, if cake vault has fruit mode.
 */
@Mixin(value = ClassicListenersLogic.class, remap = false)
public class MixinCakeTextChange
{
    @Redirect(method = "printJoinMessage",
        at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/Objectives;get(Liskallia/vault/core/data/key/FieldKey;)Ljava/lang/Object;"))
    private Object replaceEnterCakeText(Objectives instance, FieldKey<String> fieldKey)
    {
        return this.appendCakeText(instance, fieldKey);
    }


    @Redirect(method = "lambda$initServer$1", at = @At(value = "INVOKE", target = "Liskallia/vault/core/vault/objective/Objectives;get(Liskallia/vault/core/data/key/FieldKey;)Ljava/lang/Object;"))
    private Object replaceExitCakeText(Objectives instance, FieldKey<String> fieldKey)
    {
        return this.appendCakeText(instance, fieldKey);
    }


    /**
     * This method ckecks if objective is cake objective and if objective has custom field key
     * that triggers fruit cake vaults. Then it adds `Fruit` before `Cake.
     * @param instance Objectives instance.
     * @param fieldKey FieldKey.KEY
     * @return Objective name.
     */
    @Unique
    private String appendCakeText(Objectives instance, FieldKey<String> fieldKey)
    {
        if (!instance.get(fieldKey).equals("cake"))
        {
            return instance.get(fieldKey);
        }

        Optional<Objective.ObjList> cakeObjectives = instance.getOptional(Objectives.LIST);

        if (cakeObjectives.isEmpty())
        {
            return instance.get(fieldKey);
        }

        StringBuilder text = new StringBuilder();

        cakeObjectives.ifPresent(objectives ->
        {
            objectives.forEach(objectiveClass ->
            {
                if (objectiveClass instanceof ICakeObjectiveAccessor cake)
                {
                    if (objectiveClass.has(cake.getCakeType()))
                    {
                        text.append("Fruit Cake");
                    }
                }
            });
        });

        if (text.isEmpty())
        {
            return instance.get(fieldKey);
        }
        else
        {
            return text.toString();
        }
    }
}
