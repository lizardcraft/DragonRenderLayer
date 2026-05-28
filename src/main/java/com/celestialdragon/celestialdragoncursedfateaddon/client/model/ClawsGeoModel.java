package com.celestialdragon.celestialdragoncursedfateaddon.client.model;

import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;
import com.celestialdragon.celestialdragoncursedfateaddon.CelestialdragonMod;

public class ClawsGeoModel extends GeoModel<DragonFeaturesAnimatable> {
	@Override
	public ResourceLocation getAnimationResource(DragonFeaturesAnimatable object) {
		return new ResourceLocation(CelestialdragonMod.MODID, "animations/claw.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(DragonFeaturesAnimatable object) {
		return new ResourceLocation(CelestialdragonMod.MODID, "geo/claw.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(DragonFeaturesAnimatable object) {
		return new ResourceLocation(CelestialdragonMod.MODID, "textures/item/claw.png");
	}
}
