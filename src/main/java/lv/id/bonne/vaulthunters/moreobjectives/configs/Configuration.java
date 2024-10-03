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


    @Expose
    private List<ModifierCounter> cowVaultTrigger;
}