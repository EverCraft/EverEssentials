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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.VirtualTransform;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.SpawnSubjectService;

public class EESpawn extends ECommand<EverEssentials> {
		
	public EESpawn(final EverEssentials plugin) {
        super(plugin, "spawn");
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
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_GROUP.getString() + "]")
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
		if (args.size() == 1 && source instanceof Player && source.hasPermission(EEPermissions.SPAWNS.get())) {
			Set<String> spawns = new TreeSet<String>();
			
			this.plugin.getSpawn().getAll().keySet().forEach(reference -> {
				Optional<Subject> subject = this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().getSubject(reference.getSubjectIdentifier());
				if (subject.isPresent()) {
					spawns.add(subject.get().getFriendlyIdentifier().orElse(subject.get().getIdentifier()));
				} else {
					spawns.add(reference.getSubjectIdentifier());
				}
			});
			spawns.add(SpawnSubjectService.NEWBIE);
			spawns.add(SpawnSubjectService.DEFAULT);

			return spawns;
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Groupe inconnu
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandSpawn((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Groupe connu
		} else if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				// Si il a la permission
				if (source.hasPermission(EEPermissions.SPAWNS.get())) {
					
					// Default
					if (args.get(0).equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
						return CompletableFuture.completedFuture(this.commandSpawn((EPlayer) source, this.plugin.getSpawn().getDefault(), SpawnSubjectService.DEFAULT));
					// Newbie
					} else if (args.get(0).equalsIgnoreCase(SpawnSubjectService.NEWBIE)) {
						return CompletableFuture.completedFuture(this.commandSpawn((EPlayer) source, this.plugin.getSpawn().getNewbie(), SpawnSubjectService.NEWBIE));
					// Subject
					} else {
						return this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().hasSubject(args.get(0))
							.exceptionally(e -> null)
							.thenApplyAsync(result -> {
								if (result == null) {
									EAMessages.COMMAND_ERROR.sender()
										.prefix(EEMessages.PREFIX)
										.sendTo(source);
									return false;
								}
								
								if (!result) {
									EEMessages.SPAWN_ERROR_GROUP.sender()
										.replace("{name}", args.get(0))
										.sendTo(source);
									return false;
								}
								
								return this.commandSpawn((EPlayer) source, this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().loadSubject(args.get(0)).join());
							});
					}
					
				// Il n'a pas la permission
				} else {
					EAMessages.NO_PERMISSION.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSpawn(final EPlayer player) {
		final Transform<World> spawn = player.getSpawn();
		long delay = this.plugin.getConfigs().getTeleportDelay(player);
		
		if (delay > 0) {
			EEMessages.SPAWN_DELAY.sender()
				.replace("{delay}", () -> this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay))
				.sendTo(player);
		}
		
		player.setTeleport(delay, () -> this.teleport(player, spawn), player.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		return CompletableFuture.completedFuture(true);
	}
	
	private boolean commandSpawn(final EPlayer player, final Subject subject) {
		return this.commandSpawn(player, this.plugin.getSpawn().get(subject.asSubjectReference()), subject.getFriendlyIdentifier().orElse(subject.getIdentifier()));
	}
	
	private boolean commandSpawn(final EPlayer player, final Optional<VirtualTransform> virtual, final String name) {
		if (!virtual.isPresent()) {
			EEMessages.SPAWN_ERROR_SET.sender()
				.replace("{name}", name)
				.sendTo(player);
			return false;
		}
		
		Optional<Transform<World>> transform = virtual.get().getTransform();
		if (!transform.isPresent()) {
			EEMessages.SPAWN_ERROR_SET.sender()
				.replace("{name}", name)
				.sendTo(player);
			return false;
		}
		
		long delay = this.plugin.getConfigs().getTeleportDelay(player);
		
		if (delay > 0) {
			EEMessages.SPAWNS_DELAY.sender()
				.replace("{delay}", () -> this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay))
				.sendTo(player);
		}
		
		player.setTeleport(delay, () -> this.teleport(player, transform.get(), name), player.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		return false;
	}
	
	private void teleport(final EPlayer player, final Transform<World> location) {
		if (player.isOnline()) {
			if (player.teleport(location, true)) {
				EEMessages.SPAWN_PLAYER.sender()
					.replace("{spawn}", this.getButtonSpawn(location))
					.sendTo(player);

			} else {
				EEMessages.SPAWN_ERROR_TELEPORT.sender()
					.replace("{spawn}", this.getButtonSpawn(location))
					.sendTo(player);
			}
		}
	}
	
	private void teleport(final EPlayer player, final Transform<World> location, final String name) {
		if (player.isOnline()) {
			if (player.teleport(location, true)) {
				EEMessages.SPAWNS_PLAYER.sender()
					.replace("{name}", name)
					.replace("{spawn}", () -> this.getButtonSpawn(location))
					.sendTo(player);
			} else {
				EEMessages.SPAWNS_ERROR_TELEPORT.sender()
					.replace("{name}",  name)
					.replace("{spawn}", () -> this.getButtonSpawn(location))
					.sendTo(player);
			}
		}
	}
	
	private Text getButtonSpawn(final Transform<World> location){
		return EEMessages.SPAWN_NAME.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.SPAWN_NAME_HOVER.getFormat().toText(
							"{world}", location.getExtent().getName(),
							"{x}", String.valueOf(location.getLocation().getBlockX()),
							"{y}", String.valueOf(location.getLocation().getBlockY()),
							"{z}", String.valueOf(location.getLocation().getBlockZ()))))
					.build();
	}
}
