package com.min01.gravityapi.item;

import java.util.List;

import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

// based on AmethystGravity
public class GravityAnchorItem extends Item
{
    public final Direction direction;
    public GravityAnchorItem(Properties settings, Direction direction) 
    {
        super(settings);
        this.direction = direction;
    }
    
    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) 
    {
    	if(p_41408_)
    	{
    		GravityUtil.setGravityDirection(p_41406_, this.direction);
    	}
    	else
    	{
    		GravityUtil.setGravityDirection(p_41406_, Direction.DOWN);
    	}
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, Level world, List<Component> tooltip, TooltipFlag tooltipContext)
    {
    	super.appendHoverText(itemStack, world, tooltip, tooltipContext);
        tooltip.add(Component.translatable("gravity_changer.gravity_anchor.tooltip.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gravity_changer.gravity_anchor.tooltip.1").withStyle(ChatFormatting.GRAY));
    }
}
