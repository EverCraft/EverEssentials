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
package fr.evercraft.everessentials.service.spawn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.world.World;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.server.location.EVirtualTransform;
import fr.evercraft.everapi.server.location.VirtualTransform;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.services.SpawnSubjectService;
import fr.evercraft.everessentials.EverEssentials;

public class ESpawnService implements SpawnSubjectService {
	
	private final EverEssentials plugin;
	
	private final ConcurrentMap<String, VirtualTransform> subjects;
	private VirtualTransform spawnDefault;
	private VirtualTransform spawnNewbie;
	
	// MultiThreading
	private final ReadWriteLock lock;
	private final Lock write_lock;
	private final Lock read_lock;
	
	public ESpawnService(final EverEssentials plugin){
		this.plugin = plugin;
		
		this.subjects = new ConcurrentHashMap<String, VirtualTransform>();
		
		// MultiThreading
		this.lock = new ReentrantReadWriteLock();
		this.write_lock = this.lock.writeLock();
		this.read_lock = this.lock.readLock();
		
		this.reload();
		
		this.plugin.getEverAPI().getManagerService().getSpawn().register(SpawnSubjectService.Priorities.SPAWN, user -> {
			return this.getSpawn(user);
		});
		this.plugin.getEverAPI().getManagerService().getSpawn().register(SpawnSubjectService.Priorities.NEWBIE, user -> {
			if (!user.isSpawnNewbie()) return Optional.empty();
			
			Optional<VirtualTransform> spawn = this.getNewbie();
			if (!spawn.isPresent()) return Optional.empty();
			
			return spawn.get().getTransform();
		});
		
		this.plugin.getGame().getServiceManager().setProvider(this.plugin, SpawnSubjectService.class, this);
	}
	
	public void reload() {
		this.write_lock.lock();
		try {
			this.subjects.clear();
			this.spawnDefault = null;
			this.spawnNewbie = null;
			
			this.selectExecute();
		} finally {
			this.write_lock.unlock();
		}
	}

	@Override
	public Map<SubjectReference, Transform<World>> getAll() {
		this.read_lock.lock();
		try {
			ImmutableMap.Builder<SubjectReference, Transform<World>> spawns = ImmutableMap.builder();
			for (Entry<String, VirtualTransform> spawn : this.subjects.entrySet()) {
				Optional<Transform<World>> transform = spawn.getValue().getTransform();
				if (transform.isPresent()) {
					spawns.put(this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().newSubjectReference(spawn.getKey()), transform.get());
				}
			}
			return spawns.build();
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public Map<SubjectReference, VirtualTransform> getAllVirtual() {
		this.read_lock.lock();
		try {
			ImmutableMap.Builder<SubjectReference, VirtualTransform> spawns = ImmutableMap.builder();
			for (Entry<String, VirtualTransform> spawn : this.subjects.entrySet()) {
				spawns.put(this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().newSubjectReference(spawn.getKey()), spawn.getValue());
			}
			return spawns.build();
		} finally {
			this.read_lock.unlock();
		}
	}

	public Optional<VirtualTransform> get(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		this.read_lock.lock();
		try {
			return Optional.ofNullable(this.subjects.get(identifier.toLowerCase()));
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public CompletableFuture<Boolean> set(final String identifier, final @Nullable Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");

		boolean update = false;
		this.read_lock.lock();
		try {
			update = (identifier.equalsIgnoreCase(SpawnSubjectService.DEFAULT) && this.spawnDefault != null) || 
				(identifier.equalsIgnoreCase(SpawnSubjectService.NEWBIE) && this.spawnNewbie != null) ||
				this.subjects.containsKey(identifier);
		} finally {
			this.read_lock.unlock();
		}
		
		if (location == null) {
			if (!update) return CompletableFuture.completedFuture(false);
			return this.remove(identifier);
		} 
		
		if (update) {
			return this.update(identifier, location);
		} else {
			return this.add(identifier, location);
		}
	}

	public CompletableFuture<Boolean> add(final String identifier, final Transform<World> location) {
		final VirtualTransform locationVirtual = new EVirtualTransform(this.plugin, location);
		return this.addExecute(identifier, locationVirtual).thenApply(result -> {
			if (!result) return false;
			
			this.write_lock.lock();
			try {
				if (identifier.equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
					this.spawnDefault = locationVirtual;
				} else if (identifier.equalsIgnoreCase(SpawnSubjectService.NEWBIE)) {
					this.spawnNewbie = locationVirtual;
				} else {
					this.subjects.put(identifier, locationVirtual);
				}
			} finally {
				this.write_lock.unlock();
			}
			return true;
		});
	}
	
	public CompletableFuture<Boolean> update(final String identifier, final Transform<World> location) {
		final VirtualTransform locationVirtual = new EVirtualTransform(this.plugin, location);
		return this.updateExecute(identifier, locationVirtual).thenApply(result -> {
			if (!result) return false;
			
			this.write_lock.lock();
			try {
				if (identifier.equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
					this.spawnDefault = locationVirtual;
				} else if (identifier.equalsIgnoreCase(SpawnSubjectService.NEWBIE)) {
					this.spawnNewbie = locationVirtual;
				} else {
					this.subjects.put(identifier, locationVirtual);
				}
			} finally {
				this.write_lock.unlock();
			}
			return true;
		});
	}

	public CompletableFuture<Boolean> remove(final String identifier) {
		return this.removeExecute(identifier).thenApply(result -> {
			if (!result) return false;
			
			this.write_lock.lock();
			try {
				if (identifier.equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
					this.spawnDefault = null;
				} else if (identifier.equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
					this.spawnNewbie = null;
				} else {
					this.subjects.remove(identifier);
				}
			} finally {
				this.write_lock.unlock();
			}
			return true;
		});
	}

	@Override
	public CompletableFuture<Boolean> clearAll() {
		if (this.subjects.isEmpty() && this.spawnDefault == null && this.spawnNewbie == null) return CompletableFuture.completedFuture(false);
		
		return this.clearExecute().thenApply(result -> {
			if (!result) return false;
			
			this.write_lock.lock();
			try {
				this.subjects.clear();
				this.spawnDefault = null;
				this.spawnNewbie = null;
			} finally {
				this.write_lock.unlock();
			}
			return true;
		});
	}
	
	/*
	 * DataBases
	 */
	
	private void selectExecute() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableSpawns() + "` ;";
			preparedStatement = connection.prepareStatement(query);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				VirtualTransform location = new EVirtualTransform(this.plugin,	list.getString("world"), 
														list.getDouble("x"),
														list.getDouble("y"),
														list.getDouble("z"),
														list.getDouble("yaw"),
														list.getDouble("pitch"));
				if (list.getString("identifier").equalsIgnoreCase(SpawnSubjectService.NEWBIE)) {
					this.spawnNewbie = location;
				} else if (list.getString("identifier").equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
					this.spawnDefault = location;
				} else {
					this.subjects.put(list.getString("identifier"), location);
				}
				this.plugin.getELogger().debug("Loading : (spawn='" + list.getString("identifier") + "';location='" + location + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("spawns error when loading : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	private CompletableFuture<Boolean> addExecute(final String identifier, final VirtualTransform location) {
		return CompletableFuture.supplyAsync(() -> {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
	    	try {
	    		connection = this.plugin.getDataBases().getConnection();
	    		String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableSpawns() + "` "
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
				this.plugin.getELogger().debug("Adding to the database : (spawn='" + identifier + "';location='" + location + "')");
				return true;
	    	} catch (SQLException e) {
	        	this.plugin.getELogger().warn("Error during a change of spawn : " + e.getMessage());
			} catch (ServerDisableException e) {
				e.execute();
			} finally {
				try {
					if (preparedStatement != null) preparedStatement.close();
					if (connection != null) connection.close();
				} catch (SQLException e) {}
		    }
	    	return false;
		}, this.plugin.getThreadAsync());
	}
	
	private CompletableFuture<Boolean> updateExecute(final String identifier, final VirtualTransform location) {
		return CompletableFuture.supplyAsync(() -> {
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
				preparedStatement.setString(1, location.getWorldIdentifier());
				preparedStatement.setDouble(2, location.getPosition().getX());
				preparedStatement.setDouble(3, location.getPosition().getY());
				preparedStatement.setDouble(4, location.getPosition().getZ());
				preparedStatement.setDouble(5, location.getYaw());
				preparedStatement.setDouble(6, location.getPitch());
				preparedStatement.setString(7, identifier);
				
				preparedStatement.execute();
				this.plugin.getELogger().debug("Updating the database : (spawn='" + identifier + "';location='" + location + "')");
				return true;
	    	} catch (SQLException e) {
	        	this.plugin.getELogger().warn("Error during a change of spawn : " + e.getMessage());
			} catch (ServerDisableException e) {
				e.execute();
			} finally {
				try {
					if (preparedStatement != null) preparedStatement.close();
					if (connection != null) connection.close();
				} catch (SQLException e) {}
		    }
	    	return false;
		}, this.plugin.getThreadAsync());
	}
	
	private CompletableFuture<Boolean> removeExecute(final String identifier) {
		return CompletableFuture.supplyAsync(() -> {
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
				this.plugin.getELogger().debug("Remove from database : (spawn='" + identifier + "')");
				return true;
	    	} catch (SQLException e) {
	        	this.plugin.getELogger().warn("Error during a change of spawn : " + e.getMessage());
			} catch (ServerDisableException e) {
				e.execute();
			} finally {
				try {
					if (preparedStatement != null) preparedStatement.close();
					if (connection != null) connection.close();
				} catch (SQLException e) {}
		    }
	    	return false;
		}, this.plugin.getThreadAsync());
	}
	
	private CompletableFuture<Boolean> clearExecute() {
		return CompletableFuture.supplyAsync(() -> {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
	    	try {
	    		connection = this.plugin.getDataBases().getConnection();
	    		String query = 	  "TRUNCATE `" + this.plugin.getDataBases().getTableSpawns() + "` ;";
				preparedStatement = connection.prepareStatement(query);
				
				preparedStatement.execute();
				this.plugin.getELogger().debug("Removes the database spawns");
				return true;
	    	} catch (SQLException e) {
	    		this.plugin.getELogger().warn("Error spawns deletions : " + e.getMessage());
			} catch (ServerDisableException e) {
				e.execute();
			} finally {
				try {
					if (preparedStatement != null) preparedStatement.close();
					if (connection != null) connection.close();
				} catch (SQLException e) {}
		    }
	    	return false;
		}, this.plugin.getThreadAsync());
	}

	@Override
	public Optional<VirtualTransform> getDefault() {
		return Optional.ofNullable(this.spawnDefault);
	}

	@Override
	public Optional<VirtualTransform> getNewbie() {
		return Optional.ofNullable(this.spawnNewbie);
	}

	@Override
	public Optional<VirtualTransform> get(final SubjectReference subject) {
		Preconditions.checkNotNull(subject, "subject");
		return this.get(subject.getSubjectIdentifier());
	}

	@Override
	public CompletableFuture<Boolean> setDefault(final @Nullable Transform<World> location) {
		return this.set(SpawnSubjectService.DEFAULT, location);
	}

	@Override
	public CompletableFuture<Boolean> setNewbie(final @Nullable Transform<World> location) {
		return this.set(SpawnSubjectService.NEWBIE, location);
	}

	@Override
	public CompletableFuture<Boolean> set(final SubjectReference subject, final @Nullable Transform<World> location) {
		Preconditions.checkNotNull(subject, "subject");
		return this.set(subject.getSubjectIdentifier().toLowerCase(), location);
	}

	@Override
	public Optional<Transform<World>> getSpawn(final EUser user) {
		Preconditions.checkNotNull(user, "user");
		
		Optional<SubjectReference> group = user.getGroup();
		if (!group.isPresent()) return this.getSpawnDefault();
				
		Optional<Transform<World>> spawn = this.getSpawn(group.get());
		if (spawn.isPresent()) return spawn;
				
		return this.getSpawnDefault();
	}
	
	@Override
	public Optional<Transform<World>> getSpawn(final SubjectReference reference) {
		Preconditions.checkNotNull(reference, "reference");
		
		Optional<VirtualTransform> spawn = this.get(reference);
		if (!spawn.isPresent()) return Optional.empty();
				
		return spawn.get().getTransform();
	}
	
	public Optional<Transform<World>> getSpawnDefault() {
		if (this.spawnDefault == null) return Optional.empty();

		return this.spawnDefault.getTransform();
	}
}
