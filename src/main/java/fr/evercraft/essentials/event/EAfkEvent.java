package fr.evercraft.essentials.event;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.event.AfkEvent;

public class EAfkEvent implements AfkEvent {	
	
    private final EPlayer player;
    private final boolean value;
    private final Action action;
    
    private final Cause cause;
    private boolean cancel;

    public EAfkEvent(final EPlayer player, final boolean value, final Action action, final Cause cause) {
    	this.player = player;
        this.value = value;
        this.action = action;
        this.cause = cause;
        this.cancel = false;
    }

    @Override
    public EPlayer getPlayer() {
        return this.player;
    }
    
    @Override
    public boolean getValue() {
        return this.value;
    }
    
    @Override
    public Action getAction() {
        return this.action;
    }
    
    @Override
	public Cause getCause() {
		return this.cause;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}

