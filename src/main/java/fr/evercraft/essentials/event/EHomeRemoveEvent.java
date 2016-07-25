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

public class EHomeRemoveEvent extends EHomeEvent implements HomeEvent.Remove {	
	
    private final Optional<Transform<World>> location;

	public EHomeRemoveEvent(final EPlayer player, final String name, final Optional<Transform<World>> location, final Cause cause) {
    	super(player, name, Action.REMOVE, cause);
    	
    	this.location = location;
    }

	@Override
	public Optional<Transform<World>> getLocation() {
		return this.location;
	}
}

