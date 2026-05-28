package com.celestialdragon.celestialdragoncursedfateaddon.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonStateProvider implements ICapabilitySerializable<CompoundTag> {
	public static final Capability<DragonState> DRAGON_STATE = CapabilityManager.get(new CapabilityToken<DragonState>() {});

	private DragonState dragonState = null;
	private final LazyOptional<DragonState> optional = LazyOptional.of(this::getOrCreateDragonState);

	@NotNull
	private DragonState getOrCreateDragonState() {
		if (this.dragonState == null) {
			this.dragonState = new DragonState();
		}
		return this.dragonState;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == DRAGON_STATE) {
			return optional.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		getOrCreateDragonState().saveNBTData(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		getOrCreateDragonState().loadNBTData(nbt);
	}
}
