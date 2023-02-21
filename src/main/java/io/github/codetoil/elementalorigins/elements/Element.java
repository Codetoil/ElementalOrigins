package io.github.codetoil.elementalorigins.elements;

import io.github.apace100.calio.registry.DataObject;
import io.github.apace100.calio.registry.DataObjectFactory;
import io.github.apace100.origins.origin.Origin;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public record Element(Identifier id, Origin origin, TagKey<Block> blocks, TagKey<EntityType<?>> entityTypes, TagKey<Fluid> fluids,
                      TagKey<Element> weaknesses, TagKey<Element> strengths) implements DataObject<Element>
{
	public static final ElementFactory ELEMENT_FACTORY = new ElementFactory();
	@Override
	public DataObjectFactory<Element> getFactory()
	{
		return ELEMENT_FACTORY;
	}
}
