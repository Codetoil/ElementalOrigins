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

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TargetActionKnowingDamageOnHitPower extends CooldownPower
{
	private final Predicate<Pair<DamageSource, Float>> damageCondition;
	private final Predicate<Entity> targetCondition;
	private final Consumer<Pair<Entity, Float>> entityDamagePairAction;

	public TargetActionKnowingDamageOnHitPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Predicate<Pair<DamageSource, Float>> damageCondition, Consumer<Pair<Entity, Float>> entityDamagePairAction, Predicate<Entity> targetCondition)
	{
		super(type, entity, cooldownDuration, hudRender);
		this.damageCondition = damageCondition;
		this.entityDamagePairAction = entityDamagePairAction;
		this.targetCondition = targetCondition;
	}

	public void onHit(Entity target, DamageSource damageSource, float damageAmount)
	{
		if (targetCondition == null || targetCondition.test(target))
		{
			if (damageCondition == null || damageCondition.test(new Pair<>(damageSource, damageAmount)))
			{
				if (canUse())
				{
					this.entityDamagePairAction.accept(new Pair<>(target, damageAmount));
					use();
				}
			}
		}
	}

	public static PowerFactory<TargetActionKnowingDamageOnHitPower> createFactory()
	{
		return new PowerFactory<TargetActionKnowingDamageOnHitPower>(new Identifier("elementalorigins:target_action_knowing_damage_on_hit"),
				new SerializableData()
						.add("entity_damage_pair_action", ElementalOrigins.ENTITY_DAMAGE_PAIR_ACTION_FACTORY_DATA_TYPE)
						.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
						.add("cooldown", SerializableDataTypes.INT, 1)
						.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
						.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null),
				data ->
						(type, player) -> new TargetActionKnowingDamageOnHitPower(type, player,
								data.getInt("cooldown"),
								data.get("hud_render"),
								data.get("damage_condition"),
								data.get("entity_damage_pair_action"),
								data.get("target_condition")))
				.allowCondition();
	}
}
