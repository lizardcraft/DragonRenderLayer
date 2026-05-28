package com.celestialdragon.celestialdragoncursedfateaddon.init;

import com.celestialdragon.celestialdragoncursedfateaddon.item.ClawItem;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import com.celestialdragon.celestialdragoncursedfateaddon.CelestialdragonMod;

public class NovaDevModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CelestialdragonMod.MODID);
	public static final RegistryObject<Item> CLAW = REGISTRY.register("claw", () -> new ClawItem());
}
