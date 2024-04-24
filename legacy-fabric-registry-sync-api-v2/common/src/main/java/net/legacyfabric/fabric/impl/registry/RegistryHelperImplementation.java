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

package net.legacyfabric.fabric.impl.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.legacyfabric.fabric.api.event.Event;
import net.legacyfabric.fabric.api.event.EventFactory;
import net.legacyfabric.fabric.api.registry.v2.event.RegistryInitializedEvent;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.Registry;
import net.legacyfabric.fabric.api.registry.v2.registry.registrable.Registrable;
import net.legacyfabric.fabric.api.registry.v2.registry.registrable.SyncedRegistrable;
import net.legacyfabric.fabric.api.util.Identifier;
import net.legacyfabric.fabric.api.util.VersionUtils;
import net.legacyfabric.fabric.impl.registry.accessor.RegistryIdSetter;

public class RegistryHelperImplementation {
	public static final boolean hasFlatteningBegun = VersionUtils.matches(">=1.8 <=1.12.2");
	public static final Map<Identifier, Event<RegistryInitializedEvent>> INITIALIZATION_EVENTS = new HashMap<>();
	private static final Map<Identifier, Registry<?>> REGISTRIES = new HashMap<>();

	public static Event<RegistryInitializedEvent> getInitializationEvent(Identifier registryId) {
		Event<RegistryInitializedEvent> event;

		if (INITIALIZATION_EVENTS.containsKey(registryId)) {
			event = INITIALIZATION_EVENTS.get(registryId);
		} else {
			event = EventFactory.createArrayBacked(RegistryInitializedEvent.class,
					(callbacks) -> new RegistryInitializedEvent() {
						@Override
						public <T> void initialized(Registry<T> registry) {
							for (RegistryInitializedEvent callback : callbacks) {
								callback.initialized(registry);
							}
						}
					}
			);
			INITIALIZATION_EVENTS.put(registryId, event);
		}

		return event;
	}

	public static <T> Registry<T> getRegistry(Identifier identifier) {
		return (Registry<T>) REGISTRIES.get(identifier);
	}

	public static void registerRegistry(Identifier identifier, Registry<?> holder) {
		if (REGISTRIES.containsKey(identifier)) throw new IllegalArgumentException("Attempted to register registry " + identifier.toString() + " twices!");
		REGISTRIES.put(identifier, holder);

		if (holder instanceof RegistryIdSetter) ((RegistryIdSetter) holder).fabric$setId(identifier);
	}

	public static <T> void register(Registry<T> registry, Identifier identifier, T value) {
		if (!(registry instanceof Registrable)) throw new IllegalArgumentException("Can't register object to non registrable registry " + registry.fabric$getId());

		Registrable<T> registrable = (Registrable<T>) registry;
		int computedId = -1;

		if (registry instanceof SyncedRegistrable) {
			computedId = ((SyncedRegistrable<T>) registrable).fabric$nextId();
		}

		registrable.fabric$register(computedId, identifier, value);
	}

	public static <T> T register(Registry<T> registry, Identifier identifier, Function<Integer, T> valueConstructor) {
		if (!(registry instanceof SyncedRegistrable)) throw new IllegalArgumentException("Can't register object to non registrable registry " + registry.fabric$getId());

		SyncedRegistrable<T> registrable = (SyncedRegistrable<T>) registry;
		int computedId = registrable.fabric$nextId();

		T value = valueConstructor.apply(computedId);

		registrable.fabric$register(computedId, identifier, value);

		return value;
	}
}
