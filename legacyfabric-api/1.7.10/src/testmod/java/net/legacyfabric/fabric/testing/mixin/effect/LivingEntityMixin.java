package net.legacyfabric.fabric.testing.mixin.effect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import net.legacyfabric.fabric.testing.TestMod;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	public abstract EntityGroup getGroup();

	@Redirect(method = {"method_2490", "jump"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEffectInstance(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/entity/effect/StatusEffectInstance;"))
	private StatusEffectInstance ourEffectJumpsHighAsWell(LivingEntity instance, StatusEffect effect) {
		StatusEffectInstance instance1 = instance.getEffectInstance(effect);

		if (instance1 == null) {
			instance1 = instance.getEffectInstance(TestMod.EFFECT);
		}

		return instance1;
	}

	@Redirect(method = {"jump"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
	private boolean hasOurEffectJumpsHighAsWell(LivingEntity instance, StatusEffect effect) {
		boolean instance1 = instance.hasStatusEffect(effect);

		if (!instance1) {
			instance1 = instance.hasStatusEffect(TestMod.EFFECT);
		}

		return instance1;
	}

	@Inject(method = "method_2658", at = @At("RETURN"), cancellable = true)
	private void efffffffect(StatusEffectInstance instance, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {
			if (this.getGroup() == EntityGroup.UNDEAD && instance.getEffectId() == TestMod.EFFECT.id) cir.setReturnValue(false);
		}
	}
}
