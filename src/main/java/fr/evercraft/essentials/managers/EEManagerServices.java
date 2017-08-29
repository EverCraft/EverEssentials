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

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.EEssentialsService;
import fr.evercraft.essentials.service.spawn.ESpawnService;
import fr.evercraft.essentials.service.warp.EWarpService;
import fr.evercraft.everapi.services.SpawnService;
import fr.evercraft.everapi.services.essentials.EssentialsService;
import fr.evercraft.everapi.services.essentials.WarpService;

public class EEManagerServices {
	private final EverEssentials plugin;
	
	private final EEssentialsService essentials;
	
	private final EWarpService warp;
	private final ESpawnService spawn;
	
	public EEManagerServices(EverEssentials plugin){
		this.plugin = plugin;
		
		this.essentials = new EEssentialsService(this.plugin);
		this.warp = new EWarpService(this.plugin);
		this.spawn = new ESpawnService(this.plugin);
		
		this.plugin.getGame().getServiceManager().setProvider(this.plugin, EssentialsService.class, this.essentials);
		this.plugin.getGame().getServiceManager().setProvider(this.plugin, WarpService.class, this.warp);
		this.plugin.getGame().getServiceManager().setProvider(this.plugin, SpawnService.class, this.spawn);
	}
	
	public void reload(){
		this.essentials.reload();
		this.warp.reload();
		this.spawn.reload();
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
