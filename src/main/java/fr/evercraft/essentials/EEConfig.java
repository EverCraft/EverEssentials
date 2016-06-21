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
		addDefault("debug", false, "Displays plugin performance in the logs");
		addDefault("language", EMessage.ENGLISH, "Select language messages", "Examples : ", "  French : FR_fr", "  English : EN_en");
		
		addComment("SQL", 	"Save the user in a database : ",
				" H2 : \"jdbc:h2:" + this.plugin.getPath().toAbsolutePath() + "/data\"",
				" SQL : \"jdbc:mysql://[login[:password]@]<host>:<port>/<database>\"",
				" Default users are saving in the 'data.mv.db'");
		addDefault("SQL.enable", false);
		addDefault("SQL.url", "jdbc:mysql://root:password@localhost:3306/minecraft");
		addDefault("SQL.prefix", "everessentials_");
		
		addDefault("sethome-multiple.default", 1);
		addDefault("sethome-multiple.paladin", 2);
		addDefault("teleport-delay.default", 6);
		addDefault("teleport-delay.paladin", 5);
		addDefault("near-distance.default", 200);
		addDefault("near-distance.paladin", 300);
		addDefault("butcher-max-radius", 1000);
		addDefault("warp-permission", true);
		addDefault("world-teleport-permissions", false);
		
		addDefault("effect-default-duration", 60);
		addDefault("effect-default-max-duration", 600);
		addDefault("effect-default-amplifier", 0);
		
		addDefault("remove-god-on-disconnect", false);
		addDefault("remove-vanish-on-disconnect", true);
		addDefault("gamemode-kill", true);
		addDefault("gamemode-paint", true);
		
		addDefault("god-teleport-to-spawn", true);
		
		if(get("list").getValue() == null) {
			addDefault("list.Admins", "owner admin", "To merge groups, list the groups you wish to merge", "Staff: owner admin moderator");
			addDefault("list.builder", 20, "To limit groups, set a max user limit");
			addDefault("list.default", "hidden", "To hide groups, set the group as hidden");
			addDefault("list.Players", "*", "All players with no grouping");
		}
	}
	
	public boolean isWorldTeleportPermissions() {
		return get("world-teleport-permissions").getBoolean(false);
	}
	
	public boolean isWarpPermissions() {
		return get("warp-permission").getBoolean(false);
	}
	
	public int getButcherMaxRadius() {
		return get("butcher-max-radius").getInt(1000);
	}

	public boolean removeGodOnDisconnect() {
		return get("remove-god-on-disconnect").getBoolean(false);
	}
	
	public boolean removeVanishOnDisconnect() {
		return get("remove-vanish-on-disconnect").getBoolean(true);
	}
	
	public boolean isGodTeleportToSpawn() {
		return get("remove-vanish-on-disconnect").getBoolean(true);
	}
	
	public boolean isGameModeKill() {
		return get("gamemode-kill").getBoolean(true);
	}
	
	public boolean isGameModePaint() {
		return get("gamemode-paint").getBoolean(true);
	}
	
	public ConfigurationNode getConfigList() {
		return get("list");
	}
}
