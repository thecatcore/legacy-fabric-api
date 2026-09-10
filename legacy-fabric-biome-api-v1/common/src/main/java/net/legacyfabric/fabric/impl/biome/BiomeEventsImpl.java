package net.legacyfabric.fabric.impl.biome;

import net.ornithemc.osl.biomes.api.BiomeEvents;
import net.ornithemc.osl.entrypoints.api.ModInitializer;

public class BiomeEventsImpl implements ModInitializer {
	@Override
	public void init() {
		BiomeEvents.REGISTER_BIOMES.register(() -> net.legacyfabric.fabric.api.biome.BiomeEvents.REGISTER_BIOMES.invoker().run());
	}
}
