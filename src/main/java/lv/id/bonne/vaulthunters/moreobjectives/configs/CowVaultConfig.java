//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectives;
import lv.id.bonne.vaulthunters.moreobjectives.configs.data.ModifierCounter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CowVaultConfig extends Config {
    @Expose
    private String theme = null;
    @Expose
    private String objective = null;
    @Expose
    private Map<ResourceLocation, Integer> cowVaultTrigger = new HashMap<>();
    @Expose
    private Map<ResourceLocation, Integer> extraModifiers = new HashMap<>();

    private List<ModifierCounter> modifiers = new ArrayList<>();

    @Override
    protected void onLoad(@Nullable Config config) {
        if (this.theme != null && !this.theme.isBlank() && VaultRegistry.THEME.getKey(this.theme) == null) {
            MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid theme requirement '{}', resetting to default (chaos)", this.theme);
            this.theme = VaultMod.id("classic_vault_chaos").toString();
        }

        if (objective != null && !this.objective.isBlank() && VaultRegistry.OBJECTIVE.getKey(ResourceLocation.tryParse(this.objective)) == null) {
            MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid objective requirement '{}', resetting to default (none)", this.objective);
            this.objective = null;
        }

        for (ResourceLocation id : new HashSet<>(this.cowVaultTrigger.keySet())) {
            if (VaultModifierRegistry.get(id) == null) {
                MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid trigger modifier id '{}', skipping", id);
                this.cowVaultTrigger.remove(id);
            }
        }

        this.modifiers = new ArrayList<>();
        for (ResourceLocation id : new HashSet<>(this.extraModifiers.keySet())) {
            if (VaultModifierRegistry.get(id) == null) {
                MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid extra modifier id '{}', skipping", id);
                this.cowVaultTrigger.remove(id);
                continue;
            }
            this.modifiers.add(new ModifierCounter(id, this.extraModifiers.get(id)));
        }
        this.modifiers = List.copyOf(this.modifiers);
    }

    @Override
    protected void reset() {
        this.theme = VaultMod.id("classic_vault_chaos").toString();
        this.objective = null;

        this.cowVaultTrigger = new HashMap<>();
        this.cowVaultTrigger.put(VaultMod.id("wild"), 5);
        this.cowVaultTrigger.put(VaultMod.id("furious_mobs"), 5);
        this.cowVaultTrigger.put(VaultMod.id("infuriated_mobs"), 5);

        this.extraModifiers = new HashMap<>();
        this.extraModifiers.put(VaultMod.id("bronze_nuke"), 1);
    }

    public Map<ResourceLocation, Integer> getTrigger() {
        return new HashMap<>(this.cowVaultTrigger);
    }

    public boolean themeMatches(ResourceLocation theme) {
        if (this.theme == null || this.theme.isBlank()) {
            return true;
        }
        return theme != null && theme.toString().equals(this.theme);
    }

    public boolean objectiveMatches(String objective) {
        if (this.objective == null || this.objective.isBlank()) {
            return true;
        }
        return objective != null && objective.equals(this.objective);
    }

    public List<ModifierCounter> getModifiers() {
        return this.modifiers;
    }

    @Override
    public String getName() {
        return "moreobjectives_cow_vault";
    }
}
