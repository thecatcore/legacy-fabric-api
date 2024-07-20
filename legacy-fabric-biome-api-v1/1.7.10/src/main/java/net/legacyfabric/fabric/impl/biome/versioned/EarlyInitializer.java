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

package net.legacyfabric.fabric.impl.biome.versioned;

import net.minecraft.world.biome.Biome;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
import net.legacyfabric.fabric.api.registry.v2.event.RegistryInitializedEvent;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.Registry;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.RegistryEntry;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.SyncedRegistry;
import net.legacyfabric.fabric.mixin.biome.BiomeAccessor;

public class EarlyInitializer implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		RegistryInitializedEvent.event(RegistryIds.BIOMES).register(EarlyInitializer::biomeRegistryInit);
	}

	private static void biomeRegistryInit(Registry<?> holder) {
		SyncedRegistry<Biome> registry = (SyncedRegistry<Biome>) holder;

		registry.fabric$getEntryAddedCallback().register((rawId, id, object) -> {
			object.setName(id.toTranslationKey());
		});

		registry.fabric$getRegistryRemapCallback().register(changedIdsMap -> {
			for (RegistryEntry<Biome> entry : changedIdsMap.values()) {
				Biome biome = entry.getValue();

				if (biome != null) {
					((BiomeAccessor) biome).setId(entry.getId());
				}
			}
		});
	}
}
