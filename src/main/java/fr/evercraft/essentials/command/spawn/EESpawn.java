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
package fr.evercraft.essentials.command.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.SpawnService;
import fr.evercraft.everapi.text.ETextBuilder;

public class EESpawn extends EReloadCommand<EverEssentials> {
	
	public String newbies;
	
	public EESpawn(final EverEssentials plugin) {
        super(plugin, "spawn");
        
        reload();
    }
	
	@Override
	public void reload() {
		this.newbies = this.plugin.getConfigs().getSpawnNewbies();
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPAWN.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SPAWN_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.SPAWNS.get())) {
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_GROUP + "]")
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
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source instanceof Player && source.hasPermission(EEPermissions.SPAWNS.get())) {
			Set<String> homes = new TreeSet<String>();
			
			homes.addAll(this.plugin.getManagerServices().getSpawn().getAll().keySet());
			homes.add(this.newbies);
			homes.add(SpawnService.DEFAULT);

			suggests.addAll(homes);
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Groupe inconnu
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandSpawn((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
			
		// Groupe connu
		} else if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				// Si il a la permission
				if (source.hasPermission(EEPermissions.SPAWNS.get())) {
					
					// Spawn par défaut
					if (args.get(0).equalsIgnoreCase(SpawnService.DEFAULT)) {
						resultat = this.commandSpawn((EPlayer) source, this.plugin.getManagerServices().getSpawn().getDefault(), SpawnService.DEFAULT);
					// Spawn Newbie
					} else if (args.get(0).equalsIgnoreCase(this.newbies)) {
						resultat = this.commandSpawn((EPlayer) source, this.newbies);
					// Pas le spawn par défaut
					} else {
						if (this.plugin.getEverAPI().getManagerService().getPermission().isPresent()) {
							Subject group = this.plugin.getEverAPI().getManagerService().getPermission().get().getGroupSubjects().get(args.get(0));
							// Groupe existant
							if (group != null) {
								resultat = this.commandSpawn((EPlayer) source, group.getIdentifier());
							// Groupe inexistant
							} else {
								source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPAWN_ERROR_GROUP.get()
										.replaceAll("<name>", args.get(0))));
							}
						} else {
							source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPAWN_ERROR_GROUP.get()));
						}
					}
					
				// Il n'a pas la permission
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandSpawn(final EPlayer player) throws CommandException {
		final Transform<World> spawn = player.getSpawn();
		long delay = this.plugin.getConfigs().getTeleportDelay(player);
		
		if (delay > 0) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPAWN_DELAY.get()
					.replaceAll("<delay>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay)));
		}
		
		player.setTeleport(delay, () -> this.teleport(player, spawn), player.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		return true;
	}
	
	private boolean commandSpawn(final EPlayer player, final String group) throws CommandException {
		Optional<Transform<World>> spawn = this.plugin.getManagerServices().getSpawn().get(group);
		
		if (spawn.isPresent()) {
			return this.commandSpawn(player, spawn.get(), group);
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPAWN_ERROR_SET.get()
					.replaceAll("<name>", group));
		}
		
		return false;
	}
	
	private boolean commandSpawn(final EPlayer player, final Transform<World> spawn, final String name) throws CommandException {
		long delay = this.plugin.getConfigs().getTeleportDelay(player);
		
		if (delay > 0) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPAWNS_DELAY.get()
					.replaceAll("<delay>", this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay)));
		}
		
		player.setTeleport(delay, () -> this.teleport(player, spawn, name), player.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		return false;
	}
	
	private void teleport(final EPlayer player, final Transform<World> location) {
		if (player.isOnline()) {
			if (player.teleport(location, true)) {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.SPAWN_PLAYER.get())
						.replace("<spawn>", this.getButtonSpawn(location))
						.build());

			} else {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.SPAWN_ERROR_TELEPORT.get())
						.replace("<spawn>", this.getButtonSpawn(location))
						.build());
			}
		}
	}
	
	private void teleport(final EPlayer player, final Transform<World> location, final String name) {
		if (player.isOnline()) {
			if (player.teleport(location, true)) {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.SPAWNS_PLAYER.get()
								.replaceAll("<name>", name))
						.replace("<spawn>", this.getButtonSpawn(location))
						.build());
			} else {
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.SPAWNS_ERROR_TELEPORT.get()
							.replaceAll("<name>",  name))
						.replace("<spawn>", this.getButtonSpawn(location))
						.build());
			}
		}
	}
	
	private Text getButtonSpawn(final Transform<World> location){
		return EChat.of(EEMessages.SPAWN_NAME.get()).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.SPAWN_NAME_HOVER.get()
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getLocation().getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getLocation().getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getLocation().getBlockZ())))))
					.build();
	}
}
