package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.registry.DataObjectRegistry;
import io.github.apace100.calio.resource.OrderedResourceListenerInitializer;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import io.github.apace100.origins.Origins;
import io.github.codetoil.elementalorigins.ElementalOrigins;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public class ElementManager implements OrderedResourceListenerInitializer
{
	public static final Identifier ELEMENT_ID = new Identifier("elementalorigins", "element");
	public static final Identifier ELEMENT_TAG_ID = new Identifier("elementalorigins", "element_tag");
	public static final ElementFactory ELEMENT_FACTORY = new ElementFactory();
	public static final ElementTagFactory ELEMENT_TAG_FACTORY = new ElementTagFactory();
	public static final DataObjectRegistry<Element> ELEMENT_REGISTRY = new DataObjectRegistry.Builder<>(ELEMENT_ID, Element.class)
			.readFromData("elements", true)
			.dataErrorHandler((id, exception) -> ElementalOrigins.LOGGER.error("Failed to read element " + id + ", caused by", exception))
			.autoSync()
			.defaultFactory(ELEMENT_FACTORY)
			.buildAndRegister();
	public static final DataObjectRegistry<ElementTag> ELEMENT_TAG_REGISTRY = new DataObjectRegistry.Builder<>(ELEMENT_TAG_ID, ElementTag.class)
			.readFromData("tags/elements", true)
			.dataErrorHandler((id, exception) -> ElementalOrigins.LOGGER.error("Failed to read element tag " + id + ", caused by", exception))
			.autoSync()
			.defaultFactory(ELEMENT_TAG_FACTORY)
			.buildAndRegister();

	public static void init() {
		ElementFactory.init();
		ELEMENT_REGISTRY.registerFactory(ELEMENT_ID, ELEMENT_FACTORY);
		ELEMENT_TAG_REGISTRY.registerFactory(ELEMENT_TAG_ID, ELEMENT_TAG_FACTORY);
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
