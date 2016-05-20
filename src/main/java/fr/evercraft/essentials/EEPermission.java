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
package fr.evercraft.essentials;

import fr.evercraft.everapi.plugin.EPermission;
import fr.evercraft.everapi.plugin.EPlugin;

public class EEPermission extends EPermission {

	public EEPermission(EPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void load() {
		add("EVERESSENTIALS", "plugin.command");
		
		add("HELP", "plugin.help");
		
		add("RELOAD", "plugin.reload");
		
		add("AFK", "afk.command");
		add("AFK_OTHERS", "afk.otherss");
		
		add("BACK", "back.command");
		
		add("BED", "back.command");
		add("BED_OTHERS", "back.others");
		
		add("BOOK", "book.command");
		
		add("BROADCAST", "broadcast.command");
		
		add("BUTCHER", "butcher.command");
		add("BUTCHER_ANIMAL", "butcher.animal");
		add("BUTCHER_MONSTER", "butcher.monster");
		add("BUTCHER_TYPE", "butcher.type");
		add("BUTCHER_ALL", "butcher.all");
		add("BUTCHER_WORLD", "butcher.world");
		
		add("DAY", "day.command");
		
		add("CLEARINVENTORY", "clearinventory.command");
		add("CLEARINVENTORY_OTHERS", "clearinventory.others");
		
		add("EFFECT", "effect.command");
		add("EFFECT_OTHERS", "effect.others");
		
		add("ENCHANT", "enchant.command");
		
		add("EXP", "exp.command");
		add("EXP_OTHERS", "exp.others");
		
		add("EXT", "ext.command");
		add("EXT_OTHERS", "ext.others");
		
		add("FEED", "feed.command");
		add("FEED_OTHERS", "feed.others");
		
		add("FLY", "fly.command");
		add("FLY_OTHERS", "fly.others");
		
		add("GAMEMODE", "gamemode.command");
		add("GAMEMODE_OTHERS", "gamemode.others");
		
		add("GETPOS", "getpos.command");
		add("GETPOS_OTHERS", "getpos.others");
		
		add("GOD", "god.command");
		add("GOD_OTHERS", "god.others");
		
		add("HAT", "hat.command");
		
		add("HEAL", "heal.command");
		add("HEAL_OTHERS", "heal.others");
		
		add("HOME", "home.command");
		
		add("DELHOME", "delhome.command");
		
		add("HOME_OTHERS", "homeothers.command");
		
		add("SETHOME", "sethome.command");
		add("SETHOME_MULTIPLE", "sethome.multiple.command");
		add("SETHOME_MULTIPLE_GROUP", "sethome.multiple");
		add("SETHOME_MULTIPLE_UNLIMITED", "sethome.multiple.unlimited");
		
		add("INVSEE", "invsee.command");
		add("INVSEE_MODIFY", "invsee.modify");
		
		add("INFO", "info.command");
		
		add("JUMP", "jump.command");
		
		add("KICK", "kick.command");
		
		add("KICKALL", "kickall.command");
		
		add("KILL", "kill.command");
		
		add("LAG", "lag.command");
		add("LIST", "list.command");
		
		add("REPAIR", "repair.command");
		add("REPAIR_HAND", "repair.hand");
		add("REPAIR_HOTBAR", "repair.hotbar");
		add("REPAIR_ALL", "repair.all");
		
		add("MAIL", "mail.command");
		add("MAIL_SEND", "mail.send");
		add("MAIL_SENDALL", "mail.sendall");
		
		add("ME", "me.command");
		
		add("MOJANG", "mojang.command");
		
		add("MORE", "more.command");
		add("MORE_UNLIMITED", "more.unlimited");
		
		add("MOTD", "motd.command");
		
		add("NAMES", "names.command");
		
		add("NEAR", "near.command");
		
		add("OPME", "opme.command");
		
		add("PING", "ping.command");
		add("PING_OTHERS", "ping.others");
		
		add("RULES", "rules.command");
		
		add("SKULL", "skull.command");
		add("SKULL_OTHERS", "skull.others");
		
		add("SPAWNER", "spawner.command");
		
		add("SPAWNMOB", "spawnmob.command");
		
		add("SPEED", "speed.command");
		add("SPEED_FLY", "speed.fly");
		add("SPEED_WALK", "speed.walk");
		add("SPEED_OTHERS", "speed.others");
		
		add("STOP", "stop.command");
		
		add("SUDO", "sudo.command");
		add("SUDO_CONSOLE", "sudo.console");
		add("SUDO_BYPASS", "sudo.bypass");
		
		add("SUICIDE", "suicide.command");
		
		add("TP", "tp.command");
		add("TP_OTHERS", "tp.others");
		
		add("TPALL", "tpall.command");
		add("TPALL_OTHERS", "tpall.others");
		
		add("TPPOS", "tppos.command");
		add("TPPOS_OTHERS", "tppos.others");
		
		add("TIME", "time.command");
		
		add("TOP", "top.command");
		
		add("TPHERE", "tphere.command");
		
		add("TREE", "tree.command");
		
		add("UUID", "uuid.command");
		add("UUID_OTHERS", "uuid.others");
		
		add("VANISH", "vanish.command");
		add("VANISH_OTHERS", "vanish.others");
		add("VANISH_SEE", "vanish.see");
		add("VANISH_PVP", "vanish.pvp");
		add("VANISH_INTERACT", "vanish.interact");
		
		add("WARP", "warp.command");
		add("DELWARP", "delwarp.command");
		add("SETWARP", "setwarp.command");
		
		add("WEATHER", "weather.command");
		
		add("WORLDS", "worlds.command");
		add("WORLDS_OTHERS", "worlds.others");
		
		add("WHOIS", "whois.command");
		add("WHOIS_OTHERS", "whois.others");
		
		add("COLOR", "color.command");
	}
}
