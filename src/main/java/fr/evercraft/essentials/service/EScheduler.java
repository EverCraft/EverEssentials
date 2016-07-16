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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.server.player.EPlayer;


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
		this.afk_time = this.plugin.getConfigs().getAfkAuto();
		this.afk_kick_time = this.plugin.getConfigs().getAfkAutoKick();
		
		this.afk = this.afk_time > 0;
		this.afk_kick = this.afk_kick_time > 0;
	}

	public boolean start() {
		if(this.task == null) {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.async()
							.execute(() -> this.async())
							.delay(1, TimeUnit.SECONDS)
							.name("EScheduler")
							.submit(this.plugin);
			return true;
		}
		return false;
	}
	
	public boolean stop() {
		if(this.task != null) {
			this.task.cancel();
			this.task = null;
			return true;
		}
		return false;
	}
	
	public void async() {
		long current_time = System.currentTimeMillis();
		
		final List<UUID> players = new ArrayList<UUID>();
		
		for(ESubject player : this.plugin.getManagerServices().getEssentials().getAll()) {			
			// Teleport Ask
			for(Entry<UUID, Long> teleport : player.getTeleportAsk().entrySet()) {
				if(teleport.getValue() >= current_time) {
					player.removeTeleportHere(teleport.getKey());
				}
			}
			
			// Teleport Here
			for(Entry<UUID, Long> teleport : player.getTeleportHere().entrySet()) {
				if(teleport.getValue() >= current_time) {
					players.add(player.getUniqueId());
				}
			}
			
			// Teleport Delay
			if(player.getTeleport().isPresent() && player.getTeleport().get().getTime() >= current_time) {
				players.add(player.getUniqueId());
			}
			
			// AFK
			if(this.afk && !player.isAFK() && player.getLastActivated() + this.afk_time <= current_time) {
				players.add(player.getUniqueId());
			}
			
			// AFK Kick
			if(this.afk_kick  && player.getLastActivated() + this.afk_kick_time <= current_time) {
				players.add(player.getUniqueId());
			}
		}
		
		if(!players.isEmpty()) {
			this.plugin.getGame().getScheduler().createTaskBuilder()
												.execute(() -> this.sync(players))
												.name("EScheduler")
												.submit(this.plugin);
											
		}
	}
	
	public void sync(final List<UUID> players) {
		long current_time = System.currentTimeMillis();
		
		for(UUID uuid : players) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(uuid);
			if(optPlayer.isPresent()) {
				EPlayer player = optPlayer.get();
				// Teleport Delay
				Optional<Long> teleport = player.getTeleportTime();
				if(teleport.isPresent() && teleport.get() >= current_time) {
					player.teleport();
				}
				
				// AFK
				if(this.afk && !player.isAFK() && player.getLastActivated() + this.afk_time <= current_time) {
					players.add(player.getUniqueId());
				}
				
				// AFK Kick
				if(this.afk_kick  && player.getLastActivated() + this.afk_kick_time <= current_time) {
					players.add(player.getUniqueId());
				}
			}
		}
	}
}
