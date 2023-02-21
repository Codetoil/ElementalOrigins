package io.github.codetoil.elementalorigins.elements;

import com.mojang.datafixers.util.Pair;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.*;

public abstract class Element
{
	public static final SerializableData DATA = new SerializableData(); // TODO include in Data

	private final Identifier id;
	private final Origin origin;
	private final TagKey<Block> blocks;
	private final TagKey<EntityType<?>> entityTypes;
	private final TagKey<Fluid> fluids;
	private static final Map<Pair<Identifier, Identifier>, Boolean> weaknessMatrix = new HashMap<>();
	private static final Map<Pair<Identifier, Identifier>, Boolean> strengthMatrix = new HashMap<>();

	public Element(Identifier id)
	{
		this.id = id;
		this.origin = OriginRegistry.get(Element.convertToTagId(id));
		this.blocks = TagKey.of(Registries.BLOCK.getKey(), Element.convertToTagId(id));
		this.entityTypes = TagKey.of(Registries.ENTITY_TYPE.getKey(), Element.convertToTagId(id));
		this.fluids = TagKey.of(Registries.FLUID.getKey(), Element.convertToTagId(id));
	}

	private static Identifier convertToTagId(Identifier id)
	{
		return new Identifier(id.getNamespace(),":element/" + id.getPath());
	}

	public Identifier getId()
	{
		return id;
	}

	public Origin getOrigin()
	{
		return origin;
	}

	public TagKey<Block> getBlocks()
	{
		return blocks;
	}

	public TagKey<EntityType<?>> getEntityTypes()
	{
		return entityTypes;
	}

	public TagKey<Fluid> getFluids()
	{
		return fluids;
	}

	public static boolean isAWeakAgainstB(Element elementA, Element elementB)
	{
		return Element.weaknessMatrix.getOrDefault(new Pair<>(elementA.id, elementB.id), false);
	}

	public static boolean isAStrongAgainstB(Element elementA, Element elementB)
	{
		return Element.strengthMatrix.getOrDefault(new Pair<>(elementA.id, elementB.id), false);
	}

	public static boolean addWeakness(Element elementA, Element elementB)
	{
		Element.weaknessMatrix.put(new Pair<>(elementA.id, elementB.id), true);
		return isAWeakAgainstB(elementA, elementB);
	}

	public static boolean addStrength(Element elementA, Element elementB)
	{
		Element.strengthMatrix.put(new Pair<>(elementA.id, elementB.id), true);
		return isAStrongAgainstB(elementA, elementB);
	}
}
