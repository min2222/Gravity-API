package com.min01.gravityapi.item;

import java.util.List;

import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class GravityChangerItemAOE extends Item
{
    public final Direction direction;
    public GravityChangerItemAOE(Properties settings, Direction direction)
    {
        super(settings);
        this.direction = direction;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand)
    {
        AABB box = user.getBoundingBox().inflate(3);
        List<Entity> list = world.getEntitiesOfClass(Entity.class, box, e -> !(e instanceof Player));
        for(Entity entity : list) 
        {
            GravityUtil.setGravityDirection(entity, this.direction);
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }
}
