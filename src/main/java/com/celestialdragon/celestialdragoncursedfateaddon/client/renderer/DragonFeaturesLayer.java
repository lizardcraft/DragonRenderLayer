package com.celestialdragon.celestialdragoncursedfateaddon.client.renderer;

import com.celestialdragon.celestialdragoncursedfateaddon.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtils;
import com.celestialdragon.celestialdragoncursedfateaddon.CelestialdragonMod;
import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import com.celestialdragon.celestialdragoncursedfateaddon.client.model.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DragonFeaturesLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	/** Per-player, per-feature animatable instances to avoid GeckoLib animation dedup */
	private static final Map<UUID, DragonFeaturesAnimatable> WINGS_ANIMATABLES = new ConcurrentHashMap<>();
	private static final Map<UUID, DragonFeaturesAnimatable> TAIL_ANIMATABLES = new ConcurrentHashMap<>();
	private static final Map<UUID, DragonFeaturesAnimatable> HORNS_ANIMATABLES = new ConcurrentHashMap<>();
	private static final Map<UUID, DragonFeaturesAnimatable> CLAWS_ANIMATABLES = new ConcurrentHashMap<>();

	private final GeoModel<DragonFeaturesAnimatable> wingsModel;
	private final GeoModel<DragonFeaturesAnimatable> tailModel;
	private final GeoModel<DragonFeaturesAnimatable> hornsModel;
	private final GeoModel<DragonFeaturesAnimatable> clawsModel;

	public DragonFeaturesLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
		super(renderer);
		this.wingsModel = new WingsGeoModel();
		this.tailModel = new TailGeoModel();
		this.hornsModel = new HornsGeoModel();
		this.clawsModel = new ClawsGeoModel();
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
					   AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
					   float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
			if (!state.isAnyActive()) return;

			UUID uuid = player.getUUID();
			PlayerModel<AbstractClientPlayer> parentModel = this.getParentModel();
			if (state.isWingsActive()) {
				DragonFeaturesAnimatable wingsAnim = WINGS_ANIMATABLES.computeIfAbsent(uuid,
						u -> new DragonFeaturesAnimatable(DragonFeaturesAnimatable.FeatureType.WINGS));
				wingsAnim.isFlying = player.getAbilities().flying;
				wingsAnim.isFallFlying = player.isFallFlying();
				wingsAnim.attackTrigger = state.isAttackTrigger();
				wingsAnim.justLaunched = state.isJustLaunched();
				wingsAnim.isCrouching = player.isCrouching();
				renderArmorStyleModel(wingsModel, wingsAnim, poseStack, bufferSource, packedLight, partialTicks, parentModel);
			}
			if (state.isTailActive()) {
				DragonFeaturesAnimatable tailAnim = TAIL_ANIMATABLES.computeIfAbsent(uuid,
						u -> new DragonFeaturesAnimatable(DragonFeaturesAnimatable.FeatureType.TAIL));
				tailAnim.isFlying = player.getAbilities().flying;
				tailAnim.isFallFlying = player.isFallFlying();
				tailAnim.attackTrigger = state.isAttackTrigger();
				tailAnim.justLaunched = state.isJustLaunched();
				tailAnim.isCrouching = player.isCrouching();
				renderArmorStyleModel(tailModel, tailAnim, poseStack, bufferSource, packedLight, partialTicks, parentModel);
			}
			if (state.isHornsActive()) {
				DragonFeaturesAnimatable hornsAnim = HORNS_ANIMATABLES.computeIfAbsent(uuid,
						u -> new DragonFeaturesAnimatable(DragonFeaturesAnimatable.FeatureType.HORNS));
				hornsAnim.isFlying = player.getAbilities().flying;
				hornsAnim.isFallFlying = player.isFallFlying();
				hornsAnim.attackTrigger = state.isAttackTrigger();
				hornsAnim.justLaunched = state.isJustLaunched();
				hornsAnim.isCrouching = player.isCrouching();
				renderArmorStyleModel(hornsModel, hornsAnim, poseStack, bufferSource, packedLight, partialTicks, parentModel);
			}
			if (state.isClawsActive()) {
				DragonFeaturesAnimatable clawsAnim = CLAWS_ANIMATABLES.computeIfAbsent(uuid,
						u -> new DragonFeaturesAnimatable(DragonFeaturesAnimatable.FeatureType.CLAWS));
				clawsAnim.isFlying = player.getAbilities().flying;
				clawsAnim.isFallFlying = player.isFallFlying();
				clawsAnim.attackTrigger = state.isAttackTrigger();
				clawsAnim.justLaunched = state.isJustLaunched();
				clawsAnim.isCrouching = player.isCrouching();
				renderArmorStyleModel(clawsModel, clawsAnim, poseStack, bufferSource, packedLight, partialTicks, parentModel);
			}
		});
	}

		private void renderArmorStyleModel(GeoModel<DragonFeaturesAnimatable> model, DragonFeaturesAnimatable animatable,
									   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
									   float partialTick, PlayerModel<AbstractClientPlayer> playerModel) {
		poseStack.pushPose();

		poseStack.translate(0, 24 / 16f, 0);
		poseStack.scale(-1, -1, 1);

		ResourceLocation modelResource = model.getModelResource(animatable);
		BakedGeoModel bakedModel = model.getBakedModel(modelResource);

		AnimationState<DragonFeaturesAnimatable> animationState =
				new AnimationState<>(animatable, 0, 0, partialTick, false);
		long instanceId = animatable.hashCode();
		model.addAdditionalStateData(animatable, instanceId, animationState::setData);
		model.handleAnimations(animatable, instanceId, animationState);

		applyBaseTransformations(bakedModel, model, animatable, playerModel);

		ResourceLocation texture = model.getTextureResource(animatable);
		RenderType renderType = RenderType.entityTranslucent(texture);
		VertexConsumer buffer = bufferSource.getBuffer(renderType);

		for (GeoBone bone : bakedModel.topLevelBones()) {
			renderBoneRecursively(poseStack, bone, buffer, packedLight, OverlayTexture.NO_OVERLAY,
					1.0f, 1.0f, 1.0f, 1.0f);
		}

		poseStack.popPose();
	}

	private void applyBaseTransformations(BakedGeoModel bakedModel, GeoModel<DragonFeaturesAnimatable> model,
										  DragonFeaturesAnimatable animatable,
										  PlayerModel<AbstractClientPlayer> playerModel) {

		model.getBone("armorHead").ifPresent(bone -> {
			bone.setHidden(false);
			ModelPart headPart = playerModel.head;
			RenderUtils.matchModelPartRot(headPart, bone);
			bone.updatePosition(headPart.x, -headPart.y, headPart.z);
		});

		model.getBone("armorBody").ifPresent(bone -> {
			bone.setHidden(false);
			ModelPart bodyPart = playerModel.body;
			RenderUtils.matchModelPartRot(bodyPart, bone);
			bone.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
		});

		model.getBone("armorRightArm").ifPresent(bone -> {
			bone.setHidden(false);
			ModelPart rightArmPart = playerModel.rightArm;
			RenderUtils.matchModelPartRot(rightArmPart, bone);
			bone.updatePosition(rightArmPart.x + 5.5f, 2 - rightArmPart.y, rightArmPart.z);
		});

		model.getBone("armorLeftArm").ifPresent(bone -> {
			bone.setHidden(false);
			ModelPart leftArmPart = playerModel.leftArm;
			RenderUtils.matchModelPartRot(leftArmPart, bone);
			bone.updatePosition(leftArmPart.x - 5.5f, 2f - leftArmPart.y, leftArmPart.z);
		});
		model.getBone("armor").ifPresent(bone -> {
			ModelPart bodyPart = playerModel.body;
			RenderUtils.matchModelPartRot(bodyPart, bone);
			bone.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
		});

		model.getBone("armorRightLeg").ifPresent(bone -> {
			ModelPart rightLegPart = playerModel.rightLeg;
			RenderUtils.matchModelPartRot(rightLegPart, bone);
			bone.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
		});

		model.getBone("armorLeftLeg").ifPresent(bone -> {
			ModelPart leftLegPart = playerModel.leftLeg;
			RenderUtils.matchModelPartRot(leftLegPart, bone);
			bone.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
		});
	}

	private void renderBoneRecursively(PoseStack poseStack, GeoBone bone, VertexConsumer buffer,
									   int packedLight, int packedOverlay,
									   float red, float green, float blue, float alpha) {
		if (bone.isHidden())
			return;

		poseStack.pushPose();
		RenderUtils.prepMatrixForBone(poseStack, bone);

		for (GeoCube cube : bone.getCubes()) {
			poseStack.pushPose();
			RenderUtils.translateToPivotPoint(poseStack, cube);
			RenderUtils.rotateMatrixAroundCube(poseStack, cube);
			RenderUtils.translateAwayFromPivotPoint(poseStack, cube);

			Matrix3f normalMatrix = poseStack.last().normal();
			Matrix4f poseMatrix = poseStack.last().pose();

			for (GeoQuad quad : cube.quads()) {
				if (quad == null) continue;
				Vector3f normal = normalMatrix.transform(new Vector3f(quad.normal()));
				RenderUtils.fixInvertedFlatCube(cube, normal);
				createVerticesOfQuad(quad, poseMatrix, normal, buffer, packedLight, packedOverlay,
						red, green, blue, alpha);
			}
			poseStack.popPose();
		}

		if (!bone.isHidingChildren()) {
			for (GeoBone child : bone.getChildBones()) {
				renderBoneRecursively(poseStack, child, buffer, packedLight, packedOverlay,
						red, green, blue, alpha);
			}
		}

		poseStack.popPose();
	}

	private void createVerticesOfQuad(GeoQuad quad, Matrix4f poseMatrix, Vector3f normal,
									  VertexConsumer buffer, int packedLight, int packedOverlay,
									  float red, float green, float blue, float alpha) {
		for (GeoVertex vertex : quad.vertices()) {
			Vector3f position = vertex.position();
			Vector4f worldPos = poseMatrix.transform(
					new Vector4f(position.x(), position.y(), position.z(), 1.0f));
			buffer.vertex(worldPos.x(), worldPos.y(), worldPos.z(),
					red, green, blue, alpha,
					vertex.texU(), vertex.texV(),
					packedOverlay, packedLight,
					normal.x(), normal.y(), normal.z());
		}
	}

	@Mod.EventBusSubscriber(modid = CelestialdragonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientSetupEvents {
		@SubscribeEvent
		public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
			for (String skinName : event.getSkins()) {
				PlayerRenderer renderer = event.getSkin(skinName);
				if (renderer != null) {
					renderer.addLayer(new DragonFeaturesLayer(renderer));
				}
			}
		}
	}
}
