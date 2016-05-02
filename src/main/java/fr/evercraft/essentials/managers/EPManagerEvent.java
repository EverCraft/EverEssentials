/**
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
/**
 * This file is part of EverPermissions.
 *
 * EverPermissions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverPermissions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverPermissions.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.essentials.managers;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.event.VanishEvent;
public class EPManagerEvent {
	private EverEssentials plugin;
	
	public EPManagerEvent(final EverEssentials plugin) {
		this.plugin = plugin;
	}
	
	public boolean post(final EPlayer player, final VanishEvent.Action action) {
		this.plugin.getLogger().debug("Event PermUserEvent : (Identifier='" + player.getIdentifier() + "';Action='" + action.name() +"')");
		return this.plugin.getGame().getEventManager().post(new VanishEvent(this.plugin, player, action));
	}
}
