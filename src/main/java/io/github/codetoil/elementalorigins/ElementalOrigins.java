package io.github.codetoil.elementalorigins;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.action.ActionType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.codetoil.elementalorigins.elements.ElementManager;
import io.github.codetoil.elementalorigins.power.ActionKnowingDamageOnHitPower;
import io.github.codetoil.elementalorigins.power.SelfActionKnowingDamageOnHitPower;
import io.github.codetoil.elementalorigins.power.TargetActionKnowingDamageOnHitPower;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.tuples.Triplet;

import java.util.List;

import static io.github.apace100.apoli.data.ApoliDataTypes.action;

public class ElementalOrigins implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("elementalorigins");


	public static Registry<ActionFactory<Pair<Entity, Float>>> ENTITY_FLOAT_PAIR_ACTION_FACTORY_REG;
	public static Registry<ActionFactory<Triplet<Entity, Entity, Float>>> BIENTITY_FLOAT_TRIPLET_ACTION_FACTORY_REG;

	public static ActionType<Pair<Entity, Float>> ENTITY_FLOAT_PAIR_ACTION_TYPE;
	public static ActionType<Triplet<Entity, Entity, Float>> BIENTITY_FLOAT_TRIPLET_ACTION_TYPE;

	public static SerializableDataType<ActionFactory<Pair<Entity, Float>>.Instance> ENTITY_FLOAT_PAIR_ACTION_FACTORY_DATA_TYPE;
	public static SerializableDataType<ActionFactory<Triplet<Entity, Entity, Float>>.Instance> BIENTITY_FLOAT_TRIPLET_ACTION_FACTORY_DATA_TYPE;

	public static SerializableDataType<List<ActionFactory<Pair<Entity, Float>>.Instance>> ENTITY_FLOAT_PAIRS_DATA_TYPE;
	public static SerializableDataType<List<ActionFactory<Triplet<Entity, Entity, Float>>.Instance>> BIENTITY_FLOAT_TRIPLETS_DATA_TYPE;


	@Override
	@SuppressWarnings("deprecation")
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing Elemental Origins");

		ENTITY_FLOAT_PAIR_ACTION_FACTORY_REG = FabricRegistryBuilder.createSimple(ClassUtil.<ActionFactory<Pair<Entity, Float>>>castClass(ActionFactory.class), new Identifier("elementalorigins:entity_float_pair_action")).buildAndRegister();
		BIENTITY_FLOAT_TRIPLET_ACTION_FACTORY_REG = FabricRegistryBuilder.createSimple(ClassUtil.<ActionFactory<Triplet<Entity, Entity, Float>>>castClass(ActionFactory.class), new Identifier("elementalorigins:bientity_float_triplet_action")).buildAndRegister();

		ActionFactory<Pair<Entity, Float>> percentageDamageAction = new ActionFactory<>(new Identifier("elementalorigins", "percentage_damage"), new SerializableData()
				.add("percentage", SerializableDataTypes.FLOAT)
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE),
				(data, entityFloatPair) -> {
					Entity entity = entityFloatPair.getLeft();
					Float damage = entityFloatPair.getRight();
					entity.damage(data.get("damage_type"), damage * (1.0f + (Float) data.get("percentage") / 100.0f));
				});
		ActionFactory<Triplet<Entity, Entity, Float>> biEntityPercentageDamageAction = new ActionFactory<>(new Identifier("elementalorigins", "bientity_percentage_damage"), new SerializableData()
				.add("percentage", SerializableDataTypes.FLOAT)
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE),
				(data, biEntityFloatTriplet) -> {
					Entity entityA = biEntityFloatTriplet.getA();
					Entity entityB = biEntityFloatTriplet.getB();
					Float damage = biEntityFloatTriplet.getC();
					float extraDamage = damage * (1.0f + (Float) data.get("percentage") / 100.0f);
					entityA.damage(data.get("damage_type"), extraDamage);
					entityB.damage(data.get("damage_type"), extraDamage);
				});

		Registry.register(ENTITY_FLOAT_PAIR_ACTION_FACTORY_REG, percentageDamageAction.getSerializerId(), percentageDamageAction);
		Registry.register(BIENTITY_FLOAT_TRIPLET_ACTION_FACTORY_REG, biEntityPercentageDamageAction.getSerializerId(), biEntityPercentageDamageAction);

		ENTITY_FLOAT_PAIR_ACTION_TYPE = new ActionType<>("EntityFloatPair", ENTITY_FLOAT_PAIR_ACTION_FACTORY_REG);
		BIENTITY_FLOAT_TRIPLET_ACTION_TYPE = new ActionType<>("BiEntityFloatTriplet", BIENTITY_FLOAT_TRIPLET_ACTION_FACTORY_REG);

		ENTITY_FLOAT_PAIR_ACTION_FACTORY_DATA_TYPE = action(ClassUtil.castClass(ActionFactory.Instance.class), ENTITY_FLOAT_PAIR_ACTION_TYPE);
		BIENTITY_FLOAT_TRIPLET_ACTION_FACTORY_DATA_TYPE = action(ClassUtil.castClass(ActionFactory.Instance.class), BIENTITY_FLOAT_TRIPLET_ACTION_TYPE);

		ENTITY_FLOAT_PAIRS_DATA_TYPE = SerializableDataType.list(ENTITY_FLOAT_PAIR_ACTION_FACTORY_DATA_TYPE);
		BIENTITY_FLOAT_TRIPLETS_DATA_TYPE = SerializableDataType.list(BIENTITY_FLOAT_TRIPLET_ACTION_FACTORY_DATA_TYPE);


		PowerFactory<SelfActionKnowingDamageOnHitPower> selfActionKnowingDamageOnHitPowerPowerFactory =
				SelfActionKnowingDamageOnHitPower.createFactory();
		PowerFactory<TargetActionKnowingDamageOnHitPower> targetActionKnowingDamageOnHitPowerPowerFactory =
				TargetActionKnowingDamageOnHitPower.createFactory();
		PowerFactory<ActionKnowingDamageOnHitPower> actionKnowingDamageOnHitPowerPowerFactory =
				ActionKnowingDamageOnHitPower.createFactory();

		Registry.register(ApoliRegistries.POWER_FACTORY,
				selfActionKnowingDamageOnHitPowerPowerFactory.getSerializerId(),
				selfActionKnowingDamageOnHitPowerPowerFactory);
		Registry.register(ApoliRegistries.POWER_FACTORY,
				targetActionKnowingDamageOnHitPowerPowerFactory.getSerializerId(),
				targetActionKnowingDamageOnHitPowerPowerFactory);
		Registry.register(ApoliRegistries.POWER_FACTORY,
				actionKnowingDamageOnHitPowerPowerFactory.getSerializerId(),
				actionKnowingDamageOnHitPowerPowerFactory);

		ElementManager.init();
	}
}
