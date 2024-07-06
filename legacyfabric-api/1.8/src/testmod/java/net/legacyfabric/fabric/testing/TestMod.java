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

import net.legacyfabric.fabric.api.effect.PotionHelper;

import net.legacyfabric.fabric.api.entity.EntityHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;

import net.legacyfabric.fabric.api.registry.v1.RegistryHelper;
import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
import net.legacyfabric.fabric.api.registry.v2.event.RegistryInitializedEvent;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.Registry;
import net.legacyfabric.fabric.api.resource.ItemModelRegistry;
import net.legacyfabric.fabric.api.util.Identifier;

public class TestMod implements ModInitializer {
	static Identifier blockEntityTestId = new Identifier("legacy-fabric-api:test_block_entity");
	public static StatusEffect EFFECT;
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

		Item testItem = new Item().setItemGroup(ItemGroup.FOOD);
		RegistryHelper.registerItem(testItem, new Identifier("legacy-fabric-api", "test_item"));
		ItemModelRegistry.registerItemModel(testItem, new Identifier("legacy-fabric-api:test_item"));

		Block blockWithEntity = new TestBlockWithEntity(Material.DIRT).setItemGroup(ItemGroup.FOOD);
		RegistryHelper.registerBlock(blockWithEntity, blockEntityTestId);
		RegistryHelper.registerItem(new BlockItem(blockWithEntity), blockEntityTestId);

		RegistryInitializedEvent.event(RegistryIds.BLOCK_ENTITY_TYPES).register(this::registerBlockEntity);

		Identifier potionId = new Identifier("legacy-fabric-api", "test_effect");
		EFFECT = net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(RegistryIds.STATUS_EFFECTS, potionId,
				id -> new TestStatusEffect(id, new net.minecraft.util.Identifier(potionId.toString()), false, 1234567)
						.method_2440(3, 1)
						.method_2434(0.25)
		);
		PotionHelper.registerLevels(EFFECT, "!0 & !1 & !2 & !3 & 1+6");
		PotionHelper.registerAmplifyingFactor(EFFECT, "5");

		Identifier creeperId = new Identifier("legacy-fabric-api", "test_creeper");
		net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(RegistryIds.ENTITY_TYPES, creeperId, TestCreeperEntity.class);
		EntityHelper.registerSpawnEgg(creeperId, 12222, 563933);

		RegistryInitializedEvent.event(RegistryIds.ENCHANTMENTS).register(this::registerEnchantment);
	}

	public void registerEnchantment(Registry<?> registry) {
		System.err.println("Registering enchantment");
		Identifier enchantmentId = new Identifier("legacy-fabric-api", "test_enchantment");
		net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(RegistryIds.ENCHANTMENTS, enchantmentId,
				(id) -> new TestEnchantment(id, enchantmentId));
	}

	public static class TestEnchantment extends Enchantment {
		protected TestEnchantment(int id, Identifier identifier) {
			super(id, new net.minecraft.util.Identifier(identifier.toString()), 2, EnchantmentTarget.FEET);
		}

		@Override
		public void onDamage(LivingEntity livingEntity, Entity entity, int power) {
			livingEntity.addStatusEffect(new StatusEffectInstance(EFFECT.id, 50, 10));
		}

		@Override
		public void onDamaged(LivingEntity livingEntity, Entity entity, int power) {
			livingEntity.addStatusEffect(new StatusEffectInstance(EFFECT.id, 50, 10));
		}
	}

	public static class TestCreeperEntity extends CreeperEntity {

		public TestCreeperEntity(World world) {
			super(world);
		}

		@Override
		public void tick() {
			if (this.isAlive()) {
				if (this.hasStatusEffect(EFFECT)) {
					this.ignite();
				}
			}

			super.tick();
		}
	}

	public static class TestStatusEffect extends StatusEffect {

		public TestStatusEffect(int i, net.minecraft.util.Identifier identifier, boolean bl, int j) {
			super(i, identifier, bl, j);
		}

		@Override
		public void method_6087(LivingEntity livingEntity, int i) {
			if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
				livingEntity.heal(1.0F);
			}
		}

		@Override
		public boolean canApplyUpdateEffect(int duration, int amplifier) {
			int i;

			i = 50 >> amplifier;
			if (i > 0) {
				return duration % i == 0;
			} else {
				return true;
			}
		}
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
		public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
			if (!world.isClient) {
				BlockEntity entity = world.getBlockEntity(pos);

				if (entity instanceof TestBlockEntity) {
					player.sendMessage(new LiteralText(entity + " at " + pos.toString()));
				}
			}

			return true;
		}
	}

	public static class TestBlockEntity extends BlockEntity {
	}
}
