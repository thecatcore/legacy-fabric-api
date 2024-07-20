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

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectStrings;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.PlainsBiome;

import net.fabricmc.api.ModInitializer;

import net.legacyfabric.fabric.api.entity.EntityHelper;
import net.legacyfabric.fabric.api.registry.v1.RegistryHelper;
import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
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
		Block concBlock = new Block(Material.STONE, MaterialColor.BLACK).setItemGroup(ItemGroup.FOOD);
		Block concBlock2 = new Block(Material.STONE, MaterialColor.BLUE).setItemGroup(ItemGroup.FOOD);
		Block[] blocks = ThreadLocalRandom.current().nextBoolean() ? new Block[] {concBlock, concBlock2} : new Block[] {concBlock2, concBlock};

		for (Block block : blocks) {
			int color = 1644825;

			if (block == concBlock2) {
				color = 3361970;
			}

			Identifier identifier = new Identifier("legacy-fabric-api", "conc_block_" + color);

			net.legacyfabric.fabric.api.registry.v1.RegistryHelper.registerBlock(block, identifier);
			net.legacyfabric.fabric.api.registry.v1.RegistryHelper.registerItem(new BlockItem(block), identifier);
		}

		Item testItem = new Item().setItemGroup(ItemGroup.FOOD);
		RegistryHelper.registerItem(testItem, new Identifier("legacy-fabric-api", "test_item"));
		ItemModelRegistry.registerItemModel(testItem, new Identifier("legacy-fabric-api:test_item"));

		Block blockWithEntity = new TestBlockWithEntity(Material.DIRT).setItemGroup(ItemGroup.FOOD);
		RegistryHelper.registerBlock(blockWithEntity, blockEntityTestId);
		RegistryHelper.registerItem(new BlockItem(blockWithEntity), blockEntityTestId);
		RegistryHelper.registerBlockEntityType(TestBlockEntity.class, blockEntityTestId);

		Identifier potionId = new Identifier("legacy-fabric-api", "test_effect");
		EFFECT = new TestStatusEffect(false, 1234567).method_2440(3, 1).method_2434(0.25).method_12944();
		net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(StatusEffect.REGISTRY, potionId, EFFECT);
		Potion potion = new Potion(new StatusEffectInstance(EFFECT, 3600, 5));
		net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(Potion.REGISTRY, potionId, potion);
		StatusEffectStrings.method_11420(Potions.LEAPING, new StatusEffectStrings.class_2696(Items.GLISTERING_MELON), potion);

		Identifier creeperId = new Identifier("legacy-fabric-api", "test_creeper");
		net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(RegistryIds.ENTITY_TYPES, creeperId, TestCreeperEntity.class);
		EntityHelper.registerSpawnEgg(creeperId, 12222, 563933);

		Identifier enchantmentId = new Identifier("legacy-fabric-api", "test_enchantment");
		net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(Enchantment.REGISTRY, enchantmentId, new TestEnchantment());

		Identifier biomeId = new Identifier("legacy-fabric-api", "test_biome");
		net.legacyfabric.fabric.api.registry.v2.RegistryHelper.register(Biome.REGISTRY, biomeId, new TestBiome(false,
				new Biome.Settings("Test Biome").setBaseHeightModifier(0.525F).setVariationModifier(0.95F).setTemperature(0.3F).setDownfall(0.7F)));
	}

	public static class TestBiome extends PlainsBiome {
		protected TestBiome(boolean bl, Settings settings) {
			super(bl, settings);
		}
	}

	public static class TestEnchantment extends Enchantment {
		protected TestEnchantment() {
			super(Rarity.COMMON, EnchantmentTarget.FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
		}

		@Override
		public void onDamage(LivingEntity livingEntity, Entity entity, int power) {
			livingEntity.addStatusEffect(new StatusEffectInstance(EFFECT, 50, 10));
		}

		@Override
		public void onDamaged(LivingEntity livingEntity, Entity entity, int power) {
			livingEntity.addStatusEffect(new StatusEffectInstance(EFFECT, 50, 10));
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

		public TestStatusEffect(boolean bl, int i) {
			super(bl, i);
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

	public static class TestBlockWithEntity extends BlockWithEntity {
		protected TestBlockWithEntity(Material material) {
			super(material);
		}

		@Override
		public BlockEntity createBlockEntity(World world, int id) {
			return new TestBlockEntity();
		}

		@Override
		public boolean method_421(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity, Hand hand, @Nullable ItemStack itemStack, Direction direction, float f, float g, float h) {
			if (!world.isClient) {
				BlockEntity entity = world.getBlockEntity(blockPos);

				if (entity instanceof TestBlockEntity) {
					playerEntity.sendMessage(new LiteralText(entity + " at " + blockPos.toString()));
				}
			}

			return true;
		}
	}

	public static class TestBlockEntity extends BlockEntity {
	}
}
