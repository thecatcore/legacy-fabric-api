package net.legacyfabric.fabric.impl.entity;

import net.ornithemc.osl.entities.api.EntityEvents;
import net.ornithemc.osl.entrypoints.api.ModInitializer;

public class EntityEventsImpl implements ModInitializer {
	@Override
	public void init() {
		EntityEvents.REGISTER_ENTITY_TYPES.register(() -> net.legacyfabric.fabric.api.entity.EntityEvents.REGISTER_ENTITIES.invoker().run());
	}
}
