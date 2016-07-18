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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import org.spongepowered.api.world.World;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.service.teleport.Teleport;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.EssentialsSubject;
import fr.evercraft.everapi.services.essentials.Mail;
import fr.evercraft.everapi.services.essentials.event.VanishEvent;

public class ESubject implements EssentialsSubject {
	
	private final EverEssentials plugin;
	private final UUID identifier;

	private boolean god;
	private boolean vanish;
	
	private final ConcurrentMap<String, LocationSQL> homes;
	private final CopyOnWriteArraySet<UUID> ignores;
	private final CopyOnWriteArraySet<Mail> mails;
	private Optional<LocationSQL> back;
	
	// Tempo
	private boolean afk;
	private long last_activated;
	
	private final LinkedHashMap<UUID, Long> teleports;
	
	private Optional<Teleport> teleport;

	public ESubject(final EverEssentials plugin, final UUID uuid) {
		Preconditions.checkNotNull(plugin, "plugin");
		Preconditions.checkNotNull(uuid, "uuid");
		
		this.plugin = plugin;
		this.identifier = uuid;
		
		this.homes = new ConcurrentHashMap<String, LocationSQL>();
		this.ignores = new CopyOnWriteArraySet<UUID>();
		this.mails = new CopyOnWriteArraySet<Mail>();
		this.back = Optional.empty();
		
		this.afk = false;
		this.updateLastActivated();
		
		this.teleports = new LinkedHashMap<UUID, Long>();
		
		this.teleport = Optional.empty();
		
		reloadData();
	}
	
	public void reload() {
		reloadData();
		connect();
	}
	
	public void connect() {
		Optional<EPlayer> optPlayer = this.getEPlayer();
		if(optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			if(player.get(Keys.INVISIBLE).orElse(false) != vanish) {
				player.offer(Keys.INVISIBLE, vanish);
			}
		} else {
			this.plugin.getLogger().warn("Player empty : connect");
		}
		
		this.afk = false;
	}
	
	public void disconnect() {
		Optional<EPlayer> player = this.getEPlayer();
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
	
	private void loadPlayer(Connection connection) {
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTablePlayers() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			if (list.next()) {
				this.vanish = list.getBoolean("vanish");
				this.god = list.getBoolean("god");
				
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';"
														+ "vanish='" + this.vanish + "';"
														+ "god='" + this.god + "')");
			} else {
				this.insertPlayer(connection);
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Player error when loading : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	private void loadHomes(Connection connection) {
		this.homes.clear();
		
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableHomes() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.getIdentifier());;
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
	
	private void loadBack(Connection connection) {
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableBacks() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.getIdentifier());
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
	
	private void loadIgnores(Connection connection) {
		this.ignores.clear();
		PreparedStatement preparedStatement = null;
    	try {
    		String query = 	  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableIgnores() + "` "
							+ "WHERE `uuid` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.getIdentifier());
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
			preparedStatement.setString(1, this.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				Mail mail = new EMail(this.plugin, list.getInt("id"), list.getTimestamp("datetime").getTime(), list.getString("to"), list.getBoolean("read"), list.getString("message"));
				this.mails.add(mail);
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';mail='" + mail + "')");
			}
    	} catch (SQLException e) {
    		this.plugin.getLogger().warn("Mails error when loading : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	private void insertPlayer(Connection connection) {
		PreparedStatement preparedStatement = null;
		try {
			String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTablePlayers() + "` "
							+ "VALUES (?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.getIdentifier());
			preparedStatement.setBoolean(2, this.vanish);
			preparedStatement.setBoolean(3, this.god);
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Insert : (identifier='" + this.identifier + "';"
													+ "vanish='" + this.vanish + "';"
													+ "god='" + this.god + "')");
		} catch (SQLException e) {
	    	this.plugin.getLogger().warn("Error during a change of player : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
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
		Optional<EPlayer> player = this.getEPlayer();
		if(this.vanish != vanish && player.isPresent()) {
			this.vanish = vanish;
			player.get().offer(Keys.INVISIBLE, vanish);
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setVanish(this.getIdentifier(), vanish));
			
			if(vanish) {
				this.plugin.getGame().getEventManager().post(new VanishEvent(this.plugin, player.get(), VanishEvent.Action.ADD));
			} else {
				this.plugin.getGame().getEventManager().post(new VanishEvent(this.plugin, player.get(), VanishEvent.Action.REMOVE));
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
	
	@Override
	public void updateLastActivated() {
		this.last_activated = System.currentTimeMillis();
		if(this.afk) {
			this.setAFK(false);
			
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(this.identifier);
			if(player.isPresent()) {
				if(EEMessages.AFK_ALL_DISABLE.has()) {
					player.get().broadcast(EEMessages.PREFIX.getText().concat(player.get().replaceVariable(EEMessages.AFK_ALL_DISABLE.get())));
				} else {
					player.get().sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.AFK_PLAYER_DISABLE.getText()));
				}
			}
		}
	}
	
	@Override
	public long getLastActivated() {
		return this.last_activated;
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
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setGod(this.getIdentifier(), god));
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
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().addHome(this.getIdentifier(), identifier, locationSQL));
			return true;
		}
		return false;
	}

	@Override
	public boolean removeHome(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(this.homes.containsKey(identifier)) {
			this.homes.remove(identifier);
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().removeHome(this.getIdentifier(), identifier));
			return true;
		}
		return false;
	}

	@Override
	public boolean clearHome() {
		if(!this.homes.isEmpty()) {
			this.homes.clear();
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().clearHomes(this.getIdentifier()));
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
		
		Optional<EPlayer> player = this.getEPlayer();
		if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player.get(), location.getExtent())) {
			if(!this.back.isPresent()) {
				final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
				this.back = Optional.of(locationSQL);
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().addBack(this.getIdentifier(), locationSQL));
				return true;
			} else if (!this.back.get().getTransform().equals(location)) {
				final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
				this.back = Optional.of(locationSQL);
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setBack(this.getIdentifier(), locationSQL));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean clearBack() {
		if(this.back.isPresent()) {
			this.back = Optional.empty();
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().clearBack(this.getIdentifier()));
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
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().addIgnore(this.getIdentifier(), uuid.toString()));
			return true;
		}
		return false;
	}

	@Override
	public boolean removeIgnore(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		if(this.ignores.contains(uuid)) {
			this.ignores.remove(uuid);
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().removeIgnore(this.getIdentifier(), uuid.toString()));
			return true;
		}
		return false;
	}

	@Override
	public boolean clearIgnores() {		
		if(!this.ignores.isEmpty()) {
			this.ignores.clear();
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().clearIgnores(this.getIdentifier()));
			return true;
		}
		return false;
	}
	
	private Optional<EPlayer> getEPlayer() {
		return this.plugin.getEServer().getEPlayer(this.getUniqueId());
	}
	
	/*
	 * Mails
	 */

	@Override
	public Set<Mail> getMails() {
		return ImmutableSet.copyOf(this.mails);
	}
	
	@Override
	public Optional<Mail> getMail(int id) {
		Preconditions.checkNotNull(id, "id");
		
		boolean found = false;
		Iterator<Mail> mails = this.mails.iterator();
		Mail mail = null;
		while(!found && mails.hasNext()) {
			mail = mails.next();
			found = mail.getID() == id;
		}
		
		if(found) {
			return Optional.of(mail);
		}
		return Optional.empty();
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
	public boolean addMail(String to, String message) {
		Preconditions.checkNotNull(to, "to");
		Preconditions.checkNotNull(message, "message");
		
		this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().sendMail(this, to, message));
		return true;
	}

	@Override
	public Optional<Mail> removeMail(int id) {
		final Optional<Mail> mail = this.getMail(id);
		if(mail.isPresent()) {
			this.mails.remove(mail.get());
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().removeMails(this.getIdentifier(), mail.get().getID()));
			return mail;
		}
		return Optional.empty();
	}
	
	@Override
	public boolean clearMails() {
		if(!this.mails.isEmpty()) {
			this.mails.clear();
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().clearMails(this.getIdentifier()));
			return true;
		}
		return false;
	}
	
	@Override
	public Optional<Mail> readMail(int id) {		
		final Optional<Mail> mail = this.getMail(id);
		if(mail.isPresent()) {
			if(!mail.get().isRead()) {
				mail.get().setRead(true);
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().updateMail(mail.get()));
			}
			return mail;
		}
		return Optional.empty();
	}

	public void addMail(Mail mail) {
		Preconditions.checkNotNull(mail, "mail");
		this.mails.add(mail);
	}
	
	/*
	 * Teleport Ask
	 */
	
	@Override
	public boolean addTeleport(UUID uuid, long time) {
		if(!this.teleports.containsKey(uuid)) {
			this.teleports.put(uuid, time);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeTeleport(UUID uuid) {
		if(this.teleports.containsKey(uuid)) {
			this.teleports.remove(uuid);
			return true;
		}
		return false;
	}
	
	public Map<UUID, Long> getAllTeleports() {
		return ImmutableMap.copyOf(this.teleports);
	}
	
	public TeleportRequest getTeleport(UUID uuid) {
		Long time = this.teleports.get(uuid);
		if(time != null) {
			if(time != -1) {
				return TeleportRequest.VALID;
			} else {
				return TeleportRequest.EXPIRE;
			}
		}
		return TeleportRequest.EMPTY;
	}
	
	/*
	 * Teleport
	 */
	
	@Override
	public boolean teleport() {
		if(this.teleport.isPresent()) {
			this.teleport.get().run();
			this.teleport = Optional.empty();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean setTeleport(Runnable runnable) {
		return this.setTeleport(this.plugin.getConfigs().getTeleportDelay(), runnable);
	}
	
	@Override
	public boolean setTeleport(long delay, Runnable runnable) {
		Preconditions.checkNotNull(delay, "delay");
		Preconditions.checkNotNull(runnable, "runnable");
		
		if(!this.teleport.isPresent()) {
			this.teleport = Optional.of(new Teleport(delay, runnable));
			return true;
		}
		return false;
	}
	
	public Optional<Teleport> getTeleport() {
		return this.teleport;
	}
	
	/*
	 * Accesseurs
	 */
	
	
	public String getIdentifier() {
		return this.identifier.toString();
	}
	
	public UUID getUniqueId() {
		return this.identifier;
	}

	@Override
	public Optional<Long> getTeleportTime() {
		if(this.teleport.isPresent()) {
			return Optional.of(this.teleport.get().getTime());
		}
		return Optional.empty();
	}
}
