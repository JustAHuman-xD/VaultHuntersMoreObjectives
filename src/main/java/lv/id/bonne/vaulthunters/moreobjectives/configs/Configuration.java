//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectivesMod;
import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationDeserializer;
import lv.id.bonne.vaulthunters.moreobjectives.configs.adapters.ResourceLocationSerializer;
import lv.id.bonne.vaulthunters.moreobjectives.configs.annotations.JsonComment;
import lv.id.bonne.vaulthunters.moreobjectives.utils.CommentGeneration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


/**
 * The configuration file that allows modifying some of settings.
 */
@Mod.EventBusSubscriber(modid = MoreObjectivesMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Configuration
{
    /**
     * Default constructor.
     */
    public Configuration()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);

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
     *
     * @return The config file location
     */
    private File getConfigFile()
    {
        return new File("config/more_objectives.json");
    }


    /**
     * This method reads the config file from file.
     *
     * @return The configuration file.
     */
    public Configuration readConfig()
    {
        try
        {
            Configuration configuration = this.mapper.readValue(this.getConfigFile(), this.getClass());

            if (configuration.validate())
            {
                this.writeConfig();
            }

            return configuration;
        }
        catch (IOException var2)
        {
            this.generateConfig();
            MoreObjectivesMod.LOGGER.error("Failed to read config. Generated default one.");
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
     * This method returns if configs were invalid.
     * @return {@code true} if configs were invalid.
     */
    private boolean validate()
    {
        return this.getFruitCakeSettings().validate() || this.getCowVaultSettings().validate();
    }


    /**
     * This method writes the config file.
     *
     * @throws IOException Exception if writing failed.
     */
    public void writeConfig() throws IOException
    {
        File dir = new File("config/");

        if (dir.exists() || dir.mkdirs())
        {
            if (this.getConfigFile().exists() || this.getConfigFile().createNewFile())
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
     * Gets cow vault settings.
     *
     * @return the cow vault settings
     */
    public CowVaultSettings getCowVaultSettings()
    {
        if (this.cowVaultSettings == null)
        {
            this.cowVaultSettings = new CowVaultSettings();
            this.cowVaultSettings.reset();
        }

        return this.cowVaultSettings;
    }


    /**
     * Gets fruit cake settings.
     *
     * @return the fruit cake settings
     */
    public FruitCakeSettings getFruitCakeSettings()
    {
        if (this.fruitCakeSettings == null)
        {
            this.fruitCakeSettings = new FruitCakeSettings();
            this.fruitCakeSettings.reset();
        }

        return this.fruitCakeSettings;
    }


    @SubscribeEvent
    public static void onConfigReload(AddReloadListenerEvent event)
    {
        MoreObjectivesMod.LOGGER.info("Reloading configuration...");
        MoreObjectivesMod.CONFIGURATION = MoreObjectivesMod.CONFIGURATION.readConfig();
    }


    /**
     * This class allows defining the list of modifiers and their numbers in the config settings.
     */
    public static final class ModifierCounter
    {
        /**
         * Empty constructor for reader.
         */
        public ModifierCounter()
        {
        }


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

    /**
     * The object mapper for Jackson.
     */
    @JsonIgnore
    private final ObjectMapper mapper;

    @JsonProperty("fruit_cake_settings")
    @JsonComment("The Fruit Cake settings.")
    private FruitCakeSettings fruitCakeSettings;

    @JsonProperty("cow_vault_settings")
    @JsonComment("The cow vault settings.")
    private CowVaultSettings cowVaultSettings;
}