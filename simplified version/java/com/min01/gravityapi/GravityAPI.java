package com.min01.gravityapi;

import com.min01.gravityapi.capabilities.GravityCapabilities;
import com.min01.gravityapi.init.GravityBlocks;
import com.min01.gravityapi.init.GravityCreativeTabs;
import com.min01.gravityapi.init.GravityItems;
import com.min01.gravityapi.network.GravityNetwork;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GravityAPI.MODID)
public class GravityAPI
{
	public static final String MODID = "gravityapi";
	
	public GravityAPI() 
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		GravityItems.ITEMS.register(bus);
		GravityBlocks.BLOCKS.register(bus);
		GravityBlocks.BLOCK_ENTITIES.register(bus);
		GravityCreativeTabs.CREATIVE_MODE_TAB.register(bus);

		GravityNetwork.registerMessages();
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, GravityCapabilities::attachEntityCapability);
	}
}
