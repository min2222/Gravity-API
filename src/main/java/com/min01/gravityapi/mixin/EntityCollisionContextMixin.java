package com.min01.gravityapi.mixin;


import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.gravityapi.api.GravityChangerAPI;
import com.min01.gravityapi.util.RotationUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(EntityCollisionContext.class)
public abstract class EntityCollisionContextMixin {
    @Shadow
    @Final
    private Entity entity;
    
    @Shadow
    @Final
    private double entityBottom;
    
    @Redirect(
        method = "<init>(Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getY()D",
            ordinal = 0
        )
    )
    private static double redirect_init_getY_0(Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entity.getY();
        }
        
        return RotationUtil.boxWorldToPlayer(entity.getBoundingBox(), gravityDirection).minY;
    }
    
    @Inject(
        method = "Lnet/minecraft/world/phys/shapes/EntityCollisionContext;isAbove(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/core/BlockPos;Z)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue, CallbackInfoReturnable<Boolean> cir) {
        if (this.entity == null) return;
        
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.entity);
        if (gravityDirection == Direction.DOWN) return;
        
        if (shape.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }
        
        AABB shapeBox = RotationUtil.boxWorldToPlayer(
            shape.bounds().inflate(-9.999999747378752E-6D), gravityDirection
        );
        AABB posBox = RotationUtil.boxWorldToPlayer(new AABB(pos), gravityDirection);
        cir.setReturnValue(
            this.entityBottom > posBox.minY + shapeBox.maxX
        );
    }
}
