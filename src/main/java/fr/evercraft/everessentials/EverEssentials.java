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

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EverAPI;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.services.essentials.EssentialsService;
import fr.evercraft.everessentials.listeners.EEPlayerListeners;
import fr.evercraft.everessentials.managers.EEManagerCommands;
import fr.evercraft.everessentials.managers.EEManagerEvent;
import fr.evercraft.everessentials.service.EEssentialsService;
import fr.evercraft.everessentials.service.EScheduler;
import fr.evercraft.everessentials.service.spawn.ESpawnService;
import fr.evercraft.everessentials.service.warp.EWarpService;

@Plugin(id = "everessentials", 
		name = "EverEssentials", 
		version = EverAPI.VERSION, 
		description = "Commande de base",
		url = "http://evercraft.fr/",
		authors = {"rexbut","lesbleu"},
		dependencies = {
		    @Dependency(id = "everapi", version = EverAPI.VERSION),
		    @Dependency(id = "spongeapi", version = EverAPI.SPONGEAPI_VERSION)
		})
public class EverEssentials extends EPlugin<EverEssentials> {

	private EEDataBase databases;
	
	private EEConfig config;
	private EEMessage messages;
	
	private EEManagerCommands managerCommands;
	private EEManagerEvent managerEvent;
	
	private EEssentialsService essentials;
	private EWarpService warp;
	private ESpawnService spawn;
	
	private EScheduler scheduler;
	
	private EEConfigMotd motd;
	private EEConfigRules rules;

	@Override
	protected void onPreEnable() throws PluginDisableException {
		// Configurations
		this.config = new EEConfig(this);
		this.databases = new EEDataBase(this);
	}
	
	@Override
	protected void onEnable() throws PluginDisableException {
		this.essentials = new EEssentialsService(this);
		this.warp = new EWarpService(this); // After EverAPI
		this.spawn = new ESpawnService(this);
		
		this.messages = new EEMessage(this, "messages");
		this.motd = new EEConfigMotd(this, "motd");
		this.rules = new EEConfigRules(this, "rules");
		
		this.register();
	}
	
	@Override
	protected void onCompleteEnable() {
		this.managerCommands = new EEManagerCommands(this);
		this.managerEvent = new EEManagerEvent(this);
		
		// Listeners
		this.getGame().getEventManager().registerListeners(this, new EEPlayerListeners(this));
		
		this.scheduler = new EScheduler(this);
	}
	
	@Override
	protected void onReload() throws PluginDisableException, ServerDisableException {
		this.scheduler.stop();
		
		super.onReload();
		this.databases.reload();
		
		this.essentials.reload();
		this.warp.reload();
		this.spawn.reload();
		
		this.scheduler.reload();
		
		this.scheduler.start();
	}
	
	public void register() {
		this.getEverAPI().getManagerService().getSpawn().register(EssentialsService.Priorities.HOME, user -> {
			Map<String, Transform<World>> homes = user.getHomes();
			if (homes.isEmpty()) return Optional.empty();
			
			Transform<World> home = homes.get(EssentialsService.DEFAULT_HOME);
			if (home == null) return Optional.of(home);
			
			homes = new TreeMap<String, Transform<World>>(homes);
			return Optional.of(homes.values().iterator().next());
		});
	}
	
	@Override
	protected void onDisable() {

	}
	
	public EEConfig getConfigs(){
		return this.config;
	}
	
	public EEMessage getMessages(){
		return this.messages;
	}
	
	public EEConfigMotd getMotd(){
		return this.motd;
	}

	public EEConfigRules getRules() {
		return this.rules;
	}

	public EEDataBase getDataBases() {
		return this.databases;
	}

	public EScheduler getScheduler() {
		return this.scheduler;
	}
	
	public EEManagerCommands getManagerCommands() {
		return this.managerCommands;
	}
	
	public EEManagerEvent getManagerEvent() {
		return this.managerEvent;
	}
	
	public EWarpService getWarp() {
		return this.warp;
	}

	public ESpawnService getSpawn() {
		return this.spawn;
	}

	public EEssentialsService getEssentials() {
		return this.essentials;
	}
}
