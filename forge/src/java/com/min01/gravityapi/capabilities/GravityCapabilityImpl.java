package com.min01.gravityapi.capabilities;

import com.min01.gravityapi.network.GravityNetwork;
import com.min01.gravityapi.network.UpdateGravityCapabilityPacket;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

public class GravityCapabilityImpl implements IGravityCapability
{
	private Entity entity;
	private Direction direction = Direction.DOWN;
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("Direction", this.direction.ordinal());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		this.direction = Direction.values()[nbt.getInt("Direction")];
	}

	@Override
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	@Override
	public void setGravityDirection(Direction direction)
	{
		this.direction = direction;
		this.sendUpdatePacket();
	}

	@Override
	public Direction getGravityDirection() 
	{
		return this.direction;
	}

	private void sendUpdatePacket() 
	{
		if(!this.entity.level.isClientSide)
		{
			GravityNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateGravityCapabilityPacket(this.entity.getUUID(), this.direction));
		}
	}
}
