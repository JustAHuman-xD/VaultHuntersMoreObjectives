//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

import iskallia.vault.VaultMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.annotations.JsonComment;


/**
 * The settings for cow vaults
 */
public class CowVaultSettings
{
    /**
     * The default constructor.
     */
    public CowVaultSettings()
    {
        this.cowVaultTrigger = new ArrayList<>();
    }


    /**
     * This method resets cow vault settings to default values.
     */
    protected void reset()
    {
        if (this.cowVaultTrigger == null)
        {
            this.cowVaultTrigger = new ArrayList<>();
        }

        this.cowVaultTrigger.clear();

        this.cowVaultTrigger.add(new Configuration.ModifierCounter(VaultMod.id("wild"), 5));
        this.cowVaultTrigger.add(new Configuration.ModifierCounter(VaultMod.id("furious_mobs"), 5));
        this.cowVaultTrigger.add(new Configuration.ModifierCounter(VaultMod.id("infuriated_mobs"), 5));
    }


    /**
     * This method returns the list of cow vault modifier triggers.
     * @return List of modifiers that triggers cow vaults.
     */
    public List<Configuration.ModifierCounter> getCowVaultTrigger()
    {
        return this.cowVaultTrigger;
    }


    @Expose
    @SerializedName("cow_vault_triggers")
    @JsonComment("The list of modifiers that triggers cow vaults.")
    @JsonComment("The default list contains:")
    @JsonComment(" - the_vault:wild modifier 5 times")
    @JsonComment(" - the_vault:furious_mobs modifier 5 times")
    @JsonComment(" - the_vault:infuriated_mobs modifier 5 times")
    @JsonComment("You can change and modify this list, however, keep in mind that cow vaults are")
    @JsonComment("triggered only in chaos vaults, so you can use only chaos vaults modifiers.")
    private List<Configuration.ModifierCounter> cowVaultTrigger;
}
