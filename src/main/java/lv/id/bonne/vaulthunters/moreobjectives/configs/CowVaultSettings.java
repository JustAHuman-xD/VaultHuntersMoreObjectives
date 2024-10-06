//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import iskallia.vault.VaultMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationSerializer;
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
        this.theme = VaultMod.id("null");
        this.objective = "";
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

        this.theme = VaultMod.id("classic_vault_chaos");
        this.objective = "";
    }


    /**
     * This method validates config for missing fields.
     * @return @{code true} if config was invalid.
     */
    public boolean validate()
    {
        boolean updated = false;

        if (this.theme == null)
        {
            updated = true;
            this.theme = VaultMod.id("null");
        }

        if (this.cowVaultTrigger == null)
        {
            updated = true;
            this.cowVaultTrigger = new ArrayList<>();
        }

        if (this.objective == null)
        {
            updated = true;
            this.objective = "";
        }

        return updated;
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


    /**
     * Gets theme.
     *
     * @return the theme
     */
    public ResourceLocation getTheme()
    {
        return this.theme;
    }


    /**
     * Gets objective.
     *
     * @return the objective
     */
    public String getObjective()
    {
        return this.objective;
    }


    @JsonProperty("theme")
    @JsonSerialize(using = ResourceLocationSerializer.class)
    @JsonComment("This allows to define in which 'theme' cow vaults can be triggered.")
    @JsonComment("The default value `the_vault:classic_vault_chaos` makes cow vaults to be")
    @JsonComment("triggered only in chaos vaults.")
    @JsonComment("Setting value to `the_vault:null` will make it work in all themes, and all vault types.")
    private ResourceLocation theme;

    @JsonProperty("objective")
    @JsonComment("This allows to define in which 'objective' cow vaults can be triggered.")
    @JsonComment("The default value `\"\"` allows it to generate in all objectives.")
    private String objective;

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
