package com.celestialdragon.celestialdragoncursedfateaddon.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;
import com.celestialdragon.celestialdragoncursedfateaddon.event.DragonModEvents;

import java.util.function.Supplier;

public class ServerboundToggleDragonActivePacket {
	private final int featureIndex; // 0=horns, 1=wings, 2=tail, 3=claws

	public ServerboundToggleDragonActivePacket(int featureIndex) {
		this.featureIndex = featureIndex;
	}

	public ServerboundToggleDragonActivePacket(FriendlyByteBuf buf) {
		this.featureIndex = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(featureIndex);
	}

	public static void handle(ServerboundToggleDragonActivePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player != null) {
				player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
					boolean wasFullyActive = state.isDragonActive();

					// Toggle the specific feature
					state.toggleFeature(packet.featureIndex);

					boolean isNowFullyActive = state.isDragonActive();
					if (!wasFullyActive && isNowFullyActive) {
						ServerLevel level = player.serverLevel();
						level.playSound(null, player.getX(), player.getY(), player.getZ(),
							SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
						level.playSound(null, player.getX(), player.getY(), player.getZ(),
							SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.6f, 0.8f);
						double px = player.getX();
						double py = player.getY() + 1.0;
						double pz = player.getZ();
						for (int i = 0; i < 40; i++) {
							double angle = Math.random() * Math.PI * 2;
							double radius = 0.5 + Math.random() * 1.5;
							double dx = Math.cos(angle) * radius * 0.15;
							double dy = (Math.random() - 0.3) * 0.2;
							double dz = Math.sin(angle) * radius * 0.15;
							level.sendParticles(ParticleTypes.DRAGON_BREATH,
								px + Math.cos(angle) * 0.5, py + Math.random() * 1.5, pz + Math.sin(angle) * 0.5,
								1, dx, dy, dz, 0.02);
						}
						for (int i = 0; i < 15; i++) {
							double dx = (Math.random() - 0.5) * 0.1;
							double dy = Math.random() * 0.15;
							double dz = (Math.random() - 0.5) * 0.1;
							level.sendParticles(ParticleTypes.END_ROD,
								px + (Math.random() - 0.5) * 1.5, py + Math.random() * 2.0, pz + (Math.random() - 0.5) * 1.5,
								1, dx, dy, dz, 0.01);
						}
					}
					DragonModEvents.syncToAll(player);
				});
			}
		});
		ctx.setPacketHandled(true);
	}
}
