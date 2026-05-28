package com.celestialdragon.celestialdragoncursedfateaddon.mixin;

import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow private boolean wasJumpPressed;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        // Check if jump key is pressed in mid-air and player is not already flying
        if (player.input.jumping && !this.wasJumpPressed && !player.onGround() && !player.isPassenger() && !player.isInWater() && !player.isFallFlying() && !player.getAbilities().flying) {
            player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
                if (state.isWingsActive()) {
                    if (!player.hasEffect(MobEffects.LEVITATION)) {
                        player.connection.send(new net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket(player, net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                    }
                }
            });
        }
    }
}
