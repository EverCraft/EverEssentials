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
import fr.evercraft.essentials.command.*;
import fr.evercraft.essentials.command.afk.*;
import fr.evercraft.essentials.command.butcher.EEButcher;
import fr.evercraft.essentials.command.butcher.EEButcherAll;
import fr.evercraft.essentials.command.butcher.EEButcherAnimal;
import fr.evercraft.essentials.command.butcher.EEButcherMonster;
import fr.evercraft.essentials.command.butcher.EEButcherType;
import fr.evercraft.essentials.command.fly.*;
import fr.evercraft.essentials.command.freeze.*;
import fr.evercraft.essentials.command.gamerule.EEGamerule;
import fr.evercraft.essentials.command.gamerule.EEGameruleList;
import fr.evercraft.essentials.command.god.*;
import fr.evercraft.essentials.command.home.*;
import fr.evercraft.essentials.command.mail.*;
import fr.evercraft.essentials.command.message.*;
import fr.evercraft.essentials.command.repair.*;
import fr.evercraft.essentials.command.spawn.*;
import fr.evercraft.essentials.command.sub.*;
import fr.evercraft.essentials.command.teleport.*;
import fr.evercraft.essentials.command.teleport.request.*;
import fr.evercraft.essentials.command.time.*;
import fr.evercraft.essentials.command.toggle.*;
import fr.evercraft.essentials.command.warp.*;
import fr.evercraft.essentials.command.weather.*;
import fr.evercraft.essentials.command.whitelist.*;
import fr.evercraft.essentials.command.world.*;
import fr.evercraft.essentials.command.worldborder.*;
import fr.evercraft.everapi.plugin.command.*;

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
		register(new EEVanish(this.plugin));
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
		
		EEGamerule gamerule = new EEGamerule(this.plugin);
		gamerule.add(new EEGameruleList(this.plugin, gamerule));
		register(gamerule);
		
		EEGod god = new EEGod(this.plugin);
		god.add(new EEGodOn(this.plugin, god));
		god.add(new EEGodOff(this.plugin, god));
		god.add(new EEGodStatus(this.plugin, god));
		register(god);
		
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
