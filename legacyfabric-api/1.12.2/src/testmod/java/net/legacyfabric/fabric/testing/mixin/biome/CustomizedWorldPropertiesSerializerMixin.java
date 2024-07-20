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

package net.legacyfabric.fabric.testing.mixin.biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.CustomizedWorldProperties;

@Mixin(CustomizedWorldProperties.Serializer.class)
public class CustomizedWorldPropertiesSerializerMixin {
	@ModifyConstant(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/world/gen/CustomizedWorldProperties$Builder;",
			constant = @Constant(intValue = 38))
	private int fixBiomeSelector(int max) {
		return Biome.REGISTRY.stream().mapToInt(b -> Biome.REGISTRY.fabric$getRawId(b)).max().orElse((int) max);
	}
}
