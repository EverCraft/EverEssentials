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
package fr.evercraft.essentials.service.spawn;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.warp.LocationSQL;
import fr.evercraft.everapi.services.essentials.SpawnService;

public class ESpawnService implements SpawnService {
	private final EverEssentials plugin;
	
	private final ConcurrentMap<String, LocationSQL> spawns;
	
	public ESpawnService(final EverEssentials plugin){
		this.plugin = plugin;
		
		this.spawns = new ConcurrentHashMap<String, LocationSQL>();
		
		reload();
	}
	
	public void reload() {
		this.spawns.clear();
		
		this.spawns.putAll(this.plugin.getDataBases().selectSpawns());
	}

	@Override
	public Map<String, Transform<World>> getSpawns() {
		ImmutableMap.Builder<String, Transform<World>> spawns = ImmutableMap.builder();
		for(Entry<String, LocationSQL> spawn : this.spawns.entrySet()) {
			Optional<Transform<World>> transform = spawn.getValue().getTransform();
			if(transform.isPresent()) {
				spawns.put(spawn.getKey(), transform.get());
			}
		}
		return spawns.build();
	}
	
	public Map<String, LocationSQL> getAllSpawns() {
		return this.spawns;
	}
	
	@Override
	public boolean hasSpawn(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		return this.spawns.containsKey(identifier);
	}

	@Override
	public Optional<Transform<World>> getSpawn(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.spawns.containsKey(identifier)) {
			return this.spawns.get(identifier).getTransform();
		}
		return Optional.empty();
	}

	@Override
	public boolean addSpawn(String identifier, Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if(!this.spawns.containsKey(identifier)) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.spawns.put(identifier, locationSQL);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().addSpawn(identifier, locationSQL))
				.name("addSpawn").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeSpawn(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.spawns.containsKey(identifier)) {
			this.spawns.remove(identifier);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().removeSpawn(identifier))
				.name("removeSpawn").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean clearSpawns() {
		if(!this.spawns.isEmpty()) {
			this.spawns.clear();
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().clearSpawns())
				.name("clearSpawns").submit(this.plugin);
			return true;
		}
		return false;
	}
}
