package com.min01.gravityapi.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.gravityapi.capabilities.GravityCapabilities;
import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class UpdateGravityCapabilityPacket 
{
	private final UUID entityUUID;
	private final Direction direction;
	
	public UpdateGravityCapabilityPacket(UUID entityUUID, Direction direction) 
	{
		this.entityUUID = entityUUID;
		this.direction = direction;
	}

	public UpdateGravityCapabilityPacket(FriendlyByteBuf buf)
	{
		this.entityUUID = buf.readUUID();
		this.direction = buf.readEnum(Direction.class);
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
		buf.writeEnum(this.direction);
	}
	
	public static class Handler 
	{
		public static boolean onMessage(UpdateGravityCapabilityPacket message, Supplier<NetworkEvent.Context> ctx) 
		{
			ctx.get().enqueueWork(() ->
			{
				if(ctx.get().getDirection().getReceptionSide().isClient())
				{
					GravityUtil.getClientLevel(level -> 
					{
						Entity entity = GravityUtil.getEntityByUUID(level, message.entityUUID);
						entity.getCapability(GravityCapabilities.GRAVITY).ifPresent(cap -> 
						{
							cap.setGravityDirection(message.direction);
						});
					});
				}
			});

			ctx.get().setPacketHandled(true);
			return true;
		}
	}
}
