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
package fr.evercraft.essentials;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEConfig extends EConfig {
	
	public EEConfig(EverEssentials plugin) {
		super(plugin);		
	}
	
	public void reload() {
		super.reload();
		this.plugin.getLogger().setDebug(this.isDebug());
	}

	@Override
	public void loadDefault() {
		addDefault("debug", false, 	"Displays plugin performance in the logs");
		addDefault("language", EMessage.FRENCH, 
										"Select language messages", 
										"Examples : ", 
										"  French : FR_fr", 
										"  English : EN_en");
		
		// SQL
		addComment("SQL", 				"Save the user in a database : ",
										" H2 : \"jdbc:h2:" + this.plugin.getPath().toAbsolutePath() + "/data\"",
										" SQL : \"jdbc:mysql://[login[:password]@]<host>:<port>/<database>\"",
										" Default users are saving in the 'data.mv.db'");
		addDefault("SQL.enable", false);
		addDefault("SQL.url", "jdbc:mysql://root:password@localhost:3306/minecraft");
		addDefault("SQL.prefix", "everessentials_");
		
		
		// Home
		addComment("sethome-multiple", 	"Allow players to have multiple homes.",
						  				"You can set the default number of multiple homes using the 'default' rank below.",
						  				"To remove the home limit entirely, give people 'everessentials.sethome.multiple.unlimited'.",
						  				"To grant different home amounts to different people, you need to define a 'home-rank' below.",
						  				"Create the 'home-rank' below, and give the matching permission: everessentials.sethome.multiple.<home-rank>");
		if(this.get("sethome-multiple").isVirtual()) {
			addDefault("sethome-multiple.moderator", 2);	
		}
		addDefault("sethome-multiple.default", 1);
		
		// Teleport
		addDefault("teleport-delay", 3, "The delay, in seconds, before a user actually teleports.",
										"If the user moves or gets attacked in this timeframe, the teleport never occurs.");
		
		addDefault("tpa-accept-cancellation", 120, 	"Set the timeout, in seconds for players to accept a tpa before the request is cancelled.",
													"Set to -1 for no timeout.");
		
		// AFK
		addDefault("afk-auto", 3, 		"Auto-AFK",
								  		"After this timeout in seconds, the user will be set as afk.",
								  		"This feature requires the player to have everessentials.afk.auto node.",
								  		"Set to -1 for no timeout.");
		addDefault("afk-auto-kick", 120, 
										"Auto-AFK Kick",
										"After this timeout in seconds, the user will be kicked from the server.",
										"everessentials.afk.kickexempt node overrides this feature.",
										"Set to -1 for no timeout.");
						
		// Near
		if(this.get("near-distance").isVirtual()) {
			addDefault("near-distance.moderator", 300);
		}
		addDefault("near-distance.default", 200);

		// SpawnMob
		addDefault("spawnmob-limit", 50, "Mob limit on the /spawnmob command per execution.");
		
		// Butcher
		addDefault("butcher-max-radius", 1000);
		
		// Blacklist item
		addDefault("blacklist", "minecraft:dirt", "minecraft:sand");
		
		// Warp
		addDefault("warp-permission", true, "Set this true to enable permission per warp.");
		
		// World
		addDefault("world-teleport-permissions", false, "Set to true to enable per-world permissions for teleporting between worlds with essentials commands.",
														"This applies to /world, /back, /tp[a|o][here|all], but not warps.",
														"Give someone permission to teleport to a world with everessentials.worlds.<worldname>",
														"This does not affect the /home command, there is a separate toggle below for this.");
		
		// Effect
		addDefault("effect-default-duration", 60, "Int");
		addDefault("effect-default-max-duration", 600, "Int");
		addDefault("effect-default-amplifier", 0, "Int");
		
		addDefault("generate.tickPercentLimit", 0.15, "Float");
		addDefault("generate.tickInterval", 10, "Int");
		addDefault("generate.chunksPerTick", 10, "Int");
		
		addDefault("spawnmob.limit", 20, "Mob limit on the /spawnmob command per execution.");
		
		addDefault("remove-god-on-disconnect", false);
		addDefault("remove-vanish-on-disconnect", true);
		
		// GameMode
		addDefault("gamemode-kill", true);
		addDefault("gamemode-paint", true);
		
		addDefault("god-teleport-to-spawn", true);
		
		if(this.get("list").getValue() == null) {
			addDefault("list.Admins", "owner admin", "To merge groups, list the groups you wish to merge", "Staff: owner admin moderator");
			addDefault("list.builder", 20, "To limit groups, set a max user limit");
			addDefault("list.default", "hidden", "To hide groups, set the group as hidden");
			addDefault("list.Players", "*", "All players with no grouping");
		}
	}
	
	public boolean isWorldTeleportPermissions() {
		return this.get("world-teleport-permissions").getBoolean(false);
	}
	
	public boolean isWarpPermissions() {
		return this.get("warp-permission").getBoolean(false);
	}
	
	public int getButcherMaxRadius() {
		return this.get("butcher-max-radius").getInt(1000);
	}

	public boolean removeGodOnDisconnect() {
		return this.get("remove-god-on-disconnect").getBoolean(false);
	}
	
	public boolean removeVanishOnDisconnect() {
		return this.get("remove-vanish-on-disconnect").getBoolean(true);
	}
	
	public boolean isGodTeleportToSpawn() {
		return this.get("remove-vanish-on-disconnect").getBoolean(true);
	}
	
	public boolean isGameModeKill() {
		return this.get("gamemode-kill").getBoolean(true);
	}
	
	public boolean isGameModePaint() {
		return this.get("gamemode-paint").getBoolean(true);
	}
	
	public ConfigurationNode getConfigList() {
		return this.get("list");
	}

	public String getSpawnNewbies() {
		return this.get("newbies-spawnpoint").getString("newbies");
	}

	/**
	 * Retourne le délais de téléportation
	 * @return En seconde
	 */
	public long getTeleportDelay() {
		return this.get("teleport-delay").getLong(0) * 1000;
	}
	
	public long getTeleportDelay(EPlayer player) {
		if(player.hasPermission(EEPermissions.TELEPORT_BYPASS_TIME.get())) {
			return 0;
		}
		return this.get("teleport-delay").getLong(0) * 1000;
	}
	
	/**
	 * Retourne le délais d'une demande tpa
	 * @return En seconde
	 */
	public long getTpaAcceptCancellation() {
		return this.get("tpa-accept-cancellation").getLong(-1) * 1000;
	}
	
	public boolean hasTeleportDelay() {
		return this.getTeleportDelay() > 0;
	}

	public long getAfkAuto() {
		return this.get("afk-auto").getLong(-1);
	}
	
	public long getAfkAutoKick() {
		return this.get("afk-auto-kick").getLong(-1);
	}
}
