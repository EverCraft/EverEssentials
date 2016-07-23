package fr.evercraft.essentials.event;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.event.AfkEvent;

public class EAfkDisableEvent extends EAfkEvent implements AfkEvent.Disable {	

    public EAfkDisableEvent(final EPlayer player, final Action action, final Cause cause) {
    	super(player, false, action, cause);
    }
}

