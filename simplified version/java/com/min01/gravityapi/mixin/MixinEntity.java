package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.gravityapi.util.GravityBlockPos;
import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public class MixinEntity 
{
    @WrapOperation(method = "makeBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityDimensions;makeBoundingBox(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;"))
    private AABB makeBoundingBox(EntityDimensions dimensions, Vec3 pos, Operation<AABB> original)
    {
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
	    	AABB aabb = dimensions.makeBoundingBox(0, 0, 0);
	    	if(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE)
	    	{
	    		aabb = aabb.move(0.0D, -1.0E-6D, 0.0D);
	    	}
	    	return GravityUtil.boxPlayerToWorld(aabb, direction).move(pos);
		}
		else
		{
    		return original.call(dimensions, pos);
		}
    }
    
    @Inject(method = "blockPosition", at = @At("RETURN"), cancellable = true)
    private void blockPosition(CallbackInfoReturnable<BlockPos> cir)
    {
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
			GravityBlockPos pos = new GravityBlockPos(cir.getReturnValue(), direction);
			cir.setReturnValue(pos);
		}
    }
    
    @Inject(method = "getBoundingBoxForPose", at = @At("RETURN"), cancellable = true)
    private void getBoundingBoxForPose(Pose pose, CallbackInfoReturnable<AABB> cir)
    {
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
	    	AABB aabb = cir.getReturnValue();
	    	if(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE)
	    	{
	    		aabb = aabb.move(0.0D, -1.0E-6D, 0.0D);
	    	}
	    	cir.setReturnValue(GravityUtil.boxPlayerToWorld(aabb, direction));
		}
    }

    @Inject(method = "Lnet/minecraft/world/entity/Entity;getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void getEyePosition(CallbackInfoReturnable<Vec3> cir)
    {
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
	        cir.setReturnValue(GravityUtil.vecPlayerToWorld(0.0D, entity.getEyeHeight(), 0.0D, direction).add(entity.position()));
		}
    }
    
    @Inject(method = "Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void getEyePosition(float tickDelta, CallbackInfoReturnable<Vec3> cir)
    {
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
	        Vec3 vec3 = GravityUtil.vecPlayerToWorld(0.0D, entity.getEyeHeight(), 0.0D, direction);
	        double x = Mth.lerp((double) tickDelta, entity.xo, entity.getX()) + vec3.x;
	        double y = Mth.lerp((double) tickDelta, entity.yo, entity.getY()) + vec3.y;
	        double z = Mth.lerp((double) tickDelta, entity.zo, entity.getZ()) + vec3.z;
	        cir.setReturnValue(new Vec3(x, y, z));
		}
    }
    
	@ModifyVariable(method = "move", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private Vec3 move(Vec3 vec3) 
	{
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
			return GravityUtil.vecPlayerToWorld(vec3, direction);
		}
		return vec3;
	}
	
	@ModifyVariable(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0), ordinal = 0, argsOnly = true)
	private Vec3 pop(Vec3 vec3) 
	{
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
			return GravityUtil.vecWorldToPlayer(vec3, direction);
		}
		return vec3;
	}

	@ModifyVariable(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0), ordinal = 1)
	private Vec3 pop1(Vec3 vec3) 
	{
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
			return GravityUtil.vecWorldToPlayer(vec3, direction);
		}
		return vec3;
	}
	
    @Redirect(method = "setPos(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPosRaw(DDD)V"))
    private void setPos(Entity instance, double x, double y, double z)
    {
		Direction direction = GravityUtil.getGravityDirection(instance);
        Vec3 pos = new Vec3(x, y, z);
		Vec3 motion = pos.subtract(instance.position());
		Vec3 gravity = GravityUtil.vecPlayerToWorld(motion, direction);
		Vec3 vec3 = instance.position().add(gravity);
		if(GravityUtil.hasGravity(instance) && instance instanceof Projectile)
		{
			instance.setPosRaw(vec3.x, vec3.y, vec3.z);
		}
		else
		{
			instance.setPosRaw(x, y, z);
		}
    }
    
    @Inject(method = "calculateViewVector", at = @At("RETURN"), cancellable = true)
    private void calculateViewVector(CallbackInfoReturnable<Vec3> cir)
    {
		Entity entity = Entity.class.cast(this);
		if(GravityUtil.hasGravity(entity))
		{
			Direction direction = GravityUtil.getGravityDirection(entity);
	        cir.setReturnValue(GravityUtil.vecPlayerToWorld(cir.getReturnValue(), direction));
		}
    }
}
