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

import fr.evercraft.everapi.event.HomeEvent;
import fr.evercraft.everapi.server.player.EPlayer;

public class EHomeMoveEvent extends EHomeEvent implements HomeEvent.Move {	
	
    private final Optional<Transform<World>> before;
    
    private final Transform<World> after;

	public EHomeMoveEvent(final EPlayer player, final String name, final Optional<Transform<World>> before, final Transform<World> after, final Cause cause) {
    	super(player, name, Action.MOVE, cause);
    	
    	this.before = before;
    	this.after = after;
    }

	@Override
	public Optional<Transform<World>> getBeforeLocation() {
		return this.before;
	}

	@Override
	public Transform<World> getAfterLocation() {
		return this.after;
	}
}