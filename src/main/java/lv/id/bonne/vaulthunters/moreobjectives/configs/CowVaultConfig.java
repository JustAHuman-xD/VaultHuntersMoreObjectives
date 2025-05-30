//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
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
    private ResourceLocation theme = null;
    @Expose
    private String objective = null;
    @Expose
    private JsonArray extraModifiers = new JsonArray();
    @Expose
    private Map<ResourceLocation, Integer> cowVaultTrigger = new HashMap<>();

    private List<ModifierCounter> modifiers = new ArrayList<>();

    @Override
    protected void onLoad(@Nullable Config config) {
        if (VaultRegistry.THEME.getKey(this.theme) == null) {
            MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid theme '{}', resetting to default", this.theme);
            this.theme = VaultMod.id("classic_vault_chaos");
        }

        if (VaultRegistry.OBJECTIVE.getKey(ResourceLocation.tryParse(this.objective)) == null) {
            MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid objective '{}', resetting to default", this.objective);
            this.objective = null;
        }

        for (ResourceLocation id : new HashSet<>(this.cowVaultTrigger.keySet())) {
            if (VaultModifierRegistry.get(id) == null) {
                MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid trigger modifier id '{}', skipping", id);
                this.cowVaultTrigger.remove(id);
            }
        }

        this.modifiers = new ArrayList<>();
        for (JsonElement modifier : extraModifiers) {
            ResourceLocation modifierId;
            int count = 1;
            if (modifier instanceof JsonObject object) {
                modifierId = ResourceLocation.tryParse(object.get("modifier").getAsString());
                count = object.has("count") ? object.get("count").getAsInt() : 1;
            } else if (modifier instanceof JsonPrimitive primitive && primitive.isString()) {
                modifierId = ResourceLocation.tryParse(primitive.getAsString());
            } else {
                MoreObjectives.LOGGER.warn("[CowVaultConfig] Invalid extra modifier '{}', skipping", modifier);
                continue;
            }

            VaultModifier<?> vaultModifier = VaultModifierRegistry.get(modifierId);
            if (vaultModifier == null) {
                MoreObjectives.LOGGER.warn("[CowVaultConfig] Couldn't find vault modifier for id '{}', skipping", modifierId);
                continue;
            }
            this.modifiers.add(new ModifierCounter(vaultModifier, count));
        }
        this.modifiers = List.copyOf(this.modifiers);
    }

    @Override
    protected void reset() {
        this.theme = VaultMod.id("classic_vault_chaos");
        this.objective = null;

        this.extraModifiers = new JsonArray();
        this.extraModifiers.add(new ModifierCounter(VaultMod.id("bronze_nuke")).toJson());

        this.cowVaultTrigger = new HashMap<>();
        this.cowVaultTrigger.put(VaultMod.id("wild"), 5);
        this.cowVaultTrigger.put(VaultMod.id("furious_mobs"), 5);
        this.cowVaultTrigger.put(VaultMod.id("infuriated_mobs"), 5);
    }

    public Map<ResourceLocation, Integer> getTrigger() {
        return new HashMap<>(this.cowVaultTrigger);
    }

    public ResourceLocation getTheme() {
        return this.theme;
    }

    public String getObjective() {
        return this.objective;
    }

    public List<ModifierCounter> getModifiers() {
        return this.modifiers;
    }

    @Override
    public String getName() {
        return "moreobjectives_cow_vault";
    }
}
