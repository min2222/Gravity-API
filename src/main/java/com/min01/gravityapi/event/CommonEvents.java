package com.min01.gravityapi.event;

import com.min01.gravityapi.GravityAPI;
import com.min01.gravityapi.util.GCUtil;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
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
    
	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		Entity entity = event.getEntity();
		GCUtil.ENTITY_MAP.put(entity.getClass().hashCode(), entity);
		GCUtil.ENTITY_MAP2.put(entity.getClass().getSuperclass().hashCode(), entity);
	}
}
