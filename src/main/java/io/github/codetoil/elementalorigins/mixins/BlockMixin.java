package io.github.codetoil.elementalorigins.mixins;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.codetoil.elementalorigins.elements.Element;
import io.github.codetoil.elementalorigins.elements.ElementManager;
import io.github.codetoil.elementalorigins.elements.ElementTag;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(Block.class)
public abstract class BlockMixin
{
	@Inject(method="onSteppedOn", at=@At("HEAD"))
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo info) {
		if (world.isClient || entity.bypassesSteppingEffects() || !(entity instanceof LivingEntity)) {
			return;
		}

		Set<Element> entityElements = ElementManager.ELEMENT_REGISTRY.getIds().stream()
				.map(ElementManager.ELEMENT_REGISTRY::get)
				.filter(Objects::nonNull)
				.filter(element -> (element.origin().equals(Origin.get(entity)
								.get(OriginLayers.getLayer(Origins.identifier("origin"))))
						|| entity.getType().isIn(element.entityTypes()))
				)
				.collect(Collectors.toSet());

		if (world.getServer() == null)
			throw new IllegalStateException("Server does not exist");
		if (entityElements.size() == 0)
		{
			throw new IllegalStateException("Entity " + entity + " is missing an Element! "
					+ "Assign it elementalorigins:no_element if you wish to opt out on the functionality! "
					+ "(If it's a player, than ignore the statement above, something has gone horribly wrong!)");
		}

		boolean blockIsStrongAgainst = ElementManager.ELEMENT_REGISTRY.getIds()
				.stream()
				.map(ElementManager.ELEMENT_REGISTRY::get)
				.filter(Objects::nonNull)
				.filter(element1 -> entityElements.stream().map(Element::strengths)
						.map(ElementTag::values).flatMap(List::stream)
						.map(ElementManager.ELEMENT_REGISTRY::get)
						.filter(element1::equals).count()
						<
						entityElements.stream().map(Element::weaknesses)
								.map(ElementTag::values).flatMap(List::stream)
								.map(ElementManager.ELEMENT_REGISTRY::get)
								.filter(element1::equals).count()
				)
				.anyMatch(element -> state.isIn(element.blocks()));

		if (blockIsStrongAgainst && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity))
		{
			entity.damage(DamageSource.GENERIC, 1.0F);
		}
	}
}
