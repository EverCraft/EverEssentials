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
	AFK_BYPASS_AUTO("afk.bypass.auto"),
	AFK_BYPASS_KICK("afk.bypass.kick"),
	
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
	
	FREEZE("freeze.command"),
	FREEZE_OTHERS("freeze.others"),
	
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
	SETHOME_MULTIPLE_GROUP("sethome.multiples"),
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
	
	REPLY("reply.command"),
	
	MAIL("mail.command"),
	MAIL_SEND("mail.send"),
	MAIL_SENDALL("mail.sendall"),
	
	ME("me.command"),
	
	MSG("msg.command"),
	MSG_COLOR("msg.color"),
	MSG_FORMAT("msg.format"),
	MSG_MAGIC("msg.magic"),
	MSG_CHARACTER("msg.character"),
	MSG_ICONS("msg.icons"),
	
	MOJANG("mojang.command"),
	
	MORE("more.command"),
	MORE_UNLIMITED("more.unlimited"),
	
	MOTD("motd.command"),
	
	NAMES("names.command"),
	NAMES_OTHERS("names.others"),
	
	NEAR("near.command"),
	
	PING("ping.command"),
	PING_OTHERS("ping.others"),
	
	PLAYED("played.command"),
	PLAYED_OTHERS("played.others"),
	
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
	
	TPAALL("tpaall.command"),
	TPAALL_OTHERS("tpaall.others"),
	
	TPALL("tpall.command"),
	TPALL_OTHERS("tpall.others"),
	
	TPPOS("tppos.command"),
	TPPOS_OTHERS("tppos.others"),
	
	TIME("time.command"),
	
	TELEPORT_BYPASS_TIME("teleport.bypass.time"),
	TELEPORT_BYPASS_MOVE("teleport.bypass.move"),
	
	TOGGLE("toggle.command"),
	TOGGLE_OTHERS("toggle.others"),
	
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
	
	COLOR("color.command"),
	
	SIGN_COLOR("sign.color"),
	SIGN_FORMAT("sign.format"),
	SIGN_MAGIC("sign.magic"),
	
	SIGN_MAIL_BREAK("sign.break.balance"),
	SIGN_MAIL_CREATE("sign.create.balance"),
	SIGN_MAIL_USE("sign.use.balance"),
	
	SIGN_BALANCE_BREAK("sign.break.balance"),
	SIGN_BALANCE_CREATE("sign.create.balance"),
	SIGN_BALANCE_USE("sign.use.balance"),
	
	SIGN_DISPOSAL_BREAK("sign.break.disposal"),
	SIGN_DISPOSAL_CREATE("sign.create.disposal"),
	SIGN_DISPOSAL_USE("sign.use.disposal"),
	
	SIGN_ENCHANT_BREAK("sign.break.enchant"),
	SIGN_ENCHANT_CREATE("sign.create.enchant"),
	SIGN_ENCHANT_USE("sign.use.enchant"),
	
	SIGN_FREE_BREAK("sign.break.free"),
	SIGN_FREE_CREATE("csign.reate.free"),
	SIGN_FREE_USE("sign.use.free"),
	
	SIGN_FOOD_BREAK("sign.break.food"),
	SIGN_FOOD_CREATE("sign.create.food"),
	SIGN_FOOD_USE("sign.use.food"),
	
	SIGN_GAMEMODE_BREAK("sign.break.gamemode"),
	SIGN_GAMEMODE_CREATE("sign.create.gamemode"),
	SIGN_GAMEMODE_USE("sign.use.gamemode"),
	
	SIGN_HEAL_BREAK("sign.break.heal"),
	SIGN_HEAL_CREATE("sign.create.heal"),
	SIGN_HEAL_USE("sign.use.heal"),

	SIGN_KIT_BREAK("sign.break.kit"),
	SIGN_KIT_CREATE("sign.create.kit"),
	SIGN_IKIT_USE("sign.use.kit"),

	SIGN_PROTECTION_BREAK("sign.break.protection"),
	SIGN_PROTECTION_CREATE("sign.create.protection"),
	SIGN_PROTECTION_USE("sign.use.protection"),
	SIGN_PROTECTION_BYPASS("sign.protection.bypass"),
	
	SIGN_REPAIR_BREAK("sign.break.repair"),
	SIGN_REPAIR_CREATE("sign.create.repair"),
	SIGN_REPAIR_USE("sign.use.repair"),
	
	SIGN_SPAWNMOB_BREAK("sign.break.spawnmob"),
	SIGN_SPAWNMOB_CREATE("sign.create.spawnmob"),
	SIGN_SPAWNMOB_USE("sign.use.spawnmob"),
	
	SIGN_TIME_BREAK("sign.break.time"),
	SIGN_TIME_CREATE("sign.create.time"),
	SIGN_TIME_USE("sign.use.time"),
	
	SIGN_WARP_BREAK("sign.break.warp"),
	SIGN_WARP_CREATE("sign.create.warp"),
	SIGN_WARP_USE("sign.use.warp"),
	
	SIGN_WEATHER_BREAK("sign.break.weather"),
	SIGN_WEATHER_CREATE("sign.create.weather"),
	SIGN_WEATHER_USE("sign.use.weather");
	
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
