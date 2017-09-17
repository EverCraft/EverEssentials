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
package fr.evercraft.everessentials.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import fr.evercraft.everapi.services.essentials.EssentialsService;
import fr.evercraft.everapi.services.essentials.SubjectUserEssentials;
import fr.evercraft.everapi.services.essentials.SubjectVirtualEssentials;
import fr.evercraft.everapi.util.Chronometer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.service.subject.EUserSubject;
import fr.evercraft.everessentials.service.subject.EVirtualSubject;

public class EEssentialsService implements EssentialsService {
	private final EverEssentials plugin;
	
	private final ConcurrentMap<UUID, EUserSubject> subjects;
	private final LoadingCache<UUID, EUserSubject> cache;
	
	private final EVirtualSubject console;

	public EEssentialsService(final EverEssentials plugin) {		
		this.plugin = plugin;
		
		this.console = new EVirtualSubject();
		this.subjects = new ConcurrentHashMap<UUID, EUserSubject>();
		this.cache = CacheBuilder.newBuilder()
					    .maximumSize(100)
					    .expireAfterAccess(5, TimeUnit.MINUTES)
					    .removalListener(new RemovalListener<UUID, EUserSubject>() {
					    	/**
					    	 * Supprime un joueur du cache
					    	 */
							@Override
							public void onRemoval(RemovalNotification<UUID, EUserSubject> notification) {
								//EssentialsSubject.this.plugin.getManagerEvent().post(notification.getValue(), PermUserEvent.Action.USER_REMOVED);
							}
					    	
					    })
					    .build(new CacheLoader<UUID, EUserSubject>() {
					    	/**
					    	 * Ajoute un joueur au cache
					    	 */
					        @Override
					        public EUserSubject load(UUID uuid){
					        	Chronometer chronometer = new Chronometer();
					        	
					        	EUserSubject subject = new EUserSubject(EEssentialsService.this.plugin, uuid);
					        	EEssentialsService.this.plugin.getELogger().debug("Loading user '" + uuid.toString() + "' in " +  chronometer.getMilliseconds().toString() + " ms");
					            
					            //EssentialsSubject.this.plugin.getManagerEvent().post(subject, PermUserEvent.Action.USER_ADDED);
					            return subject;
					        }
					    });
		
		this.plugin.getGame().getServiceManager().setProvider(this.plugin, EssentialsService.class, this);
	}

	@Override
	public Optional<SubjectUserEssentials> get(UUID uuid) {
		return Optional.ofNullable(this.getSubject(uuid).orElse(null));
	}
	
	public Optional<EUserSubject> getSubject(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		try {
			if (!this.subjects.containsKey(uuid)) {
				return Optional.of(this.cache.get(uuid));
	    	}
	    	return Optional.ofNullable(this.subjects.get(uuid));
		} catch (ExecutionException e) {
			this.plugin.getELogger().warn("Error : Loading user (identifier='" + uuid + "';message='" + e.getMessage() + "')");
			return Optional.empty();
		}
	}
	
	public Optional<EUserSubject> getOnline(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		return Optional.ofNullable(this.subjects.get(uuid));
	}
	
	@Override
	public boolean hasRegistered(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		try {
			return this.plugin.getGame().getServer().getPlayer(uuid).isPresent();
		} catch (IllegalArgumentException e) {}
		return false;
	}
	
	/**
	 * Rechargement : Vide le cache et recharge tous les joueurs
	 */
	public void reload() {		
		this.cache.cleanUp();
		for (EUserSubject subject : this.subjects.values()) {
			subject.reloadData();
		}
	}
	
	/**
	 * Ajoute un joueur à la liste
	 * @param identifier L'UUID du joueur
	 */
	public void registerPlayer(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		EUserSubject player = this.cache.getIfPresent(uuid);
		// Si le joueur est dans le cache
		if (player != null) {
			player.connect();
			this.subjects.putIfAbsent(uuid, player);
			this.plugin.getELogger().debug("Loading player cache : " + uuid.toString());
		// Si le joueur n'est pas dans le cache
		} else {
			Chronometer chronometer = new Chronometer();
			player = new EUserSubject(this.plugin, uuid);
			player.connect();
			this.subjects.putIfAbsent(uuid, player);
			this.plugin.getELogger().debug("Loading player '" + uuid.toString() + "' in " +  chronometer.getMilliseconds().toString() + " ms");
		}
		//this.plugin.getManagerEvent().post(player, PermUserEvent.Action.USER_ADDED);
	}
	
	/**
	 * Supprime un joueur à la liste et l'ajoute au cache
	 * @param identifier L'UUID du joueur
	 */
	public void removePlayer(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		EUserSubject player = this.subjects.remove(uuid);
		// Si le joueur existe
		if (player != null) {
			player.disconnect();
			this.cache.put(uuid, player);
			//this.plugin.getManagerEvent().post(player, PermUserEvent.Action.USER_REMOVED);
			this.plugin.getELogger().debug("Unloading the player : " + uuid.toString());
		}
	}
	
	@Override
	public String getPermissionVanishSee() {
		return EEPermissions.VANISH_SEE.get();
	}

	public Collection<EUserSubject> getAll() {
		Set<EUserSubject> list = new HashSet<EUserSubject>();
		list.addAll(this.subjects.values());
		list.addAll(this.cache.asMap().values());
		return list;
	}
	
	public Collection<EUserSubject> getOnlines() {
		return this.subjects.values();
	}
	
	@Override
	public SubjectVirtualEssentials getConsole() {
		return this.console;
	}
}
