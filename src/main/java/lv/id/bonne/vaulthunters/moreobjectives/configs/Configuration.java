//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationDeserializer;
import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationSerializer;
import lv.id.bonne.vaulthunters.moreobjectives.configs.annotations.JsonComment;
import lv.id.bonne.vaulthunters.moreobjectives.utils.CommentGeneration;
import net.minecraft.resources.ResourceLocation;


/**
 * The configuration file that allows modifying some of settings.
 */
public class Configuration
{
    /**
     * Default constructor.
     */
    public Configuration()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule();

        module.addSerializer(ResourceLocation.class, new ResourceLocationSerializer());
        module.addDeserializer(ResourceLocation.class, new ResourceLocationDeserializer());

        this.mapper.registerModule(module);
    }


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
            return this.mapper.readValue(this.getConfigFile(), this.getClass());
        }
        catch (IOException var2)
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
            if (!this.getConfigFile().exists() || this.getConfigFile().createNewFile())
            {
                try
                {
                    Path path = Paths.get(this.getConfigFile().toURI());
                    Files.write(path, CommentGeneration.writeWithComments(this.mapper, this).getBytes());
                }
                catch (IllegalAccessException e)
                {
                    throw new IOException(e);
                }
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


        @JsonProperty("modifier")
        @JsonSerialize(using = ResourceLocationSerializer.class)
        @JsonComment("The Vault Hunters modifier identifier.")
        private ResourceLocation modifier;

        @JsonProperty("count")
        @JsonComment("The count of modifiers.")
        private int count;
    }


    @JsonProperty("fruit_cake_settings")
    @JsonComment("The Fruit Cake settings.")
    public FruitCakeSettings fruitCakeSettings;

    @JsonProperty("cow_vault_settings")
    @JsonComment("The cow vault settings.")
    private CowVaultSettings cowVaultSettings;

    /**
     * The object mapper for Jackson.
     */
    @JsonIgnore
    private final ObjectMapper mapper;
}