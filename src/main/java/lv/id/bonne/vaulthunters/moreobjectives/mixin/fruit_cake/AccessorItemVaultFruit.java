package lv.id.bonne.vaulthunters.moreobjectives.mixin.fruit_cake;

import iskallia.vault.item.ItemVaultFruit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemVaultFruit.class, remap = false)
public interface AccessorItemVaultFruit {
    @Accessor int getExtraVaultTicks();
}
