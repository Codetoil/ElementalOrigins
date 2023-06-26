package io.github.codetoil.elementalorigins.mixins;

import io.github.codetoil.elementalorigins.elements.ElementManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin
{
	@Inject(method="onSteppedOn", at=@At("HEAD"))
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo info) {
		if (world.isClient || entity.bypassesSteppingEffects() || !(entity instanceof LivingEntity)) {
			return;
		}

		ElementManager.onSteppedOn(state, entity);
	}
}
