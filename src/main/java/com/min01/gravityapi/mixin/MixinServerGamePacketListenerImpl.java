package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl
{
	@Shadow
	public ServerPlayer player;
	   
	@ModifyArg(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
	private Vec3 handleMovePlayer(Vec3 vec3)
	{
		if(GravityUtil.hasGravity(this.player))
		{
			Direction direction = GravityUtil.getGravityDirection(this.player);
			return GravityUtil.vecWorldToPlayer(vec3, direction);
		}
		return vec3;
	}

	@ModifyArg(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"), index = 1)
	private Vec3 handleMoveVehicle(Vec3 vec3)
	{
		if(GravityUtil.hasGravity(this.player))
		{
			Direction direction = GravityUtil.getGravityDirection(this.player);
			return GravityUtil.vecWorldToPlayer(vec3, direction);
		}
		return vec3;
	}
}
