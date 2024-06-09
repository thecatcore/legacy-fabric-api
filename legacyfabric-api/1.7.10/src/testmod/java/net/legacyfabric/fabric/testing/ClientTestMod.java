package net.legacyfabric.fabric.testing;

import net.fabricmc.api.ClientModInitializer;

import net.legacyfabric.fabric.api.client.rendering.v1.EntityRendererRegistry;

import net.minecraft.client.render.entity.EntityRenderer;

public class ClientTestMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(TestMod.TestCreeperEntity.class,
				(entityRenderDispatcher, context) -> {
					EntityRenderer renderer = new TestCreeperEntityRenderer();
					renderer.setRenderDispatcher(entityRenderDispatcher);
					return renderer;
				});
	}
}
