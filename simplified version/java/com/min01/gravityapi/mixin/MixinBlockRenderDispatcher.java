package com.min01.gravityapi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockRenderDispatcher.class)
public class MixinBlockRenderDispatcher 
{
	@Inject(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V", at = @At("HEAD"), cancellable = true)
	private void renderBatched(BlockState p_234356_, BlockPos p_234357_, BlockAndTintGetter p_234358_, PoseStack p_234359_, VertexConsumer p_234360_, boolean p_234361_, RandomSource p_234362_, net.minecraftforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType, CallbackInfo ci) 
	{
		//FIXME invisible face, door, torch etc
		p_234359_.translate(0.5F, 0.5F, 0.5F);
		p_234359_.mulPose(Axis.XP.rotationDegrees(180.0F));
		p_234359_.mulPose(Axis.YP.rotationDegrees(180.0F));
		p_234359_.translate(-0.5F, -0.5F, -0.5F);
	}
}
