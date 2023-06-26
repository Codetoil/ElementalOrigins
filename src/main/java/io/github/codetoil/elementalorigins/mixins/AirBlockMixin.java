package io.github.codetoil.elementalorigins.mixins;

import io.github.codetoil.elementalorigins.elements.ElementManager;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AirBlock.class)
public abstract class AirBlockMixin
{
	@Inject(method="getOutlineShape", at=@At("RETURN"), cancellable = true)
	public void getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context,
								CallbackInfoReturnable<VoxelShape> cir)
	{
		if (!cir.getReturnValue().isEmpty() || cir.isCancelled() || !(context instanceof EntityShapeContext))
		{
			return;
		}

		ElementManager.getOutlineShape(state, world, pos, (EntityShapeContext) context, cir);
	}
}
