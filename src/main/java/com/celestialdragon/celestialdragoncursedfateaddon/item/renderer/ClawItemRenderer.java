package com.celestialdragon.celestialdragoncursedfateaddon.item.renderer;

import com.celestialdragon.celestialdragoncursedfateaddon.item.ClawItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import com.celestialdragon.celestialdragoncursedfateaddon.item.model.ClawItemModel;

import java.util.Set;
import java.util.HashSet;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class ClawItemRenderer extends GeoItemRenderer<ClawItem> {
	public ClawItemRenderer() {
		super(new ClawItemModel());
	}

	@Override
	public RenderType getRenderType(ClawItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;
	protected boolean renderArms = false;
	protected MultiBufferSource currentBuffer;
	protected RenderType renderType;
	public ItemDisplayContext transformType;
	protected ClawItem animatable;
	private final Set<String> hiddenBones = new HashSet<>();
	private final Set<String> suppressedBones = new HashSet<>();

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
		this.transformType = transformType;
		super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
	}

	@Override
	public void actuallyRender(PoseStack matrixStackIn, ClawItem animatable, BakedGeoModel model, RenderType type, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, boolean isRenderer, float partialTicks, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.currentBuffer = renderTypeBuffer;
		this.renderType = type;
		this.animatable = animatable;

		model.getBone("armorBody").ifPresent(b -> b.setHidden(true));
		model.getBone("armorHead").ifPresent(b -> b.setHidden(true));

		if (this.transformType == net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
			model.getBone("armorRightArm").ifPresent(b -> b.setHidden(true));
			model.getBone("armorLeftArm").ifPresent(b -> {
				b.setHidden(false);
				b.updatePosition(-5f, -22f, 0f);
			});
		} else if (this.transformType == net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
			model.getBone("armorRightArm").ifPresent(b -> b.setHidden(true));
			model.getBone("armorLeftArm").ifPresent(b -> {
				b.setHidden(false);
				b.updatePosition(0f, 0f, 0f);
			});
		} else if (this.transformType == net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
			model.getBone("armorLeftArm").ifPresent(b -> b.setHidden(true));
			model.getBone("armorRightArm").ifPresent(b -> {
				b.setHidden(false);
				b.updatePosition(5f, -22f, 0f);
			});
		} else if (this.transformType == net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
			model.getBone("armorLeftArm").ifPresent(b -> b.setHidden(true));
			model.getBone("armorRightArm").ifPresent(b -> {
				b.setHidden(false);
				b.updatePosition(0f, 0f, 0f);
			});
		} else {
			model.getBone("armorLeftArm").ifPresent(b -> b.setHidden(true));
			model.getBone("armorRightArm").ifPresent(b -> {
				b.setHidden(false);
				b.updatePosition(0f, 0f, 0f);
			});
		}

		super.actuallyRender(matrixStackIn, animatable, model, type, renderTypeBuffer, vertexBuilder, isRenderer, partialTicks, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (this.renderArms) {
			this.renderArms = false;
		}
	}

	@Override
	public ResourceLocation getTextureLocation(ClawItem instance) {
		return super.getTextureLocation(instance);
	}
}
