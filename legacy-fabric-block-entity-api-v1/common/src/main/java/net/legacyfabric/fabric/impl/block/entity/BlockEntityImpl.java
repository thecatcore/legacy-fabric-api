package net.legacyfabric.fabric.impl.block.entity;

import net.ornithemc.osl.blockentities.api.BlockEntityEvents;
import net.ornithemc.osl.blockentities.api.BlockEntityTypeRegistry;
import net.ornithemc.osl.entrypoints.api.ModInitializer;

public class BlockEntityImpl implements ModInitializer {
	@Override
	public void init() {
		BlockEntityEvents.REGISTER_BLOCK_ENTITY_TYPES.register(() -> {
			net.legacyfabric.fabric.api.block.entity.v1.BlockEntityEvents.REGISTER_BLOCK_ENTITIES.invoker().accept(BlockEntityTypeRegistry::register);
		});
	}
}
