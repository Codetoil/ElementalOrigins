package io.github.codetoil.elementalorigins.mixins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.codetoil.elementalorigins.power.ActionKnowingDamageOnHitPower;
import io.github.codetoil.elementalorigins.power.SelfActionKnowingDamageOnHitPower;
import io.github.codetoil.elementalorigins.power.TargetActionKnowingDamageOnHitPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Inject(method = "damage", at = @At("RETURN"))
	private void invokeHitActions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if(cir.getReturnValue()) {
			Entity attacker = source.getAttacker();
			if(attacker != null) {
				PowerHolderComponent.withPowers(attacker, ActionKnowingDamageOnHitPower.class, p -> true, p -> p.onHit((LivingEntity)(Object)this, source, amount));
			}
			PowerHolderComponent.withPowers(attacker, SelfActionKnowingDamageOnHitPower.class, p -> true, p -> p.onHit((LivingEntity)(Object)this, source, amount));
			PowerHolderComponent.withPowers(attacker, TargetActionKnowingDamageOnHitPower.class, p-> true, p -> p.onHit((LivingEntity)(Object)this, source, amount));
		}
	}
}
