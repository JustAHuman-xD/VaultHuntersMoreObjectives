//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.utils;


import iskallia.vault.core.data.key.FieldKey;


/**
 * This interface allows access to the private static field CAKE_TYPE.
 */
public interface ICakeObjectiveAccessor
{
    /**
     * Returns CAKE_TYPE or assert.
     * @return CAKE_TYPE field.
     */
    default FieldKey<String> getCakeType()
    {
        throw new AssertionError();
    }
}
