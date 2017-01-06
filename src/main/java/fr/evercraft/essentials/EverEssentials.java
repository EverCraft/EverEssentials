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

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import fr.evercraft.essentials.listeners.EEPlayerListeners;
import fr.evercraft.essentials.managers.EEManagerCommands;
import fr.evercraft.essentials.managers.EEManagerEvent;
import fr.evercraft.essentials.managers.EEManagerServices;
import fr.evercraft.essentials.service.EScheduler;
import fr.evercraft.everapi.EverAPI;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.plugin.EPlugin;

@Plugin(id = "everessentials", 
		name = "EverEssentials", 
		version = EverAPI.VERSION, 
		description = "Commande de base",
		url = "http://evercraft.fr/",
		authors = {"rexbut","lesbleu"},
		dependencies = {
		    @Dependency(id = "everapi", version = EverAPI.VERSION),
		    @Dependency(id = "everchat", version = EverAPI.VERSION, optional = true),
		    @Dependency(id = "spongeapi", version = EverAPI.SPONGEAPI_VERSION)
		})
public class EverEssentials extends EPlugin<EverEssentials> {

	private EEDataBase databases;
	
	private EEConfig config;
	private EEMessage messages;
	
	private EEManagerServices managerServices;
	private EEManagerCommands managerCommands;
	private EEManagerEvent managerEvent;
	
	private EScheduler scheduler;
	
	private EEConfigMotd motd;
	private EEConfigRules rules;

	@Override
	protected void onPreEnable() throws PluginDisableException {
		// Configurations
		this.config = new EEConfig(this);
		this.databases = new EEDataBase(this);
		
		this.managerServices = new EEManagerServices(this);
		
		this.messages = new EEMessage(this, "messages");
		this.motd = new EEConfigMotd(this, "motd");
		this.rules = new EEConfigRules(this, "rules");
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
	protected void onReload() throws PluginDisableException {
		this.scheduler.stop();
		
		this.reloadConfigurations();
		
		
		this.databases.reload();
		
		this.managerServices.reload();
		this.managerCommands.reload();
		this.scheduler.reload();
		
		this.scheduler.start();
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
	
	public EEManagerServices getManagerServices() {
		return this.managerServices;
	}

	public EEManagerEvent getManagerEvent() {
		return this.managerEvent;
	}
}
