package lv.id.bonne.vaulthunters.moreobjectives.configs.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

public record ModifierCounter(ResourceLocation modifierId, VaultModifier<?> modifier, int count) {
    public ModifierCounter(ResourceLocation modifierId) {
        this(modifierId, 1);
    }

    public ModifierCounter(ResourceLocation modifierId, int count) {
        this(modifierId, VaultModifierRegistry.get(modifierId), count);
    }

    public ModifierCounter(VaultModifier<?> modifier) {
        this(modifier.getId(), modifier, 1);
    }

    public ModifierCounter(VaultModifier<?> modifier, int count) {
        this(modifier.getId(), modifier, count);
    }

    public JsonElement toJson() {
        if (count <= 1) {
            return new JsonPrimitive(modifierId.toString());
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("modifier", new JsonPrimitive(modifierId.toString()));
        jsonObject.add("count", new JsonPrimitive(count));
        return jsonObject;
    }
}
