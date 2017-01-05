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

import java.util.Arrays;
import java.util.List;

import fr.evercraft.everapi.message.replace.EReplacesPlayer;
import fr.evercraft.everapi.message.replace.EReplacesServer;
import fr.evercraft.everapi.plugin.file.EConfig;

public class EEMotd extends EConfig<EverEssentials> {

	public EEMotd(EverEssentials plugin, String name) {
		super(plugin, name);
	}

	@Override
	public void loadDefault() {
		List<String> list = Arrays.asList(
				"&6&m                                                                                &r",	
				"   &7Bienvenue &6" + EReplacesPlayer.DISPLAYNAME_FORMAT.getName() + "&7 sur le serveur &6&lPVP/Faction&7",
				"   &7Adresse du Teamspeak : &6evercraft.fr",
				"   &7Vous avez &6" + EReplacesPlayer.BALANCE_FORMAT.getName(),
				"   &7Nous sommes le &6" + EReplacesServer.DATE.getName() + "&7 et il est &6" + EReplacesServer.TIME.getName() + "&7",
				"&6&m                                                                                &r");
		addDefault("motd", list);
		addDefault("enable", true);
	}
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<String> getMotd() {
		return this.getListString("motd");
	}
}
