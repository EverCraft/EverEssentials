package fr.evercraft.essentials.service;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

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

	public String getMessage() {
		return this.message;
	}
}
