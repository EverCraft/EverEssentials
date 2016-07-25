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

import java.util.UUID;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.event.IgnoreEvent;
import fr.evercraft.everapi.server.player.EPlayer;

public class EIgnoreAddEvent extends EIgnoreEvent implements IgnoreEvent.Add {	

    public EIgnoreAddEvent(final EPlayer player, final UUID uuid, final Cause cause) {
    	super(player, uuid, Action.ADD, cause);
    }
}

