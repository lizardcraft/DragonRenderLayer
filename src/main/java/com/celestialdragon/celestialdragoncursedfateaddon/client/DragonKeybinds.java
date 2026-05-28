package com.celestialdragon.celestialdragoncursedfateaddon.client;

import com.celestialdragon.celestialdragoncursedfateaddon.CelestialdragonMod;
import com.celestialdragon.celestialdragoncursedfateaddon.network.ServerboundToggleDragonActivePacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = CelestialdragonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DragonKeybinds {
	public static final String CATEGORY = "key.categories.lizardcraft_dragon";

	public static final KeyMapping TOGGLE_HORNS = new KeyMapping(
		"key.nova_dev.toggle_horns",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_G,
		CATEGORY
	);

	public static final KeyMapping TOGGLE_WINGS = new KeyMapping(
		"key.nova_dev.toggle_wings",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_H,
		CATEGORY
	);

	public static final KeyMapping TOGGLE_TAIL = new KeyMapping(
		"key.nova_dev.toggle_tail",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_V,
		CATEGORY
	);

	public static final KeyMapping TOGGLE_CLAWS = new KeyMapping(
		"key.nova_dev.toggle_claws",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_B,
		CATEGORY
	);

	public static final KeyMapping TOGGLE_VISIBILITY = new KeyMapping(
		"key.nova_dev.toggle_visibility",
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_X,
		CATEGORY
	);

	@SubscribeEvent
	public static void registerKeybinds(RegisterKeyMappingsEvent event) {
		event.register(TOGGLE_HORNS);
		event.register(TOGGLE_WINGS);
		event.register(TOGGLE_TAIL);
		event.register(TOGGLE_CLAWS);
		event.register(TOGGLE_VISIBILITY);
	}

	@Mod.EventBusSubscriber(modid = CelestialdragonMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
	public static class ClientForgeEvents {
		private static boolean wasHornsPressed = false;
		private static boolean wasWingsPressed = false;
		private static boolean wasTailPressed = false;
		private static boolean wasClawsPressed = false;
		private static boolean wasVisibilityPressed = false;

		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				Minecraft mc = Minecraft.getInstance();
				if (mc.player != null && mc.screen == null) {
					boolean hornsDown = TOGGLE_HORNS.isDown();
					if (hornsDown && !wasHornsPressed) {
						CelestialdragonMod.PACKET_HANDLER.sendToServer(new ServerboundToggleDragonActivePacket(0));
					}
					wasHornsPressed = hornsDown;
					boolean wingsDown = TOGGLE_WINGS.isDown();
					if (wingsDown && !wasWingsPressed) {
						CelestialdragonMod.PACKET_HANDLER.sendToServer(new ServerboundToggleDragonActivePacket(1));
					}
					wasWingsPressed = wingsDown;
					boolean tailDown = TOGGLE_TAIL.isDown();
					if (tailDown && !wasTailPressed) {
						CelestialdragonMod.PACKET_HANDLER.sendToServer(new ServerboundToggleDragonActivePacket(2));
					}
					wasTailPressed = tailDown;
					boolean clawsDown = TOGGLE_CLAWS.isDown();
					if (clawsDown && !wasClawsPressed) {
						CelestialdragonMod.PACKET_HANDLER.sendToServer(new ServerboundToggleDragonActivePacket(3));
					}
					wasClawsPressed = clawsDown;

					boolean visibilityDown = TOGGLE_VISIBILITY.isDown();
					if (visibilityDown && !wasVisibilityPressed) {
						CelestialdragonMod.PACKET_HANDLER.sendToServer(new ServerboundToggleDragonActivePacket(4));
					}
					wasVisibilityPressed = visibilityDown;
				}
			}
		}
	}
}
