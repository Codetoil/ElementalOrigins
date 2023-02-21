package io.github.codetoil.elementalorigins.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
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
		if (!entity.bypassesSteppingEffects() && entity instanceof LivingEntity) {
			if (state.getBlock().equals(Blocks.MAGMA_BLOCK) && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity))
			{// TODO TEMP, replace with element
				entity.damage(DamageSource.HOT_FLOOR, 1.0F);
			}
		}
	}
}
