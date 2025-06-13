package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(LivingEntity.class)
public class MixinLivingEntity 
{
    @Inject(method = "calculateEntityAnimation", at = @At("HEAD"), cancellable = true)
    private void calculateEntityAnimation(boolean flutter, CallbackInfo ci) 
    {
		LivingEntity entity = LivingEntity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
	        ci.cancel();
			Direction direction = GravityUtil.getGravityDirection(entity);
	        Vec3 playerPosDelta = GravityUtil.vecWorldToPlayer(entity.getX() - entity.xo, entity.getY() - entity.yo, entity.getZ() - entity.zo, direction);
	        double d = playerPosDelta.x;
	        double e = flutter ? playerPosDelta.y : 0.0D;
	        double f = playerPosDelta.z;
	        float g = (float)Math.sqrt(d * d + e * e + f * f) * 4.0F;
	        if(g > 1.0F)
	        {
	            g = 1.0F;
	        }
	        entity.walkAnimation.update(g, 0.4F);
		}
    }
}
