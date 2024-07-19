package net.legacyfabric.fabric.testing;

import java.util.Map;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
import net.legacyfabric.fabric.api.registry.v2.event.RegistryInitializedEvent;
import net.legacyfabric.fabric.api.registry.v2.registry.SyncedRegistrableRegistry;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.Registry;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.RegistryEntry;
import net.legacyfabric.fabric.api.registry.v2.registry.registrable.DesynchronizeableRegistrable;

public class TestModPreLaunch implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		RegistryInitializedEvent.event(RegistryIds.BLOCKS).register(TestModPreLaunch::registryInitialized);
		RegistryInitializedEvent.event(RegistryIds.BLOCK_ENTITY_TYPES).register(TestModPreLaunch::registryInitialized);
		RegistryInitializedEvent.event(RegistryIds.BIOMES).register(TestModPreLaunch::registryInitialized);
		RegistryInitializedEvent.event(RegistryIds.ENCHANTMENTS).register(TestModPreLaunch::registryInitialized);
		RegistryInitializedEvent.event(RegistryIds.ENTITY_TYPES).register(TestModPreLaunch::registryInitialized);
		RegistryInitializedEvent.event(RegistryIds.ITEMS).register(TestModPreLaunch::registryInitialized);
		RegistryInitializedEvent.event(RegistryIds.POTIONS).register(TestModPreLaunch::registryInitialized);
		RegistryInitializedEvent.event(RegistryIds.STATUS_EFFECTS).register(TestModPreLaunch::registryInitialized);
	}

	public static void registryInitialized(Registry<?> registry) {
		System.err.println("Initialized registry: " + registry.fabric$getId());

		registry.fabric$getEntryAddedCallback().register((rawId, id, object) -> {
			System.err.println("Registry " + registry.fabric$getId() + " entry: " + rawId + " " + id + " " + object);
		});

		if (registry instanceof SyncedRegistrableRegistry) {
			SyncedRegistrableRegistry<?> syncedRegistry = (SyncedRegistrableRegistry) registry;

			if (syncedRegistry instanceof DesynchronizeableRegistrable && !((DesynchronizeableRegistrable) syncedRegistry).canSynchronize()) return;

			syncedRegistry.fabric$getRegistryRemapCallback().register(changedIdsMap -> {
				for (Map.Entry<Integer, ? extends RegistryEntry<?>> entry : changedIdsMap.entrySet()) {
					int id = entry.getKey();
					RegistryEntry<?> changedEntry = entry.getValue();
					System.err.println("Registry " + registry.fabric$getId() + " entry " + changedEntry.getIdentifier() + " remapped from " + id +
							" to " + changedEntry.getId() + " with value " + changedEntry.getValue());
				}
			});
		}
	}
}
