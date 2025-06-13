package com.min01.gravityapi.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class GravityChangerItem extends Item
{
    public final Direction direction;
    public GravityChangerItem(Properties settings, Direction direction)
    {
        super(settings);
        this.direction = direction;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand)
    {
        GravityUtil.setGravityDirection(user, this.direction);
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context)
    {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(Component.translatable("gravity_changer.gravity_changer.tooltip.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gravity_changer.gravity_changer.tooltip.1").withStyle(ChatFormatting.GRAY));
    }
}
