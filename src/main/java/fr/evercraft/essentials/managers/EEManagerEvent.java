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
package fr.evercraft.essentials.managers;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.event.EAfkDisableEvent;
import fr.evercraft.essentials.event.EAfkEnableEvent;
import fr.evercraft.essentials.event.EVanishDisableEvent;
import fr.evercraft.essentials.event.EVanishEnableEvent;
import fr.evercraft.everapi.event.AfkEvent;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEManagerEvent {
	private EverEssentials plugin;
	
	public EEManagerEvent(final EverEssentials plugin) {
		this.plugin = plugin;
	}
	
	public Cause getCause() {
		return Cause.source(this.plugin).build();
	}
	
	/*
	 * Afk
	 */
	
	public boolean afk(UUID uuid, boolean value, AfkEvent.Action action) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.afk(player.get(), value, action);
		}
		return false;
	}
	
	public boolean afk(final EPlayer player, final boolean value, final AfkEvent.Action action) {
		if(value) {
			this.plugin.getLogger().debug("Event AfkEvent.Enable : (UUID='" + player.getIdentifier() + "';value='" + value + "';Action='" + action.name() +"')");
			return this.plugin.getGame().getEventManager().post(new EAfkEnableEvent(player, action, this.getCause()));
		} else {
			this.plugin.getLogger().debug("Event AfkEvent.Disable : (UUID='" + player.getIdentifier() + "';value='" + value + "';Action='" + action.name() +"')");
			return this.plugin.getGame().getEventManager().post(new EAfkDisableEvent(player, action, this.getCause()));
		}
	}
	
	public boolean vanish(UUID uuid, boolean value) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.vanish(player.get(), value);
		}
		return false;
	}
	
	public boolean vanish(final EPlayer player, final boolean value) {
		if(value) {
			this.plugin.getLogger().debug("Event VanishEvent.Enable : (UUID='" + player.getIdentifier() + "';value='" + value + "')");
			return this.plugin.getGame().getEventManager().post(new EVanishEnableEvent(player, this.getCause()));
		} else {
			this.plugin.getLogger().debug("Event VanishEvent.Disable : (UUID='" + player.getIdentifier() + "';value='" + value + "')");
			return this.plugin.getGame().getEventManager().post(new EVanishDisableEvent(player, this.getCause()));
		}
	}
}
