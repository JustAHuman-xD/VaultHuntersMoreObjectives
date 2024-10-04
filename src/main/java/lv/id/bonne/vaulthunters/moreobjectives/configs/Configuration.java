//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import iskallia.vault.VaultMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationAdapter;
import net.minecraft.resources.ResourceLocation;


public class Configuration
{
    public Configuration()
    {
    }


    public void generateConfig()
    {
        this.reset();

        try
        {
            this.writeConfig();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private File getConfigFile()
    {
        return new File(this.root + this.getName() + this.extension);
    }


    public String getName()
    {
        return "more_objectives";
    }


    public Configuration readConfig()
    {
        try
        {
            return GSON.fromJson(new FileReader(this.getConfigFile()), this.getClass());
        }
        catch (FileNotFoundException var2)
        {
            this.generateConfig();
            return this;
        }
    }


    protected void reset()
    {
        if (cowVaultTrigger == null)
        {
            cowVaultTrigger = new ArrayList<>();
        }

        cowVaultTrigger.clear();

        cowVaultTrigger.add(new ModifierCounter(VaultMod.id("wild"), 5));
        cowVaultTrigger.add(new ModifierCounter(VaultMod.id("furious_mobs"), 5));
        cowVaultTrigger.add(new ModifierCounter(VaultMod.id("infuriated_mobs"), 5));


        cakeVault = new CakeVault(0.1f,
            List.of(new ModifierCounter(VaultMod.id("rotten"), 1),
                new ModifierCounter(VaultMod.id("shortened"), 15)),
            List.of(new Fruit("Sweet Kiwi", VaultMod.id("sweet_kiwi"), 200, 0.6888f),
                new Fruit("Grapes", VaultMod.id("grapes"), 400, 0.1721f),
                new Fruit("Bitter Lemon", VaultMod.id("bitter_lemon"), 600, 0.0767f),
                new Fruit("Mango", VaultMod.id("mango"), 900, 0.0340f),
                new Fruit("Sour Orange", VaultMod.id("sour_orange"), 1200, 0.0191f),
                new Fruit("Star Fruit", VaultMod.id("star_fruit"), 1800, 0.0085f),
                new Fruit("Mystic Pear", VaultMod.id("mystic_pear"), 6000, 0.0008f)));
    }





    public void writeConfig() throws IOException
    {
        File dir = new File(this.root);

        if (dir.exists() || dir.mkdirs())
        {
            if (this.getConfigFile().exists() || this.getConfigFile().createNewFile())
            {
                FileWriter writer = new FileWriter(this.getConfigFile());
                GSON.toJson(this, writer);
                writer.flush();
                writer.close();
            }
        }
    }


    public Map<ResourceLocation, AtomicInteger> getCowVaultsRequirements()
    {
        Map<ResourceLocation, AtomicInteger> map = new HashMap<>();

        this.cowVaultTrigger.forEach(value -> map.computeIfAbsent(value.modifier(),
            modifier -> new AtomicInteger(value.count())));

        return map;
    }


    private final String root = "config/";

    private final String extension = ".json";

    private static final Gson GSON = new GsonBuilder().
        registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter()).
        excludeFieldsWithoutExposeAnnotation().
        setPrettyPrinting().
        create();


    public static final class ModifierCounter
    {
        @Expose
        private final ResourceLocation modifier;

        @Expose
        private final int count;


        public ModifierCounter(ResourceLocation modifier, int count)
        {
            this.modifier = modifier;
            this.count = count;
        }


        public ResourceLocation modifier()
        {
            return modifier;
        }


        public int count()
        {
            return count;
        }


        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass())
            {
                return false;
            }
            var that = (ModifierCounter) obj;
            return Objects.equals(this.modifier, that.modifier) &&
                this.count == that.count;
        }


        @Override
        public int hashCode()
        {
            return Objects.hash(modifier, count);
        }


        @Override
        public String toString()
        {
            return "ModifierCounter[" +
                "modifier=" + modifier + ", " +
                "count=" + count + ']';
        }
    }


    public static final class CakeVault
    {
        public CakeVault(float chance, List<ModifierCounter> startModifiers, List<Fruit> fruits)
        {
            this.chance = chance;
            this.startModifiers = startModifiers;
            this.fruits = fruits;

            fruitMap = new TreeMap<>();


            float fc = 0;
            for (Fruit fruit : fruits)
            {
                fc += fruit.chance;
                fruitMap.put(fc, fruit);
            }
        }


        public TreeMap<Float, Fruit> getFruits()
        {
            if (fruitMap == null)
            {
                fruitMap = new TreeMap<>();

                float fc = 0;
                for (Fruit fruit : fruits)
                {
                    fc += fruit.chance;
                    fruitMap.put(fc, fruit);
                }
            }

            return fruitMap;
        }


        @Expose
        public float chance;

        @Expose
        public List<ModifierCounter> startModifiers;

        @Expose
        public List<Fruit> fruits;


        private TreeMap<Float, Fruit> fruitMap;
    }


    public static class Fruit
    {
        public Fruit(String name, ResourceLocation icon, int increment, float chance)
        {
            this.name = name;
            this.icon = icon;
            this.increment = increment;
            this.chance = chance;
        }

        @Expose
        public String name;

        @Expose
        public ResourceLocation icon;

        @Expose
        public int increment;

        @Expose
        public float chance;
    }


    @Expose
    private List<ModifierCounter> cowVaultTrigger;

    @Expose
    public CakeVault cakeVault;
}