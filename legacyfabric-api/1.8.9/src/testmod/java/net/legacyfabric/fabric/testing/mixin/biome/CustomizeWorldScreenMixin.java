package net.legacyfabric.fabric.testing.mixin.biome;

import net.legacyfabric.fabric.api.registry.v2.RegistryHelper;
import net.legacyfabric.fabric.api.registry.v2.RegistryIds;
import net.legacyfabric.fabric.api.registry.v2.registry.holder.SyncedRegistry;

import net.minecraft.client.gui.screen.CustomizeWorldScreen;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CustomizeWorldScreen.class)
public class CustomizeWorldScreenMixin {
	@ModifyArg(method = "initPages",
			at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/client/gui/widget/PagedEntryListWidget$LabelSupplierEntry;<init>(ILjava/lang/String;ZLnet/minecraft/client/gui/widget/SliderWidget$LabelSupplier;FFF)V"),
			index = 5
	)
	private float allowSelectingAllBiomesInSelector(float max) {
		System.out.println(Biome.getBiomes().length);
		SyncedRegistry<Biome> registry = (SyncedRegistry<Biome>) (Object) RegistryHelper.getRegistry(RegistryIds.BIOMES);
		return registry.stream().mapToInt(b -> registry.fabric$getRawId(b)).max().orElse((int) max) - 1;
	}
}
