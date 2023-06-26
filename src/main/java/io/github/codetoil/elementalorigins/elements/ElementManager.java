package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.registry.DataObjectRegistry;
import io.github.apace100.calio.resource.OrderedResourceListenerInitializer;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.codetoil.elementalorigins.ElementalOrigins;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ElementManager implements OrderedResourceListenerInitializer
{
	public static final Identifier ELEMENT_ID = new Identifier("elementalorigins", "element");
	public static final Identifier ELEMENT_TAG_ID = new Identifier("elementalorigins", "element_tag");
	public static final ElementFactory ELEMENT_FACTORY = new ElementFactory();
	public static final ElementTagFactory ELEMENT_TAG_FACTORY = new ElementTagFactory();
	public static final DataObjectRegistry<Element> ELEMENT_REGISTRY =
			new DataObjectRegistry.Builder<>(ELEMENT_ID, Element.class)
			.readFromData("elements", true)
			.dataErrorHandler((id, exception) ->
					ElementalOrigins.LOGGER.error("Failed to read element " + id + ", caused by", exception))
			.autoSync()
			.defaultFactory(ELEMENT_FACTORY)
			.buildAndRegister();
	public static final DataObjectRegistry<ElementTag> ELEMENT_TAG_REGISTRY =
			new DataObjectRegistry.Builder<>(ELEMENT_TAG_ID, ElementTag.class)
			.readFromData("tags/elements", true)
			.dataErrorHandler((id, exception) ->
					ElementalOrigins.LOGGER.error("Failed to read element tag " + id + ", caused by", exception))
			.autoSync()
			.defaultFactory(ELEMENT_TAG_FACTORY)
			.buildAndRegister();

	public static void init() {
		ElementFactory.init();
		ELEMENT_REGISTRY.registerFactory(ELEMENT_ID, ELEMENT_FACTORY);
		ELEMENT_TAG_REGISTRY.registerFactory(ELEMENT_TAG_ID, ELEMENT_TAG_FACTORY);
	}

	public static Set<Element> getEntityElements(Entity entity)
	{
		return ElementManager.ELEMENT_REGISTRY.getIds().stream()
				.map(ElementManager.ELEMENT_REGISTRY::get)
				.filter(Objects::nonNull)
				.filter(element -> (element.origin().equals(Origin.get(entity)
						.get(OriginLayers.getLayer(Origins.identifier("origin"))))
						|| entity.getType().isIn(element.entityTypes()))
				)
				.collect(Collectors.toSet());
	}

	private static Set<Element> getActualWeaknesses(Entity entity, Set<Element> entityElements)
	{
		if (entityElements.size() == 0)
		{
			throw new IllegalStateException("Entity " + entity + " is missing an Element! "
					+ "Assign it elementalorigins:no_element if you wish to opt out on the functionality! "
					+ "(If it's a player, than ignore the statement above, something has gone horribly wrong!)");
		}

		return ElementManager.ELEMENT_REGISTRY.getIds()
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
				).collect(Collectors.toSet());
	}

	public static Set<Element> getActualWeaknesses(Entity entity)
	{
		Set<Element> entityElements = getEntityElements(entity);
		return getActualWeaknesses(entity, entityElements);
	}

	private static Set<Element> getActualStrengths(Entity entity, Set<Element> entityElements)
	{
		if (entityElements.size() == 0)
		{
			throw new IllegalStateException("Entity " + entity + " is missing an Element! "
					+ "Assign it elementalorigins:no_element if you wish to opt out on the functionality! "
					+ "(If it's a player, than ignore the statement above, something has gone horribly wrong!)");
		}

		return ElementManager.ELEMENT_REGISTRY.getIds()
				.stream()
				.map(ElementManager.ELEMENT_REGISTRY::get)
				.filter(Objects::nonNull)
				.filter(element1 -> entityElements.stream().map(Element::strengths)
						.map(ElementTag::values).flatMap(List::stream)
						.map(ElementManager.ELEMENT_REGISTRY::get)
						.filter(element1::equals).count()
						>
						entityElements.stream().map(Element::weaknesses)
								.map(ElementTag::values).flatMap(List::stream)
								.map(ElementManager.ELEMENT_REGISTRY::get)
								.filter(element1::equals).count()
				).collect(Collectors.toSet());
	}
	public static Set<Element> getActualStrengths(Entity entity)
	{
		Set<Element> entityElements = getEntityElements(entity);
		return getActualStrengths(entity, entityElements);
	}

	public static void onSteppedOn(BlockState state, Entity entity)
	{
		Set<Element> entityElements = getEntityElements(entity);
		Set<Element> actualWeaknesses = getActualWeaknesses(entity, entityElements);

		boolean blockIsStrongAgainst = actualWeaknesses.stream().anyMatch(element -> state.isIn(element.blocks()));

		if (blockIsStrongAgainst && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity))
		{
			/*entity.damage(new DamageSource(entityElements.stream().map(Element::id).map(Identifier::getPath)
					.reduce("", (prev, curr) -> prev.isEmpty() ? curr : prev + "-" + curr) + actualWeaknesses
					.stream().filter(element -> state.isIn(element.blocks())).map(Element::id).map(Identifier::getPath)
					.reduce("", (prev, curr) -> prev + "-" + curr)).setBypassesArmor()
					.setUnblockable(), 1.0F);*/
		}
	}

	public static void getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityShapeContext context,
									   CallbackInfoReturnable<VoxelShape> cir)
	{
		if (getEntityElements(context.getEntity()).stream().filter(element -> {
					Origin origin = element.origin();
					return origin.hasPowerType(ElementalOrigins.EARTH_REACH_POWER_TYPE);
				})
				.anyMatch(element -> state.isIn(element.blocks())))
		{
			cir.setReturnValue(VoxelShapes.fullCube());
		}
	}

	@Override
	public void registerResourceListeners(OrderedResourceListenerManager manager) {
		Identifier originData = Origins.identifier("origins");
		IdentifiableResourceReloadListener elementLoader = ELEMENT_REGISTRY.getLoader();
		IdentifiableResourceReloadListener elementTagLoader = ELEMENT_TAG_REGISTRY.getLoader();
		manager.register(ResourceType.SERVER_DATA, elementTagLoader).after(originData).complete();
		manager.register(ResourceType.SERVER_DATA, elementLoader).after(ELEMENT_TAG_ID).complete();
	}
}
