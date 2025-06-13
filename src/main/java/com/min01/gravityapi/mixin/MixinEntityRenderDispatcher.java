package com.min01.gravityapi.mixin;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.gravityapi.util.GravityUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher 
{
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 0, shift = At.Shift.AFTER))
    private <E extends Entity> void translate(E p_114385_, double p_114386_, double p_114387_, double p_114388_, float p_114389_, float p_114390_, PoseStack p_114391_, MultiBufferSource p_114392_, int p_114393_, CallbackInfo ci)
    {
        if(GravityUtil.hasGravity(p_114385_))
        {
        	Direction direction = GravityUtil.getGravityDirection(p_114385_);
            Quaternionf quat = GravityUtil.getWorldRotationQuaternion(direction);
        	p_114391_.mulPose(new Quaternionf(quat).conjugate());
        }
    }
    
    @ModifyVariable(method = "renderHitbox", at = @At(value = "INVOKE_ASSIGN",  target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;", ordinal = 0), ordinal = 0)
    private static AABB renderHitbox(AABB aabb, PoseStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) 
    {
    	if(GravityUtil.hasGravity(entity))
    	{
        	Direction direction = GravityUtil.getGravityDirection(entity);
        	return GravityUtil.boxWorldToPlayer(aabb, direction);
        }
        return aabb;
    }
}
