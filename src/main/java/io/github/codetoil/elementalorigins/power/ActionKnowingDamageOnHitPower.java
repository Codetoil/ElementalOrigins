package io.github.codetoil.elementalorigins.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.codetoil.elementalorigins.ElementalOrigins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import oshi.util.tuples.Triplet;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionKnowingDamageOnHitPower extends CooldownPower
{
	private final Predicate<Pair<DamageSource, Float>> damageCondition;
	private final Predicate<Pair<Entity, Entity>> bientityCondition;
	private final Consumer<Triplet<Entity, Entity, Float>> bientityDamageTripletAction;

	public ActionKnowingDamageOnHitPower(PowerType<?> type, LivingEntity entity, int cooldownDuration,
										 HudRender hudRender, Predicate<Pair<DamageSource, Float>> damageCondition,
										 Consumer<Triplet<Entity, Entity, Float>> bientityDamageTripletAction,
										 Predicate<Pair<Entity,Entity>> bientityCondition)
	{
		super(type, entity, cooldownDuration, hudRender);
		this.damageCondition = damageCondition;
		this.bientityDamageTripletAction = bientityDamageTripletAction;
		this.bientityCondition = bientityCondition;
	}

	public void onHit(Entity target, DamageSource damageSource, float damageAmount)
	{
		if (bientityCondition == null || bientityCondition.test(new Pair<>(this.entity, target)))
		{
			if (damageCondition == null || damageCondition.test(new Pair<>(damageSource, damageAmount)))
			{
				if (canUse())
				{
					this.bientityDamageTripletAction.accept(new Triplet<>(this.entity, target, damageAmount));
					use();
				}
			}
		}
	}

	public static PowerFactory<ActionKnowingDamageOnHitPower> createFactory()
	{
		return new PowerFactory<ActionKnowingDamageOnHitPower>(
				new Identifier("elementalorigins:action_knowing_damage_on_hit"),
				new SerializableData()
						.add("bientity_damage_triplet_action",
								ElementalOrigins.BIENTITY_DAMAGE_TRIPLET_ACTION_FACTORY_DATA_TYPE)
						.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
						.add("cooldown", SerializableDataTypes.INT, 1)
						.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
						.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
				data ->
						(type, player) -> new ActionKnowingDamageOnHitPower(type, player,
								data.getInt("cooldown"),
								data.get("hud_render"),
								data.get("damage_condition"),
								data.get("bientity_damage_triplet_action"),
								data.get("bientity_condition")))
				.allowCondition();
	}
}
