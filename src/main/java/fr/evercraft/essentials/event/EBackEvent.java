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

import java.util.Optional;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.event.BackEvent;
import fr.evercraft.everapi.server.player.EPlayer;

public class EBackEvent implements BackEvent {	
	
    private final EPlayer player;
    private final Optional<Transform<World>> before;
    private final Optional<Transform<World>> after;
    
    private final Cause cause;
    private boolean cancel;

    public EBackEvent(final EPlayer player, final Optional<Transform<World>> before, final Optional<Transform<World>> after, final Cause cause) {
    	this.player = player;
        this.before = before;
        this.after = after;
        
        this.cause = cause;
        this.cancel = false;
    }

    @Override
    public EPlayer getPlayer() {
        return this.player;
    }
    
    @Override
	public Optional<Transform<World>> getBeforeLocation() {
		return this.before;
	}

	@Override
	public Optional<Transform<World>> getAfterLocation() {
		return this.after;
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

