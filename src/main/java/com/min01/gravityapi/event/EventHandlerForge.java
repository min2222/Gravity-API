package com.min01.gravityapi.event;

import com.min01.gravityapi.GravityAPI;
import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = GravityAPI.MODID)
public class EventHandlerForge 
{
	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		Entity entity = event.getEntity();
		GravityUtil.ENTITY_MAP.put(entity.getClass().hashCode(), entity);
		GravityUtil.ENTITY_MAP2.put(entity.getClass().getSuperclass().hashCode(), entity);
	}
}
