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
package fr.evercraft.everessentials;

import fr.evercraft.everapi.plugin.EnumPermission;
import fr.evercraft.everapi.plugin.file.EnumMessage;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public enum EEPermissions implements EnumPermission {
	EVERESSENTIALS("commands.execute", EEMessages.PERMISSIONS_COMMANDS_EXECUTE, true),
	HELP("commands.help", EEMessages.PERMISSIONS_COMMANDS_HELP, true),
	RELOAD("commands.reload", EEMessages.PERMISSIONS_COMMANDS_RELOAD),
	
	AFK("commands.afk.execute", EEMessages.PERMISSIONS_COMMANDS_AFK_EXECUTE, true),
	AFK_OTHERS("commands.afk.others", EEMessages.PERMISSIONS_COMMANDS_AFK_OTHERS),
	AFK_BYPASS_AUTO("commands.afk.bypass.auto", EEMessages.PERMISSIONS_COMMANDS_AFK_BYPASS_AUTO),
	AFK_BYPASS_KICK("commands.afk.bypass.kick", EEMessages.PERMISSIONS_COMMANDS_AFK_BYPASS_KICK),
	
	BACK("commands.back.execute", EEMessages.PERMISSIONS_COMMANDS_BACK_EXECUTE),
	
	BED("commands.bed.execute", EEMessages.PERMISSIONS_COMMANDS_BED_EXECUTE),
	BED_OTHERS("commands.bed.others", EEMessages.PERMISSIONS_COMMANDS_BED_OTHERS),
	
	BOOK("commands.book.execute", EEMessages.PERMISSIONS_COMMANDS_BOOK_EXECUTE),
	
	BROADCAST("commands.broadcast.execute", EEMessages.PERMISSIONS_COMMANDS_BROADCAST_EXECUTE),
	
	BUTCHER("commands.butcher.execute", EEMessages.PERMISSIONS_COMMANDS_BUTCHER_EXECUTE),
	BUTCHER_ANIMAL("commands.butcher.animal", EEMessages.PERMISSIONS_COMMANDS_BUTCHER_ANIMAL),
	BUTCHER_MONSTER("commands.butcher.monster", EEMessages.PERMISSIONS_COMMANDS_BUTCHER_MONSTER),
	BUTCHER_TYPE("commands.butcher.type", EEMessages.PERMISSIONS_COMMANDS_BUTCHER_TYPE),
	BUTCHER_ALL("commands.butcher.all", EEMessages.PERMISSIONS_COMMANDS_BUTCHER_ALL),
	BUTCHER_WORLD("commands.butcher.world", EEMessages.PERMISSIONS_COMMANDS_BUTCHER_WORLD),
	
	CLEAREFFECT("commands.cleareffect.execute", EEMessages.PERMISSIONS_COMMANDS_CLEAREFFECT_EXECUTE),
	CLEAREFFECT_OTHERS("commands.cleareffect.others", EEMessages.PERMISSIONS_COMMANDS_CLEAREFFECT_OTHERS),
	
	CLEARINVENTORY("commands.clearinventory.execute", EEMessages.PERMISSIONS_COMMANDS_CLEARINVENTORY_EXECUTE),
	CLEARINVENTORY_OTHERS("commands.clearinventory.others", EEMessages.PERMISSIONS_COMMANDS_CLEARINVENTORY_OTHERS),
	
	COLOR("commands.color.execute", EEMessages.PERMISSIONS_COMMANDS_COLOR_EXECUTE, true),
	
	EFFECT("commands.effect.execute", EEMessages.PERMISSIONS_COMMANDS_EFFECT_EXECUTE),
	
	ENCHANT("commands.enchant.execute", EEMessages.PERMISSIONS_COMMANDS_ENCHANT_EXECUTE),
	
	ENDERCHEST("commands.enderchest.execute", EEMessages.PERMISSIONS_COMMANDS_ENDERCHEST_EXECUTE),
	ENDERCHEST_OTHERS("commands.enderchest.others", EEMessages.PERMISSIONS_COMMANDS_ENDERCHEST_OTHERS),
	
	EXP("commands.exp.execute", EEMessages.PERMISSIONS_COMMANDS_EXP_EXECUTE),
	EXP_OTHERS("commands.exp.others", EEMessages.PERMISSIONS_COMMANDS_EXP_OTHERS),
	
	EXT("commands.ext.execute", EEMessages.PERMISSIONS_COMMANDS_EXT_EXECUTE),
	EXT_OTHERS("commands.ext.others", EEMessages.PERMISSIONS_COMMANDS_EXT_OTHERS),
	
	FEED("commands.feed.execute", EEMessages.PERMISSIONS_COMMANDS_FEED_EXECUTE),
	FEED_OTHERS("commands.feed.others", EEMessages.PERMISSIONS_COMMANDS_FEED_OTHERS),
	
	FORMAT("commands.format.execute", EEMessages.PERMISSIONS_COMMANDS_FORMAT_EXECUTE, true),
	
	FREEZE("commands.freeze.execute", EEMessages.PERMISSIONS_COMMANDS_FREEZE_EXECUTE),
	FREEZE_OTHERS("commands.freeze.others", EEMessages.PERMISSIONS_COMMANDS_FREEZE_OTHERS),
	
	FLY("commands.fly.execute", EEMessages.PERMISSIONS_COMMANDS_FLY_EXECUTE),
	FLY_OTHERS("commands.fly.others", EEMessages.PERMISSIONS_COMMANDS_FLY_OTHERS),
	
	GAMEMODE("commands.gamemode.execute", EEMessages.PERMISSIONS_COMMANDS_GAMEMODE_EXECUTE),
	GAMEMODE_OTHERS("commands.gamemode.others", EEMessages.PERMISSIONS_COMMANDS_GAMEMODE_OTHERS),
	
	GENERATE("commands.generate.execute", EEMessages.PERMISSIONS_COMMANDS_GENERATE_EXECUTE),
	
	GETPOS("commands.getpos.execute", EEMessages.PERMISSIONS_COMMANDS_GETPOS_EXECUTE),
	GETPOS_OTHERS("commands.getpos.others", EEMessages.PERMISSIONS_COMMANDS_GETPOS_OTHERS),
	
	GOD("commands.god.execute", EEMessages.PERMISSIONS_COMMANDS_GOD_EXECUTE),
	GOD_OTHERS("commands.god.others", EEMessages.PERMISSIONS_COMMANDS_GOD_OTHERS),
	
	HAT("commands.hat.execute", EEMessages.PERMISSIONS_COMMANDS_HAT_EXECUTE),
	
	HEAL("commands.heal.execute", EEMessages.PERMISSIONS_COMMANDS_HEAL_EXECUTE),
	HEAL_OTHERS("commands.heal.others", EEMessages.PERMISSIONS_COMMANDS_HEAL_OTHERS),
	
	HOME("commands.home.execute", EEMessages.PERMISSIONS_COMMANDS_HOME_EXECUTE, true),
	
	DELHOME("commands.delhome.execute", EEMessages.PERMISSIONS_COMMANDS_DELHOME_EXECUTE),
	
	HOME_OTHERS("commands.homeothers.execute", EEMessages.PERMISSIONS_COMMANDS_HOMEOTHERS_EXECUTE),
	HOME_OTHERS_DELETE("commands.homeothers.delete", EEMessages.PERMISSIONS_COMMANDS_HOMEOTHERS_DELETE),
	
	SETHOME("commands.sethome.execute", EEMessages.PERMISSIONS_COMMANDS_SETHOME_EXECUTE, true),
	SETHOME_MULTIPLE("commands.sethome.multiple.execute", EEMessages.PERMISSIONS_COMMANDS_SETHOME_MULTIPLE_EXECUTE),
	SETHOME_MULTIPLE_GROUP("commands.sethome.multiples", EEMessages.PERMISSIONS_COMMANDS_SETHOME_MULTIPLES),
	SETHOME_MULTIPLE_UNLIMITED("commands.sethome.multiple.unlimited", EEMessages.PERMISSIONS_COMMANDS_SETHOME_MULTIPLE_UNLIMITED),
	
	IGNORE("commands.ignore.execute", EEMessages.PERMISSIONS_COMMANDS_IGNORE_EXECUTE, true),
	IGNORE_OTHERS("commands.ignore.others", EEMessages.PERMISSIONS_COMMANDS_IGNORE_OTHERS),
	IGNORE_BYPASS("commands.ignore.bypass", EEMessages.PERMISSIONS_COMMANDS_IGNORE_BYPASS),
	
	INVSEE("commands.invsee.execute", EEMessages.PERMISSIONS_COMMANDS_INVSEE_EXECUTE),
	INVSEE_MODIFY("commands.invsee.modify", EEMessages.PERMISSIONS_COMMANDS_INVSEE_MODIFY),
	
	INFO("commands.info.execute", EEMessages.PERMISSIONS_COMMANDS_INFO_EXECUTE, true),
	
	ITEM("commands.item.execute", EEMessages.PERMISSIONS_COMMANDS_ITEM_EXECUTE),
	ITEM_BYPASS("commands.item.bypass", EEMessages.PERMISSIONS_COMMANDS_ITEM_BYPASS),
	
	ITEM_NAME("commands.itemname.execute", EEMessages.PERMISSIONS_COMMANDS_ITEMNAME_EXECUTE),
	ITEM_LORE("commands.itemlore.execute", EEMessages.PERMISSIONS_COMMANDS_ITEMLORE_EXECUTE),
	
	JUMP("commands.jump.execute", EEMessages.PERMISSIONS_COMMANDS_JUMP_EXECUTE),
	
	KICK("commands.kick.execute", EEMessages.PERMISSIONS_COMMANDS_KICK_EXECUTE),
	KICK_BYPASS("commands.kick.bypass", EEMessages.PERMISSIONS_COMMANDS_KICK_BYPASS),
	
	KICKALL("commands.kickall.execute", EEMessages.PERMISSIONS_COMMANDS_KICKALL_EXECUTE),
	
	KILL("commands.kill.execute", EEMessages.PERMISSIONS_COMMANDS_KILL_EXECUTE),
	
	LAG("commands.lag.execute", EEMessages.PERMISSIONS_COMMANDS_LAG_EXECUTE),
	
	LIST("commands.list.execute", EEMessages.PERMISSIONS_COMMANDS_LIST_EXECUTE),
	
	REPAIR("commands.repair.execute", EEMessages.PERMISSIONS_COMMANDS_REPAIR_EXECUTE),
	REPAIR_HAND("commands.repair.hand", EEMessages.PERMISSIONS_COMMANDS_REPAIR_HAND),
	REPAIR_HOTBAR("commands.repair.hotbar", EEMessages.PERMISSIONS_COMMANDS_REPAIR_HOTBAR),
	REPAIR_ALL("commands.repair.all", EEMessages.PERMISSIONS_COMMANDS_REPAIR_ALL),
	
	REPLY("commands.reply.execute", EEMessages.PERMISSIONS_COMMANDS_REPLY_EXECUTE, true),
	
	MAIL("commands.mail.execute", EEMessages.PERMISSIONS_COMMANDS_MAIL_EXECUTE, true),
	MAIL_SEND("commands.mail.send", EEMessages.PERMISSIONS_COMMANDS_MAIL_SEND, true),
	MAIL_SENDALL("commands.mail.sendall", EEMessages.PERMISSIONS_COMMANDS_MAIL_SENDALL),
	
	ME("commands.me.execute", EEMessages.PERMISSIONS_COMMANDS_ME_EXECUTE),
	
	MSG("commands.msg.execute", EEMessages.PERMISSIONS_COMMANDS_MSG_EXECUTE, true),
	MSG_COLOR("commands.msg.color", EEMessages.PERMISSIONS_COMMANDS_MSG_COLOR),
	MSG_FORMAT("commands.msg.format", EEMessages.PERMISSIONS_COMMANDS_MSG_FORMAT),
	MSG_MAGIC("commands.msg.magic", EEMessages.PERMISSIONS_COMMANDS_MSG_MAGIC),
	MSG_CHARACTER("commands.msg.character", EEMessages.PERMISSIONS_COMMANDS_MSG_CHARACTER),
	MSG_COMMAND("commands.msg.command", EEMessages.PERMISSIONS_COMMANDS_MSG_COMMAND),
	MSG_URL("commands.msg.url", EEMessages.PERMISSIONS_COMMANDS_MSG_URL),
	MSG_ICONS("commands.msg.icons", EEMessages.PERMISSIONS_COMMANDS_MSG_ICONS),
	
	MOJANG("commands.mojang.execute", EEMessages.PERMISSIONS_COMMANDS_MOJANG_EXECUTE),
	
	MORE("commands.more.execute", EEMessages.PERMISSIONS_COMMANDS_MORE_EXECUTE),
	MORE_UNLIMITED("commands.more.unlimited", EEMessages.PERMISSIONS_COMMANDS_MORE_UNLIMITED),
	
	MOTD("commands.motd.execute", EEMessages.PERMISSIONS_COMMANDS_MOTD_EXECUTE),
	
	NAMES("commands.names.execute", EEMessages.PERMISSIONS_COMMANDS_NAMES_EXECUTE),
	NAMES_OTHERS("commands.names.others", EEMessages.PERMISSIONS_COMMANDS_NAMES_OTHERS),
	
	NEAR("commands.near.execute", EEMessages.PERMISSIONS_COMMANDS_NEAR_EXECUTE),
	NEARS("commands.nears", EEMessages.PERMISSIONS_COMMANDS_NEARS),
	
	PING("commands.ping.execute", EEMessages.PERMISSIONS_COMMANDS_PING_EXECUTE, true),
	PING_OTHERS("commands.ping.others", EEMessages.PERMISSIONS_COMMANDS_PING_OTHERS),
	
	PLAYED("commands.played.execute", EEMessages.PERMISSIONS_COMMANDS_PLAYED_EXECUTE),
	PLAYED_OTHERS("commands.played.others", EEMessages.PERMISSIONS_COMMANDS_PLAYED_OTHERS),
	
	RULES("commands.rules.execute", EEMessages.PERMISSIONS_COMMANDS_RULES_EXECUTE),
	
	SAY("commands.say.execute", EEMessages.PERMISSIONS_COMMANDS_SAY_EXECUTE),
	
	SEEN("commands.seen.execute", EEMessages.PERMISSIONS_COMMANDS_SEEN_EXECUTE),

	SKULL("commands.skull.execute", EEMessages.PERMISSIONS_COMMANDS_SKULL_EXECUTE),
	SKULL_OTHERS("commands.skull.others", EEMessages.PERMISSIONS_COMMANDS_SKULL_OTHERS),
	
	//Spawn
	SPAWN("commands.spawn.execute", EEMessages.PERMISSIONS_COMMANDS_SPAWN_EXECUTE, true),
	SPAWNS("commands.spawns.execute", EEMessages.PERMISSIONS_COMMANDS_SPAWNS_EXECUTE),
	SETSPAWN("commands.setspawn.execute", EEMessages.PERMISSIONS_COMMANDS_SETSPAWN_EXECUTE),
	DELSPAWN("commands.delspawn.execute", EEMessages.PERMISSIONS_COMMANDS_DELSPAWN_EXECUTE),
	
	SPAWNER("commands.spawner.execute", EEMessages.PERMISSIONS_COMMANDS_SPAWNER_EXECUTE),
	
	SPAWNMOB("commands.spawnmob.execute", EEMessages.PERMISSIONS_COMMANDS_SPAWNMOB_EXECUTE),
	
	SPEED("commands.speed.execute", EEMessages.PERMISSIONS_COMMANDS_SPEED_EXECUTE),
	SPEED_FLY("commands.speed.fly", EEMessages.PERMISSIONS_COMMANDS_SPEED_FLY),
	SPEED_WALK("commands.speed.walk", EEMessages.PERMISSIONS_COMMANDS_SPEED_WALK),
	SPEED_OTHERS("commands.speed.others", EEMessages.PERMISSIONS_COMMANDS_SPEED_OTHERS),
	
	STOP("commands.stop.execute", EEMessages.PERMISSIONS_COMMANDS_STOP_EXECUTE),
	
	SUDO("commands.sudo.execute", EEMessages.PERMISSIONS_COMMANDS_SUDO_EXECUTE),
	SUDO_CONSOLE("commands.sudo.console", EEMessages.PERMISSIONS_COMMANDS_SUDO_CONSOLE),
	SUDO_BYPASS("commands.sudo.bypass", EEMessages.PERMISSIONS_COMMANDS_SUDO_BYPASS),
	
	SUICIDE("commands.suicide.execute", EEMessages.PERMISSIONS_COMMANDS_SUICIDE_EXECUTE, true),
	
	TP("commands.tp.execute", EEMessages.PERMISSIONS_COMMANDS_TP_EXECUTE),
	TP_OTHERS("commands.tp.others", EEMessages.PERMISSIONS_COMMANDS_TP_OTHERS),
	
	TPACCEPT("commands.tpaccept.execute", EEMessages.PERMISSIONS_COMMANDS_TPACCEPT_EXECUTE),
	TPDENY("commands.tpadeny.execute", EEMessages.PERMISSIONS_COMMANDS_TPADENY_EXECUTE),
	
	TPA("commands.tpa.execute", EEMessages.PERMISSIONS_COMMANDS_TPA_EXECUTE),
	TPAHERE("commands.tpahere.execute", EEMessages.PERMISSIONS_COMMANDS_TPAHERE_EXECUTE),
	
	TPAALL("commands.tpaall.execute", EEMessages.PERMISSIONS_COMMANDS_TPAALL_EXECUTE),
	TPAALL_OTHERS("commands.tpaall.others", EEMessages.PERMISSIONS_COMMANDS_TPAALL_OTHERS),
	
	TPALL("commands.tpall.execute", EEMessages.PERMISSIONS_COMMANDS_TPALL_EXECUTE),
	TPALL_OTHERS("commands.tpall.others", EEMessages.PERMISSIONS_COMMANDS_TPALL_OTHERS),
	
	TPPOS("commands.tppos.execute", EEMessages.PERMISSIONS_COMMANDS_TPPOS_EXECUTE),
	TPPOS_OTHERS("commands.tppos.others", EEMessages.PERMISSIONS_COMMANDS_TPPOS_OTHERS),
	
	TIME("commands.time.execute", EEMessages.PERMISSIONS_COMMANDS_TIME_EXECUTE),
	
	TELEPORT_BYPASS_TIME("teleport.bypass.time", EEMessages.PERMISSIONS_TELEPORT_BYPASS_TIME),
	TELEPORT_BYPASS_MOVE("teleport.bypass.move", EEMessages.PERMISSIONS_TELEPORT_BYPASS_MOVE),
	
	TOGGLE("commands.toggle.execute", EEMessages.PERMISSIONS_COMMANDS_TOGGLE_EXECUTE),
	TOGGLE_OTHERS("commands.toggle.others", EEMessages.PERMISSIONS_COMMANDS_TOOGLE_OTHERS),
	
	TOP("commands.top.execute", EEMessages.PERMISSIONS_COMMANDS_TOP_EXECUTE),
	
	TPHERE("commands.tphere.execute", EEMessages.PERMISSIONS_COMMANDS_TPHERE_EXECUTE),
	
	TREE("commands.tree.execute", EEMessages.PERMISSIONS_COMMANDS_TREE_EXECUTE),
	
	UUID("commands.uuid.execute", EEMessages.PERMISSIONS_COMMANDS_UUID_EXECUTE, true),
	UUID_OTHERS("commands.uuid.others", EEMessages.PERMISSIONS_COMMANDS_UUID_OTHERS),
	
	VANISH("commands.vanish.execute", EEMessages.PERMISSIONS_COMMANDS_VANISH_EXECUTE),
	VANISH_OTHERS("commands.vanish.others", EEMessages.PERMISSIONS_COMMANDS_VANISH_OTHERS),
	VANISH_SEE("commands.vanish.see", EEMessages.PERMISSIONS_COMMANDS_VANISH_SEE),
	VANISH_PVP("commands.vanish.pvp", EEMessages.PERMISSIONS_COMMANDS_VANISH_PVP),
	VANISH_INTERACT("commands.vanish.interact", EEMessages.PERMISSIONS_COMMANDS_VANISH_INTERACT),
	
	WARP("commands.warp.execute", EEMessages.PERMISSIONS_COMMANDS_WARP_EXECUTE, true),
	WARP_NAME("commands.warps", EEMessages.PERMISSIONS_COMMANDS_WARPS),
	WARP_OTHERS("commands.warp.others", EEMessages.PERMISSIONS_COMMANDS_WARP_OTHERS),
	DELWARP("commands.delwarp.execute", EEMessages.PERMISSIONS_COMMANDS_DELWARP_EXECUTE),
	SETWARP("commands.setwarp.execute", EEMessages.PERMISSIONS_COMMANDS_SETWARP_EXECUTE),
	
	WEATHER("commands.weather.execute", EEMessages.PERMISSIONS_COMMANDS_WEATHER_EXECUTE),
	
	WHITELIST("commands.whitelist.execute", EEMessages.PERMISSIONS_COMMANDS_WHITELIST_EXECUTE),
	WHITELIST_MANAGE("commands.whitelist.manage", EEMessages.PERMISSIONS_COMMANDS_WHITELIST_MANAGE),
	
	WORLDBORDER("commands.worldborder.execute", EEMessages.PERMISSIONS_COMMANDS_WORLDBORDER_EXECUTE),
	
	WORLDS("commands.worlds.execute", EEMessages.PERMISSIONS_COMMANDS_WORLDS_EXECUTE),
	WORLDS_OTHERS("commands.worlds.others", EEMessages.PERMISSIONS_COMMANDS_WORLDS_OTHERS),
	
	WHOIS("commands.whois.execute", EEMessages.PERMISSIONS_COMMANDS_WHOIS_EXECUTE),
	WHOIS_OTHERS("commands.whois.others", EEMessages.PERMISSIONS_COMMANDS_WHOIS_OTHERS);
	
	private static final String PREFIX = "everessentials";
	
	private final String permission;
	private final EnumMessage message;
	private final boolean value;
    
    private EEPermissions(final String permission, final EnumMessage message) {
    	this(permission, message, false);
    }
    
    private EEPermissions(final String permission, final EnumMessage message, final boolean value) {   	    	
    	this.permission = PREFIX + "." + permission;
    	this.message = message;
    	this.value = value;
    }

    @Override
    public String get() {
    	return this.permission;
	}

	@Override
	public boolean getDefault() {
		return this.value;
	}

	@Override
	public EnumMessage getMessage() {
		return this.message;
	}
}
