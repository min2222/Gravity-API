package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("makeBoundingBox")
    AABB gc_makeBoundingBox();
}
