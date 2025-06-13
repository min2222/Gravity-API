package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.min01.gravityapi.api.GravityChangerAPI;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

@Mixin(Boat.class)
public class BoatMixin {
    @ModifyConstant(method = "Lnet/minecraft/world/entity/vehicle/Boat;floatBoat()V", constant = @Constant(doubleValue = -0.03999999910593033))
    private double multiplyGravity(double constant) {
        return constant * GravityChangerAPI.getGravityStrength(((Entity) (Object) this));
    }
}
