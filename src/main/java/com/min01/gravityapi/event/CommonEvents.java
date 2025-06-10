package com.min01.gravityapi.event;

import com.min01.gravityapi.GravityAPI;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = GravityAPI.MODID)
public class CommonEvents
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
    	//GravityCommand.register(event.getDispatcher());
    }
}
