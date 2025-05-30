package lv.id.bonne.vaulthunters.moreobjectives.configs.data;

import com.google.gson.JsonObject;
import iskallia.vault.item.ItemVaultFruit;
import lv.id.bonne.vaulthunters.moreobjectives.mixin.fruit_cake.AccessorItemVaultFruit;
import net.minecraft.world.item.Item;

public record Fruit(String id, Item icon, int ticks, float chance) {
    public Fruit(ItemVaultFruit icon, float chance) {
        this(icon.getRegistryName().toString(), icon, ((AccessorItemVaultFruit) icon).getExtraVaultTicks(), chance);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("icon", icon.getRegistryName().toString());
        if (!(icon instanceof AccessorItemVaultFruit fruit) || fruit.getExtraVaultTicks() != ticks) {
            json.addProperty("ticks", ticks);
        }
        json.addProperty("chance", chance);
        return json;
    }
}
