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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.spawn.ESpawnService;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.VirtualTransform;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.SpawnSubjectService;

public class EESpawnDel extends ECommand<EverEssentials> {
	
	public EESpawnDel(final EverEssentials plugin) {
        super(plugin, "delspawn");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.DELSPAWN.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.DELSPAWN_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_GROUP.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source instanceof Player) {
			Set<String> spawns = new TreeSet<String>();
			
			SpawnSubjectService service = this.plugin.getSpawn();
			this.plugin.getSpawn().getAll().keySet().forEach(reference -> {
				Optional<Subject> subject = this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().getSubject(reference.getSubjectIdentifier());
				if (subject.isPresent()) {
					spawns.add(subject.get().getFriendlyIdentifier().orElse(subject.get().getIdentifier()));
				} else {
					spawns.add(reference.getSubjectIdentifier());
				}
			});
			if (service.getDefault().isPresent()) {
				spawns.add(SpawnSubjectService.DEFAULT);
			}
			if (service.getNewbie().isPresent()) {
				spawns.add(SpawnSubjectService.NEWBIE);
			}

			return spawns;
		} else if (args.size() == 2) {
			return Arrays.asList("confirmation");
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException, ServerDisableException {
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			return this.execute((EPlayer) source, args.get(0), false);
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			return this.execute((EPlayer) source, args.get(0), true);
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> execute(final CommandSource source, String argument, final boolean confirmation) {
		ESpawnService service = this.plugin.getSpawn();
		
		if (argument.equalsIgnoreCase(SpawnSubjectService.NEWBIE)) {
			if (!service.getNewbie().isPresent()) {
				EEMessages.DELSPAWN_INCONNU.sender()
					.replace("{name}", SpawnSubjectService.NEWBIE)
					.sendTo(source);
				return CompletableFuture.completedFuture(false);
			}
			
			return this.commandDeleteSpawn((EPlayer) source, SpawnSubjectService.NEWBIE, SpawnSubjectService.NEWBIE, service.getNewbie().get(), confirmation);
		} else if (argument.equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
			if (!service.getDefault().isPresent()) {
				EEMessages.DELSPAWN_INCONNU.sender()
					.replace("{name}", SpawnSubjectService.DEFAULT)
					.sendTo(source);
				return CompletableFuture.completedFuture(false);
			}
			
			return this.commandDeleteSpawn((EPlayer) source, SpawnSubjectService.DEFAULT, SpawnSubjectService.DEFAULT, service.getDefault().get(), confirmation);
		} else {
			return this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().hasSubject(argument)
				.exceptionally(e -> null)
				.thenCompose(result -> {
					if (result == null) {
						EAMessages.COMMAND_ERROR.sender()
							.prefix(EEMessages.PREFIX)
							.sendTo(source);
						return CompletableFuture.completedFuture(false);
					}
					
					String identifier = argument;
					String name = argument;
					if (result) {
						Subject subject = this.plugin.getEverAPI().getManagerService().getPermission().getGroupSubjects().loadSubject(argument).join();
						identifier = subject.getIdentifier();
						name = subject.getFriendlyIdentifier().orElse(identifier);
					}
					
					Optional<VirtualTransform> virtual = service.get(identifier);
					if (!virtual.isPresent()) {
						EEMessages.DELSPAWN_INCONNU.sender()
							.replace("{name}", name)
							.sendTo(source);
						return CompletableFuture.completedFuture(false);
					}
					
					return this.commandDeleteSpawn((EPlayer) source, identifier, name, virtual.get(), confirmation);
				});
		}
	}
	
	private CompletableFuture<Boolean> commandDeleteSpawn(final EPlayer player, final String identifier, final String name, final VirtualTransform virtual, final boolean confirmation) {
		if (!confirmation) {
			EEMessages.DELSPAWN_CONFIRMATION.sender()
				.replace("{spawn}", () -> this.getButtonSpawn(name, virtual))
				.replace("{confirmation}", () -> this.getButtonConfirmation(name))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		ESpawnService service = this.plugin.getSpawn();
		CompletableFuture<Boolean> value;
		
		if (identifier.equalsIgnoreCase(SpawnSubjectService.NEWBIE)) {
			value = service.setNewbie(null);
		} else if (identifier.equalsIgnoreCase(SpawnSubjectService.DEFAULT)) {
			value = service.setDefault(null);
		} else {
			value = service.set(identifier, null);
		}
		
		return value.exceptionally(e -> false)
			.thenApply(result -> {
				if (!result) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(player);
					return false;
				}
				
				EEMessages.DELSPAWN_DELETE.sender()
					.replace("{spawn}", this.getButtonSpawn(name, virtual))
					.sendTo(player);
				return true;
			});
	}
	
	private Text getButtonSpawn(final String name, final VirtualTransform location) {
		return EEMessages.DELSPAWN_NAME.getFormat().toText("{name}", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.DELSPAWN_NAME_HOVER.getFormat().toText(
							"{name}", name,
							"{world}", location.getWorldName().orElse(location.getWorldIdentifier()),
							"{x}", String.valueOf(location.getPosition().getFloorX()),
							"{y}", String.valueOf(location.getPosition().getFloorY()),
							"{z}", String.valueOf(location.getPosition().getFloorZ()))))
					.build();
	}
	
	private Text getButtonConfirmation(final String name){
		return EEMessages.DELSPAWN_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.DELSPAWN_CONFIRMATION_VALID_HOVER.getFormat()
							.toText("{name}", name)))
					.onClick(TextActions.runCommand("/delspawn \"" + name + "\" confirmation"))
					.build();
	}
}
