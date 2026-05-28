package com.celestialdragon.celestialdragoncursedfateaddon.mixin;

import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin<T extends LivingEntity> {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Player player) {
            player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
                if (state.isWingsActive()) {
                    ci.cancel();
                }
            });
        }
    }
}
