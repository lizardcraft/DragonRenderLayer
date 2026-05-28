package com.celestialdragon.celestialdragoncursedfateaddon.item.model;

import com.celestialdragon.celestialdragoncursedfateaddon.item.ClawItem;
import software.bernie.geckolib.model.GeoModel;
import com.celestialdragon.celestialdragoncursedfateaddon.CelestialdragonMod;
import net.minecraft.resources.ResourceLocation;

public class ClawItemModel extends GeoModel<ClawItem> {
	@Override
	public ResourceLocation getAnimationResource(ClawItem animatable) {
		return new ResourceLocation("celestialdragon", "animations/claw.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ClawItem animatable) {
		return new ResourceLocation("celestialdragon", "geo/claw.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ClawItem animatable) {
		return new ResourceLocation("celestialdragon", "textures/item/claw.png");
	}
}
