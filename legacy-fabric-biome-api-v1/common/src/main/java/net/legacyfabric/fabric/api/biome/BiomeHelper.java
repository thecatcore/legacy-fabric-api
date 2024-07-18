package net.legacyfabric.fabric.api.biome;

import net.legacyfabric.fabric.api.registry.v2.RegistryHelper;
import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.RegistryEntry;
import net.legacyfabric.fabric.api.util.Identifier;

import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BiomeHelper {
	public static List<RegistryEntry<Biome>> registerBiomeWithParent(Identifier parentId, Function<Integer, Biome> parentGetter, Identifier biomeId, BiFunction<Integer, Biome, Biome> biomeGetter) {
		return RegistryHelper.registerMultiple(
				RegistryIds.BIOMES,
				RegistryHelper.createEntryCreator(parentId, parentGetter),
				RegistryHelper.createEntryCreator(biomeId, id -> biomeGetter.apply(id, RegistryHelper.getValue(RegistryIds.BIOMES, parentId)), 128)
		);
	}
}
