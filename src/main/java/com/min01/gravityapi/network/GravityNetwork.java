package com.min01.gravityapi.network;

import com.min01.gravityapi.GravityAPI;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class GravityNetwork
{
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(GravityAPI.MODID, "gravityapi"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	
	public static int ID = 0;
	public static void registerMessages()
	{
		CHANNEL.registerMessage(ID++, UpdateGravityCapabilityPacket.class, UpdateGravityCapabilityPacket::encode, UpdateGravityCapabilityPacket::new, UpdateGravityCapabilityPacket.Handler::onMessage);
	}
	
    public static <MSG> void sendToServer(MSG message) 
    {
    	CHANNEL.sendToServer(message);
    }
    
    public static <MSG> void sendToAll(MSG message)
    {
    	for(ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) 
    	{
    		CHANNEL.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    	}
    }
    
    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player)
    {
    	CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
