package net.legacyfabric.fabric.mixin.enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void initEnchantmentRegistry(CallbackInfo ci) {
		try {
			Class.forName(Enchantment.class.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
