//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import iskallia.vault.VaultMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.annotations.JsonComment;
import net.minecraft.resources.ResourceLocation;


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
     * This method constructs and returns cow vault requirements.
     * @return Map that links modifier to it's required count.
     */
    public Map<ResourceLocation, AtomicInteger> getCowVaultsRequirements()
    {
        Map<ResourceLocation, AtomicInteger> map = new HashMap<>();

        this.getCowVaultTrigger().forEach(value ->
            map.computeIfAbsent(value.modifier(), modifier -> new AtomicInteger(value.count())));

        return map;
    }


    /**
     * This method returns the list of cow vault modifier triggers.
     * @return List of modifiers that triggers cow vaults.
     */
    public List<Configuration.ModifierCounter> getCowVaultTrigger()
    {
        return this.cowVaultTrigger;
    }


    @JsonProperty("cow_vault_triggers")
    @JsonComment("The list of modifiers that triggers cow vaults.")
    @JsonComment("The default list contains:")
    @JsonComment(" - the_vault:wild modifier 5 times")
    @JsonComment(" - the_vault:furious_mobs modifier 5 times")
    @JsonComment(" - the_vault:infuriated_mobs modifier 5 times")
    @JsonComment("You can change and modify this list, however, keep in mind that cow vaults are")
    @JsonComment("triggered only in chaos vaults, so you can use only chaos vaults modifiers.")
    private List<Configuration.ModifierCounter> cowVaultTrigger;
}
