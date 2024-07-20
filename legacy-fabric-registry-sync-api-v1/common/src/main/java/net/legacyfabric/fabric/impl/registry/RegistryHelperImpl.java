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

import java.util.IdentityHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import net.legacyfabric.fabric.api.registry.v1.RegistryHelper;
import net.legacyfabric.fabric.api.util.Identifier;
import net.legacyfabric.fabric.api.util.VersionUtils;
import net.legacyfabric.fabric.impl.client.registry.sync.ClientRegistryRemapper;
import net.legacyfabric.fabric.impl.registry.sync.ServerRegistryRemapper;
import net.legacyfabric.fabric.impl.registry.sync.compat.IdListCompat;
import net.legacyfabric.fabric.impl.registry.sync.compat.RegistriesGetter;
import net.legacyfabric.fabric.impl.registry.sync.compat.SimpleRegistryCompat;
import net.legacyfabric.fabric.impl.registry.sync.remappers.RegistryRemapper;
import net.legacyfabric.fabric.impl.registry.util.ArrayAndMapBasedRegistry;
import net.legacyfabric.fabric.impl.registry.util.NumericalIdPair;

@ApiStatus.Internal
public class RegistryHelperImpl {
	private static final boolean hasFlatteningBegun = VersionUtils.matches(">=1.8 <=1.12.2");
	public static final boolean bootstrap = VersionUtils.matches(">1.8.9");
	public static RegistriesGetter registriesGetter = null;

	public static <T> int register(T object, Identifier id, Identifier registryId) {
		RegistryRemapper<T> remapper = RegistryRemapper.getRegistryRemapper(registryId);
		int rawId = nextId(remapper.getRegistry());
		remapper.register(rawId, id, object);

		return rawId;
	}

	public static <T> T register(RegistryHelper.EntryCreator<T> entryCreator, Identifier id, Identifier registryId) {
		return register(entryCreator, id, registryId, instance -> { });
	}

	public static <T> T register(RegistryHelper.EntryCreator<T> entryCreator, Identifier id, Identifier registryId, Consumer<T> beforeRegistration) {
		RegistryRemapper<T> remapper = RegistryRemapper.getRegistryRemapper(registryId);
		SimpleRegistryCompat<?, T> registry = remapper.getRegistry();
		int rawId = nextId(registry);

		if (registry instanceof ArrayAndMapBasedRegistry) ((ArrayAndMapBasedRegistry<?, T>) registry).updateArrayLength(rawId);

		T instance = entryCreator.create(rawId);
		beforeRegistration.accept(instance);

		remapper.register(rawId, id, instance);

		return instance;
	}

	public static <V> V getValue(Identifier id, Identifier registryId) {
		RegistryRemapper<V> registryRemapper = RegistryRemapper.getRegistryRemapper(registryId);
		return registryRemapper.getRegistry().getValue(id);
	}

	public static RegistryRemapper<?> registerRegistryRemapper(RegistryRemapper<RegistryRemapper<?>> registryRemapperRegistryRemapper, RegistryRemapper<?> registryRemapper) {
		int rawId = nextId(registryRemapperRegistryRemapper.getRegistry());
		registryRemapperRegistryRemapper.register(rawId, registryRemapper.registryId, registryRemapper);

		return registryRemapper;
	}

	public static void registerRegistryRemapper(Supplier<RegistryRemapper<?>> remapperSupplier) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			ClientRegistryRemapper.getInstance().registrerRegistryRemapper(remapperSupplier.get());
		}

		ServerRegistryRemapper.getInstance().registrerRegistryRemapper(remapperSupplier.get());
	}

	public static <V> RegistryRemapper<V> getRegistryRemapper(Identifier identifier) {
		return (RegistryRemapper<V>) ServerRegistryRemapper.getInstance().getRegistryRemapperRegistryRemapper().getRegistry().getValue(identifier);
	}

	private static String formatTranslationKey(Identifier key) {
		return key.getNamespace() + "." + key.getPath();
	}

	public static int nextId(SimpleRegistryCompat<?, ?> registry) {
		return nextId(registry.getIds(), registry);
	}

	public static int nextId(SimpleRegistryCompat<?, ?> registry, int minId) {
		return nextId(registry.getIds(), registry, minId);
	}

	public static NumericalIdPair nextIds(SimpleRegistryCompat<?, ?> registry, int offset) {
		int id = 0;

		RegistryRemapper<?> registryRemapper = RegistryRemapper.getRegistryRemapper(registry);

		if (registryRemapper == null) {
			registryRemapper = RegistryRemapper.DEFAULT_CLIENT_INSTANCE;
		}

		while (id < registryRemapper.getMinId()
				|| !(getIdList(registry).fromInt(id) == null && getIdList(registry).fromInt(id + offset) == null)
		) {
			id++;
		}

		return new NumericalIdPair(id, id + offset);
	}

	public static int nextId(IdListCompat<?> idList, SimpleRegistryCompat<?, ?> registry, int minId) {
		return nextId(idList, registry, HashBiMap.create(), minId);
	}

	public static int nextId(IdListCompat<?> idList, SimpleRegistryCompat<?, ?> registry) {
		return nextId(idList, registry, 0);
	}

	public static int nextId(IdListCompat<?> idList, SimpleRegistryCompat<?, ?> registry, BiMap<Identifier, Integer> missingMap) {
		return nextId(idList, registry, missingMap, 0);
	}

	public static int nextId(IdListCompat<?> idList, SimpleRegistryCompat<?, ?> registry, BiMap<Identifier, Integer> missingMap, int minId) {
		int id = minId;

		RegistryRemapper<?> registryRemapper = RegistryRemapper.getRegistryRemapper(registry);

		if (registryRemapper == null) {
			registryRemapper = RegistryRemapper.DEFAULT_CLIENT_INSTANCE;
		}

		while (idList.fromInt(id) != null
				|| id < registryRemapper.getMinId()
				|| missingMap.containsValue(id)
		) {
			id++;
		}

		return id;
	}

	public static <K, V> IdListCompat<V> getIdList(SimpleRegistryCompat<K, V> registry) {
		return registry.getIds();
	}

	public static <K, V> BiMap<V, K> getObjects(SimpleRegistryCompat<K, V> registry) {
		return (BiMap<V, K>) registry.getObjects();
	}

	public static <K, V> IdentityHashMap<V, Integer> getIdMap(IdListCompat<V> idList, SimpleRegistryCompat<K, V> registry) {
		return idList.getIdMap(registry);
	}

	public static <K, V> IdentityHashMap<V, Integer> getIdMap(SimpleRegistryCompat<K, V> registry) {
		return getIdMap(getIdList(registry), registry);
	}
}
