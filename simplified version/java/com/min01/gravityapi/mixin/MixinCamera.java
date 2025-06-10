package com.min01.gravityapi.mixin;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

@Mixin(Camera.class)
public abstract class MixinCamera 
{
    @Shadow
    protected abstract void setPosition(double x, double y, double z);
    
    @Shadow
    private Entity entity;
    
    @Shadow
    @Final
    private Quaternionf rotation;
    
    @Shadow
    private float eyeHeightOld;
    
    @Shadow
    private float eyeHeight;
    
    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", ordinal = 0))
    private void setPosition(Camera camera, double x, double y, double z, Operation<Void> original, BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta)
    {
    	if(GravityUtil.hasGravity(focusedEntity))
    	{
            Direction direction = GravityUtil.getGravityDirection(focusedEntity);
            Quaternionf quat = GravityUtil.getWorldRotationQuaternion(direction);
            
            double entityX = Mth.lerp((double) tickDelta, focusedEntity.xo, focusedEntity.getX());
            double entityY = Mth.lerp((double) tickDelta, focusedEntity.yo, focusedEntity.getY());
            double entityZ = Mth.lerp((double) tickDelta, focusedEntity.zo, focusedEntity.getZ());
            
            double currentCameraY = Mth.lerp(tickDelta, this.eyeHeightOld, this.eyeHeight);
            Quaternionf quat2 = new Quaternionf(quat).conjugate();
        
            Vec3 eyeOffset = GravityUtil.rotate(new Vec3(0, currentCameraY, 0), quat2);
            
            original.call(this, entityX + eyeOffset.x(), entityY + eyeOffset.y(), entityZ + eyeOffset.z());
    	}
    	else
    	{
    		original.call(this, x, y, z);
    	}
    }
    
    @Inject(method = "Lnet/minecraft/client/Camera;setRotation(FF)V", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", shift = At.Shift.AFTER, remap = false))
    private void setRotation(CallbackInfo ci) 
    {
        if(this.entity != null && GravityUtil.hasGravity(this.entity)) 
        {
            Direction direction = GravityUtil.getGravityDirection(this.entity);
            Quaternionf quat = new Quaternionf(GravityUtil.getWorldRotationQuaternion(direction));
            quat.conjugate();
            quat.mul(this.rotation);
            this.rotation.set(quat.x(), quat.y(), quat.z(), quat.w());
        }
    }
}
