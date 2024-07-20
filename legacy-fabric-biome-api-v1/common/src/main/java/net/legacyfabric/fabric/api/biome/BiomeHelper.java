/*
 * Copyright (c) 2020 - 2024 Legacy Fabric
 * Copyright (c) 2016 - 2022 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.legacyfabric.fabric.api.biome;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.world.biome.Biome;

import net.legacyfabric.fabric.api.registry.v2.RegistryHelper;
import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.RegistryEntry;
import net.legacyfabric.fabric.api.util.Identifier;

public class BiomeHelper {
	public static List<RegistryEntry<Biome>> registerBiomeWithParent(Identifier parentId, Function<Integer, Biome> parentGetter, Identifier biomeId, BiFunction<Integer, Biome, Biome> biomeGetter) {
		return RegistryHelper.registerMultiple(
				RegistryIds.BIOMES,
				RegistryHelper.createEntryCreator(parentId, parentGetter),
				RegistryHelper.createEntryCreator(biomeId, id -> biomeGetter.apply(id, RegistryHelper.getValue(RegistryIds.BIOMES, parentId)), 128)
		);
	}
}
