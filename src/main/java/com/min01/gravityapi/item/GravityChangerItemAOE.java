package com.min01.gravityapi.item;

import java.util.List;

import com.min01.gravityapi.api.GravityChangerAPI;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class GravityChangerItemAOE extends Item {

    
    public final Direction gravityDirection;
    
    public GravityChangerItemAOE(Properties settings, Direction _gravityDirection) {
        super(settings);
        gravityDirection = _gravityDirection;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide()) {
            AABB box = user.getBoundingBox().inflate(3);
            List<Entity> list = world.getEntitiesOfClass(Entity.class, box, e -> !(e instanceof Player));
            for (Entity entity : list) {
                GravityChangerAPI.setBaseGravityDirection(entity, gravityDirection);
            }
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }
}
