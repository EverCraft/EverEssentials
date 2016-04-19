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

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import fr.evercraft.essentials.listeners.EEPlayerListeners;
import fr.evercraft.essentials.managers.EEManagerCommands;
import fr.evercraft.essentials.managers.EEManagerServices;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.plugin.EPlugin;

@Plugin(id = "fr.evercraft.everessentials", 
		name = "EverEssentials", 
		version = "0.1", 
		description = "Commande de base",
		url = "http://evercraft.fr/",
		authors = {"rexbut","lesbleu"},
		dependencies = {
		    @Dependency(id = "fr.evercraft.everapi", version = "1.0"),
		    @Dependency(id = "fr.evercraft.everchat", optional = true)
		})
public class EverEssentials extends EPlugin {

	private EEDataBase databases;
	
	private EEConfig config;
	private EEMessage messages;
	private EEPermission permissions;
	
	private EEManagerServices managerServices;
	private EEManagerCommands managerCommands;
	
	private EEMotd motd;
	private EEConfigRules rules;

	@Override
	protected void onPreEnable() throws PluginDisableException {
		// Configurations
		this.config = new EEConfig(this);
		this.databases = new EEDataBase(this);
		
		if(!this.databases.isEnable()) {
			throw new PluginDisableException("This plugin requires a database");
		}
		
		this.managerServices = new EEManagerServices(this);
		
		this.messages = new EEMessage(this, "messages");
		this.permissions = new EEPermission(this);
		this.motd = new EEMotd(this, "motd");
		this.rules = new EEConfigRules(this, "rules");
	}
	
	@Override
	protected void onCompleteEnable() {		
		// Commandes
		new EECommand(this);
		this.managerCommands = new EEManagerCommands(this);
		
		// Listeners
		this.getGame().getEventManager().registerListeners(this, new EEPlayerListeners(this));
	}
	
	@Override
	protected void onReload() {
		this.reloadConfigurations();
		this.managerServices.reload();
		this.managerCommands.reload();
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
	
	public EEMotd getMotd(){
		return this.motd;
	}
	
	public EEPermission getPermissions(){
		return this.permissions;
	}

	public EEConfigRules getRules() {
		return this.rules;
	}

	public EEManagerCommands getManagerCommands() {
		return this.managerCommands;
	}
	
	public EEManagerServices getManagerServices() {
		return this.managerServices;
	}

	public EEDataBase getDataBases() {
		return this.databases;
	}
}
