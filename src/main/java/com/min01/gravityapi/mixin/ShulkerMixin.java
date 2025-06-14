package com.min01.gravityapi.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.gravityapi.api.GravityChangerAPI;
import com.min01.gravityapi.util.RotationUtil;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.phys.Vec3;

@Mixin(value = Shulker.class, priority = 1001)
public abstract class ShulkerMixin {
    @WrapOperation(
        method = "onPeekAmountChange",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
            ordinal = 0
        )
    )
    private void wrapOperation_pushEntities_move_0(Entity entity, MoverType movementType, Vec3 vec3d, Operation<Void> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            original.call(entity, movementType, vec3d);
            return;
        }
        
        original.call(entity, movementType, RotationUtil.vecWorldToPlayer(vec3d, gravityDirection));
    }
}
