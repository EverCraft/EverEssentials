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

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.service.subject.EUserSubject;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.TeleportDelay;
import fr.evercraft.everapi.services.essentials.TeleportRequest;
import fr.evercraft.everapi.services.essentials.TeleportRequest.Type;


public class EScheduler {
	private final EverEssentials plugin;
	
	private Task task;
	
	private boolean afk;
	private boolean afk_kick;
	
	private long afk_time;
	private long afk_kick_time;
	
	public EScheduler(final EverEssentials plugin) {		
		this.plugin = plugin;
		
		reload();
	}
	
	public void reload() {
		this.afk_time = this.plugin.getConfigs().getAfkAuto() * 60000;
		this.afk_kick_time = this.plugin.getConfigs().getAfkAutoKick() * 60000;
		
		this.afk = this.afk_time > 0;
		this.afk_kick = this.afk_kick_time > 0;
	}

	public boolean start() {
		if (this.task == null && !this.plugin.getEServer().getOnlinePlayers().isEmpty()) {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.async()
							.execute(() -> this.async())
							.interval(1, TimeUnit.SECONDS)
							.name("EScheduler")
							.submit(this.plugin);
			return true;
		}
		return false;
	}
	
	public boolean stop() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
			return true;
		}
		return false;
	}
	
	public void async() {
		long current_time = System.currentTimeMillis();
		
		final Set<UUID> players = new HashSet<UUID>();
		
		for (EUserSubject player : this.plugin.getEssentials().getOnlines()) {			
			// Teleport Ask
			for (Entry<UUID, TeleportRequest> teleport : player.getAllTeleportsAsk().entrySet()) {
				if (!teleport.getValue().isExpire() && teleport.getValue().getTime().isPresent() &&  teleport.getValue().getTime().get() <= current_time) {
					players.add(player.getUniqueId());
				}
			}
			
			// Teleport Delay
			if (player.getTeleportDelay().isPresent() && player.getTeleportDelay().get().getTime() <= current_time) {
				players.add(player.getUniqueId());
			}
			
			// AFK
			if (this.afk && !(player.isAfk() || player.isAfkAutoFake()) && player.getLastActivated() + this.afk_time <= current_time) {
				players.add(player.getUniqueId());
			}
			
			// AFK Kick
			if (this.afk_kick && !player.isAfkKickFake() && player.getLastActivated() + this.afk_kick_time <= current_time) {
				players.add(player.getUniqueId());
			}
		}
		
		if (!players.isEmpty()) {
			this.plugin.getGame().getScheduler().createTaskBuilder()
												.execute(() -> this.sync(players))
												.name("EScheduler")
												.submit(this.plugin);
											
		}
	}
	
	public void sync(final Set<UUID> players) {
		long current_time = System.currentTimeMillis();
		
		for (UUID uuid : players) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(uuid);
			if (optPlayer.isPresent()) {
				EPlayer player = optPlayer.get();
				
				// Teleport Ask
				for (Entry<UUID, TeleportRequest> teleport : player.getAllTeleportsAsk().entrySet()) {
					if (!teleport.getValue().isExpire() && teleport.getValue().getTime().isPresent() &&  teleport.getValue().getTime().get() <= current_time) {
						teleport.getValue().setExpire(true);
						
						Optional<EPlayer> others = this.plugin.getEServer().getEPlayer(teleport.getKey());
						if (others.isPresent()) {
							if (teleport.getValue().getType().equals(Type.TPA)) {
								EEMessages.TPA_STAFF_EXPIRE.sender()
									.replace("{player}", player.getName())
									.sendTo(others.get());
							} else if (teleport.getValue().getType().equals(Type.TPAHERE)) {
								EEMessages.TPAHERE_STAFF_EXPIRE.sender()
									.replace("{player}", player.getName())
									.sendTo(others.get());
							}
						}
					}
				}
				
				// Teleport Delay
				Optional<TeleportDelay> teleport = player.getTeleportDelay();
				if (teleport.isPresent() && teleport.get().getTime() <= current_time) {
					player.runTeleportDelay();
				}
				
				// AFK
				if (this.afk && !(player.isAfk() || player.isAfkAutoFake()) && player.getLastActivated() + this.afk_time <= current_time) {
					if (player.hasPermission(EEPermissions.AFK_BYPASS_AUTO.get())) {
						player.setAfkAutoFake(true);
					} else {
						Optional<EUserSubject> subject = this.plugin.getEssentials().getSubject(player.getUniqueId());
						if (subject.isPresent()) {
							if (subject.get().setAfkAuto(true)) {
								EEMessages.AFK_ON_PLAYER.sendTo(player);
								EEMessages.AFK_ON_ALL.sender()
									.replace(player.getReplaces())
									.sendAll(this.plugin.getEServer().getOnlineEPlayers(), others -> !others.equals(player));
							} else {
								player.setAfkAutoFake(true);
							}
						}
					}
				}
				
				// AFK Kick
				if (this.afk_kick && !player.isAfkKickFake() && player.getLastActivated() + this.afk_kick_time <= current_time) {
					if (player.hasPermission(EEPermissions.AFK_BYPASS_KICK.get())) {
						player.setAfkKickFake(true);
					} else {
						player.kick(EEMessages.AFK_KICK.getText());
					}
				}
			}
		}
	}
}
