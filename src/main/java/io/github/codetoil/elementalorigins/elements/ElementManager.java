package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.registry.DataObjectRegistry;
import io.github.apace100.origins.Origins;

import static io.github.codetoil.elementalorigins.elements.ElementFactory.ELEMENT_ID;

public class ElementManager
{
	public static final DataObjectRegistry<Element> REGISTRY = new DataObjectRegistry.Builder<>(ELEMENT_ID, Element.class)
			.readFromData("element", true)
			.dataErrorHandler((id, exception) -> Origins.LOGGER.error("Failed to read element " + id + ", caused by", exception))
			.defaultFactory(Element.ELEMENT_FACTORY)
			.buildAndRegister();

	public static void init() {}
}
