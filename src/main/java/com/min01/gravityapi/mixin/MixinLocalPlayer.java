package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.gravityapi.util.GravityUtil;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer
{
	public MixinLocalPlayer(ClientLevel p_250460_, GameProfile p_249912_)
	{
		super(p_250460_, p_249912_);
	}

	@Redirect(method = "suffocatesAt", at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;", ordinal = 0))
	private AABB suffocatesAt(double x1, double y1, double z1, double x2, double y2, double z2, BlockPos pos) 
    {
		if(GravityUtil.hasGravity(this))
		{
			Direction direction = GravityUtil.getGravityDirection(this);
            AABB playerBox = this.getBoundingBox();
            Vec3 playerMask = GravityUtil.maskPlayerToWorld(0.0D, 1.0D, 0.0D, direction);
            AABB posBox = new AABB(pos);
            Vec3 posMask = GravityUtil.maskPlayerToWorld(1.0D, 0.0D, 1.0D, direction); 
            return new AABB(playerMask.multiply(playerBox.minX, playerBox.minY, playerBox.minZ).add(posMask.multiply(posBox.minX, posBox.minY, posBox.minZ)), playerMask.multiply(playerBox.maxX, playerBox.maxY, playerBox.maxZ).add(posMask.multiply(posBox.maxX, posBox.maxY, posBox.maxZ)));
		}
		return new AABB(x1, y1, z1, x2, y2, z2);
    }
	
	@Inject(method = "Lnet/minecraft/client/player/LocalPlayer;moveTowardsClosestSpace(DD)V", at = @At("HEAD"), cancellable = true)
	private void moveTowardsClosestSpace(double x, double z, CallbackInfo ci) 
	{
		if(GravityUtil.hasGravity(this))
		{
			Direction gravityDirection = GravityUtil.getGravityDirection(this);
	        ci.cancel();
	        Vec3 pos = GravityUtil.vecPlayerToWorld(x - this.getX(), 0.0D, z - this.getZ(), gravityDirection).add(this.position());
	        BlockPos blockPos = BlockPos.containing(pos);
	        if(this.suffocatesAt(blockPos))
	        {
	            double dx = pos.x - (double) blockPos.getX();
	            double dy = pos.y - (double) blockPos.getY();
	            double dz = pos.z - (double) blockPos.getZ();
	            Direction direction = null;
	            double minDistToEdge = Double.MAX_VALUE;
	            Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
	            for(Direction playerDirection : directions)
	            {
	                Direction worldDirection = GravityUtil.dirPlayerToWorld(playerDirection, gravityDirection);
	                double g = worldDirection.getAxis().choose(dx, dy, dz);
	                double distToEdge = worldDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - g : g;
	                if(distToEdge < minDistToEdge && !this.suffocatesAt(blockPos.relative(worldDirection)))
	                {
	                    minDistToEdge = distToEdge;
	                    direction = playerDirection;
	                }
	            }
	            if(direction != null) 
	            {
	                Vec3 velocity = this.getDeltaMovement();
	                if(direction.getAxis() == Direction.Axis.X)
	                {
	                    this.setDeltaMovement(0.1D * (double) direction.getStepX(), velocity.y, velocity.z);
	                }
	                else if(direction.getAxis() == Direction.Axis.Z) 
	                {
	                    this.setDeltaMovement(velocity.x, velocity.y, 0.1D * (double) direction.getStepZ());
	                }
	            }
	        }
		}
	}
	
    @Shadow
    protected boolean suffocatesAt(BlockPos pos)
    {
    	throw new IllegalStateException();
    }
}
