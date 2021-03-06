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
package fr.evercraft.everessentials.command.home;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsMap;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.EssentialsService;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEHomeSet extends ECommand<EverEssentials> {
	
	private List<Entry<String, Integer>> permissions;
	private int permission_default;
	
	public EEHomeSet(final EverEssentials plugin) {
        super(plugin, "sethome", "setresidence");
        reload();
    }
	
	@Override
	public void reload() {
		super.reload();
		
		Map<String, Integer> permissions = new HashMap<String, Integer>();
		this.permission_default = this.plugin.getConfigs().get("sethome-multiple.default").getInt(1);
		for (Entry<Object, ? extends ConfigurationNode> key : this.plugin.getConfigs().get("sethome-multiple").getChildrenMap().entrySet()) {
			if (key.getKey() instanceof String) {
				permissions.put((String) key.getKey(), key.getValue().getInt(this.permission_default));
			}
		}
		this.permissions = UtilsMap.valueDESC(permissions);
	}
	
	public int getMaxHome(final EPlayer player) {
		if (player.hasPermission(EEPermissions.SETHOME_MULTIPLE.get())) {
			return Integer.MAX_VALUE;
		} else {
			int max = this.permission_default;
			int cpt = 0;
			while (cpt < this.permissions.size() && max == this.permission_default) {
				if (player.hasPermission(EEPermissions.SETHOME_MULTIPLE.get() + "." + this.permissions.get(cpt).getKey())) {
					max = this.permissions.get(cpt).getValue();
				}
				cpt++;
			}
			return max;
		}
	}
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SETHOME.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SETHOME_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.SETHOME_MULTIPLE.get())) {
			return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_HOME.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.SETHOME_MULTIPLE.get())) {
			return Arrays.asList("home...");
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandSetHome((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.SETHOME_MULTIPLE.get())) {
				// Si la source est un joueur
				if (source instanceof EPlayer) {
					return this.commandSetHome((EPlayer) source, args.get(0)); 
				// La source n'est pas un joueur
				} else {
					EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSetHome(final EPlayer player) {
		int max = this.getMaxHome(player);
		int homes = player.getHomes().size();
		
		boolean hasHome = player.hasHome(EssentialsService.DEFAULT_HOME);
		
		// Il a déjà le nombre maximum d'home
		if (!hasHome && homes != 0 && homes >= max) {
			EEMessages.SETHOME_MULTIPLE_ERROR_MAX.sender()
				.replace("{nombre}", String.valueOf(getMaxHome(player)))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// Ajout d'un home
		if (!hasHome) {
			return this.commandSetHomeAdd(player);
		// Modifie un home
		} else {
			return this.commandSetHomeMove(player);
		}
	}
	
	private CompletableFuture<Boolean> commandSetHomeAdd(final EPlayer player) {
		if (!player.addHome(EssentialsService.DEFAULT_HOME)) {
			EEMessages.SETHOME_SET_CANCEL.sender()
				.replace("{home}", this.getButtonHome(EssentialsService.DEFAULT_HOME, player.getLocation()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		EEMessages.SETHOME_SET.sender()
			.replace("{home}", this.getButtonHome(EssentialsService.DEFAULT_HOME, player.getLocation()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandSetHomeMove(final EPlayer player) {
		if (!player.moveHome(EssentialsService.DEFAULT_HOME)) {
			EEMessages.SETHOME_MOVE_CANCEL.sender()
				.replace("{home}", this.getButtonHome(EssentialsService.DEFAULT_HOME, player.getLocation()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		EEMessages.SETHOME_MOVE.sender()
			.replace("{home}", this.getButtonHome(EssentialsService.DEFAULT_HOME, player.getLocation()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandSetHome(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		// Il n'a pas la permission multihome
		if (!player.hasPermission(EEPermissions.SETHOME_MULTIPLE.get())) {
			EEMessages.SETHOME_MULTIPLE_NO_PERMISSION.sender()
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		int max = this.getMaxHome(player);
		int homes = player.getHomes().size();
		boolean hasHome = player.hasHome(name);
		
		// Il a déjà le nombre maximum d'home
		if (!hasHome && homes != 0 && homes >= max) {
			EEMessages.SETHOME_MULTIPLE_ERROR_MAX.sender()
				.replace("{nombre}", String.valueOf(max))
				.sendTo(player);
		}
		
		// Ajout d'un home
		if (!hasHome) {
			return this.commandSetHomeAdd(player, name);
		// Modifie un home
		} else {
			return this.commandSetHomeMove(player, name);
		}
	}
	
	private CompletableFuture<Boolean> commandSetHomeAdd(final EPlayer player, final String name) {
		if (!player.addHome(name)) {
			EEMessages.SETHOME_MULTIPLE_SET_CANCEL.sender()
				.replace("{home}", this.getButtonHome(name, player.getLocation()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.SETHOME_MULTIPLE_SET.sender()
			.replace("{home}", this.getButtonHome(name, player.getLocation()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandSetHomeMove(final EPlayer player, final String name) {
		if (!player.moveHome(name)) {
			EEMessages.SETHOME_MULTIPLE_MOVE_CANCEL.sender()
				.replace("{home}", this.getButtonHome(name, player.getLocation()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.SETHOME_MULTIPLE_MOVE.sender()
			.replace("{home}", this.getButtonHome(name, player.getLocation()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}

	private Text getButtonHome(final String name, final Location<World> location){
		return EEMessages.HOME_NAME.getFormat().toText("{name}", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.HOME_NAME_HOVER.getFormat().toText(
								"{home}", name,
								"{world}", location.getExtent().getName(),
								"{x}", String.valueOf(location.getBlockX()),
								"{y}", String.valueOf(location.getBlockY()),
								"{z}", String.valueOf(location.getBlockZ()))))
					.build();
	}
}
