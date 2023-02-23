package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.registry.DataObject;
import io.github.apace100.calio.registry.DataObjectFactory;
import net.minecraft.util.Identifier;

import java.util.List;

public record ElementTag(List<Identifier> values) implements DataObject<ElementTag>
{
	@Override
	public DataObjectFactory<ElementTag> getFactory()
	{
		return ElementManager.ELEMENT_TAG_FACTORY;
	}
}
