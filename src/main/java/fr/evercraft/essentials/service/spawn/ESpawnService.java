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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.services.essentials.SpawnService;

public class ESpawnService implements SpawnService {
	
	private final EverEssentials plugin;
	
	private final ConcurrentMap<String, LocationSQL> spawns;
	
	public ESpawnService(final EverEssentials plugin){
		this.plugin = plugin;
		
		this.spawns = new ConcurrentHashMap<String, LocationSQL>();
		
		this.reload();
	}
	
	public void reload() {
		this.spawns.clear();
		
		this.spawns.putAll(this.selectAsync());
	}

	@Override
	public Map<String, Transform<World>> getAll() {
		ImmutableMap.Builder<String, Transform<World>> spawns = ImmutableMap.builder();
		for (Entry<String, LocationSQL> spawn : this.spawns.entrySet()) {
			Optional<Transform<World>> transform = spawn.getValue().getTransform();
			if (transform.isPresent()) {
				spawns.put(spawn.getKey(), transform.get());
			}
		}
		return spawns.build();
	}
	
	public Map<String, LocationSQL> getAllSQL() {
		return this.spawns;
	}
	
	@Override
	public boolean has(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		return this.spawns.containsKey(identifier);
	}

	@Override
	public Optional<Transform<World>> get(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if (this.spawns.containsKey(identifier)) {
			return this.spawns.get(identifier).getTransform();
		}
		return Optional.empty();
	}
	
	@Override
	public Transform<World> getDefault() {
		Optional<Transform<World>> spawn = this.get(SpawnService.DEFAULT);
		if (spawn.isPresent()) {
			return spawn.get();
		}
		return this.plugin.getEServer().getSpawn();
	}

	@Override
	public boolean add(final String identifier, final Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if (!this.spawns.containsKey(identifier)) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.spawns.put(identifier, locationSQL);
			this.plugin.getThreadAsync().execute(() -> this.addAsync(identifier, locationSQL));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean update(final String identifier, final Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if (this.spawns.containsKey(identifier)) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.spawns.put(identifier, locationSQL);
			this.plugin.getThreadAsync().execute(() -> this.updateAsync(identifier, locationSQL));
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if (this.spawns.containsKey(identifier)) {
			this.spawns.remove(identifier);
			this.plugin.getThreadAsync().execute(() -> this.removeAsync(identifier));
			return true;
		}
		return false;
	}

	@Override
	public boolean clearAll() {
		if (!this.spawns.isEmpty()) {
			this.spawns.clear();
			this.plugin.getThreadAsync().execute(() -> this.clearAsync());
			return true;
		}
		return false;
	}
	
	/*
	 * DataBases
	 */
	
	private Map<String, LocationSQL> selectAsync() {
		Map<String, LocationSQL> spawns = new HashMap<String, LocationSQL>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableSpawns() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				LocationSQL location = new LocationSQL(this.plugin,	list.getString("world"), 
														list.getDouble("x"),
														list.getDouble("y"),
														list.getDouble("z"),
														list.getDouble("yaw"),
														list.getDouble("pitch"));
				spawns.put(list.getString("identifier"), location);
				this.plugin.getLogger().debug("Loading : (spawn='" + list.getString("identifier") + "';location='" + location + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("spawns error when loading : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    	return spawns;
	}
	
	private void addAsync(final String identifier, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableSpawns() + "` "
    						+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, location.getWorldUUID());
			preparedStatement.setDouble(3, location.getX());
			preparedStatement.setDouble(4, location.getY());
			preparedStatement.setDouble(5, location.getZ());
			preparedStatement.setDouble(6, location.getYaw());
			preparedStatement.setDouble(7, location.getPitch());
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Adding to the database : (spawn='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of spawn : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	private void updateAsync(final String identifier, final LocationSQL location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "UPDATE `" + this.plugin.getDataBases().getTableSpawns() + "` "
    						+ "SET `world` = ?, "
	    						+ "`x` = ?, "
	    						+ "`y` = ?, "
	    						+ "`z` = ?, "
	    						+ "`yaw` = ?, "
	    						+ "`pitch` = ? "
    						+ "WHERE `identifier` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, location.getWorldUUID());
			preparedStatement.setDouble(2, location.getX());
			preparedStatement.setDouble(3, location.getY());
			preparedStatement.setDouble(4, location.getZ());
			preparedStatement.setDouble(5, location.getYaw());
			preparedStatement.setDouble(6, location.getPitch());
			preparedStatement.setString(7, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (spawn='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of spawn : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	private void removeAsync(final String identifier) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "DELETE " 
		    				+ "FROM `" + this.plugin.getDataBases().getTableSpawns() + "` "
		    				+ "WHERE `identifier` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Remove from database : (spawn='" + identifier + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of spawn : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	private void clearAsync() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "TRUNCATE `" + this.plugin.getDataBases().getTableSpawns() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Removes the database spawns");
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Error spawns deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
}
