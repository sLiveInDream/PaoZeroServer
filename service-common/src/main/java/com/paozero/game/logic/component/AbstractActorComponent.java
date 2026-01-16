package com.paozero.game.logic.component;

public abstract class AbstractActorComponent {
	private boolean active = true;
	private boolean dirty = false;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
	}

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
