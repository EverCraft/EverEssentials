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
package fr.evercraft.essentials.service;

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

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.java.Chronometer;
import fr.evercraft.everapi.services.essentials.EssentialsService;

public class EEssentialsService implements EssentialsService {
	private final EverEssentials plugin;
	
	private final ConcurrentMap<String, ESubject> subjects;
	private final LoadingCache<String, ESubject> cache;

	public EEssentialsService(final EverEssentials plugin) {		
		this.plugin = plugin;
		
		this.subjects = new ConcurrentHashMap<String, ESubject>();
		this.cache = CacheBuilder.newBuilder()
					    .maximumSize(100)
					    .expireAfterAccess(5, TimeUnit.MINUTES)
					    .removalListener(new RemovalListener<String, ESubject>() {
					    	/**
					    	 * Supprime un joueur du cache
					    	 */
							@Override
							public void onRemoval(RemovalNotification<String, ESubject> notification) {
								//EssentialsSubject.this.plugin.getManagerEvent().post(notification.getValue(), PermUserEvent.Action.USER_REMOVED);
							}
					    	
					    })
					    .build(new CacheLoader<String, ESubject>() {
					    	/**
					    	 * Ajoute un joueur au cache
					    	 */
					        @Override
					        public ESubject load(String identifier){
					        	Chronometer chronometer = new Chronometer();
					        	
					        	ESubject subject = new ESubject(EEssentialsService.this.plugin, identifier);
					        	EEssentialsService.this.plugin.getLogger().debug("Loading user '" + identifier + "' in " +  chronometer.getMilliseconds().toString() + " ms");
					            
					            //EssentialsSubject.this.plugin.getManagerEvent().post(subject, PermUserEvent.Action.USER_ADDED);
					            return subject;
					        }
					    });
	}

	@Override
	public ESubject get(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		try {
			if(!this.subjects.containsKey(identifier)) {
				return this.cache.get(identifier);
	    	}
	    	return this.subjects.get(identifier);
		} catch (ExecutionException e) {
			this.plugin.getLogger().warn("Error : Loading user (identifier='" + identifier + "';message='" + e.getMessage() + "')");
			return null;
		}
	}
	
	@Override
	public boolean hasRegistered(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		try {
			return this.plugin.getGame().getServer().getPlayer(UUID.fromString(identifier)).isPresent();
		} catch (IllegalArgumentException e) {}
		return false;
	}
	
	/**
	 * Rechargement : Vide le cache et recharge tous les joueurs
	 */
	public void reload() {
		this.cache.cleanUp();
		for(ESubject subject : this.subjects.values()) {
			subject.reload();
		}
	}
	
	/**
	 * Ajoute un joueur à la liste
	 * @param identifier L'UUID du joueur
	 */
	public void registerPlayer(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		ESubject player = this.cache.getIfPresent(identifier);
		// Si le joueur est dans le cache
		if(player != null) {
			this.subjects.putIfAbsent(identifier, player);
			this.plugin.getLogger().debug("Loading player cache : " + identifier);
		// Si le joueur n'est pas dans le cache
		} else {
			Chronometer chronometer = new Chronometer();
			player = new ESubject(this.plugin, identifier);
			this.subjects.putIfAbsent(identifier, player);
			this.plugin.getLogger().debug("Loading player '" + identifier + "' in " +  chronometer.getMilliseconds().toString() + " ms");
		}
		//this.plugin.getManagerEvent().post(player, PermUserEvent.Action.USER_ADDED);
	}
	
	/**
	 * Supprime un joueur à la liste et l'ajoute au cache
	 * @param identifier L'UUID du joueur
	 */
	public void removePlayer(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		ESubject player = this.subjects.remove(identifier);
		// Si le joueur existe
		if(player != null) {
			this.cache.put(identifier, player);
			//this.plugin.getManagerEvent().post(player, PermUserEvent.Action.USER_REMOVED);
			this.plugin.getLogger().debug("Unloading the player : " + identifier);
		}
	}
}
