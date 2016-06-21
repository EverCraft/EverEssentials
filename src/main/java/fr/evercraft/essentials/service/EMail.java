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
package fr.evercraft.essentials.service;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import com.google.common.base.Preconditions;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.services.essentials.Mail;

public class EMail implements Mail {
	private final EverEssentials plugin;
	
	private final int id;
	private final long time;
	
	private final String to;
	
	private boolean read;
	private String message;

	public EMail(EverEssentials plugin, int id, long time, String to, boolean read, String message) {
		Preconditions.checkNotNull(plugin, "plugin");
		Preconditions.checkNotNull(message, "message");
		
		this.plugin = plugin;
		this.id = id;
		this.time = time;
		this.to = to;
		this.read = read;
		this.message = message;
	}
	
	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public long getDateTime() {
		return this.time;
	}

	@Override
	public boolean isRead() {
		return this.read;
	}

	@Override
	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public String getTo() {
		return this.to;
	}

	@Override
	public Optional<User> getToPlayer() {
		return this.plugin.getEServer().getUser(this.to);
	}
	
	@Override
	public String getToName() {
		Optional<User> user = this.getToPlayer();
		if(user.isPresent()) {
			return user.get().getName();
		}
		return getTo();
	}

	@Override
	public Text getText() {
		return EChat.of(this.plugin.getChat().replace(this.message));
	}

	@Override
	public String toString() {
		return "EMail [id=" + this.id + ", time=" + this.time + ", to=" + this.to + ", read=" + this.read + ", message=" + this.message + "]";
	}
	
	
}
