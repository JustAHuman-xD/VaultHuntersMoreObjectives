//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.moreobjectives.events;

import iskallia.vault.core.event.Event;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SpawnerCreateEntityEvent extends Event<SpawnerCreateEntityEvent, SpawnerCreateEntityEvent.Data> {
    public SpawnerCreateEntityEvent() {}

    protected SpawnerCreateEntityEvent(SpawnerCreateEntityEvent parent) {
        super(parent);
    }

    public Data invoke(Level level, EntityType<?> entityType) {
        return this.invoke(new Data(level, entityType));
    }

    public SpawnerCreateEntityEvent in(Level level) {
        return this.filter(data -> level == data.level);
    }

    @Override
    public SpawnerCreateEntityEvent createChild() {
        return new SpawnerCreateEntityEvent(this);
    }

    public static class Data {
        private final Level level;
        private EntityType<?> entityType;

        protected Data(Level level, EntityType<?> entityType) {
            this.level = level;
            this.entityType = entityType;
        }

        public void setEntityType(EntityType<?> entityType) {
            this.entityType = entityType;
        }

        public EntityType<?> getEntityType() {
            return this.entityType;
        }

        public Level getLevel() {
            return this.level;
        }
    }
}
