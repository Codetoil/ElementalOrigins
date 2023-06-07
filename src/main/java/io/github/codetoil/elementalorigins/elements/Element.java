package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.registry.DataObject;
import io.github.apace100.calio.registry.DataObjectFactory;
import io.github.apace100.origins.origin.Origin;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record Element(Identifier id, Origin origin, TagKey<Block> blocks, TagKey<EntityType<?>> entityTypes, TagKey<Fluid> fluids,
                      ElementTag weaknesses, ElementTag strengths) implements DataObject<Element>
{

	@Override
	public DataObjectFactory<Element> getFactory()
	{
		return ElementManager.ELEMENT_FACTORY;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		Element element = (Element) o;
		return id.equals(element.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}
}
