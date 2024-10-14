//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import iskallia.vault.VaultMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationSerializer;
import lv.id.bonne.vaulthunters.moreobjectives.configs.annotations.JsonComment;
import net.minecraft.resources.ResourceLocation;


/**
 * This class stores cake vault settings.
 */
public class FruitCakeSettings
{
    /**
     * Default empty constructor
     */
    public FruitCakeSettings()
    {
        this.chance = 0;
        this.fruits = new ArrayList<>();
        this.startModifiers = new ArrayList<>();
    }


    /**
     * This method resets fruit cake values to default ones.
     */
    public void reset()
    {
        this.chance = 0.1f;

        if (this.startModifiers == null)
        {
            this.startModifiers = new ArrayList<>();
        }

        this.startModifiers.clear();

        this.startModifiers.add(new Configuration.ModifierCounter(VaultMod.id("rotten"), 1));
        this.startModifiers.add(new Configuration.ModifierCounter(VaultMod.id("shortened"), 15));

        if (this.fruits == null)
        {
            this.fruits = new ArrayList<>();
        }

        this.fruits.clear();

        this.fruits.add(new Fruit("Sweet Kiwi", VaultMod.id("sweet_kiwi"), 200, 0.6888f));
        this.fruits.add(new Fruit("Grapes", VaultMod.id("grapes"), 400, 0.1721f));
        this.fruits.add(new Fruit("Bitter Lemon", VaultMod.id("bitter_lemon"), 600, 0.0767f));
        this.fruits.add(new Fruit("Mango", VaultMod.id("mango"), 900, 0.0340f));
        this.fruits.add(new Fruit("Sour Orange", VaultMod.id("sour_orange"), 1200, 0.0191f));
        this.fruits.add(new Fruit("Star Fruit", VaultMod.id("star_fruit"), 1800, 0.0085f));
        this.fruits.add(new Fruit("Mystic Pear", VaultMod.id("mystic_pear"), 6000, 0.0008f));

        if (this.memberPunishments == null)
        {
            this.memberPunishments = new TreeMap<>();
        }

        this.memberPunishments.clear();

        this.memberPunishments.put(30, new ArrayList<>(List.of("Star Fruit")));
        this.memberPunishments.put(32, new ArrayList<>(List.of("Star Fruit, Mango")));
    }


    /**
     * This method validates config for missing fields.
     * @return @{code true} if config was invalid.
     */
    public boolean validate()
    {
        boolean updated = false;

        if (this.chance < 0 || this.chance > 1)
        {
            updated = true;
            this.chance = 0;
        }

        if (this.startModifiers == null)
        {
            updated = true;
            this.startModifiers = new ArrayList<>();
        }

        if (this.fruits == null)
        {
            updated = true;
            this.fruits = new ArrayList<>();
        }

        if (this.memberPunishments == null)
        {
            this.memberPunishments = new TreeMap<>();
        }

        return updated;
    }


    /**
     * This method returns fruits chance map.
     *
     * @return The fruit chance map.
     */
    public TreeMap<Float, Fruit> getFruitChances(int memberCount)
    {
        if (this.memberPunishments == null)
        {
            this.memberPunishments = new TreeMap<>();
        }

        Map.Entry<Integer, List<String>> punishment = this.memberPunishments.floorEntry(memberCount);

        if (punishment != null)
        {
            TreeMap<Float, Fruit> returnMap = new TreeMap<>();

            List<Fruit> correctedFruitList = this.fruits.stream().
                filter(fruit -> !punishment.getValue().contains(fruit.getName())).
                toList();

            float totalChance = (float) correctedFruitList.stream().mapToDouble(Fruit::getChance).sum();
            float fruitChance = 0;

            for (Fruit fruit : correctedFruitList)
            {
                fruitChance += (fruit.chance / totalChance);
                returnMap.put(fruitChance, fruit);
            }

            return returnMap;
        }

        if (this.fruitMap == null)
        {
            // Repopulate fruit map
            this.fruitMap = new TreeMap<>();

            float totalChance = (float) this.fruits.stream().mapToDouble(Fruit::getChance).sum();
            float fruitChance = 0;

            for (Fruit fruit : this.fruits)
            {
                fruitChance += (fruit.chance / totalChance);
                this.fruitMap.put(fruitChance, fruit);
            }
        }

        return this.fruitMap;
    }


    /**
     * Gets fruits.
     *
     * @return the fruits
     */
    public List<Fruit> getFruits()
    {
        return this.fruits;
    }


    /**
     * Gets chance.
     *
     * @return the chance
     */
    public float getChance()
    {
        return this.chance;
    }


    /**
     * Gets start modifiers.
     *
     * @return the start modifiers
     */
    public List<Configuration.ModifierCounter> getStartModifiers()
    {
        return this.startModifiers;
    }


    /**
     * Gets member punishments.
     *
     * @return the member punishments
     */
    public TreeMap<Integer, List<String>> getMemberPunishments()
    {
        return this.memberPunishments;
    }


    /**
     * The fruit incrementing for cake.
     */
    public static class Fruit
    {
        /**
         * Empty constructor for reader
         */
        public Fruit()
        {
        }


        /**
         * The default constructor for fruit.
         *
         * @param name The name of fruit
         * @param icon The resource location icon for fruit.
         * @param increment The time increment for cake in ticks.
         * @param chance The chance for fruit to be spawned.
         */
        public Fruit(String name, ResourceLocation icon, int increment, float chance)
        {
            this.name = name;
            this.icon = icon;
            this.increment = increment;
            this.chance = chance;
        }


        /**
         * Gets chance for the fruit cake.
         *
         * @return the chance
         */
        public float getChance()
        {
            return this.chance;
        }


        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName()
        {
            return name;
        }


        /**
         * Gets icon.
         *
         * @return the icon
         */
        public ResourceLocation getIcon()
        {
            return icon;
        }


        /**
         * Gets increment.
         *
         * @return the increment
         */
        public int getIncrement()
        {
            return increment;
        }


        @JsonProperty("name")
        @JsonComment("The name of fruit that will be added before cake name when player clicks on cake.")
        private String name;

        @JsonProperty("icon")
        @JsonSerialize(using = ResourceLocationSerializer.class)
        @JsonComment("The icon resource location. You can use any item in minecraft, as long as you")
        @JsonComment("provide correct resource location to it.")
        @JsonComment("The icon is spawned over cake as item to show it.")
        private ResourceLocation icon;

        @JsonProperty("increment")
        @JsonComment("The time increment in game ticks that is added for clicking on a cake.")
        @JsonComment("A second is 20 game ticks (normally) so your time in seconds need to be")
        @JsonComment("multiplied by 20.")
        private int increment;

        @JsonProperty("chance")
        @JsonComment("The chance for cake to spawn with this fruit.")
        @JsonComment("The value should be between 0 to 1.")
        private float chance;
    }


    @JsonProperty("chance")
    @JsonComment("The chance for cake objective to be Fruit Cake.")
    @JsonComment("The default chance is 0.1 which is 10%.")
    @JsonComment("Value 0 and bellow will mean that fruit cakes are disabled.")
    @JsonComment("Value 1 and above will mean that all cake vaults will be fruit vaults.")
    private float chance;

    @JsonProperty("start_modifiers")
    @JsonComment("The start modifiers that are applied on the cake vault start.")
    @JsonComment("You can set any modifiers you want to be added for the vault.")
    @JsonComment("The default modifiers are:")
    @JsonComment(" - the_vault:rotten modifier 1 times")
    @JsonComment(" - the_vault:shortened modifier 15 times")
    private List<Configuration.ModifierCounter> startModifiers;

    @JsonProperty("fruits")
    @JsonComment("The fruits that can be added for cakes to be tastier.")
    @JsonComment("Chosen fruits matches VH fruits, however, you can use any values and names you want.")
    @JsonComment("The default fruits are:")
    @JsonComment(" - Sweet Kiwi with icon the_vault:sweet_kiwi, time added 200 and 0.6888 chance to spawn.")
    @JsonComment(" - Grapes with icon the_vault:grapes, time added 400 and 0.1721 chance to spawn.")
    @JsonComment(" - Bitter Lemon with icon the_vault:bitter_lemon, time added 600 and 0.0767 chance to spawn.")
    @JsonComment(" - Mango with icon the_vault:mango, time added 900 and 0.0340 chance to spawn.")
    @JsonComment(" - Sour Orange with icon the_vault:sour_orange, time added 1200 and 0.0191 chance to spawn.")
    @JsonComment(" - Star Fruit with icon the_vault:star_fruit, time added 1800 and 0.0085 chance to spawn.")
    @JsonComment(" - Mystic Pear with icon the_vault:mystic_pear, time added 6000 and 0.0008 chance to spawn.")
    private List<Fruit> fruits;


    @JsonProperty("member_count_punishments")
    @JsonSetter(nulls = Nulls.SKIP)
    @JsonComment("This map allows to define list of fruits that are disabled if you have more then X players in vault.")
    @JsonComment("You can define it as you want, but you need to keep fruit names the same here.")
    @JsonComment("Blocking fruit will increase chance to have other fruits instead.")
    @JsonComment("The default just shows the way how it can be added.")
    @JsonComment("  The number <30> shows on how many vault runners it will be triggered.")
    @JsonComment("  The list of names are the exact you defined in `Fruit` list.")
    @JsonComment("  The fruits are removed from defined and all next levels.")
    @JsonComment("  In default setting 30 and 31 vault runners will not get Star Fruit, but 32 and more runners")
    @JsonComment("  will not get Star Fruit and Mangos. Bellow 30 vault runners will have all fruits.")
    private TreeMap<Integer, List<String>> memberPunishments;

    /**
     * This tree map allows getting fruits by their chance value.
     */
    @JsonIgnore
    private TreeMap<Float, Fruit> fruitMap;
}


