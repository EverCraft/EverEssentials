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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.SpawnService;
import fr.evercraft.everapi.text.ETextBuilder;

public class EESpawn extends ECommand<EverEssentials> {
	
	public EESpawn(final EverEssentials plugin) {
        super(plugin, "spawn");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPAWN.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SPAWN_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/spawn [" + EAMessages.ARGS_GROUP + "]").onClick(TextActions.suggestCommand("/spawn "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source instanceof Player){
			suggests.addAll(this.plugin.getManagerServices().getSpawn().getAll().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Groupe inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandSpawn((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Groupe connu
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				// Si il a la permission
				if(source.hasPermission(EEPermissions.SPAWN_OTHERS.get())) {
					// Spawn par défaut
					if(args.get(0).equalsIgnoreCase(SpawnService.DEFAULT)) {
						resultat = this.commandSpawn((EPlayer) source, this.plugin.getManagerServices().getSpawn().getDefault(), SpawnService.DEFAULT);
					// Pas le spawn par défaut
					} else {
						if(this.plugin.getEverAPI().getManagerService().getPermission().isPresent()) {
							Subject group = this.plugin.getEverAPI().getManagerService().getPermission().get().getGroupSubjects().get(args.get(0));
							// Groupe existant
							if(group != null) {
								if(this.plugin.getManagerServices().getSpawn().getAll().containsKey(group.getIdentifier())) {
									resultat = this.commandSpawn((EPlayer) source, this.plugin.getManagerServices().getSpawn().get(group), group.getIdentifier());
								} else {
									source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPAWN_ERROR_SET.get()));
								}
							// Groupe inexistant
							} else {
								source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPAWN_ERROR_GROUP.get()));
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
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandSpawn(final EPlayer player) throws CommandException {
		Transform<World> spawn = player.getSpawn();
		if(player.setTransform(spawn)) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.SPAWN_PLAYER.get())
					.replace("<spawn>", getButtonSpawn(spawn))
					.build());
			return true;
		} else {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.SPAWN_ERROR_TELEPORT.get())
					.replace("<spawn>", getButtonSpawn(spawn))
					.build());
		}
		return false;
	}
	
	private boolean commandSpawn(final EPlayer player, final Transform<World> spawn, final String name) throws CommandException {
		if(player.setTransform(spawn)) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.SPAWN_OTHERS.get()
							.replaceAll("<group>", name))
					.replace("<spawn>", getButtonSpawn(spawn))
					.build());
			return true;
		} else {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.SPAWN_ERROR_OTHERS_TELEPORT.get()
						.replaceAll("<group>",  name))
					.replace("<spawn>", getButtonSpawn(spawn))
					.build());
		}
		return false;
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
