package net.legacyfabric.fabric.impl.effect.versioned;

import net.legacyfabric.fabric.mixin.effect.StatusEffectStringsAccessor;

import net.minecraft.entity.effect.StatusEffect;

public class PotionHelperImpl {
	public static void registerLevels(StatusEffect effect, String levels) {
		StatusEffectStringsAccessor.getField_9162().put(effect.getId(), levels);
	}

	public static void registerAmplifyingFactor(StatusEffect effect, String amplifyingFactor) {
		StatusEffectStringsAccessor.getField_9163().put(effect.getId(), amplifyingFactor);
	}
}
