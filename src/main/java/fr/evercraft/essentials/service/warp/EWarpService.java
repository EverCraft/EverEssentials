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
package fr.evercraft.essentials.service.warp;

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
import fr.evercraft.everapi.server.location.EVirtualTransform;
import fr.evercraft.everapi.server.location.VirtualTransform;
import fr.evercraft.everapi.services.essentials.WarpService;

public class EWarpService implements WarpService {
	private final EverEssentials plugin;
	
	private final ConcurrentMap<String, VirtualTransform> warps;
	
	public EWarpService(final EverEssentials plugin){
		this.plugin = plugin;
		
		this.warps = new ConcurrentHashMap<String, VirtualTransform>();
		
		reload();
	}
	
	public void reload() {
		this.warps.clear();
		
		this.warps.putAll(this.selectAsync());
	}

	@Override
	public Map<String, Transform<World>> getAll() {
		ImmutableMap.Builder<String, Transform<World>> warps = ImmutableMap.builder();
		for (Entry<String, VirtualTransform> warp : this.warps.entrySet()) {
			Optional<Transform<World>> transform = warp.getValue().getTransform();
			if (transform.isPresent()) {
				warps.put(warp.getKey(), transform.get());
			}
		}
		return warps.build();
	}
	
	public Map<String, VirtualTransform> getAllSQL() {
		return this.warps;
	}
	
	@Override
	public boolean has(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		return this.warps.containsKey(identifier);
	}

	@Override
	public Optional<Transform<World>> get(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if (this.warps.containsKey(identifier)) {
			return this.warps.get(identifier).getTransform();
		}
		return Optional.empty();
	}

	@Override
	public boolean add(final String identifier, final Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if (!this.warps.containsKey(identifier)) {
			final VirtualTransform locationSQL = new EVirtualTransform(this.plugin, location);
			this.warps.put(identifier, locationSQL);
			this.plugin.getThreadAsync().execute(() -> this.addAsync(identifier, locationSQL));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean update(final String identifier, final Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if (this.warps.containsKey(identifier)) {
			final VirtualTransform locationSQL = new EVirtualTransform(this.plugin, location);
			this.warps.put(identifier, locationSQL);
			this.plugin.getThreadAsync().execute(() -> this.updateAsync(identifier, locationSQL));
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if (this.warps.containsKey(identifier)) {
			this.warps.remove(identifier);
			this.plugin.getThreadAsync().execute(() -> this.removeAsync(identifier));
			return true;
		}
		return false;
	}

	@Override
	public boolean clearAll() {
		if (!this.warps.isEmpty()) {
			this.warps.clear();
			this.plugin.getThreadAsync().execute(() -> this.clearAsync());
			return true;
		}
		return false;
	}
	
	/*
	 * DataBases
	 */
	
	private Map<String, VirtualTransform> selectAsync() {
		Map<String, VirtualTransform> warps = new HashMap<String, VirtualTransform>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableWarps() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				VirtualTransform location = new EVirtualTransform(this.plugin,	list.getString("world"), 
														list.getDouble("x"),
														list.getDouble("y"),
														list.getDouble("z"),
														list.getDouble("yaw"),
														list.getDouble("pitch"));
				warps.put(list.getString("identifier"), location);
				this.plugin.getLogger().debug("Loading : (warp='" + list.getString("identifier") + "';location='" + location + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Warps error when loading : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    	return warps;
	}
	
	private void addAsync(final String identifier, final VirtualTransform location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableWarps() + "` "
    						+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, location.getWorldIdentifier());
			preparedStatement.setDouble(3, location.getPosition().getX());
			preparedStatement.setDouble(4, location.getPosition().getY());
			preparedStatement.setDouble(5, location.getPosition().getZ());
			preparedStatement.setDouble(6, location.getYaw());
			preparedStatement.setDouble(7, location.getPitch());
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Adding to the database : (warp='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of warp : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	private void updateAsync(final String identifier, final VirtualTransform location) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "UPDATE `" + this.plugin.getDataBases().getTableWarps() + "` "
    						+ "SET `world` = ?, "
	    						+ "`x` = ?, "
	    						+ "`y` = ?, "
	    						+ "`z` = ?, "
	    						+ "`yaw` = ?, "
	    						+ "`pitch` = ? "
    						+ "WHERE `identifier` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, location.getWorldIdentifier());
			preparedStatement.setDouble(2, location.getPosition().getX());
			preparedStatement.setDouble(3, location.getPosition().getY());
			preparedStatement.setDouble(4, location.getPosition().getZ());
			preparedStatement.setDouble(5, location.getYaw());
			preparedStatement.setDouble(6, location.getPitch());
			preparedStatement.setString(7, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Updating the database : (warp='" + identifier + "';location='" + location + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of warp : " + e.getMessage());
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
		    				+ "FROM `" + this.plugin.getDataBases().getTableWarps() + "` "
		    				+ "WHERE `identifier` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Remove from database : (warp='" + identifier + "')");
    	} catch (SQLException e) {
        	this.plugin.getLogger().warn("Error during a change of warp : " + e.getMessage());
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
    		String query = 	  "TRUNCATE `" + this.plugin.getDataBases().getTableWarps() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Removes the database warps");
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Error warps deletions : " + e.getMessage());
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
