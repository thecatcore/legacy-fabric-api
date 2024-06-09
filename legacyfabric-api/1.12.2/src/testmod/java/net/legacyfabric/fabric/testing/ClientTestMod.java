package net.legacyfabric.fabric.testing;

import net.fabricmc.api.ClientModInitializer;

import net.legacyfabric.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ClientTestMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(TestMod.TestCreeperEntity.class,
				(entityRenderDispatcher, context) -> new TestCreeperEntityRenderer(entityRenderDispatcher));
	}
}
