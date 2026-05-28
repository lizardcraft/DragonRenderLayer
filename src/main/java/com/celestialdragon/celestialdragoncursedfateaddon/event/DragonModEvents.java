package com.celestialdragon.celestialdragoncursedfateaddon.event;

import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonState;
import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import com.celestialdragon.celestialdragoncursedfateaddon.network.ClientboundSyncDragonStatePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.celestialdragon.celestialdragoncursedfateaddon.CelestialdragonMod;

@Mod.EventBusSubscriber(modid = CelestialdragonMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DragonModEvents {

	@Mod.EventBusSubscriber(modid = CelestialdragonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModBusEvents {
		@SubscribeEvent
		public static void registerCapabilities(RegisterCapabilitiesEvent event) {
			event.register(DragonState.class);
		}
	}

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			event.addCapability(new ResourceLocation(CelestialdragonMod.MODID, "dragon_state"), new DragonStateProvider());
		}
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		if (event.getTarget() instanceof Player target) {
			target.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
				if (event.getEntity() instanceof ServerPlayer tracker) {
					CelestialdragonMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> tracker),
						new ClientboundSyncDragonStatePacket(
							target.getId(),
							state.isHornsActive(),
							state.isWingsActive(),
							state.isTailActive(),
							state.isClawsActive(),
							state.isFlying(),
							state.isFallFlying(),
							state.isAttackTrigger(),
							state.isJustLaunched(),
							state.isPlayerVisible()
						)
					);
				}
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		Player oldPlayer = event.getOriginal();
		Player newPlayer = event.getEntity();
		
		try {
			oldPlayer.reviveCaps();
		} catch (Exception e) {
		}
		
		newPlayer.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(newState -> {
			oldPlayer.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(newState::copyFrom);
		});
		
		try {
			oldPlayer.invalidateCaps();
		} catch (Exception e) {
		}
		
		if (newPlayer instanceof ServerPlayer serverPlayer) {
			syncToAll(serverPlayer);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Player player = event.player;
			player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
				if (!player.level().isClientSide()) {
					boolean isFlying = player.getAbilities().flying;
					boolean isFallFlying = player.isFallFlying();
					boolean wasFlying = state.isFlying();
					boolean wasFallFlying = state.isFallFlying();

					state.setFlying(isFlying);
					state.setFallFlying(isFallFlying);

					boolean onGround = player.onGround();
					boolean wasOnGround = state.wasOnGround();
					state.setWasOnGround(onGround);

					boolean justLaunched = false;
					if (wasOnGround && !onGround && !isFlying && !isFallFlying && player.getDeltaMovement().y > 0.0) {
						state.setJustLaunched(true);
						justLaunched = true;
					}
					if (!wasFallFlying && isFallFlying) {
						state.setJustLaunched(true);
						justLaunched = true;
					}

					if (player.swinging && player.swingTime == 1) {
						state.setAttackTrigger(true);
					}
					boolean wasAttackActive = state.isAttackTrigger();
					boolean wasJustLaunchedActive = state.isJustLaunched();
					
					state.tick();

					boolean syncNeeded = (isFlying != wasFlying)
						|| (isFallFlying != wasFallFlying)
						|| justLaunched
						|| (wasAttackActive != state.isAttackTrigger())
						|| (wasJustLaunchedActive != state.isJustLaunched());

					if (syncNeeded) {
						syncToAll(player);
					}
				}
			});
		}
	}

	@SubscribeEvent
	public static void onAttack(AttackEntityEvent event) {
		Player player = event.getEntity();
		if (!player.level().isClientSide()) {
			player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
				state.setAttackTrigger(true);
				syncToAll(player);
			});
		}
	}

	@SubscribeEvent
	public static void onRightClickItem(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
		Player player = event.getEntity();
		net.minecraft.world.item.ItemStack stack = event.getItemStack();
		if (stack.is(net.minecraft.world.item.Items.FIREWORK_ROCKET) && player.isFallFlying()) {
			player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
				state.setJustLaunched(true);
				if (!player.level().isClientSide()) {
					syncToAll(player);
				}
			});
		}
	}

	public static void syncToAll(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
				CelestialdragonMod.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer),
					new ClientboundSyncDragonStatePacket(
						serverPlayer.getId(),
						state.isHornsActive(),
						state.isWingsActive(),
						state.isTailActive(),
						state.isClawsActive(),
						state.isFlying(),
						state.isFallFlying(),
						state.isAttackTrigger(),
						state.isJustLaunched(),
						state.isPlayerVisible()
					)
				);
			});
		}
	}
}
