//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.moreobjectives.events;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.Event;

public class ExtraCommonEvents {
    public static final SpawnerCreateEntityEvent SPAWNER_ENTITY_CREATE = register(new SpawnerCreateEntityEvent());


    /**
     * Register event to VH Common Event registry.
     *
     * @param event The event that will be registered.
     * @param <T>   The type of the event.
     * @return The event that was registered.
     */
    private static <T extends Event<?, ?>> T register(T event) {
        CommonEvents.REGISTRY.add(event);
        return event;
    }
}
