/*
 * This file is part of EverEssentials.
 *
 * EverEssentials is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverEssentials is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverEssentials.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.essentials.event;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.event.FreezeEvent;
import fr.evercraft.everapi.server.player.EPlayer;

public class EFreezeEvent implements FreezeEvent {	
	
    private final EPlayer player;
    private final boolean value;
    
    private final Cause cause;
    private boolean cancel;

    public EFreezeEvent(final EPlayer player, final boolean value, final Cause cause) {
    	this.player = player;
        this.value = value;
        
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

