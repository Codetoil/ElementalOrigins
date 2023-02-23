package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.registry.DataObjectFactory;
import io.github.apace100.origins.origin.OriginRegistry;
import org.spongepowered.include.com.google.common.base.Throwables;

public class ElementFactory implements DataObjectFactory<Element>
{
	private static final SerializableData DATA = new SerializableData()
			.add("element_id", SerializableDataTypes.IDENTIFIER)
			.add("origin", SerializableDataTypes.IDENTIFIER)
			.add("blocks", SerializableDataTypes.BLOCK_TAG)
			.add("entities", SerializableDataTypes.ENTITY_TAG)
			.add("fluids", SerializableDataTypes.FLUID_TAG)
			.add("weaknesses", SerializableDataTypes.IDENTIFIER)
			.add("strengths", SerializableDataTypes.IDENTIFIER);

	@Override
	public SerializableData getData()
	{
		return DATA;
	}

	@Override
	public Element fromData(SerializableData.Instance instance)
	{
		if (ElementManager.ELEMENT_TAG_REGISTRY.get(instance.get("weaknesses")) == null)
		{
			throw new RuntimeException("Weaknesses for element " + instance.getId("element_id") + " are null");
		}
		if (ElementManager.ELEMENT_TAG_REGISTRY.get(instance.get("strengths")) == null)
		{
			throw new RuntimeException("Strengths for element " + instance.getId("element_id") + " are null");
		}
		return new Element(instance.getId("element_id"),
				OriginRegistry.get(instance.getId("origin")),
				instance.get("blocks"),
				instance.get("entities"),
				instance.get("fluids"),
				ElementManager.ELEMENT_TAG_REGISTRY.get(instance.get("weaknesses")),
				ElementManager.ELEMENT_TAG_REGISTRY.get(instance.get("strengths"))
		);
	}

	@Override
	public SerializableData.Instance toData(Element element)
	{
		SerializableData.Instance result = DATA.new Instance();
		result.set("element_id", element.id());
		result.set("origin", element.origin().getIdentifier());
		result.set("blocks", element.blocks());
		result.set("entities", element.entityTypes());
		result.set("fluids", element.fluids());
		result.set("weaknesses", ElementManager.ELEMENT_TAG_REGISTRY.getId(element.weaknesses()));
		result.set("strengths", ElementManager.ELEMENT_TAG_REGISTRY.getId(element.strengths()));
		return result;
	}

	public static void init()
	{
	}
}
