package net.legacyfabric.fabric.test.registry;

import net.minecraft.entity.mob.player.PlayerEntity;

import net.ornithemc.osl.blockstates.api.block.state.BlockState;
import net.ornithemc.osl.core.api.util.math.BlockPos;
import net.ornithemc.osl.core.api.util.math.Direction;

import net.minecraft.block.BlockWithBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class TestBlockWithEntity extends BlockWithBlockEntity {
	protected TestBlockWithEntity(Material material) {
		super(AUTO_ASSIGN_ID, material);
	}

	@Override
	@Deprecated
	public boolean use(World world, int x, int y, int z, PlayerEntity player, int face, float faceX, float faceY, float faceZ) {
		return this.use(world, BlockPos.pooled(x, y, z), world.getBlockState(x, y, z), player, Direction.byData3d(face), faceX, faceY, faceZ);
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float faceX, float faceY, float faceZ) {
		if (!world.isMultiplayer) {
			BlockEntity entity = world.getBlockEntity(pos.x(), pos.y(), pos.z());

			if (entity instanceof TestBlockEntity) {
				player.sendMessage(entity + " at " + pos);
			}
		}

		return true;
	}

	@Override
	public BlockEntity createBlockEntity(World world) {
		return new TestBlockEntity();
	}
}
