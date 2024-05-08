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

package net.legacyfabric.fabric.testing;


import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;

import net.legacyfabric.fabric.api.registry.v1.RegistryHelper;
import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
import net.legacyfabric.fabric.api.registry.v2.event.RegistryInitializedEvent;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.Registry;
import net.legacyfabric.fabric.api.util.Identifier;

public class TestMod implements ModInitializer {
	static Identifier blockEntityTestId = new Identifier("legacy-fabric-api:test_block_entity");

	@Override
	public void onInitialize() {
		registerItem();
	}

	private void registerItem() {
		Block concBlock = new Block(Material.STONE).setItemGroup(ItemGroup.FOOD);
		Block concBlock2 = new Block(Material.GLASS).setItemGroup(ItemGroup.FOOD);
		Block[] blocks = ThreadLocalRandom.current().nextBoolean() ? new Block[] {concBlock, concBlock2} : new Block[] {concBlock2, concBlock};

		for (Block block : blocks) {
			Identifier identifier = new Identifier("legacy-fabric-api:conc_block_" + block.getMaterial().getColor().color);
			RegistryHelper.registerBlock(block, identifier);
			RegistryHelper.registerItem(new BlockItem(block), identifier);
		}

		Item testItem = new Item().setItemGroup(ItemGroup.FOOD).getFromId("legacy-fabric-api:test_item");
		RegistryHelper.registerItem(testItem, new Identifier("legacy-fabric-api", "test_item"));

		Block blockWithEntity = new TestBlockWithEntity(Material.DIRT).setItemGroup(ItemGroup.FOOD);
		RegistryHelper.registerBlock(blockWithEntity, blockEntityTestId);
		RegistryHelper.registerItem(new BlockItem(blockWithEntity), blockEntityTestId);

		RegistryInitializedEvent.event(RegistryIds.BLOCK_ENTITY_TYPES).register(this::registerBlockEntity);
	}

	public void registerBlockEntity(Registry<?> registry) {
		System.err.println("Registering block entity");
		RegistryHelper.registerBlockEntityType(TestBlockEntity.class, blockEntityTestId);
	}

	public static class TestBlockWithEntity extends BlockWithEntity {
		protected TestBlockWithEntity(Material material) {
			super(material);
		}

		@Override
		public BlockEntity createBlockEntity(World world, int id) {
			return new TestBlockEntity();
		}

		@Override
		public boolean onActivated(World world, int x, int y, int z, PlayerEntity player, int i, float f, float g, float h) {
			if (!world.isClient) {
				BlockEntity entity = world.getBlockEntity(x, y, z);

				if (entity instanceof TestBlockEntity) {
					player.sendMessage(new LiteralText(entity + " at " + x + "," + y + "," + z));
				}
			}

			return true;
		}
	}

	public static class TestBlockEntity extends BlockEntity {
	}
}
