//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.moreobjectives.core.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.ClassicMobLogic;
import iskallia.vault.core.vault.MobLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModEntities;
import lv.id.bonne.vaulthunters.moreobjectives.MoreObjectives;
import lv.id.bonne.vaulthunters.moreobjectives.events.ExtraCommonEvents;

public class CowMobLogic extends ClassicMobLogic {
    public static final FieldRegistry FIELDS = ClassicMobLogic.FIELDS.merge(new FieldRegistry());
    public static final SupplierKey<MobLogic> KEY = SupplierKey.of(MoreObjectives.id("cow_logic"), MobLogic.class).with(Version.v1_0, CowMobLogic::new);

    @Override
    public void initServer(VirtualWorld world, Vault vault) {
        ExtraCommonEvents.SPAWNER_ENTITY_CREATE.in(world).register(this, data -> data.setEntityType(ModEntities.AGGRESSIVE_COW));
        super.initServer(world, vault);
    }

    @Override
    public SupplierKey<MobLogic> getKey() {
        return KEY;
    }

    @Override
    public FieldRegistry getFields() {
        return FIELDS;
    }
}
