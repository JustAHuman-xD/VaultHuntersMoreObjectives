//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemVaultFruit;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectives;
import lv.id.bonne.vaulthunters.moreobjectives.configs.data.Fruit;
import lv.id.bonne.vaulthunters.moreobjectives.configs.data.ModifierCounter;
import lv.id.bonne.vaulthunters.moreobjectives.mixin.fruit_cake.AccessorItemVaultFruit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class FruitCakeConfig extends Config {
    @Expose
    private float chance = 0.1f;
    @Expose
    private Map<ResourceLocation, Integer> startModifiers = new HashMap<>();
    @Expose
    private Map<String, JsonObject> fruits = new HashMap<>();
    @Expose
    private Map<Integer, List<String>> fruitScaling = new HashMap<>();

    private List<ModifierCounter> modifiers = new ArrayList<>();

    private final Map<String, Fruit> fruitMap = new HashMap<>();
    private final TreeMap<Integer, List<Fruit>> scaling = new TreeMap<>();
    private final Map<Integer, TreeMap<Float, Fruit>> fruitChances = new ConcurrentHashMap<>();

    @Override
    protected void onLoad(@Nullable Config config) {
        if (chance <= 0) {
            MoreObjectives.LOGGER.info("[FruitCakeConfig] Chance is <= 0, fruit cake vaults will not spawn.");
        }

        for (Map.Entry<ResourceLocation, Integer> entry : startModifiers.entrySet()) {
            ResourceLocation id = entry.getKey();
            if (VaultModifierRegistry.get(id) == null) {
                MoreObjectives.LOGGER.warn("[FruitCakeConfig] Invalid start modifier id '{}', skipping", id);
                continue;
            }
            this.modifiers.add(new ModifierCounter(id, entry.getValue()));
        }
        this.modifiers = List.copyOf(this.modifiers);

        for (Map.Entry<String, JsonObject> entry : fruits.entrySet()) {
            String id = entry.getKey();
            JsonObject json = entry.getValue();

            String iconId = id;
            if (json.get("icon") instanceof JsonPrimitive serializedIcon && serializedIcon.isString()) {
                iconId = serializedIcon.getAsString();
            }
            Item icon = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(iconId));
            if (icon == null) {
                MoreObjectives.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has an invalid icon '{}', skipping", id, iconId);
                continue;
            }

            if (!(icon instanceof ItemVaultFruit) && (!(json.get("ticks") instanceof JsonPrimitive ticksJson) || !ticksJson.isNumber())) {
                MoreObjectives.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has an invalid ticks '{}', skipping", id, json.get("ticks"));
                continue;
            }
            int ticks = json.has("ticks") ? json.get("ticks").getAsInt() : ((AccessorItemVaultFruit) icon).getExtraVaultTicks();

            if (!(json.get("weight") instanceof JsonPrimitive weightJson) || !weightJson.isNumber()) {
                MoreObjectives.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has an invalid weight '{}', skipping", id, json.get("weight"));
                continue;
            }
            float weight = weightJson.getAsFloat();
            if (weight <= 0) {
                MoreObjectives.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has a weight <= 0, skipping", id);
                continue;
            }

            this.fruitMap.put(id, new Fruit(id, icon, ticks, weight));
        }

        for (Map.Entry<Integer, List<String>> entry : fruitScaling.entrySet()) {
            int memberCount = entry.getKey();
            List<Fruit> fruits = new ArrayList<>();
            for (String fruitId : entry.getValue()) {
                Fruit fruit = this.fruitMap.get(fruitId);
                if (fruit == null) {
                    MoreObjectives.LOGGER.warn("[FruitCakeConfig] Scaling for '{}' players, fruit '{}' not found, skipping", memberCount, fruitId);
                    continue;
                }
                fruits.add(fruit);
            }
            this.scaling.put(memberCount, fruits);
        }
    }

    @Override
    public void reset() {
        this.chance = 0.1f;

        this.startModifiers = new HashMap<>();
        this.startModifiers.put(VaultMod.id("rotten"), 1);
        this.startModifiers.put(VaultMod.id("shortened"), 15);

        this.fruits = new HashMap<>();
        Fruit kiwi = new Fruit(ModItems.SWEET_KIWI, 0.6888f);
        this.fruits.put(kiwi.id(), kiwi.toJson());
        Fruit grapes = new Fruit(ModItems.GRAPES, 0.1721f);
        this.fruits.put(grapes.id(), grapes.toJson());
        Fruit lemon = new Fruit(ModItems.BITTER_LEMON, 0.0767f);
        this.fruits.put(lemon.id(), lemon.toJson());
        Fruit mango = new Fruit(ModItems.MANGO, 0.0340f);
        this.fruits.put(mango.id(), mango.toJson());
        Fruit orange = new Fruit(ModItems.SOUR_ORANGE, 0.0191f);
        this.fruits.put(orange.id(), orange.toJson());
        Fruit star = new Fruit(ModItems.STAR_FRUIT, 0.0085f);
        this.fruits.put(star.id(), star.toJson());
        Fruit pear = new Fruit(ModItems.MYSTIC_PEAR, 0.0008f);
        this.fruits.put(pear.id(), pear.toJson());

        this.fruitScaling = new HashMap<>();
        this.fruitScaling.put(30, List.of(star.id()));
        this.fruitScaling.put(32, List.of(star.id(), mango.id()));
    }

    public TreeMap<Float, Fruit> getFruitChances(int memberCount) {
        Map.Entry<Integer, List<Fruit>> punishment = this.scaling.floorEntry(memberCount);
        if (punishment != null) {
            return fruitChances.computeIfAbsent(memberCount, count -> {
                TreeMap<Float, Fruit> withPunishment = new TreeMap<>();
                float totalChance = (float) this.fruitMap.values().stream()
                        .filter(fruit -> !punishment.getValue().contains(fruit))
                        .mapToDouble(Fruit::weight).sum();
                float fruitChance = 0;
                for (Fruit fruit : this.fruitMap.values()) {
                    if (!punishment.getValue().contains(fruit)) {
                        fruitChance += (fruit.weight() / totalChance);
                        withPunishment.put(fruitChance, fruit);
                    }
                }
                return withPunishment;
            });
        }

        return this.fruitChances.computeIfAbsent(-1, dummy -> {
            TreeMap<Float, Fruit> fruitChances = new TreeMap<>();
            float totalChance = (float) this.fruitMap.values().stream().mapToDouble(Fruit::weight).sum();
            float fruitChance = 0;
            for (Fruit fruit : this.fruitMap.values()) {
                fruitChance += (fruit.weight() / totalChance);
                fruitChances.put(fruitChance, fruit);
            }
            return fruitChances;
        });
    }

    public float getChance() {
        return this.chance;
    }

    public List<ModifierCounter> getModifiers() {
        return this.modifiers;
    }

    public Fruit getFruit(String id) {
        return this.fruitMap.get(id);
    }

    @Override
    public String getName() {
        return "moreobjectives_fruit_cake";
    }
}


