package com.celestialdragon.celestialdragoncursedfateaddon.capability;

import net.minecraft.nbt.CompoundTag;

public class DragonState {
	private boolean hornsActive = false;
	private boolean wingsActive = false;
	private boolean tailActive = false;
	private boolean clawsActive = false;
	private boolean playerVisible = true;

	private boolean isFlying = false;
	private boolean isFallFlying = false;
	private boolean attackTrigger = false;
	private boolean justLaunched = false;

	private int attackTriggerTimer = 0;
	private int justLaunchedTimer = 0;
	private boolean wasOnGround = true;
	public boolean isDragonActive() {
		return hornsActive && wingsActive && tailActive && clawsActive;
	}
	public boolean isHornsActive() { return hornsActive; }
	public boolean isWingsActive() { return wingsActive; }
	public boolean isTailActive() { return tailActive; }
	public boolean isClawsActive() { return clawsActive; }
	public void setHornsActive(boolean active) { this.hornsActive = active; }
	public void setWingsActive(boolean active) { this.wingsActive = active; }
	public void setTailActive(boolean active) { this.tailActive = active; }
	public void setClawsActive(boolean active) { this.clawsActive = active; }
	public boolean isPlayerVisible() { return playerVisible; }
	public void setPlayerVisible(boolean visible) { this.playerVisible = visible; }
	public boolean isAnyActive() {
		return hornsActive || wingsActive || tailActive || clawsActive;
	}
	public void toggleFeature(int featureIndex) {
		switch (featureIndex) {
			case 0 -> hornsActive = !hornsActive;
			case 1 -> wingsActive = !wingsActive;
			case 2 -> tailActive = !tailActive;
			case 3 -> clawsActive = !clawsActive;
			case 4 -> playerVisible = !playerVisible;
		}
	}
	public void setDragonActive(boolean dragonActive) {
		this.hornsActive = dragonActive;
		this.wingsActive = dragonActive;
		this.tailActive = dragonActive;
		this.clawsActive = dragonActive;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public void setFlying(boolean flying) {
		this.isFlying = flying;
	}

	public boolean isFallFlying() {
		return isFallFlying;
	}

	public void setFallFlying(boolean fallFlying) {
		this.isFallFlying = fallFlying;
	}

	public boolean isAttackTrigger() {
		return attackTrigger;
	}

	public void setAttackTrigger(boolean attackTrigger) {
		this.attackTrigger = attackTrigger;
		if (attackTrigger) {
			this.attackTriggerTimer = 10;
		}
	}

	public boolean isJustLaunched() {
		return justLaunched;
	}

	public void setJustLaunched(boolean justLaunched) {
		this.justLaunched = justLaunched;
		if (justLaunched) {
			this.justLaunchedTimer = 20;
		}
	}

	public int getAttackTriggerTimer() {
		return attackTriggerTimer;
	}

	public void setAttackTriggerTimer(int timer) {
		this.attackTriggerTimer = timer;
	}

	public int getJustLaunchedTimer() {
		return justLaunchedTimer;
	}

	public void setJustLaunchedTimer(int timer) {
		this.justLaunchedTimer = timer;
	}

	public boolean wasOnGround() {
		return wasOnGround;
	}

	public void setWasOnGround(boolean wasOnGround) {
		this.wasOnGround = wasOnGround;
	}

	public void tick() {
		if (attackTriggerTimer > 0) {
			attackTriggerTimer--;
			if (attackTriggerTimer == 0) {
				attackTrigger = false;
			}
		}
		if (justLaunchedTimer > 0) {
			justLaunchedTimer--;
			if (justLaunchedTimer == 0) {
				justLaunched = false;
			}
		}
	}

	public void copyFrom(DragonState other) {
		this.hornsActive = other.hornsActive;
		this.wingsActive = other.wingsActive;
		this.tailActive = other.tailActive;
		this.clawsActive = other.clawsActive;
		this.isFlying = other.isFlying;
		this.isFallFlying = other.isFallFlying;
		this.attackTrigger = other.attackTrigger;
		this.justLaunched = other.justLaunched;
		this.attackTriggerTimer = other.attackTriggerTimer;
		this.justLaunchedTimer = other.justLaunchedTimer;
		this.wasOnGround = other.wasOnGround;
		this.playerVisible = other.playerVisible;
	}

	public void saveNBTData(CompoundTag compound) {
		compound.putBoolean("hornsActive", hornsActive);
		compound.putBoolean("wingsActive", wingsActive);
		compound.putBoolean("tailActive", tailActive);
		compound.putBoolean("clawsActive", clawsActive);
		compound.putBoolean("isFlying", isFlying);
		compound.putBoolean("isFallFlying", isFallFlying);
		compound.putBoolean("attackTrigger", attackTrigger);
		compound.putBoolean("justLaunched", justLaunched);
		compound.putInt("attackTriggerTimer", attackTriggerTimer);
		compound.putInt("justLaunchedTimer", justLaunchedTimer);
		compound.putBoolean("wasOnGround", wasOnGround);
		compound.putBoolean("playerVisible", playerVisible);
		compound.putBoolean("dragonActive", isDragonActive());
	}

	public void loadNBTData(CompoundTag compound) {
		if (compound.contains("hornsActive")) {
			hornsActive = compound.getBoolean("hornsActive");
			wingsActive = compound.getBoolean("wingsActive");
			tailActive = compound.getBoolean("tailActive");
			clawsActive = compound.getBoolean("clawsActive");
		} else {
			boolean legacy = compound.getBoolean("dragonActive");
			hornsActive = legacy;
			wingsActive = legacy;
			tailActive = legacy;
			clawsActive = legacy;
		}
		isFlying = compound.getBoolean("isFlying");
		isFallFlying = compound.getBoolean("isFallFlying");
		attackTrigger = compound.getBoolean("attackTrigger");
		justLaunched = compound.getBoolean("justLaunched");
		attackTriggerTimer = compound.getInt("attackTriggerTimer");
		justLaunchedTimer = compound.getInt("justLaunchedTimer");
		this.wasOnGround = compound.getBoolean("wasOnGround");
		if (compound.contains("playerVisible")) {
			playerVisible = compound.getBoolean("playerVisible");
		} else {
			playerVisible = true;
		}
	}
}
