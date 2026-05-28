package com.celestialdragon.celestialdragoncursedfateaddon.client;

import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import com.celestialdragon.celestialdragoncursedfateaddon.init.NovaDevModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid ="celestialdragon", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderArmEventHandler {
    private static ItemStack cachedClawStack = null;

    private static ItemStack getClawStack() {
        if (cachedClawStack == null) {
            cachedClawStack = new ItemStack(NovaDevModItems.CLAW.get());
        }
        return cachedClawStack;
    }

    @SubscribeEvent
    public static void onRenderPlayer(net.minecraftforge.client.event.RenderPlayerEvent.Pre event) {
        event.getEntity().getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
            if (!state.isPlayerVisible()) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player == null) return;

        player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
            if (state.isClawsActive()) {
                //event.setCanceled(true);

                PoseStack poseStack = event.getPoseStack();
                MultiBufferSource bufferSource = event.getMultiBufferSource();
                int packedLight = event.getPackedLight();
                InteractionHand hand = event.getHand();
                float swingProgress = event.getSwingProgress();
                float equipProgress = event.getEquipProgress();

                boolean isMainHand = hand == InteractionHand.MAIN_HAND;
                HumanoidArm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
                boolean isRightArm = arm == HumanoidArm.RIGHT;
                int side = isRightArm ? 1 : -1;

                poseStack.pushPose();

                float sqrtSwing = Mth.sqrt(swingProgress);
                poseStack.translate(
                    side * (-0.4F * Mth.sin(sqrtSwing * (float)Math.PI)),
                    0.2F * Mth.sin(sqrtSwing * ((float)Math.PI * 2F)),
                    -0.2F * Mth.sin(swingProgress * (float)Math.PI)
                );

                poseStack.translate(side * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);

                float f = Mth.sin(swingProgress * swingProgress * (float)Math.PI);
                float f1 = Mth.sin(sqrtSwing * (float)Math.PI);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + f * -20.0F)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * f1 * -20.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(f1 * -80.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F));

                ItemDisplayContext ctx = isRightArm
                    ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                    : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

                mc.getItemRenderer().renderStatic(
                    player,
                    getClawStack(),
                    ctx,
                    !isRightArm,
                    poseStack,
                    bufferSource,
                    player.level(),
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    player.getId() + ctx.ordinal()
                );

                poseStack.popPose();
            }
        });
    }
}
