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

import org.spongepowered.api.command.CommandSource;
import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EnumPermission;

public enum EEPermissions implements EnumPermission {
	EVERESSENTIALS("plugin.command"),
	
	HELP("plugin.help"),
	RELOAD("plugin.reload"),
	
	AFK("afk.command"),
	AFK_OTHERS("afk.others"),
	
	BACK("back.command"),
	
	BED("bed.command"),
	BED_OTHERS("bed.others"),
	
	BOOK("book.command"),
	
	BROADCAST("broadcast.command"),
	
	BUTCHER("butcher.command"),
	BUTCHER_ANIMAL("butcher.animal"),
	BUTCHER_MONSTER("butcher.monster"),
	BUTCHER_TYPE("butcher.type"),
	BUTCHER_ALL("butcher.all"),
	BUTCHER_WORLD("butcher.world"),
	
	CLEAREFFECT("cleareffect.command"),
	CLEAREFFECT_OTHERS("cleareffect.others"),
	
	CLEARINVENTORY("clearinventory.command"),
	CLEARINVENTORY_OTHERS("clearinventory.others"),
	
	EFFECT("effect.command"),
	
	ENCHANT("enchant.command"),
	
	EXP("exp.command"),
	EXP_OTHERS("exp.others"),
	
	EXT("ext.command"),
	EXT_OTHERS("ext.others"),
	
	FEED("feed.command"),
	FEED_OTHERS("feed.others"),
	
	FLY("fly.command"),
	FLY_OTHERS("fly.others"),
	
	GAMEMODE("gamemode.command"),
	GAMEMODE_OTHERS("gamemode.others"),
	
	GENERATE("generate.command"),
	
	GETPOS("getpos.command"),
	GETPOS_OTHERS("getpos.others"),
	
	GOD("god.command"),
	GOD_OTHERS("god.others"),
	
	HAT("hat.command"),
	
	HEAL("heal.command"),
	HEAL_OTHERS("heal.others"),
	
	HOME("home.command"),
	
	DELHOME("delhome.command"),
	
	HOME_OTHERS("homeothers.command"),
	
	SETHOME("sethome.command"),
	SETHOME_MULTIPLE("sethome.multiple.command"),
	SETHOME_MULTIPLE_GROUP("sethome.multiple"),
	SETHOME_MULTIPLE_UNLIMITED("sethome.multiple.unlimited"),
	
	INVSEE("invsee.command"),
	INVSEE_MODIFY("invsee.modify"),
	
	INFO("info.command"),
	
	ITEM("item.command"),
	ITEM_BYPASS("item.bypass"),
	
	JUMP("jump.command"),
	
	KICK("kick.command"),
	
	KICKALL("kickall.command"),
	
	KILL("kill.command"),
	
	LAG("lag.command"),
	
	LIST("list.command"),
	
	REPAIR("repair.command"),
	REPAIR_HAND("repair.hand"),
	REPAIR_HOTBAR("repair.hotbar"),
	REPAIR_ALL("repair.all"),
	
	MAIL("mail.command"),
	MAIL_SEND("mail.send"),
	MAIL_SENDALL("mail.sendall"),
	
	ME("me.command"),
	
	MOJANG("mojang.command"),
	
	MORE("more.command"),
	MORE_UNLIMITED("more.unlimited"),
	
	MOTD("motd.command"),
	
	NAMES("names.command"),
	NAMES_OTHERS("names.others"),
	
	NEAR("near.command"),
	
	PING("ping.command"),
	PING_OTHERS("ping.others"),
	
	RULES("rules.command"),
	
	SAY("say.command"),
	
	SKULL("skull.command"),
	SKULL_OTHERS("skull.others"),
	
	//Spawn
	SPAWN("spawn.command"),
	SPAWNS("spawns.command"),
	SETSPAWN("setspawn.command"),
	DELSPAWN("delspawn.command"),
	
	SPAWNER("spawner.command"),
	
	SPAWNMOB("spawnmob.command"),
	
	SPEED("speed.command"),
	SPEED_FLY("speed.fly"),
	SPEED_WALK("speed.walk"),
	SPEED_OTHERS("speed.others"),
	
	STOP("stop.command"),
	
	SUDO("sudo.command"),
	SUDO_CONSOLE("sudo.console"),
	SUDO_BYPASS("sudo.bypass"),
	
	SUICIDE("suicide.command"),
	
	TP("tp.command"),
	TP_OTHERS("tp.others"),
	
	TPACCEPT("tpaccept.command"),
	TPDENY("tpadeny.command"),
	
	TPA("tpa.command"),
	TPAHERE("tpahere.command"),
	
	TPALL("tpall.command"),
	TPALL_OTHERS("tpall.others"),
	
	TPPOS("tppos.command"),
	TPPOS_OTHERS("tppos.others"),
	
	TIME("time.command"),
	
	TOGGLE("toggle.command"),
	
	TOP("top.command"),
	
	TPHERE("tphere.command"),
	
	TREE("tree.command"),
	
	UUID("uuid.command"),
	UUID_OTHERS("uuid.others"),
	
	VANISH("vanish.command"),
	VANISH_OTHERS("vanish.others"),
	VANISH_SEE("vanish.see"),
	VANISH_PVP("vanish.pvp"),
	VANISH_INTERACT("vanish.interact"),
	
	WARP("warp.command"),
	WARP_NAME("warps"),
	WARP_OTHERS("warp.others"),
	DELWARP("delwarp.command"),
	SETWARP("setwarp.command"),
	
	WEATHER("weather.command"),
	
	WHITELIST("whitelist.command"),
	WHITELIST_MANAGE("whitelist.manage"),
	
	WORLDBORDER("worldborder.command"),
	
	WORLDS("worlds.command"),
	WORLDS_OTHERS("worlds.others"),
	
	WORLD("world"),
	
	WHOIS("whois.command"),
	WHOIS_OTHERS("whois.others"),
	
	COLOR("color.command");
	
	private final static String prefix = "everessentials";
	
	private final String permission;
    
    private EEPermissions(final String permission) {   	
    	Preconditions.checkNotNull(permission, "La permission '" + this.name() + "' n'est pas définit");
    	
    	this.permission = permission;
    }

    public String get() {
		return EEPermissions.prefix + "." + this.permission;
	}
    
    public boolean has(CommandSource player) {
    	return player.hasPermission(this.get());
    }
}
