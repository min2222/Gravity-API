package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.min01.gravityapi.api.GravityChangerAPI;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Fox;

@Mixin(Fox.class)
public class FoxMixin {
    @ModifyVariable(method = "Lnet/minecraft/world/entity/animal/Fox;calculateFallDamage(FF)I", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float diminishFallDamage(float value) {
        return value * (float) Math.sqrt(GravityChangerAPI.getGravityStrength(((Entity) (Object) this)));
    }
}
