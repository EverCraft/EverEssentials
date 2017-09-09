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
	EVERESSENTIALS("commands.execute"),
	HELP("commands.help"),
	RELOAD("commands.reload"),
	
	AFK("commands.afk.execute"),
	AFK_OTHERS("commands.afk.others"),
	AFK_BYPASS_AUTO("commands.afk.bypass.auto"),
	AFK_BYPASS_KICK("commands.afk.bypass.kick"),
	
	BACK("commands.back.execute"),
	
	BED("commands.bed.execute"),
	BED_OTHERS("commands.bed.others"),
	
	BOOK("commands.book.execute"),
	
	BROADCAST("commands.broadcast.execute"),
	
	BUTCHER("commands.butcher.execute"),
	BUTCHER_ANIMAL("commands.butcher.animal"),
	BUTCHER_MONSTER("commands.butcher.monster"),
	BUTCHER_TYPE("commands.butcher.type"),
	BUTCHER_ALL("commands.butcher.all"),
	BUTCHER_WORLD("commands.butcher.world"),
	
	CLEAREFFECT("commands.cleareffect.execute"),
	CLEAREFFECT_OTHERS("commands.cleareffect.others"),
	
	CLEARINVENTORY("commands.clearinventory.execute"),
	CLEARINVENTORY_OTHERS("commands.clearinventory.others"),
	
	COLOR("commands.color.execute"),
	
	EFFECT("commands.effect.execute"),
	
	ENCHANT("commands.enchant.execute"),
	
	ENDERCHEST("commands.enderchest.execute"),
	ENDERCHEST_OTHERS("commands.enderchest.others"),
	
	EXP("commands.exp.execute"),
	EXP_OTHERS("commands.exp.others"),
	
	EXT("commands.ext.execute"),
	EXT_OTHERS("commands.ext.others"),
	
	FEED("commands.feed.execute"),
	FEED_OTHERS("commands.feed.others"),
	
	FORMAT("commands.format.execute"),
	
	FREEZE("commands.freeze.execute"),
	FREEZE_OTHERS("commands.freeze.others"),
	
	FLY("commands.fly.execute"),
	FLY_OTHERS("commands.fly.others"),
	
	GAMEMODE("commands.gamemode.execute"),
	GAMEMODE_OTHERS("commands.gamemode.others"),
	
	GENERATE("commands.generate.execute"),
	
	GETPOS("commands.getpos.execute"),
	GETPOS_OTHERS("commands.getpos.others"),
	
	GOD("commands.god.execute"),
	GOD_OTHERS("commands.god.others"),
	
	HAT("commands.hat.execute"),
	
	HEAL("commands.heal.execute"),
	HEAL_OTHERS("commands.heal.others"),
	
	HOME("commands.home.execute"),
	
	DELHOME("commands.delhome.execute"),
	
	HOME_OTHERS("commands.homeothers.execute"),
	HOME_OTHERS_DELETE("commands.homeothers.delete"),
	
	SETHOME("commands.sethome.execute"),
	SETHOME_MULTIPLE("commands.sethome.multiple.execute"),
	SETHOME_MULTIPLE_GROUP("commands.sethome.multiples"),
	SETHOME_MULTIPLE_UNLIMITED("commands.sethome.multiple.unlimited"),
	
	IGNORE("commands.ignore.execute"),
	IGNORE_OTHERS("commands.ignore.others"),
	IGNORE_BYPASS("commands.ignore.bypass"),
	
	INVSEE("commands.invsee.execute"),
	INVSEE_MODIFY("commands.invsee.modify"),
	
	INFO("commands.info.execute"),
	
	ITEM("commands.item.execute"),
	ITEM_BYPASS("commands.item.bypass"),
	
	ITEM_NAME("commands.itemname.execute"),
	ITEM_LORE("commands.itemlore.execute"),
	
	JUMP("commands.jump.execute"),
	
	KICK("commands.kick.execute"),
	KICK_BYPASS("commands.kick.bypass"),
	
	KICKALL("commands.kickall.execute"),
	
	KILL("commands.kill.execute"),
	
	LAG("commands.lag.execute"),
	
	LIST("commands.list.execute"),
	
	REPAIR("commands.repair.execute"),
	REPAIR_HAND("commands.repair.hand"),
	REPAIR_HOTBAR("commands.repair.hotbar"),
	REPAIR_ALL("commands.repair.all"),
	
	REPLY("commands.reply.execute"),
	
	MAIL("commands.mail.execute"),
	MAIL_SEND("commands.mail.send"),
	MAIL_SENDALL("commands.mail.sendall"),
	
	ME("commands.me.execute"),
	
	MSG("commands.msg.execute"),
	MSG_COLOR("commands.msg.color"),
	MSG_FORMAT("commands.msg.format"),
	MSG_MAGIC("commands.msg.magic"),
	MSG_CHARACTER("commands.msg.character"),
	MSG_COMMAND("commands.msg.execute"),
	MSG_URL("commands.msg.url"),
	MSG_ICONS("commands.msg.icons"),
	
	MOJANG("commands.mojang.execute"),
	
	MORE("commands.more.execute"),
	MORE_UNLIMITED("commands.more.unlimited"),
	
	MOTD("commands.motd.execute"),
	
	NAMES("commands.names.execute"),
	NAMES_OTHERS("commands.names.others"),
	
	NEAR("commands.near.execute"),
	NEARS("commands.near"),
	
	PING("commands.ping.execute"),
	PING_OTHERS("commands.ping.others"),
	
	PLAYED("commands.played.execute"),
	PLAYED_OTHERS("commands.played.others"),
	
	RULES("commands.rules.execute"),
	
	SAY("commands.say.execute"),
	
	SEEN("commands.seen.execute"),

	SKULL("commands.skull.execute"),
	SKULL_OTHERS("commands.skull.others"),
	
	//Spawn
	SPAWN("commands.spawn.execute"),
	SPAWNS("commands.spawns.execute"),
	SETSPAWN("commands.setspawn.execute"),
	DELSPAWN("commands.delspawn.execute"),
	
	SPAWNER("commands.spawner.execute"),
	
	SPAWNMOB("commands.spawnmob.execute"),
	
	SPEED("commands.speed.execute"),
	SPEED_FLY("commands.speed.fly"),
	SPEED_WALK("commands.speed.walk"),
	SPEED_OTHERS("commands.speed.others"),
	
	STOP("commands.stop.execute"),
	
	SUDO("commands.sudo.execute"),
	SUDO_CONSOLE("commands.sudo.console"),
	SUDO_BYPASS("commands.sudo.bypass"),
	
	SUICIDE("commands.suicide.execute"),
	
	TP("commands.tp.execute"),
	TP_OTHERS("commands.tp.others"),
	
	TPACCEPT("commands.tpaccept.execute"),
	TPDENY("commands.tpadeny.execute"),
	
	TPA("commands.tpa.execute"),
	TPAHERE("commands.tpahere.execute"),
	
	TPAALL("commands.tpaall.execute"),
	TPAALL_OTHERS("commands.tpaall.others"),
	
	TPALL("commands.tpall.execute"),
	TPALL_OTHERS("commands.tpall.others"),
	
	TPPOS("commands.tppos.execute"),
	TPPOS_OTHERS("commands.tppos.others"),
	
	TIME("commands.time.execute"),
	
	TELEPORT_BYPASS_TIME("commands.teleport.bypass.time"),
	TELEPORT_BYPASS_MOVE("commands.teleport.bypass.move"),
	
	TOGGLE("commands.toggle.execute"),
	TOGGLE_OTHERS("commands.toggle.others"),
	
	TOP("commands.top.execute"),
	
	TPHERE("commands.tphere.execute"),
	
	TREE("commands.tree.execute"),
	
	UUID("commands.uuid.execute"),
	UUID_OTHERS("commands.uuid.others"),
	
	VANISH("commands.vanish.execute"),
	VANISH_OTHERS("commands.vanish.others"),
	VANISH_SEE("commands.vanish.see"),
	VANISH_PVP("commands.vanish.pvp"),
	VANISH_INTERACT("commands.vanish.interact"),
	
	WARP("commands.warp.execute"),
	WARP_NAME("commands.warps"),
	WARP_OTHERS("commands.warp.others"),
	DELWARP("commands.delwarp.execute"),
	SETWARP("commands.setwarp.execute"),
	
	WEATHER("commands.weather.execute"),
	
	WHITELIST("commands.whitelist.execute"),
	WHITELIST_MANAGE("commands.whitelist.manage"),
	
	WORLDBORDER("commands.worldborder.execute"),
	
	WORLDS("commands.worlds.execute"),
	WORLDS_OTHERS("commands.worlds.others"),
	
	WHOIS("commands.whois.execute"),
	WHOIS_OTHERS("commands.whois.others");
	
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
