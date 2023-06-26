package io.github.codetoil.elementalorigins;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.action.ActionType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.codetoil.elementalorigins.elements.ElementManager;
import io.github.codetoil.elementalorigins.power.ActionKnowingDamageOnHitPower;
import io.github.codetoil.elementalorigins.power.EarthReachPower;
import io.github.codetoil.elementalorigins.power.SelfActionKnowingDamageOnHitPower;
import io.github.codetoil.elementalorigins.power.TargetActionKnowingDamageOnHitPower;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
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



	public static RegistryKey<Registry<ActionFactory<Pair<Entity, Float>>>> ENTITY_DAMAGE_PAIR_ACTION_FACTORY_REG_KEY;
	public static RegistryKey<Registry<ActionFactory<Triplet<Entity, Entity, Float>>>>
			BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_REG_KEY;
	public static Registry<ActionFactory<Pair<Entity, Float>>> ENTITY_DAMAGE_PAIR_ACTION_FACTORY_REG;
	public static Registry<ActionFactory<Triplet<Entity, Entity, Float>>> BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_REG;

	public static ActionType<Pair<Entity, Float>> ENTITY_DAMAGE_PAIR_ACTION_TYPE;
	public static ActionType<Triplet<Entity, Entity, Float>> BIENTITY_DAMAGE_TRIPLET_ACTION_TYPE;

	public static SerializableDataType<ActionFactory<Pair<Entity, Float>>.Instance>
			ENTITY_DAMAGE_PAIR_ACTION_FACTORY_DATA_TYPE;
	public static SerializableDataType<ActionFactory<Triplet<Entity, Entity, Float>>.Instance>
			BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_DATA_TYPE;

	public static SerializableDataType<List<ActionFactory<Pair<Entity, Float>>.Instance>> ENTITY_DAMAGE_PAIRS_DATA_TYPE;
	public static SerializableDataType<List<ActionFactory<Triplet<Entity, Entity, Float>>.Instance>>
			BIENTITY_DAMAGE_TRIPLETS_DATA_TYPE;

	public static PowerType<SelfActionKnowingDamageOnHitPower> SELF_ACTION_KNOWING_DAMAGE_ON_HIT_POWER_TYPE;
	public static PowerType<TargetActionKnowingDamageOnHitPower> TARGET_ACTION_KNOWING_DAMAGE_ON_HIT_POWER_TYPE;
	public static PowerType<ActionKnowingDamageOnHitPower> ACTION_KNOWING_DAMAGE_ON_HIT_POWER_TYPE;
	public static PowerType<EarthReachPower> EARTH_REACH_POWER_TYPE;


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing Elemental Origins");

		ENTITY_DAMAGE_PAIR_ACTION_FACTORY_REG_KEY =
				RegistryKey.ofRegistry(new Identifier("elementalorigins:entity_damage_pair_action"));
		BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_REG_KEY =
				RegistryKey.ofRegistry(new Identifier("elementalorigins:bientity_damage_triplet_action"));
		ENTITY_DAMAGE_PAIR_ACTION_FACTORY_REG = FabricRegistryBuilder
				.createSimple(ENTITY_DAMAGE_PAIR_ACTION_FACTORY_REG_KEY).buildAndRegister();
		BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_REG = FabricRegistryBuilder
				.createSimple(BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_REG_KEY).buildAndRegister();

		ActionFactory<Pair<Entity, Float>> percentageDamageAction =
				new ActionFactory<>(new Identifier("elementalorigins", "percentage_damage"),
						new SerializableData()
				.add("percentage", SerializableDataTypes.FLOAT)
				.add("source", ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, null)
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null),
				(data, entityFloatPair) -> {
					Entity entity = entityFloatPair.getLeft();
					Float damage = entityFloatPair.getRight();
					DamageSource damageSource = MiscUtil.createDamageSource(
							entity.getDamageSources(), data.get("source"), data.get("damage_type"));
					entity.damage(damageSource, damage * (1.0f + (Float) data.get("percentage") / 100.0f));
				});
		ActionFactory<Triplet<Entity, Entity, Float>> biEntityPercentageDamageAction =
				new ActionFactory<>(new Identifier("elementalorigins", "bientity_percentage_damage"),
						new SerializableData()
				.add("percentage", SerializableDataTypes.FLOAT)
				.add("source", ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, null)
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null),
				(data, biEntityFloatTriplet) -> {
					Entity entityA = biEntityFloatTriplet.getA();
					Entity entityB = biEntityFloatTriplet.getB();
					DamageSource source = MiscUtil.createDamageSource(
							entityA.getDamageSources(), data.get("source"), data.get("damage_type"),
							entityA);
					Float damage = biEntityFloatTriplet.getC();
					float extraDamage = damage * (1.0f + (Float) data.get("percentage") / 100.0f);
					entityB.damage(source, extraDamage);
				});

		Registry.register(ENTITY_DAMAGE_PAIR_ACTION_FACTORY_REG, percentageDamageAction.getSerializerId(),
				percentageDamageAction);
		Registry.register(BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_REG, biEntityPercentageDamageAction.getSerializerId(),
				biEntityPercentageDamageAction);

		ENTITY_DAMAGE_PAIR_ACTION_TYPE = new ActionType<>("EntityDamagePair",
				ENTITY_DAMAGE_PAIR_ACTION_FACTORY_REG);
		BIENTITY_DAMAGE_TRIPLET_ACTION_TYPE = new ActionType<>("BiEntityDamageTriplet",
				BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_REG);

		ENTITY_DAMAGE_PAIR_ACTION_FACTORY_DATA_TYPE = action(ClassUtil.castClass(ActionFactory.Instance.class),
				ENTITY_DAMAGE_PAIR_ACTION_TYPE);
		BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_DATA_TYPE = action(ClassUtil.castClass(ActionFactory.Instance.class),
				BIENTITY_DAMAGE_TRIPLET_ACTION_TYPE);

		ENTITY_DAMAGE_PAIRS_DATA_TYPE = SerializableDataType.list(ENTITY_DAMAGE_PAIR_ACTION_FACTORY_DATA_TYPE);
		BIENTITY_DAMAGE_TRIPLETS_DATA_TYPE =
				SerializableDataType.list(BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_DATA_TYPE);


		PowerFactory<SelfActionKnowingDamageOnHitPower> selfActionKnowingDamageOnHitPowerPowerFactory =
				SelfActionKnowingDamageOnHitPower.createFactory();
		PowerFactory<TargetActionKnowingDamageOnHitPower> targetActionKnowingDamageOnHitPowerPowerFactory =
				TargetActionKnowingDamageOnHitPower.createFactory();
		PowerFactory<ActionKnowingDamageOnHitPower> actionKnowingDamageOnHitPowerPowerFactory =
				ActionKnowingDamageOnHitPower.createFactory();

		PowerFactory<EarthReachPower> earthReachPowerPowerFactory = EarthReachPower.createFactory();

		Registry.register(ApoliRegistries.POWER_FACTORY,
				selfActionKnowingDamageOnHitPowerPowerFactory.getSerializerId(),
				selfActionKnowingDamageOnHitPowerPowerFactory);
		Registry.register(ApoliRegistries.POWER_FACTORY,
				targetActionKnowingDamageOnHitPowerPowerFactory.getSerializerId(),
				targetActionKnowingDamageOnHitPowerPowerFactory);
		Registry.register(ApoliRegistries.POWER_FACTORY,
				actionKnowingDamageOnHitPowerPowerFactory.getSerializerId(),
				actionKnowingDamageOnHitPowerPowerFactory);

		Registry.register(ApoliRegistries.POWER_FACTORY, earthReachPowerPowerFactory.getSerializerId(),
				earthReachPowerPowerFactory);

		SELF_ACTION_KNOWING_DAMAGE_ON_HIT_POWER_TYPE =
				new PowerTypeReference<>(selfActionKnowingDamageOnHitPowerPowerFactory.getSerializerId());
		TARGET_ACTION_KNOWING_DAMAGE_ON_HIT_POWER_TYPE =
				new PowerTypeReference<>(targetActionKnowingDamageOnHitPowerPowerFactory.getSerializerId());
		ACTION_KNOWING_DAMAGE_ON_HIT_POWER_TYPE =
				new PowerTypeReference<>(actionKnowingDamageOnHitPowerPowerFactory.getSerializerId());
		EARTH_REACH_POWER_TYPE = new PowerTypeReference<>(earthReachPowerPowerFactory.getSerializerId());

		ElementManager.init();
	}
}
