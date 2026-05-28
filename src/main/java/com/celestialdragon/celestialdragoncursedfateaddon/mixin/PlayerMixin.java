package com.celestialdragon.celestialdragoncursedfateaddon.mixin;

import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "getItemBySlot", at = @At("RETURN"), cancellable = true)
    private void onGetItemBySlot(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
                if (slot == EquipmentSlot.CHEST) {
            Player player = (Player) (Object) this;
            player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
                if (state.isWingsActive()) {
                    
                    StackWalker walker = StackWalker.getInstance();
                    boolean isFlightCheck = walker.walk(frames -> frames.anyMatch(frame -> {
                        String methodName = frame.getMethodName();
                        String className = frame.getClassName();
                        return methodName.equals("tryToStartFallFlying") 
                            || methodName.equals("updateFallFlying") 
                            || (methodName.equals("aiStep") && className.endsWith("LocalPlayer"));
                    }));
                    if (isFlightCheck) {
                        cir.setReturnValue(new ItemStack(Items.ELYTRA));
                    }
                }
            });
        }
    }
}
