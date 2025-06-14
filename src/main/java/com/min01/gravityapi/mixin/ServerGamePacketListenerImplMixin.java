package com.min01.gravityapi.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.gravityapi.api.GravityChangerAPI;
import com.min01.gravityapi.util.RotationUtil;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    private static double gravitychanger$onPlayerMove_playerMovementY;
    
    @Shadow
    public ServerPlayer player;
    
    @Shadow
    private static double clampHorizontal(double d) {return 0;}
    
    ;
    
    @Shadow
    private static double clampVertical(double d) {return 0;}
    
    ;
    
    @Shadow
    private double lastGoodX;
    
    @Shadow
    private double lastGoodY;
    
    @Shadow
    private double lastGoodZ;

//    @Redirect(
//            method = "onPlayerMove",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D",
//                    ordinal = 3
//            )
//    )
//    private double redirect_onPlayerMove_getY_3(ServerPlayerEntity serverPlayerEntity) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection(serverPlayerEntity);
//        if(gravityDirection == Direction.DOWN) {
//            return serverPlayerEntity.getY();
//        }
//
//        return RotationUtil.vecWorldToPlayer(serverPlayerEntity.getPos(), gravityDirection).y;
//    }
//
//    @Redirect(
//            method = "onPlayerMove",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getY()D",
//                    ordinal = 7
//            )
//    )
//    private double redirect_onPlayerMove_getY_7(ServerPlayerEntity serverPlayerEntity) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection(serverPlayerEntity);
//        if(gravityDirection == Direction.DOWN) {
//            return serverPlayerEntity.getY();
//        }
//
//        return RotationUtil.vecWorldToPlayer(serverPlayerEntity.getPos(), gravityDirection).y;
//    }
//
//    @ModifyVariable(
//            method = "onPlayerMove",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;isOnGround()Z",
//                    ordinal = 0
//            ),
//            ordinal = 0
//    )
//    private boolean modify_onPlayerMove_boolean_0(boolean value, PlayerMoveC2SPacket packet) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
//        if(gravityDirection == Direction.DOWN) {
//            return value;
//        }
//
//        gravitychanger$onPlayerMove_playerMovementY = RotationUtil.vecWorldToPlayer(
//                clampHorizontal(packet.getX(this.player.getX())) - this.updatedX,
//                clampVertical(packet.getY(this.player.getY())) - this.updatedY,
//                clampHorizontal(packet.getZ(this.player.getZ())) - this.updatedZ,
//                gravityDirection
//        ).y;
//        return gravitychanger$onPlayerMove_playerMovementY > 0.0D;
//    }
//
//    @ModifyVariable(
//            method = "onPlayerMove",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D",
//                    ordinal = 5
//            ),
//            ordinal = 10
//    )
//    private double modify_onPlayerMove_double_12(double value) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
//        if(gravityDirection == Direction.DOWN) {
//            return value;
//        }
//
//        return gravitychanger$onPlayerMove_playerMovementY;
//    }
//
//    @ModifyArg(
//            method = "onPlayerMove",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
//                    ordinal = 0
//            ),
//            index = 1
//    )
//    private Vec3d modify_onPlayerMove_move_0(Vec3d vec3d) {
//        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
//        if(gravityDirection == Direction.DOWN) {
//            return vec3d;
//        }
//
//        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
//    }
    
    @ModifyArg(
        method = "handleMovePlayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
        )
    )
    private Vec3 modify_onPlayerMove_move_1(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    //@Redirect(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getY()D",
    //                ordinal = 0
    //        )
    //)
    //private double redirect_onVehicleMove_getY_0(Entity instance) {
    //    Direction gravityDirection = ((EntityAccessor) instance).gravitychanger$getAppliedGravityDirection();
    //    if(gravityDirection == Direction.DOWN) {
    //        return instance.getY();
    //    }
//
    //    return RotationUtil.vecWorldToPlayer(instance.getPos(), gravityDirection).y;
    //}
//
    //@Redirect(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getY()D",
    //                ordinal = 2
    //        )
    //)
    //private double redirect_onVehicleMove_getY_2(Entity instance) {
    //    Direction gravityDirection = ((EntityAccessor) instance).gravitychanger$getAppliedGravityDirection();
    //    if(gravityDirection == Direction.DOWN) {
    //        return instance.getY();
    //    }
//
    //    return RotationUtil.vecWorldToPlayer(instance.getPos(), gravityDirection).y;
    //}
    
    @ModifyArg(
        method = "handleMoveVehicle",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
        ),
        index = 1
    )
    private Vec3 modify_onVehicleMove_move_0(Vec3 vec3d) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }
        
        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
    
    //@ModifyVariable(
    //        method = "onVehicleMove",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/entity/Entity;getX()D",
    //                ordinal = 1
    //        ),ordinal = 0
    //)
    //private double modify_onVehicleMove_double_12(double value) {
    //    Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
    //    if(gravityDirection == Direction.DOWN) {
    //        return value;
    //    }
//
    //    return gravitychanger$onPlayerMove_playerMovementY;
    //}
    
    
    @WrapOperation(
        method = "noBlocksAround",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
        )
    )
    private AABB modify_onVehicleMove_move_0(AABB instance, double x, double y, double z, Operation<AABB> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this.player);
        Vec3 argVec = new Vec3(x, y, z);
        argVec = RotationUtil.vecWorldToPlayer(argVec, gravityDirection);
        return original.call(instance, argVec.x, argVec.y, argVec.z);
    }
    
}
