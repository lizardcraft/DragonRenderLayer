package com.celestialdragon.celestialdragoncursedfateaddon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import com.celestialdragon.celestialdragoncursedfateaddon.capability.DragonStateProvider;

import java.util.function.Supplier;

public class ClientboundSyncDragonStatePacket {
	private final int entityId;
	private final boolean hornsActive;
	private final boolean wingsActive;
	private final boolean tailActive;
	private final boolean clawsActive;
	private final boolean isFlying;
	private final boolean isFallFlying;
	private final boolean attackTrigger;
	private final boolean justLaunched;
	private final boolean playerVisible;

	public ClientboundSyncDragonStatePacket(int entityId, boolean hornsActive, boolean wingsActive,
											boolean tailActive, boolean clawsActive,
											boolean isFlying, boolean isFallFlying,
											boolean attackTrigger, boolean justLaunched, boolean playerVisible) {
		this.entityId = entityId;
		this.hornsActive = hornsActive;
		this.wingsActive = wingsActive;
		this.tailActive = tailActive;
		this.clawsActive = clawsActive;
		this.isFlying = isFlying;
		this.isFallFlying = isFallFlying;
		this.attackTrigger = attackTrigger;
		this.justLaunched = justLaunched;
		this.playerVisible = playerVisible;
	}

	public ClientboundSyncDragonStatePacket(FriendlyByteBuf buf) {
		this.entityId = buf.readInt();
		this.hornsActive = buf.readBoolean();
		this.wingsActive = buf.readBoolean();
		this.tailActive = buf.readBoolean();
		this.clawsActive = buf.readBoolean();
		this.isFlying = buf.readBoolean();
		this.isFallFlying = buf.readBoolean();
		this.attackTrigger = buf.readBoolean();
		this.justLaunched = buf.readBoolean();
		this.playerVisible = buf.readBoolean();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeBoolean(hornsActive);
		buf.writeBoolean(wingsActive);
		buf.writeBoolean(tailActive);
		buf.writeBoolean(clawsActive);
		buf.writeBoolean(isFlying);
		buf.writeBoolean(isFallFlying);
		buf.writeBoolean(attackTrigger);
		buf.writeBoolean(justLaunched);
		buf.writeBoolean(playerVisible);
	}

	public static void handle(ClientboundSyncDragonStatePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> {
			ClientHandler.handle(packet);
		});
		ctx.setPacketHandled(true);
	}

	private static class ClientHandler {
		private static void handle(ClientboundSyncDragonStatePacket packet) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level != null) {
				Entity entity = mc.level.getEntity(packet.entityId);
				if (entity instanceof Player player) {
					player.getCapability(DragonStateProvider.DRAGON_STATE).ifPresent(state -> {
						state.setHornsActive(packet.hornsActive);
						state.setWingsActive(packet.wingsActive);
						state.setTailActive(packet.tailActive);
						state.setClawsActive(packet.clawsActive);
						state.setFlying(packet.isFlying);
						state.setFallFlying(packet.isFallFlying);
						state.setAttackTrigger(packet.attackTrigger);
						state.setJustLaunched(packet.justLaunched);
						state.setPlayerVisible(packet.playerVisible);
					});
				}
			}
		}
	}
}
