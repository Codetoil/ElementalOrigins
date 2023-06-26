package io.github.codetoil.elementalorigins.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EarthReachPower extends Power
{
	public EarthReachPower(PowerType<?> type, LivingEntity entity)
	{
		super(type, entity);
	}

	public static PowerFactory<EarthReachPower> createFactory()
	{
		return new PowerFactory<EarthReachPower>(new Identifier("elementalorigins:earth_reach"),
				new SerializableData(),
				data -> EarthReachPower::new
		).allowCondition();
	}
}
