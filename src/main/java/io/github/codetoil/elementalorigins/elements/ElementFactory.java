package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.registry.DataObjectFactory;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.codetoil.elementalorigins.ElementalOrigins;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ElementFactory implements DataObjectFactory<Element>
{
	public static final SerializableData DATA = new SerializableData()
			.add("element_id", SerializableDataTypes.IDENTIFIER)
			.add("origin", SerializableDataTypes.IDENTIFIER)
			.add("blocks", SerializableDataTypes.BLOCK_TAG)
			.add("entities", SerializableDataTypes.ENTITY_TYPE)
			.add("fluids", SerializableDataTypes.FLUID_TAG)
			.add("weaknesses", ElementalOrigins.ELEMENT_TAG_DATA_TYPE)
			.add("strengths", ElementalOrigins.ELEMENT_TAG_DATA_TYPE);


	public static final Identifier ELEMENT_ID = new Identifier("elementalorigins", "element");

	@Override
	public SerializableData getData()
	{
		return DATA;
	}

	@Override
	public Element fromData(SerializableData.Instance instance)
	{
		return new Element(instance.getId("element_id"),
				OriginRegistry.get(instance.getId("origin")),
				TagKey.of(RegistryKeys.BLOCK, instance.getId("blocks")),
				TagKey.of(RegistryKeys.ENTITY_TYPE, instance.getId("entities")),
				TagKey.of(RegistryKeys.FLUID, instance.getId("fluids")),
				TagKey.of(ElementalOrigins.ELEMENT_REG_KEY, instance.getId("weaknesses")),
				TagKey.of(ElementalOrigins.ELEMENT_REG_KEY, instance.getId("strengths"))
		);
	}

	@Override
	public SerializableData.Instance toData(Element element)
	{
		SerializableData.Instance result = DATA.new Instance();
		result.set("element_id", element.id());
		result.set("origin", element.origin().getIdentifier());
		result.set("blocks", element.blocks().id());
		result.set("entities", element.entityTypes().id());
		result.set("fluids", element.fluids().id());
		result.set("weaknesses", element.weaknesses().id());
		result.set("strengths", element.strengths().id());
		return result;
	}
}
