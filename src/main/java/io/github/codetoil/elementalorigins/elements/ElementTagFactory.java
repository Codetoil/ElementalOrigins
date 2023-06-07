package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.registry.DataObjectFactory;

public class ElementTagFactory implements DataObjectFactory<ElementTag>
{
	private static final SerializableData DATA = new SerializableData()
			.add("values", SerializableDataTypes.IDENTIFIERS);
	@Override
	public SerializableData getData()
	{
		return DATA;
	}

	@Override
	public ElementTag fromData(SerializableData.Instance instance)
	{
		return new ElementTag(instance.get("values"));
	}

	@Override
	public SerializableData.Instance toData(ElementTag elementTag)
	{
		SerializableData.Instance result = DATA.new Instance();
		result.set("values", elementTag.values());
		return result;
	}
}
