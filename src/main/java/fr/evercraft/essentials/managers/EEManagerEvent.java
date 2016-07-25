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

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.event.*;
import fr.evercraft.everapi.event.AfkEvent;
import fr.evercraft.everapi.event.IgnoreEvent;
import fr.evercraft.everapi.event.MailEvent;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.Mail;

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
	
	/*
	 * Toggle
	 */
	
	public boolean toggle(UUID uuid, boolean value) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.toggle(player.get(), value);
		}
		return false;
	}
	
	public boolean toggle(final EPlayer player, final boolean value) {
		if(value) {
			this.plugin.getLogger().debug("Event ToogleEvent.Enable : (UUID='" + player.getIdentifier() + "';value='" + value + "')");
			return this.plugin.getGame().getEventManager().post(new EToggleEnableEvent(player, this.getCause()));
		} else {
			this.plugin.getLogger().debug("Event ToogleEvent.Disable : (UUID='" + player.getIdentifier() + "';value='" + value + "')");
			return this.plugin.getGame().getEventManager().post(new EToggleDisableEvent(player, this.getCause()));
		}
	}
	
	/*
	 * God
	 */
	
	public boolean god(UUID uuid, boolean value) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.god(player.get(), value);
		}
		return false;
	}
	
	public boolean god(final EPlayer player, final boolean value) {
		if(value) {
			this.plugin.getLogger().debug("Event GodEvent.Enable : (UUID='" + player.getIdentifier() + "';value='" + value + "')");
			return this.plugin.getGame().getEventManager().post(new EGodEnableEvent(player, this.getCause()));
		} else {
			this.plugin.getLogger().debug("Event GodEvent.Disable : (UUID='" + player.getIdentifier() + "';value='" + value + "')");
			return this.plugin.getGame().getEventManager().post(new EGodDisableEvent(player, this.getCause()));
		}
	}
	
	/*
	 * Vanish
	 */
	
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
	
	/*
	 * Mail
	 */
	
	public boolean mail(UUID uuid, final CommandSource source, final String message) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.mail(player.get(), source, message);
		}
		return false;
	}
	
	public boolean mail(final EPlayer player, final CommandSource source, final String message) {
		this.plugin.getLogger().debug("Event MailEvent.Add : (UUID='" + player.getIdentifier() + "';to='" + source.getIdentifier() + "';message='" + message + "')");
		return this.plugin.getGame().getEventManager().post(new EMailAddEvent(player, source, message, this.getCause()));
	}
	
	public boolean mail(UUID uuid, final Mail mail, final MailEvent.Action action) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.mail(player.get(), mail, action);
		}
		return false;
	}
	
	public boolean mail(final EPlayer player, final Mail mail, final MailEvent.Action action) {
		if(action.equals(MailEvent.Action.REMOVE)) {
			this.plugin.getLogger().debug("Event MailEvent.Remove : (UUID='" + player.getIdentifier() + "';mail='" + mail + "')");
			return this.plugin.getGame().getEventManager().post(new EMailRemoveEvent(player, mail, this.getCause()));
		} else if(action.equals(MailEvent.Action.READ)) {
			this.plugin.getLogger().debug("Event MailEvent.Read : (UUID='" + player.getIdentifier() + "';mail='" + mail + "')");
			return this.plugin.getGame().getEventManager().post(new EMailReadEvent(player, mail, this.getCause()));
		}
		return false;
	}
	
	/*
	 * Back
	 */
	
	public boolean back(UUID uuid, final Optional<Transform<World>> before, final Optional<Transform<World>> after) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.back(player.get(), before, after);
		}
		return false;
	}
	
	public boolean back(final EPlayer player, final Optional<Transform<World>> before, final Optional<Transform<World>> after) {
		this.plugin.getLogger().debug("Event BackEvent : (UUID='" + player.getIdentifier() + "';before='" + before + "';after='" + after + "')");
		return this.plugin.getGame().getEventManager().post(new EBackEvent(player, before, after, this.getCause()));
	}
	
	/*
	 * Home
	 */
	
	public boolean homeAdd(UUID uuid, final String name, final Transform<World> location) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.homeAdd(player.get(), name, location);
		}
		return false;
	}
	
	public boolean homeAdd(final EPlayer player, final String name, final Transform<World> location) {
		this.plugin.getLogger().debug("Event HomeEvent.Add : (UUID='" + player.getIdentifier() + "';name='" + name + "';location='" + location + "')");
		return this.plugin.getGame().getEventManager().post(new EHomeAddEvent(player, name, location, this.getCause()));
	}
	
	public boolean homeRemove(UUID uuid, final String name, final Optional<Transform<World>> location) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.homeRemove(player.get(), name, location);
		}
		return false;
	}
	
	public boolean homeRemove(final EPlayer player, final String name, final Optional<Transform<World>> location) {
		this.plugin.getLogger().debug("Event HomeEvent.Remove : (UUID='" + player.getIdentifier() + "';name='" + name + "';location='" + location + "')");
		return this.plugin.getGame().getEventManager().post(new EHomeRemoveEvent(player, name, location, this.getCause()));
	}
	
	public boolean homeMove(UUID uuid, final String name, final Optional<Transform<World>> before, final Transform<World> after) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.homeMove(player.get(), name, before, after);
		}
		return false;
	}
	
	public boolean homeMove(final EPlayer player, final String name, final Optional<Transform<World>> before, final Transform<World> after) {
		this.plugin.getLogger().debug("Event HomeEvent.Move : (UUID='" + player.getIdentifier() + "';name='" + name + "';before='" + before + "';after='" + after + "')");
		return this.plugin.getGame().getEventManager().post(new EHomeMoveEvent(player, name, before, after, this.getCause()));
	}
	
	/*
	 * Ignore
	 */
	
	public boolean ignore(final UUID uuid, final UUID ignore, final IgnoreEvent.Action action) {
		Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(uuid);
		if(player.isPresent()) {
			return this.ignore(player.get(), ignore, action);
		}
		return false;
	}
	
	public boolean ignore(final EPlayer player, final UUID ignore, final IgnoreEvent.Action action) {
		if(action.equals(IgnoreEvent.Action.ADD)) {
			this.plugin.getLogger().debug("Event IgnoreEvent.Add : (UUID='" + player.getIdentifier() + "';ignore='" + ignore + "')");
			return this.plugin.getGame().getEventManager().post(new EIgnoreAddEvent(player, ignore, this.getCause()));
		} else if(action.equals(IgnoreEvent.Action.REMOVE)) {
			this.plugin.getLogger().debug("Event IgnoreEvent.Remove : (UUID='" + player.getIdentifier() + "';ignore='" + ignore + "')");
			return this.plugin.getGame().getEventManager().post(new EIgnoreRemoveEvent(player, ignore, this.getCause()));
		}
		return false;
	}
}
