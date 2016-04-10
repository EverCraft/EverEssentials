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
package fr.evercraft.essentials.service.warp;

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
import fr.evercraft.everapi.services.essentials.WarpService;

public class EWarpService implements WarpService {
	private final EverEssentials plugin;
	
	private final ConcurrentMap<String, LocationSQL> warps;
	
	public EWarpService(final EverEssentials plugin){
		this.plugin = plugin;
		
		this.warps = new ConcurrentHashMap<String, LocationSQL>();
		
		reload();
	}
	
	public void reload() {
		this.warps.clear();
		
		this.warps.putAll(this.plugin.getDataBases().selectWarps());
	}

	@Override
	public Map<String, Transform<World>> getWarps() {
		ImmutableMap.Builder<String, Transform<World>> warps = ImmutableMap.builder();
		for(Entry<String, LocationSQL> warp : this.warps.entrySet()) {
			Optional<Transform<World>> transform = warp.getValue().getTransform();
			if(transform.isPresent()) {
				warps.put(warp.getKey(), transform.get());
			}
		}
		return warps.build();
	}
	
	public Map<String, LocationSQL> getAllWarps() {
		return this.warps;
	}
	
	@Override
	public boolean hasWarp(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		return this.warps.containsKey(identifier);
	}

	@Override
	public Optional<Transform<World>> getWarp(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.warps.containsKey(identifier)) {
			return this.warps.get(identifier).getTransform();
		}
		return Optional.empty();
	}

	@Override
	public boolean addWarp(String identifier, Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if(!this.warps.containsKey(identifier)) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.warps.put(identifier, locationSQL);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().addWarp(identifier, locationSQL))
				.name("addWarp").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeWarp(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.warps.containsKey(identifier)) {
			this.warps.remove(identifier);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().removeWarp(identifier))
				.name("removeWarp").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean clearWarps() {
		if(!this.warps.isEmpty()) {
			this.warps.clear();
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().clearWarps())
				.name("clearWarps").submit(this.plugin);
			return true;
		}
		return false;
	}
}
