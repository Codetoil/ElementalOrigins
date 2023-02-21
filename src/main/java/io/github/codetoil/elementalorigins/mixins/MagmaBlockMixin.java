package io.github.codetoil.elementalorigins.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MagmaBlock.class)
public abstract class MagmaBlockMixin extends Block
{
	private MagmaBlockMixin(Settings settings)
	{
		super(settings);
	}

	/**
	 * @author Codetoil
	 * @reason It's moved over to {@link BlockMixin}
	 */
	@Overwrite
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		super.onSteppedOn(world, pos, state, entity);
	}
}
