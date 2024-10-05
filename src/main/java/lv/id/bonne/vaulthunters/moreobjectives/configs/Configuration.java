//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationAdapter;
import lv.id.bonne.vaulthunters.moreobjectives.configs.annotations.JsonComment;
import net.minecraft.resources.ResourceLocation;


/**
 * The configuration file that allows modifying some of settings.
 */
public class Configuration
{
    /**
     * This method generates config if it is missing.
     */
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


    /**
     * This returns the location of the config file.
     * @return The config file location
     */
    private File getConfigFile()
    {
        return new File("config/more_objectives.json");
    }


    /**
     * This method reads the config file from file.
     * @return The configuration file.
     */
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


    /**
     * This method resets configs to default values.
     */
    protected void reset()
    {
        if (this.cowVaultSettings == null)
        {
            this.cowVaultSettings = new CowVaultSettings();
        }

        this.cowVaultSettings.reset();


        if (this.fruitCakeSettings == null)
        {
            this.fruitCakeSettings = new FruitCakeSettings();
        }

        this.fruitCakeSettings.reset();
    }


    /**
     * This method writes the config file.
     * @throws IOException Exception if writing failed.
     */
    public void writeConfig() throws IOException
    {
        File dir = new File("config/");

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


    /**
     * This method constructs and returns cow vault requirements.
     * @return Map that links modifier to it's required count.
     */
    public Map<ResourceLocation, AtomicInteger> getCowVaultsRequirements()
    {
        Map<ResourceLocation, AtomicInteger> map = new HashMap<>();

        this.cowVaultSettings.getCowVaultTrigger().forEach(value ->
            map.computeIfAbsent(value.modifier(), modifier -> new AtomicInteger(value.count())));

        return map;
    }


    /**
     * This class allows defining the list of modifiers and their numbers in the config settings.
     */
    public static final class ModifierCounter
    {
        /**
         * Instantiates a new Modifier counter.
         *
         * @param modifier the modifier
         * @param count the count
         */
        public ModifierCounter(ResourceLocation modifier, int count)
        {
            this.modifier = modifier;
            this.count = count;
        }


        /**
         * Modifier resource location.
         *
         * @return the resource location
         */
        public ResourceLocation modifier()
        {
            return this.modifier;
        }


        /**
         * Count int.
         *
         * @return the int
         */
        public int count()
        {
            return this.count;
        }


        @Expose
        @JsonComment("The Vault Hunters modifier identifier.")
        private ResourceLocation modifier;

        @Expose
        @JsonComment("The count of modifiers.")
        private int count;
    }


    @Expose
    @SerializedName("fruit_cake_settings")
    @JsonComment("The Fruit Cake settings.")
    public FruitCakeSettings fruitCakeSettings;

    @Expose
    @SerializedName("cow_vault_settings")
    @JsonComment("The cow vault settings.")
    private CowVaultSettings cowVaultSettings;

    /**
     * The GSON builder.
     */
    private static final Gson GSON = new GsonBuilder().
        registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter()).
        excludeFieldsWithoutExposeAnnotation().
        setPrettyPrinting().
        create();
}