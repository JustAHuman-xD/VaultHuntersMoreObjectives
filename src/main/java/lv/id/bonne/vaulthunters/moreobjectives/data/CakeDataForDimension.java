package lv.id.bonne.vaulthunters.moreobjectives.data;


import java.util.function.Function;

import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectivesMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;


/**
 * The type Cake data for dimension.
 */
public class CakeDataForDimension extends SavedData
{
    public CakeDataForDimension()
    {
        MoreObjectivesMod.LOGGER.debug("Creating config");
    }


    @Override
    public CompoundTag save(CompoundTag compoundTag)
    {
        MoreObjectivesMod.LOGGER.debug("Saving config");

        compoundTag.putBoolean("fruit_cake", this.fruitCake);
        compoundTag.putString("cake_type", this.cakeType);
        compoundTag.putString("last_cake_type", this.lastCakeType);
        return compoundTag;
    }


    /**
     * Is fruit cake boolean.
     *
     * @return the boolean
     */
    public boolean isFruitCake()
    {
        return this.fruitCake;
    }


    /**
     * Sets fruit cake.
     *
     * @param fruitCake the fruit cake
     */
    public void setFruitCake(boolean fruitCake)
    {
        this.fruitCake = fruitCake;
        this.setDirty();
    }


    /**
     * Sets cake type.
     *
     * @param cakeType the cake type
     */
    public void setCakeType(String cakeType)
    {
        this.lastCakeType = this.cakeType;
        this.cakeType = cakeType;
        this.setDirty();
    }


    /**
     * Gets last cake type.
     *
     * @return the last cake type
     */
    public String getLastCakeType()
    {
        return this.lastCakeType;
    }


    /**
     * Return data file from minecraft server instance
     *
     * @return ExtraCommandsData
     */
    public static CakeDataForDimension get(ServerLevel level)
    {
        return level.getDataStorage().computeIfAbsent(CakeDataForDimension.load(),
            CakeDataForDimension::new,
            DATA_NAME);
    }


    /**
     * Load ExtraCommandsWorldData data from tag.
     *
     * @return Function that would load data.
     */
    public static Function<CompoundTag, CakeDataForDimension> load()
    {
        return (tag) ->
        {
            MoreObjectivesMod.LOGGER.debug("Loading config");
            CakeDataForDimension data = new CakeDataForDimension();
            data.fruitCake = tag.getBoolean("fruit_cake");
            data.cakeType = tag.getString("cake_type");
            data.lastCakeType = tag.getString("last_cake_type");
            return data;
        };
    }


    /**
     * Indicates if current cake vault is fruit cake vault.
     */
    private boolean fruitCake = false;

    /**
     * The next generated cake type.
     */
    private String cakeType = "";

    /**
     * The last generated cake type.
     */
    private String lastCakeType = "";

    /**
     * The data file name.
     */
    private static final String DATA_NAME = "more_objectives_cake_data";
}
