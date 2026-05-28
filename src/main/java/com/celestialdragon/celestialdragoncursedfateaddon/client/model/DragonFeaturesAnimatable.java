package com.celestialdragon.celestialdragoncursedfateaddon.client.model;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class DragonFeaturesAnimatable implements GeoAnimatable {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public boolean isFlying = false;
	public boolean isFallFlying = false;
	public boolean attackTrigger = false;
	public boolean justLaunched = false;
	public boolean isCrouching = false;

	/** Which feature this animatable is for. Determines which controller to register. */
	public enum FeatureType { WINGS, TAIL, HORNS, CLAWS }
	private final FeatureType featureType;

	public DragonFeaturesAnimatable() {
		this(FeatureType.WINGS);
	}

	public DragonFeaturesAnimatable(FeatureType featureType) {
		this.featureType = featureType;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		switch (featureType) {
			case WINGS -> data.add(new AnimationController<>(this, "wingsController", 5, event -> {
				if (this.justLaunched) {
					event.getController().setAnimation(RawAnimation.begin().thenPlay("flap"));
				} else if (this.isFlying || this.isFallFlying) {
					if (this.isCrouching) {
						event.getController().setAnimation(RawAnimation.begin().thenLoop("glide"));
					} else {
						event.getController().setAnimation(RawAnimation.begin().thenLoop("flying"));
					}
				} else {
					event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
				}
				return PlayState.CONTINUE;
			}));

			case TAIL -> data.add(new AnimationController<>(this, "tailController", 5, event -> {
				if (this.attackTrigger) {
					event.getController().setAnimation(RawAnimation.begin().thenPlay("slam"));
				} else if (this.isFlying || this.isFallFlying) {
					event.getController().setAnimation(RawAnimation.begin().thenLoop("flying"));
				} else if (this.isCrouching) {
					event.getController().setAnimation(RawAnimation.begin().thenLoop("crouch"));
				} else {
					event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
				}
				return PlayState.CONTINUE;
			}));

			case HORNS -> data.add(new AnimationController<>(this, "hornsController", 5, event -> {
				event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
				return PlayState.CONTINUE;
			}));

			case CLAWS -> data.add(new AnimationController<>(this, "clawsController", 5, event -> {
				event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
				return PlayState.CONTINUE;
			}));
		}
	}

	/** Copy state flags from another animatable (for sharing state across per-feature instances). */
	public void copyStateFrom(DragonFeaturesAnimatable other) {
		this.isFlying = other.isFlying;
		this.isFallFlying = other.isFallFlying;
		this.attackTrigger = other.attackTrigger;
		this.justLaunched = other.justLaunched;
		this.isCrouching = other.isCrouching;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public double getTick(Object o) {
		return RenderUtils.getCurrentTick();
	}
}
