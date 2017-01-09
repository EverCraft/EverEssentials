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
package fr.evercraft.essentials.service.subject;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.World;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.event.AfkEvent;
import fr.evercraft.everapi.event.IgnoreEvent;
import fr.evercraft.everapi.event.MailEvent;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.SubjectUserEssentials;
import fr.evercraft.everapi.services.essentials.Mail;
import fr.evercraft.everapi.services.essentials.TeleportDelay;
import fr.evercraft.everapi.services.essentials.TeleportRequest;
import fr.evercraft.everapi.services.essentials.TeleportRequest.Type;
import fr.evercraft.everapi.sponge.UtilsNetwork;

public class EUserSubject implements SubjectUserEssentials {
	
	private final EverEssentials plugin;
	
	private final UUID identifier;
	
	private final ConcurrentMap<String, LocationSQL> homes;
	private final CopyOnWriteArraySet<UUID> ignores;
	private final CopyOnWriteArraySet<Mail> mails;
	private Optional<LocationSQL> back;
	
	private boolean god;
	private boolean vanish;
	private boolean toggle;
	private boolean freeze;
	private Optional<InetAddress> last_ip;
	private long total_played;
	
	// Tempo
	private boolean afk;
	private long last_activated;
	
	private boolean afk_auto_fake;
	private boolean afk_kick_fake;
	
	private Optional<Long> last_played;
	
	private final LinkedHashMap<UUID, TeleportRequest> teleports;
	
	private Optional<TeleportDelay> teleport;
	
	private Optional<String> replyTo;

	public EUserSubject(final EverEssentials plugin, final UUID uuid) {
		Preconditions.checkNotNull(plugin, "plugin");
		Preconditions.checkNotNull(uuid, "uuid");
		
		this.plugin = plugin;
		this.identifier = uuid;
		
		this.homes = new ConcurrentHashMap<String, LocationSQL>();
		this.ignores = new CopyOnWriteArraySet<UUID>();
		this.mails = new CopyOnWriteArraySet<Mail>();
		this.back = Optional.empty();
		
		this.god = false;
		this.vanish = false;
		this.toggle = true;
		this.freeze = false;
		this.last_ip = Optional.empty();
		
		// Tempo
		this.afk = false;
		this.updateLastActivated();
		
		this.afk_auto_fake = false;
		this.afk_kick_fake = false;
		
		this.last_played = Optional.empty();
				
		this.teleports = new LinkedHashMap<UUID, TeleportRequest>();
		this.teleport = Optional.empty();
		
		this.replyTo = Optional.empty();
		
		reloadData();
	}
	
	public void reload() {
		this.reloadData();
		this.connect();
		
		this.teleports.clear();
	}
	
	public void connect() {
		Optional<EPlayer> optPlayer = this.getEPlayer();
		if (optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			if (player.get(Keys.INVISIBLE).orElse(false) != vanish) {
				player.offer(Keys.INVISIBLE, vanish);
			}
			
			this.startTotalTimePlayed();
			this.setLastIp(player.getConnection().getAddress().getAddress());
			
		} else {
			this.plugin.getLogger().warn("Player empty : connect");
		}
		this.afk = false;
	}
	
	public void disconnect() {
		Optional<EPlayer> player = this.getEPlayer();
		if (player.isPresent()) {
			if (this.plugin.getConfigs().removeVanishOnDisconnect() && this.vanish) {
				this.setVanish(false);
			}
			if (this.plugin.getConfigs().removeGodOnDisconnect() && this.god) {
				this.setGod(false);
			}
			this.stopTotalTimePlayed();
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
				this.toggle = list.getBoolean("toggle");
				this.freeze = list.getBoolean("freeze");
				this.total_played = list.getLong("total_time_played");
				this.last_ip = UtilsNetwork.getHost(list.getString("last_ip"));
				
				this.plugin.getLogger().debug("Loading : (identifier='" + this.identifier + "';"
														+ "vanish='" + this.vanish + "';"
														+ "god='" + this.god + "';"
														+ "toggle='" + this.toggle + "';"
														+ "freeze='" + this.freeze + "';"
														+ "total_played='" + this.total_played + "';"
														+ "last_ip='" + list.getString("last_ip") + "';"
														+ ")");
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
							+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.getIdentifier());
			preparedStatement.setBoolean(2, this.vanish);
			preparedStatement.setBoolean(3, this.god);
			preparedStatement.setBoolean(4, this.toggle);
			preparedStatement.setBoolean(5, this.freeze);
			preparedStatement.setLong(6, this.total_played);
			if(this.last_ip.isPresent()) {
				preparedStatement.setString(7, UtilsNetwork.getHostString(this.last_ip.get()));
			} else {
				preparedStatement.setString(7, null);
			}
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Insert : (identifier='" + this.identifier + "';"
													+ "vanish='" + this.vanish + "';"
													+ "god='" + this.god + "';"
													+ "toggle='" + this.toggle + "';"
													+ "freeze='" + this.freeze + "';"
													+ "total_played='" + this.total_played + "';"
													+ "last_ip='" + this.last_ip.orElse(null) + "';"
													+ ")");
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

	
	public boolean setVanish(final boolean vanish) {
		Optional<EPlayer> player = this.getEPlayer();
		if (this.vanish != vanish && player.isPresent()) {
			this.vanish = vanish;
			player.get().offer(Keys.INVISIBLE, vanish);
			
			if (this.plugin.getManagerEvent().vanish(player.get(), this.vanish)) {
				this.vanish = !vanish;
				player.get().offer(Keys.INVISIBLE, !vanish);
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setVanish(this.getIdentifier(), vanish));
				return true;
			}
		}
		return false;
	}

	/*
	 * AFK
	 */
	
	@Override
	public boolean isAfk() {
		return this.afk;
	}

	@Override
	public boolean setAfk(final boolean afk) {
		return this.setAfk(afk, AfkEvent.Action.PLUGIN);
	}
	
	public boolean setAfkAuto(final boolean afk) {
		return this.setAfk(afk, AfkEvent.Action.AUTO);
	}
	
	public boolean setAfk(final boolean afk, final AfkEvent.Action action) {
		if (this.afk != afk) {
			this.afk = afk;
			
			// Event
			if (this.plugin.getManagerEvent().afk(this.getUniqueId(), this.afk, action)) {
				this.afk = !afk;
			} else {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isAfkAutoFake() {
		return this.afk_auto_fake;
	}
	
	@Override
	public boolean setAfkAutoFake(final boolean afk) {
		if (this.afk_auto_fake != afk) {
			this.afk_auto_fake = afk;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isAfkKickFake() {
		return this.afk_kick_fake;
	}
	
	@Override
	public boolean setAfkKickFake(final boolean afk) {
		if (this.afk_kick_fake != afk) {
			this.afk_kick_fake = afk;
			return true;
		}
		return false;
	}
	
	@Override
	public void updateLastActivated() {
		this.last_activated = System.currentTimeMillis();
		if (this.afk) {
			this.setAfk(false, AfkEvent.Action.PLAYER);
			
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(this.identifier);
			if (player.isPresent()) {
				EEMessages.AFK_OFF_PLAYER.sendTo(player.get());
				EEMessages.AFK_OFF_ALL.sender()
					.replace(player.get().getReplacesAll())
					.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> !other.equals(player));
			}
		}
		this.afk_auto_fake = false;
		this.afk_kick_fake = false;
	}
	
	@Override
	public long getLastActivated() {
		return this.last_activated;
	}

	/*
	 * Total time played
	 */

	@Override
	public long getTotalTimePlayed() {
		if (this.last_played.isPresent()) {
			return this.total_played + (System.currentTimeMillis() - this.last_played.get());
		}
		return this.total_played;
	}
	
	@Override
	public boolean startTotalTimePlayed() {
		this.last_played = Optional.of(System.currentTimeMillis());
		return true;
	}
	
	@Override
	public boolean stopTotalTimePlayed() {
		if (this.last_played.isPresent()) {
			this.total_played = this.total_played + (System.currentTimeMillis() - this.last_played.get());
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setTotalTimePlayed(this.getIdentifier(), this.total_played));
		}
		this.last_played = Optional.empty();
		return true;
	}
	
	/*
	 * Last ip
	 */
	
	@Override
	public Optional<InetAddress> getLastIP() {
		return this.last_ip;
	}

	
	@Override
	public boolean setLastIp(final InetAddress address) {
		Preconditions.checkNotNull(address, "address");
		if(!this.last_ip.isPresent() || !UtilsNetwork.getHostString(this.last_ip.get()).equals(UtilsNetwork.getHostString(address))) {
			this.last_ip = Optional.of(address);
			this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setLastIp(this.getIdentifier(), address));
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
		if (this.god != god) {
			this.god = god;
			
			if (this.plugin.getManagerEvent().god(this.getUniqueId(), god)) {
				this.god = !god;
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setGod(this.getIdentifier(), god));
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Toggle
	 */

	@Override
	public boolean isToggle() {
		return this.toggle;
	}

	@Override
	public boolean setToggle(final boolean toggle) {		
		if (this.toggle != toggle) {
			this.toggle = toggle;
			
			if (this.plugin.getManagerEvent().toggle(this.getUniqueId(), toggle)) {
				this.toggle = !toggle;
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setToggle(this.getIdentifier(), toggle));
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Freeze
	 */

	@Override
	public boolean isFreeze() {
		return this.freeze;
	}

	@Override
	public boolean setFreeze(final boolean freeze) {		
		if (this.freeze != freeze) {
			this.freeze = freeze;
			
			if (this.plugin.getManagerEvent().freeze(this.getUniqueId(), freeze)) {
				this.freeze = !freeze;
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().setFreeze(this.getIdentifier(), freeze));
				return true;
			}
		}
		return false;
	}

	/*
	 * Homes
	 */
	
	@Override
	public Map<String, Transform<World>> getHomes() {
		ImmutableMap.Builder<String, Transform<World>> homes = ImmutableMap.builder();
		for (Entry<String, LocationSQL> home : this.homes.entrySet()) {
			Optional<Transform<World>> transform = home.getValue().getTransform();
			if (transform.isPresent()) {
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
		
		if (this.homes.containsKey(identifier)) {
			return this.homes.get(identifier).getTransform();
		}
		return Optional.empty();
	}
	
	public Optional<LocationSQL> getHomeLocation(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if (this.homes.containsKey(identifier)) {
			return Optional.ofNullable(this.homes.get(identifier));
		}
		return Optional.empty();
	}

	@Override
	public boolean addHome(final String identifier, final Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		if (!this.homes.containsKey(identifier)) {
			final LocationSQL locationSQL = new LocationSQL(this.plugin, location);
			this.homes.put(identifier, locationSQL);
			
			if (this.plugin.getManagerEvent().homeAdd(this.getUniqueId(), identifier, location)) {
				this.homes.remove(identifier);
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().addHome(this.getIdentifier(), identifier, locationSQL));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean moveHome(final String identifier, final Transform<World> location) {
		Preconditions.checkNotNull(identifier, "identifier");
		Preconditions.checkNotNull(location, "location");
		
		LocationSQL before_sql = this.homes.get(identifier);
		if (before_sql != null) {
			final LocationSQL after_sql = new LocationSQL(this.plugin, location);
			this.homes.put(identifier, after_sql);
			
			if (this.plugin.getManagerEvent().homeMove(this.getUniqueId(), identifier, before_sql.getTransform(), location)) {
				this.homes.put(identifier, before_sql);
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().moveHome(this.getIdentifier(), identifier, after_sql));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeHome(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		LocationSQL location = this.homes.get(identifier);
		if (location != null) {
			this.homes.remove(identifier);

			if (this.plugin.getManagerEvent().homeRemove(this.getUniqueId(), identifier, location.getTransform())) {
				this.homes.put(identifier, location);
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().removeHome(this.getIdentifier(), identifier));
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Back
	 */

	@Override
	public Optional<Transform<World>> getBack() {
		if (this.back.isPresent()) {
			return this.back.get().getTransform();
		}
		return Optional.empty();
	}
	
	@Override
	public boolean setBack(Transform<World> location) {
		Preconditions.checkNotNull(location, "location");
		
		Optional<EPlayer> player = this.getEPlayer();
		if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player.get(), location.getExtent())) {
			if (!this.back.isPresent()) {
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
		if (this.back.isPresent()) {
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
	public boolean ignore(User user) {
		return this.ignore(user.getUniqueId()) && !user.hasPermission(EEPermissions.IGNORE_BYPASS.get());
	}
	
	@Override
	public boolean ignore(UUID uuid) {
		return this.ignores.contains(uuid);
	}
	
	@Override
	public Set<UUID> getIgnores() {
		return ImmutableSet.copyOf(this.ignores);
	}

	@Override
	public boolean addIgnore(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		if (!this.ignores.contains(uuid)) {
			this.ignores.add(uuid);
			
			if (this.plugin.getManagerEvent().ignore(this.getUniqueId(), uuid,IgnoreEvent.Action.ADD)) {
				this.ignores.remove(uuid);
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().addIgnore(this.getIdentifier(), uuid.toString()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeIgnore(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		if (this.ignores.contains(uuid)) {
			this.ignores.remove(uuid);
			
			if (this.plugin.getManagerEvent().ignore(this.getUniqueId(), uuid,IgnoreEvent.Action.REMOVE)) {
				this.ignores.add(uuid);
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().removeIgnore(this.getIdentifier(), uuid.toString()));
				return true;
			}
		}
		return false;
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
		boolean found = false;
		Iterator<Mail> mails = this.mails.iterator();
		Mail mail = null;
		while(!found && mails.hasNext()) {
			mail = mails.next();
			found = mail.getID() == id;
		}
		
		if (found) {
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
	public boolean addMail(CommandSource to, String message) {
		Preconditions.checkNotNull(to, "to");
		Preconditions.checkNotNull(message, "message");
		
		if (!this.plugin.getManagerEvent().mail(this.getUniqueId(), to, message)) {
			this.plugin.getThreadAsync().execute(() -> {
				Optional<Mail> optMail = this.plugin.getDataBases().sendMail(this, to.getIdentifier(), message);
				optMail.ifPresent(mail -> 
					this.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> this.plugin.getManagerEvent().mail(this.getUniqueId(), mail, MailEvent.Action.RECEIVE))
						.submit(this.plugin));
			});
			return true;
		}
		return false;
	}

	@Override
	public boolean removeMail(Mail mail) {
		Preconditions.checkNotNull(mail, "mail");
		
		if (this.mails.remove(mail)) {
			if (this.plugin.getManagerEvent().mail(this.getUniqueId(), mail, MailEvent.Action.REMOVE)) {
				this.mails.add(mail);
			} else {
				this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().removeMails(this.getIdentifier(), mail.getID()));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean readMail(Mail mail) {
		Preconditions.checkNotNull(mail, "mail");
		
		if (this.mails.contains(mail)) {
			if (!mail.isRead()) {
				mail.setRead(true);
				
				if (this.plugin.getManagerEvent().mail(this.getUniqueId(), mail, MailEvent.Action.READ)) {
					mail.setRead(false);
				} else {
					this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().updateMail(mail));
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean clearMails() {
		if (!this.mails.isEmpty()) {
			List<Mail> mails = new ArrayList<Mail>(this.mails);
			this.mails.clear();
			
			for (Mail mail : mails) {
				if (this.plugin.getManagerEvent().mail(this.getUniqueId(), mail, MailEvent.Action.REMOVE)) {
					this.mails.add(mail);
				} else {
					this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().removeMails(this.getIdentifier(), mail.getID()));
				}
			}
			return this.mails.isEmpty() || this.mails.size() < mails.size();
		}
		return false;
	}

	public void addMail(Mail mail) {
		Preconditions.checkNotNull(mail, "mail");
		
		this.mails.add(mail);
	}
	
	/*
	 * Teleport Ask
	 */
	
	@Override
	public boolean addTeleportAsk(UUID uuid, long delay) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		TeleportRequest teleport = this.teleports.get(uuid);
		if (teleport == null || teleport.isExpire()) {
			this.teleports.put(uuid, new TeleportRequest(Type.TPA, delay));
			return true;
		} else {
			teleport.setDelay(delay);
		}
		return false;
	}
	
	@Override
	public boolean addTeleportAskHere(UUID uuid, long delay, @Nullable Transform<World> location) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		TeleportRequest teleport = this.teleports.get(uuid);
		if (teleport == null || teleport.isExpire()) {
			this.teleports.put(uuid, new TeleportRequest(Type.TPAHERE, delay, location));
			return true;
		} else {
			teleport.setDelay(delay);
			teleport.setLocation(location);
		}
		return false;
	}
	
	@Override
	public boolean removeTeleportAsk(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		if (this.teleports.containsKey(uuid)) {
			this.teleports.remove(uuid);
			return true;
		}
		return false;
	}
	
	public Map<UUID, TeleportRequest> getAllTeleportsAsk() {
		return ImmutableMap.copyOf(this.teleports);
	}
	
	public Optional<TeleportRequest> getTeleportAsk(UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		return Optional.ofNullable(this.teleports.get(uuid));
	}
	
	/*
	 * Teleport
	 */
	
	@Override
	public boolean hasTeleportDelay() {
		return this.teleport.isPresent();
	}
	
	@Override
	public Optional<TeleportDelay> getTeleportDelay() {
		return this.teleport;
	}
	
	@Override
	public boolean setTeleport(Runnable runnable, boolean canMove) {
		Preconditions.checkNotNull(runnable, "runnable");
		
		return this.setTeleport(System.currentTimeMillis() + this.plugin.getConfigs().getTeleportDelay(), runnable, canMove);
	}
	
	@Override
	public boolean setTeleport(long delay, Runnable runnable, boolean canMove) {
		Preconditions.checkNotNull(runnable, "runnable");
		
		if (!this.teleport.isPresent()) {
			this.teleport = Optional.of(new TeleportDelay(delay, runnable, canMove));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean runTeleportDelay() {
		if (this.teleport.isPresent()) {
			TeleportDelay teleport = this.teleport.get();
			this.teleport = Optional.empty();
			teleport.run();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean cancelTeleportDelay() {
		if (this.teleport.isPresent()) {
			this.teleport = Optional.empty();
			return true;
		}
		return false;
	}
	
	/*
	 * ReplyTo
	 */
	
	@Override
	public boolean setReplyTo(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		this.replyTo = Optional.ofNullable(identifier);
		return true;
	}

	@Override
	public Optional<String> getReplyTo() {
		return this.replyTo;
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
	
	private Optional<EPlayer> getEPlayer() {
		return this.plugin.getEServer().getEPlayer(this.getUniqueId());
	}
}
