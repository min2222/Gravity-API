package com.min01.gravityapi.mixin;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.gravityapi.util.GravityUtil;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;

@Mixin(GameRenderer.class)
public class MixinGameRenderer 
{
    @Shadow
    @Final
    private Camera mainCamera;
    
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 3, shift = At.Shift.AFTER))
    private void renderLevel(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) 
    {
        if(this.mainCamera.getEntity() != null) 
        {
            Entity entity = this.mainCamera.getEntity();
            if(GravityUtil.hasGravity(entity))
            {
                Direction direction = GravityUtil.getGravityDirection(entity);
                Quaternionf quat = new Quaternionf(GravityUtil.getWorldRotationQuaternion(direction));
                if(direction.getAxis() == Axis.Y)
                {
                	this.mainCamera.setAnglesInternal(-this.mainCamera.getYRot(), this.mainCamera.getXRot());
                }
                matrix.mulPose(quat);
            }
        }
    }
}
