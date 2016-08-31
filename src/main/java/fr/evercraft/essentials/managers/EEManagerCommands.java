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
package fr.evercraft.essentials.managers;

import java.util.HashSet;
import java.util.Optional;

import org.spongepowered.api.command.CommandMapping;

import fr.evercraft.essentials.EECommand;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.command.EEBack;
import fr.evercraft.essentials.command.EEBed;
import fr.evercraft.essentials.command.EEBook;
import fr.evercraft.essentials.command.EEBroadcast;
import fr.evercraft.essentials.command.EEClearEffect;
import fr.evercraft.essentials.command.EEClearInventory;
import fr.evercraft.essentials.command.EEColor;
import fr.evercraft.essentials.command.EEEffect;
import fr.evercraft.essentials.command.EEEnchant;
import fr.evercraft.essentials.command.EEExp;
import fr.evercraft.essentials.command.EEExt;
import fr.evercraft.essentials.command.EEFeed;
import fr.evercraft.essentials.command.EEGameMode;
import fr.evercraft.essentials.command.EEGenerate;
import fr.evercraft.essentials.command.EEGetPos;
import fr.evercraft.essentials.command.EEHat;
import fr.evercraft.essentials.command.EEHeal;
import fr.evercraft.essentials.command.EEHelp;
import fr.evercraft.essentials.command.EEInfo;
import fr.evercraft.essentials.command.EEItem;
import fr.evercraft.essentials.command.EEJump;
import fr.evercraft.essentials.command.EEKick;
import fr.evercraft.essentials.command.EEKickall;
import fr.evercraft.essentials.command.EEKill;
import fr.evercraft.essentials.command.EELag;
import fr.evercraft.essentials.command.EEList;
import fr.evercraft.essentials.command.EEMe;
import fr.evercraft.essentials.command.EEMojang;
import fr.evercraft.essentials.command.EEMore;
import fr.evercraft.essentials.command.EEMotd;
import fr.evercraft.essentials.command.EEName;
import fr.evercraft.essentials.command.EENear;
import fr.evercraft.essentials.command.EEPing;
import fr.evercraft.essentials.command.EEPlayed;
import fr.evercraft.essentials.command.EEReloadAll;
import fr.evercraft.essentials.command.EERules;
import fr.evercraft.essentials.command.EESay;
import fr.evercraft.essentials.command.EESeeInventory;
import fr.evercraft.essentials.command.EESeed;
import fr.evercraft.essentials.command.EESkull;
import fr.evercraft.essentials.command.EESpawnMob;
import fr.evercraft.essentials.command.EESpawner;
import fr.evercraft.essentials.command.EESpeed;
import fr.evercraft.essentials.command.EEStop;
import fr.evercraft.essentials.command.EESudo;
import fr.evercraft.essentials.command.EESuicide;
import fr.evercraft.essentials.command.EETop;
import fr.evercraft.essentials.command.EETree;
import fr.evercraft.essentials.command.EEUuid;
import fr.evercraft.essentials.command.EEWhois;
import fr.evercraft.essentials.command.afk.EEAfk;
import fr.evercraft.essentials.command.afk.EEAfkOff;
import fr.evercraft.essentials.command.afk.EEAfkOn;
import fr.evercraft.essentials.command.afk.EEAfkStatus;
import fr.evercraft.essentials.command.butcher.EEButcher;
import fr.evercraft.essentials.command.butcher.EEButcherAll;
import fr.evercraft.essentials.command.butcher.EEButcherAnimal;
import fr.evercraft.essentials.command.butcher.EEButcherMonster;
import fr.evercraft.essentials.command.butcher.EEButcherType;
import fr.evercraft.essentials.command.fly.EEFly;
import fr.evercraft.essentials.command.fly.EEFlyOff;
import fr.evercraft.essentials.command.fly.EEFlyOn;
import fr.evercraft.essentials.command.fly.EEFlyStatus;
import fr.evercraft.essentials.command.freeze.EEFreeze;
import fr.evercraft.essentials.command.freeze.EEFreezeOff;
import fr.evercraft.essentials.command.freeze.EEFreezeOn;
import fr.evercraft.essentials.command.freeze.EEFreezeStatus;
import fr.evercraft.essentials.command.god.EEGod;
import fr.evercraft.essentials.command.god.EEGodOff;
import fr.evercraft.essentials.command.god.EEGodOn;
import fr.evercraft.essentials.command.god.EEGodStatus;
import fr.evercraft.essentials.command.home.EEHome;
import fr.evercraft.essentials.command.home.EEHomeDel;
import fr.evercraft.essentials.command.home.EEHomeOthers;
import fr.evercraft.essentials.command.home.EEHomeSet;
import fr.evercraft.essentials.command.itemname.EEItemName;
import fr.evercraft.essentials.command.itemname.EEItemNameClear;
import fr.evercraft.essentials.command.itemname.EEItemNameSet;
import fr.evercraft.essentials.command.mail.EEMail;
import fr.evercraft.essentials.command.mail.EEMailClear;
import fr.evercraft.essentials.command.mail.EEMailDelete;
import fr.evercraft.essentials.command.mail.EEMailRead;
import fr.evercraft.essentials.command.mail.EEMailSend;
import fr.evercraft.essentials.command.message.EEMsg;
import fr.evercraft.essentials.command.message.EEReply;
import fr.evercraft.essentials.command.repair.EERepair;
import fr.evercraft.essentials.command.repair.EERepairAll;
import fr.evercraft.essentials.command.repair.EERepairHand;
import fr.evercraft.essentials.command.repair.EERepairHotBar;
import fr.evercraft.essentials.command.spawn.EESpawn;
import fr.evercraft.essentials.command.spawn.EESpawnDel;
import fr.evercraft.essentials.command.spawn.EESpawnSet;
import fr.evercraft.essentials.command.spawn.EESpawns;
import fr.evercraft.essentials.command.sub.EEReload;
import fr.evercraft.essentials.command.teleport.EETeleportation;
import fr.evercraft.essentials.command.teleport.EETeleportationAll;
import fr.evercraft.essentials.command.teleport.EETeleportationHere;
import fr.evercraft.essentials.command.teleport.EETeleportationPosition;
import fr.evercraft.essentials.command.teleport.request.EETeleportationAccept;
import fr.evercraft.essentials.command.teleport.request.EETeleportationAsk;
import fr.evercraft.essentials.command.teleport.request.EETeleportationAskAll;
import fr.evercraft.essentials.command.teleport.request.EETeleportationAskHere;
import fr.evercraft.essentials.command.teleport.request.EETeleportationDeny;
import fr.evercraft.essentials.command.time.EETime;
import fr.evercraft.essentials.command.time.EETimeDay;
import fr.evercraft.essentials.command.time.EETimeNight;
import fr.evercraft.essentials.command.toggle.EEToggle;
import fr.evercraft.essentials.command.toggle.EEToggleOff;
import fr.evercraft.essentials.command.toggle.EEToggleOn;
import fr.evercraft.essentials.command.toggle.EEToggleStatus;
import fr.evercraft.essentials.command.vanish.EEVanish;
import fr.evercraft.essentials.command.vanish.EEVanishOff;
import fr.evercraft.essentials.command.vanish.EEVanishOn;
import fr.evercraft.essentials.command.vanish.EEVanishStatus;
import fr.evercraft.essentials.command.warp.EEWarp;
import fr.evercraft.essentials.command.warp.EEWarpDel;
import fr.evercraft.essentials.command.warp.EEWarpSet;
import fr.evercraft.essentials.command.weather.EEWeather;
import fr.evercraft.essentials.command.weather.EEWeatherRain;
import fr.evercraft.essentials.command.weather.EEWeatherStorm;
import fr.evercraft.essentials.command.weather.EEWeatherSun;
import fr.evercraft.essentials.command.whitelist.EEWhitelist;
import fr.evercraft.essentials.command.whitelist.EEWhitelistAdd;
import fr.evercraft.essentials.command.whitelist.EEWhitelistList;
import fr.evercraft.essentials.command.whitelist.EEWhitelistOff;
import fr.evercraft.essentials.command.whitelist.EEWhitelistOn;
import fr.evercraft.essentials.command.whitelist.EEWhitelistRemove;
import fr.evercraft.essentials.command.whitelist.EEWhitelistStatus;
import fr.evercraft.essentials.command.world.EEWorlds;
import fr.evercraft.essentials.command.world.EEWorldsEnd;
import fr.evercraft.essentials.command.world.EEWorldsNether;
import fr.evercraft.essentials.command.worldborder.EEWorldborder;
import fr.evercraft.essentials.command.worldborder.EEWorldborderAdd;
import fr.evercraft.essentials.command.worldborder.EEWorldborderCenter;
import fr.evercraft.essentials.command.worldborder.EEWorldborderDamage;
import fr.evercraft.essentials.command.worldborder.EEWorldborderInfo;
import fr.evercraft.essentials.command.worldborder.EEWorldborderSet;
import fr.evercraft.essentials.command.worldborder.EEWorldborderWarning;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.plugin.command.EReloadCommand;

public class EEManagerCommands extends HashSet<ECommand<EverEssentials>> {
	
	private static final long serialVersionUID = -570936893274825176L;

	private final EverEssentials plugin;
	
	private final EECommand command;
	
	public EEManagerCommands(EverEssentials plugin){
		super();
		
		this.plugin = plugin;
		
		this.command = new EECommand(this.plugin);
		this.command.add(new EEReload(this.plugin, this.command));
		
		load();
	}
	
	public void load() {	
		register(new EEBack(this.plugin));
		register(new EEBed(this.plugin));
		register(new EEBook(this.plugin));
		register(new EEBroadcast(this.plugin));
		register(new EEClearInventory(this.plugin));
		register(new EEClearEffect(this.plugin));
		register(new EEColor(this.plugin));
		register(new EEEffect(this.plugin));
		register(new EEEnchant(this.plugin));
		register(new EEExp(this.plugin));
		register(new EEExt(this.plugin));
		register(new EEFeed(this.plugin));
		register(new EEGameMode(this.plugin));
		register(new EEGenerate(this.plugin));
		register(new EEGetPos(this.plugin));
		register(new EEHat(this.plugin));
		register(new EEHeal(this.plugin));
		register(new EEHome(this.plugin));
		register(new EEHomeDel(this.plugin));
		register(new EEHomeSet(this.plugin));
		register(new EEHomeOthers(this.plugin));
		register(new EEInfo(this.plugin));
		register(new EEItem(this.plugin));
		register(new EEJump(this.plugin));
		register(new EEKick(this.plugin));
		register(new EEKickall(this.plugin));
		register(new EEKill(this.plugin));
		register(new EELag(this.plugin));
		register(new EEList(this.plugin));
		register(new EEMe(this.plugin));
		register(new EEMojang(this.plugin));
		register(new EEMore(this.plugin));
		register(new EEMotd(this.plugin));
		register(new EEMsg(this.plugin));
		register(new EEName(this.plugin));
		register(new EENear(this.plugin));
		register(new EEReloadAll(this.plugin));
		register(new EERepair(this.plugin));
		register(new EERepairAll(this.plugin));
		register(new EERepairHand(this.plugin));
		register(new EERepairHotBar(this.plugin));
		register(new EEReply(this.plugin));
		register(new EERules(this.plugin));
		register(new EEPing(this.plugin));
		register(new EEPlayed(this.plugin));
		register(new EESay(this.plugin));
		register(new EESeed(this.plugin));
		register(new EESeeInventory(this.plugin));
		register(new EESkull(this.plugin));
		register(new EESpawn(this.plugin));
		register(new EESpawns(this.plugin));
		register(new EESpawnSet(this.plugin));
		register(new EESpawnDel(this.plugin));
		register(new EESpawner(this.plugin));
		register(new EESpawnMob(this.plugin));
		register(new EESpeed(this.plugin));
		register(new EEStop(this.plugin));
		register(new EESudo(this.plugin));
		register(new EESuicide(this.plugin));
		register(new EETeleportation(this.plugin));
		register(new EETeleportationAll(this.plugin));
		register(new EETeleportationHere(this.plugin));
		register(new EETeleportationPosition(this.plugin));
		register(new EETeleportationAsk(this.plugin));
		register(new EETeleportationAskAll(this.plugin));
		register(new EETeleportationAskHere(this.plugin));
		register(new EETeleportationAccept(this.plugin));
		register(new EETeleportationDeny(this.plugin));
		register(new EETime(this.plugin));
		register(new EETimeDay(this.plugin));
		register(new EETimeNight(this.plugin));
		register(new EETop(this.plugin));
		register(new EETree(this.plugin));
		register(new EEUuid(this.plugin));
		register(new EEWarp(this.plugin));
		register(new EEWarpDel(this.plugin));
		register(new EEWarpSet(this.plugin));
		register(new EEWeather(this.plugin));
		register(new EEWeatherRain(this.plugin));
		register(new EEWeatherStorm(this.plugin));
		register(new EEWeatherSun(this.plugin));
		register(new EEWhois(this.plugin));
		register(new EEWorlds(this.plugin));
		register(new EEWorldsEnd(this.plugin));
		register(new EEWorldsNether(this.plugin));
		
		// Commands	
		EEAfk afk = new EEAfk(this.plugin);
		afk.add(new EEAfkOn(this.plugin, afk));
		afk.add(new EEAfkOff(this.plugin, afk));
		afk.add(new EEAfkStatus(this.plugin, afk));
		register(afk);
		
		EEButcher butcher = new EEButcher(this.plugin);
		butcher.add(new EEButcherAll(this.plugin, butcher));
		butcher.add(new EEButcherAnimal(this.plugin, butcher));
		butcher.add(new EEButcherMonster(this.plugin, butcher));
		butcher.add(new EEButcherType(this.plugin, butcher));
		register(butcher);
		
		EEFly fly = new EEFly(this.plugin);
		fly.add(new EEFlyOn(this.plugin, fly));
		fly.add(new EEFlyOff(this.plugin, fly));
		fly.add(new EEFlyStatus(this.plugin, fly));
		register(fly);
		
		EEFreeze freeze = new EEFreeze(this.plugin);
		freeze.add(new EEFreezeOn(this.plugin, freeze));
		freeze.add(new EEFreezeOff(this.plugin, freeze));
		freeze.add(new EEFreezeStatus(this.plugin, freeze));
		register(freeze);
		
		/*
		EEGamerule gamerule = new EEGamerule(this.plugin);
		gamerule.add(new EEGameruleList(this.plugin, gamerule));
		register(gamerule);
		*/
		
		EEGod god = new EEGod(this.plugin);
		god.add(new EEGodOn(this.plugin, god));
		god.add(new EEGodOff(this.plugin, god));
		god.add(new EEGodStatus(this.plugin, god));
		register(god);
		
		EEItemName item = new EEItemName(this.plugin);
		item.add(new EEItemNameSet(this.plugin, item));
		item.add(new EEItemNameClear(this.plugin, item));
		
		EEMail mail = new EEMail(this.plugin);
		mail.add(new EEMailClear(this.plugin, mail));
		mail.add(new EEMailDelete(this.plugin, mail));
		mail.add(new EEMailRead(this.plugin, mail));
		mail.add(new EEMailSend(this.plugin, mail));
		
		EEToggle toggle = new EEToggle(this.plugin);
		toggle.add(new EEToggleOn(this.plugin, toggle));
		toggle.add(new EEToggleOff(this.plugin, toggle));
		toggle.add(new EEToggleStatus(this.plugin, toggle));
		register(toggle);
		
		EEVanish vanish = new EEVanish(this.plugin);
		vanish.add(new EEVanishOn(this.plugin, vanish));
		vanish.add(new EEVanishOff(this.plugin, vanish));
		vanish.add(new EEVanishStatus(this.plugin, vanish));
		register(vanish);
		
		EEWhitelist whitelist = new EEWhitelist(this.plugin);
		whitelist.add(new EEWhitelistOn(this.plugin, whitelist));
		whitelist.add(new EEWhitelistOff(this.plugin, whitelist));
		whitelist.add(new EEWhitelistStatus(this.plugin, whitelist));
		whitelist.add(new EEWhitelistAdd(this.plugin, whitelist));
		whitelist.add(new EEWhitelistRemove(this.plugin, whitelist));
		whitelist.add(new EEWhitelistList(this.plugin, whitelist));
		register(whitelist);
		
		EEWorldborder world = new EEWorldborder(this.plugin);
		world.add(new EEWorldborderInfo(this.plugin, world));
		world.add(new EEWorldborderSet(this.plugin, world));
		world.add(new EEWorldborderCenter(this.plugin, world));
		world.add(new EEWorldborderAdd(this.plugin, world));
		world.add(new EEWorldborderDamage(this.plugin, world));
		world.add(new EEWorldborderWarning(this.plugin, world));
		register(world);
		
		// Help
		Optional<? extends CommandMapping> help = this.plugin.getGame().getCommandManager().get("help");
        if (help.isPresent()) {
        	this.plugin.getGame().getCommandManager().removeMapping(help.get());
        }
        register(new EEHelp(this.plugin));
	}
	
	public void reload(){
		for (ECommand<EverEssentials> command : this) {
			if (command instanceof EReloadCommand) {
				((EReloadCommand<EverEssentials>) command).reload();
			}
		}
	}
	
	private void register(ECommand<EverEssentials> command) {
		this.command.add(command);
		this.add(command);
	}
}
