package net.legacyfabric.fabric.test.registry;

import net.minecraft.entity.mob.monster.CreeperEntity;
import net.minecraft.world.World;

import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import net.ornithemc.osl.core.api.util.NamespacedIdentifiers;
import net.ornithemc.osl.resource.loader.api.resource.ResourcePath;
import net.ornithemc.osl.resource.loader.api.resource.ResourceType;

public class TestCreeperEntity extends CreeperEntity {
	private static final NamespacedIdentifier TEXTURE = NamespacedIdentifiers.from("legacy-fabric-api", "textures/entity/creeper/creeper.png");

	public TestCreeperEntity(World world) {
		super(world);
		this.texture = ResourcePath.nameOf(ResourceType.CLIENT_ASSETS, TEXTURE);
	}

	@Override
	public void tick() {
		//		if (this.isAlive()) {
		//			if (this.hasStatusEffect(EFFECT)) {
		//				this.setIgnited();
		//			}
		//		}

		super.tick();
	}
}
