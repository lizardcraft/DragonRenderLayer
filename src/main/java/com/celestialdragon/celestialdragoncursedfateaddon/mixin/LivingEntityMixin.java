package com.celestialdragon.celestialdragoncursedfateaddon.mixin;

import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void startFallFlying();
    @Shadow public abstract boolean hasEffect(net.minecraft.world.effect.MobEffect effect);
    @Shadow protected abstract boolean getSharedFlag(int flag);
    @Shadow protected abstract void setSharedFlag(int flag, boolean value);

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
                if (state.isWingsActive()) {
                    if (!player.onGround() && !player.isPassenger() && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION)) {
                        this.startFallFlying();
                        cir.setReturnValue(true);
                    }
                }
            });
        }
    }

    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void onUpdateFallFlying(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
                if (state.isWingsActive()) {
                    boolean flag = this.getSharedFlag(7);
                    if (flag && !player.onGround() && !player.isPassenger() && !player.hasEffect(MobEffects.LEVITATION)) {
                        
                    } else {
                        flag = false;
                    }
                    if (!player.level().isClientSide()) {
                        this.setSharedFlag(7, flag);
                    }
                    ci.cancel();
                }
            });
        }
    }
}
