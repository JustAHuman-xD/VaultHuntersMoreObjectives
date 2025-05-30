package lv.id.bonne.vaulthunters.moreobjectives.data;

import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectives;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class CakeWorldData extends SavedData {
    private static final String DATA_NAME = "more_objectives_cake_data";
    private boolean fruitCake = false;
    private String cakeType = "";
    private String lastCakeType = "";

    public CakeWorldData() {
        MoreObjectives.LOGGER.debug("Creating new cake world data");
    }

    public void setFruitCake(boolean fruitCake) {
        this.fruitCake = fruitCake;
        this.setDirty();
    }

    public void setCakeType(String cakeType) {
        this.lastCakeType = this.cakeType;
        this.cakeType = cakeType;
        this.setDirty();
    }

    public boolean isFruitCake() {
        return this.fruitCake;
    }

    public String getLastCakeType() {
        return this.lastCakeType;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        MoreObjectives.LOGGER.debug("Saving cake world data");
        compoundTag.putBoolean("fruit_cake", this.fruitCake);
        compoundTag.putString("cake_type", this.cakeType);
        compoundTag.putString("last_cake_type", this.lastCakeType);
        return compoundTag;
    }

    public static CakeWorldData load(CompoundTag tag) {
        MoreObjectives.LOGGER.debug("Loading cake world data");
        CakeWorldData data = new CakeWorldData();
        data.fruitCake = tag.getBoolean("fruit_cake");
        data.cakeType = tag.getString("cake_type");
        data.lastCakeType = tag.getString("last_cake_type");
        return data;
    }

    public static CakeWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(CakeWorldData::load, CakeWorldData::new, DATA_NAME);
    }

    public static boolean isFruitCake(ServerLevel level) {
        return level != null && get(level).isFruitCake();
    }
}
