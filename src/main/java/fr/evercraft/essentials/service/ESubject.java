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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.warp.LocationSQL;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.services.essentials.EssentialsSubject;
import fr.evercraft.everapi.services.essentials.Mail;

public class ESubject implements EssentialsSubject {
	
	private final EverEssentials plugin;
	private final String identifier;

	private boolean god;
	private boolean vanish;
	
	private long mute;
	private long ban;
	
	private final ConcurrentMap<String, LocationSQL> homes;
	private final CopyOnWriteArraySet<UUID> ignores;
	private final CopyOnWriteArraySet<Mail> mails;
	private Optional<LocationSQL> back;
	
	// Tempo
	private boolean afk;
	private boolean insert;

	public ESubject(final EverEssentials plugin, final UUID uuid) {
		Preconditions.checkNotNull(plugin, "plugin");
		Preconditions.checkNotNull(uuid, "uuid");
		
		this.plugin = plugin;
		this.identifier = uuid.toString();
		
		this.homes = new ConcurrentHashMap<String, LocationSQL>();
		this.ignores = new CopyOnWriteArraySet<UUID>();
		this.mails = new CopyOnWriteArraySet<Mail>();
		this.back = Optional.empty();
		
		reloadData();
	}
	
	public void reload() {
		reloadData();
		connect();
	}
	
	public void connect() {
		Optional<Player> player = this.getPlayer();
		if(player.isPresent()) {
			player.get().offer(Keys.INVISIBLE, vanish);
		} else {
			this.plugin.getLogger().warn("Player empty : connect");
		}
		
		this.afk = false;
	}
	
	public void disconnect() {
		Optional<Player> player = this.getPlayer();
		if(player.isPresent()) {
			if(this.plugin.getConfigs().removeVanishOnDisconnect() && this.vanish) {
				this.setVanish(false);
			}
			if(this.plugin.getConfigs().removeGodOnDisconnect() && this.god) {
				this.setGod(false);
			}
		} else {
			this.plugin.getLogger().warn("Player empty : disconnect");
		}
	}
	
	public void reloadData() {
		Connection connection = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		this.loadPlayer(connection);
    		this.loadHomes(connection);
    		this.loadBack(connection);
    		this.loadIgnores(connection);
    		this.loadMails(connection);
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
	}
	
	public void loadPlayer(Connection connection) {
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTablePlayers() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			ResultSet list = preparedStatement.executeQuery();
			if (list.next()) {
				this.vanish = list.getBoolean("vanish");
				this.god = list.getBoolean("god");
				
				this.mute = list.getLong("mute");
				this.ban = list.getLong("ban");
				
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';"
														+ "vanish='" + this.vanish + "';"
														+ "god='" + this.god + "';"
														+ "mute='" + this.mute + "';"
														+ "ban='" + this.ban + "')");
				this.insert = true;
			} else {
				this.insert = false;
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Player error when loading : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	public void loadHomes(Connection connection) {
		this.homes.clear();
		
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableHomes() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);;
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				this.homes.put(list.getString("name"), new LocationSQL(	this.plugin, 
																	list.getString("world"), 
																	list.getDouble("x"),
																	list.getDouble("y"),
																	list.getDouble("z"),
																	list.getDouble("yaw"),
																	list.getDouble("pitch")));
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';home='" + list.getString("name") + "';location='" + homes.get(list.getString("name")) + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Homes error when loading : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	public void loadBack(Connection connection) {
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableBacks() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			ResultSet list = preparedStatement.executeQuery();
			if (list.next()) {
				this.back = Optional.of(new LocationSQL(	this.plugin,list.getString("world"), 
															list.getDouble("x"),
															list.getDouble("y"),
															list.getDouble("z"),
															list.getDouble("yaw"),
															list.getDouble("pitch")));
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';back='" + back.get() + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Back error when loading : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	public void loadIgnores(Connection connection) {
		this.ignores.clear();
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableIgnores() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				this.ignores.add(UUID.fromString(list.getString("ignore")));
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';ignore='" + list.getString("ignore") + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Ignores error when loading : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	public void loadMails(Connection connection) {
		this.mails.clear();
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableMails() + "` "
							+ "WHERE `player` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				this.mails.add(new EMail(this.plugin, list.getInt("id"), list.getLong("datetime"), list.getString("to"), list.getBoolean("read"), list.getString("message")));
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';ignore='" + list.getString("ignore") + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Ignores error when loading : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	public void insertPlayer() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTablePlayers() + "` "
							+ "VALUES (?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			preparedStatement.setBoolean(2, this.vanish);
			preparedStatement.setBoolean(3, this.god);
			preparedStatement.setLong(4, this.mute);
			preparedStatement.setLong(5, this.ban);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Insert : (identifier='" + this.identifier + "';"
													+ "vanish='" + this.vanish + "';"
													+ "god='" + this.god + "';"
													+ "mute='" + this.mute + "';"
													+ "ban='" + this.ban + "')");
		} catch (SQLException e) {
	    	this.plugin.getLogger().warn("Error during a change of player : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	/*
	 * Vanish
	 */

	@Override
	public boolean isVanish() {
		return this.vanish;
	}

	@Override
	public boolean setVanish(final boolean vanish) {
		Optional<Player> player = this.getPlayer();
		if(this.vanish != vanish && player.isPresent()) {
			this.vanish = vanish;
			player.get().offer(Keys.INVISIBLE, vanish);
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setVanish(this.identifier, vanish))
					.name("setVanish").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setVanish").submit(this.plugin);
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Mute
	 */

	@Override
	public boolean isMute() {
		return this.mute != 0;
	}

	@Override
	public boolean setMute(final boolean mute) {
		if(mute && this.mute != -1) {
			this.mute = -1;
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setMute(this.identifier, -1))
				.name("setMute").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setMute").submit(this.plugin);
			}
			return true;
		} else if(!mute && this.mute != 0) {
			this.mute = 0;
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setMute(this.identifier, 0))
				.name("setMute").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setMute").submit(this.plugin);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setMute(final long time) {
		if(this.mute != time) {
			this.mute = time;
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setMute(this.identifier, time))
					.name("setTempMute").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setTempMute").submit(this.plugin);
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Ban
	 */

	@Override
	public boolean isBan() {
		return this.mute != 0;
	}

	@Override
	public boolean setBan(final boolean ban) {
		if(ban && this.ban != -1) {
			this.ban = -1;
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setMute(this.identifier, -1))
					.name("setBan").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setBan").submit(this.plugin);
			}
			return true;
		} else if(!ban && this.ban != 0) {
			this.ban = 0;
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setMute(this.identifier, 0))
					.name("setBan").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setBan").submit(this.plugin);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setBan(final long time) {
		if(this.ban != time) {
			this.ban = time;
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setMute(this.identifier, time))
					.name("setTempBan").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setTempBan").submit(this.plugin);
			}
			return true;
		}
		return false;
	}

	/*
	 * AFK
	 */
	
	@Override
	public boolean isAFK() {
		return this.afk;
	}

	@Override
	public boolean setAFK(final boolean afk) {
		if(this.afk != afk) {
			this.afk = afk;
			return true;
		}
		return false;
	}
	
	/*
	 * God
	 */

	@Override
	public boolean isGod() {
		return this.god;
	}

	@Override
	public boolean setGod(final boolean god) {		
		if(this.god != god) {
			this.god = god;
			if(this.insert) {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setGod(this.identifier, god))
					.name("setGod").submit(this.plugin);
			} else {
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.insertPlayer())
					.name("setGod").submit(this.plugin);
			}
			return true;
		}
		return false;
	}

	/*
	 * Homes
	 */
	
	@Override
	public Map<String, Transform<World>> getHomes() {
		ImmutableMap.Builder<String, Transform<World>> homes = ImmutableMap.builder();
		for(Entry<String, LocationSQL> home : this.homes.entrySet()) {
			Optional<Transform<World>> transform = home.getValue().getTransform();
			if(transform.isPresent()) {
				homes.put(home.getKey(), transform.get());
			}
		}
		return homes.build();
	}
	
	public Map<String, LocationSQL> getAllHomes() {
		return ImmutableMap.copyOf(this.homes);
	}	

	@Override
	public boolean hasHome(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		return this.homes.containsKey(identifier);
	}
	
	@Override
	public Optional<Transform<World>> getHome(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.homes.containsKey(identifier)) {
			return this.homes.get(identifier).getTransform();
		}
		return Optional.empty();
	}
	
	public Optional<LocationSQL> getHomeLocation(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.homes.containsKey(identifier)) {
			return Optional.ofNullable(this.homes.get(identifier));
		}
		return Optional.empty();
	}

	@Override
	public boolean addHome(final String identifier, final Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if(!this.homes.containsKey(identifier)) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.homes.put(identifier, locationSQL);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().addHome(this.identifier, identifier, locationSQL))
				.name("addHome").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeHome(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.homes.containsKey(identifier)) {
			this.homes.remove(identifier);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().removeHome(this.identifier, identifier))
				.name("removeHome").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean clearHome() {
		if(!this.homes.isEmpty()) {
			this.homes.clear();
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().clearHomes(this.identifier))
				.name("clearHome").submit(this.plugin);
			return true;
		}
		return false;
	}
	
	/*
	 * Back
	 */

	@Override
	public Optional<Transform<World>> getBack() {
		if(this.back.isPresent()) {
			return this.back.get().getTransform();
		}
		return Optional.empty();
	}
	
	@Override
	public boolean setBack(Transform<World> location) {
		Preconditions.checkNotNull(location, "location");
		
		if(!this.back.isPresent()) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.back = Optional.of(locationSQL);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().addBack(this.identifier, locationSQL))
				.name("setBack").submit(this.plugin);
			return true;
		} else if (!this.back.get().getTransform().equals(location)) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.back = Optional.of(locationSQL);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().setBack(this.identifier, locationSQL))
				.name("setBack").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean clearBack() {
		if(this.back.isPresent()) {
			this.back = Optional.empty();
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().clearBack(this.identifier))
				.name("clearBack").submit(this.plugin);
			return true;
		}
		return false;
	}

	/*
	 * Ignores
	 */
	
	@Override
	public Set<UUID> getIgnores() {
		return ImmutableSet.copyOf(this.ignores);
	}

	@Override
	public boolean addIgnore(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		if(!this.ignores.contains(uuid)) {
			this.ignores.add(uuid);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().addIgnore(this.identifier, uuid.toString()))
				.name("addIgnore").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeIgnore(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		if(this.ignores.contains(uuid)) {
			this.ignores.remove(uuid);
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().removeIgnore(this.identifier, uuid.toString()))
				.name("removeIgnore").submit(this.plugin);
			return true;
		}
		return false;
	}

	@Override
	public boolean clearIgnores() {		
		if(!this.ignores.isEmpty()) {
			this.ignores.clear();
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().clearIgnores(this.identifier))
				.name("clearIgnores").submit(this.plugin);
			return true;
		}
		return false;
	}
	
	private Optional<Player> getPlayer() {
		return this.plugin.getGame().getServer().getPlayer(UUID.fromString(this.identifier));
	}
	
	/*
	 * Mails
	 */

	@Override
	public Set<Mail> getMails() {
		return ImmutableSet.copyOf(this.mails);
	}

	@Override
	public boolean hasMail() {
		boolean noRead = false;
		Iterator<Mail> mails = this.mails.iterator();
		while(mails.hasNext() && !noRead) {
			noRead = !mails.next().isRead();
		}
		return noRead;
	}

	@Override
	public boolean sendMail(String to, String message) {
		Preconditions.checkNotNull(to, "to");
		Preconditions.checkNotNull(message, "message");
		
		this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().sendMail(this, to, message))
			.name("sendMail").submit(this.plugin);
		return true;
	}

	@Override
	public boolean removeMail(int id) {
		Preconditions.checkNotNull(id, "id");
		
		boolean found = false;
		Iterator<Mail> mails = this.mails.iterator();
		while(!found && mails.hasNext()) {
			final Mail mail = mails.next();
			if(mail.getID() == id) {
				this.mails.remove(mail);
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().removeMails(this.identifier, mail.getID()))
					.name("removeMail").submit(this.plugin);
			}
		}
		return found;
	}
	
	@Override
	public boolean clearMails() {
		if(!this.mails.isEmpty()) {
			this.mails.clear();
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.plugin.getDataBases().clearMails(this.identifier))
				.name("clearMails").submit(this.plugin);
			return true;
		}
		return false;
	}

	public void addMail(Mail mail) {
		Preconditions.checkNotNull(mail, "mail");
		
		this.mails.add(mail);
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
}
