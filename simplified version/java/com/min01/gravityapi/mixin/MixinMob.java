package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.gravityapi.entity.GravityPathNavigation;
import com.min01.gravityapi.util.GravityUtil;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;

@Mixin(Mob.class)
public class MixinMob 
{
	@Shadow
	protected PathNavigation navigation;
    
    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tick(CallbackInfo ci)
    {
    	Mob mob = Mob.class.cast(this);
    	if(GravityUtil.hasGravity(mob))
    	{
    		if(!(this.navigation instanceof GravityPathNavigation))
    		{
    			this.navigation = new GravityPathNavigation(mob, mob.level);
    		}
    	}
    	else if(this.navigation instanceof GravityPathNavigation)
    	{
    		this.navigation = this.createNavigation(mob.level);
    	}
    }
    
    @Shadow
    protected PathNavigation createNavigation(Level p_21480_)
    {
    	throw new IllegalStateException();
    }
}
