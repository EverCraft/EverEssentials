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

import java.util.TreeMap;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.command.*;
import fr.evercraft.essentials.command.home.*;
import fr.evercraft.essentials.command.repair.*;
import fr.evercraft.essentials.command.spawn.*;
import fr.evercraft.essentials.command.teleport.*;
import fr.evercraft.essentials.command.time.*;
import fr.evercraft.essentials.command.warp.*;
import fr.evercraft.essentials.command.weather.*;
import fr.evercraft.essentials.command.world.*;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.plugin.command.EReloadCommand;

public class EEManagerCommands extends TreeMap<String, ECommand<EverEssentials>>{
	
	private static final long serialVersionUID = -570936893274825176L;

	private EverEssentials plugin;
	
	private EEHomeSet homeSet;
	private EENear near;
	private EEWarp warp;
	
	public EEManagerCommands(EverEssentials plugin){
		super();
		
		this.plugin = plugin;
		load();
	}
	
	public void load() {
		this.homeSet = new EEHomeSet(this.plugin);
		this.near = new EENear(this.plugin);
		this.warp = new EEWarp(this.plugin);
		
		register(this.homeSet);
		register(this.near);
		register(this.warp);
		
		register(new EEAfk(this.plugin));
		register(new EEBack(this.plugin));
		register(new EEBed(this.plugin));
		register(new EEBook(this.plugin));
		register(new EEBroadcast(this.plugin));
		register(new EEButcher(this.plugin));
		register(new EEClearInventory(this.plugin));
		register(new EEClearEffect(this.plugin));
		register(new EEColor(this.plugin));
		register(new EEEffect(this.plugin));
		register(new EEEnchant(this.plugin));
		register(new EEExp(this.plugin));
		register(new EEExt(this.plugin));
		register(new EEFeed(this.plugin));
		register(new EEFly(this.plugin));
		register(new EEGameMode(this.plugin));
		register(new EEGetPos(this.plugin));
		register(new EEGod(this.plugin));
		register(new EEHat(this.plugin));
		register(new EEHeal(this.plugin));
		register(new EEHome(this.plugin));
		register(new EEHomeDel(this.plugin));
		register(new EEHomeSet(this.plugin));
		register(new EEHomeOthers(this.plugin));
		register(new EEInfo(this.plugin));
		register(new EEJump(this.plugin));
		register(new EEKick(this.plugin));
		register(new EEKickall(this.plugin));
		register(new EEKill(this.plugin));
		register(new EELag(this.plugin));
		register(new EEList(this.plugin));
		register(new EEMail(this.plugin));
		register(new EEMe(this.plugin));
		register(new EEMojang(this.plugin));
		register(new EEMore(this.plugin));
		register(new EEMotd(this.plugin));
		register(new EENames(this.plugin));
		register(new EENear(this.plugin));
		register(new EEReload(this.plugin));
		register(new EERepair(this.plugin));
		register(new EERepairAll(this.plugin));
		register(new EERepairHand(this.plugin));
		register(new EERepairHotBar(this.plugin));
		register(new EERules(this.plugin));
		register(new EEPing(this.plugin));
		register(new EESeeInventory(this.plugin));
		register(new EESkull(this.plugin));
		register(new EESpawn(this.plugin));
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
	}
	
	public void reload(){
		for(ECommand<EverEssentials> command : this.values()) {
			if(command instanceof EReloadCommand) {
				((EReloadCommand<EverEssentials>) command).reload();
			}
		}
	}
	
	private void register(ECommand<EverEssentials> command) {
		this.put(command.getName(), command);
	}
}
