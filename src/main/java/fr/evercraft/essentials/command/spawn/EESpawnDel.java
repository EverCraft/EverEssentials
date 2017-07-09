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
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

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
		if (args.size() == 1 && source instanceof Player){
			return this.plugin.getManagerServices().getSpawn().getAll().keySet();
		} else if (args.size() == 2){
			return Arrays.asList("confirmation");
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException, ServerDisableException {
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			return this.commandDeleteSpawn((EPlayer) source, args.get(0));
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			return this.commandDeleteSpawnConfirmation((EPlayer) source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandDeleteSpawn(final EPlayer player, final String spawn_name) {
		Optional<Transform<World>> spawn = this.plugin.getManagerServices().getSpawn().get(spawn_name);
		
		// Le serveur a un spawn qui porte ce nom
		if (spawn.isPresent()) {
			EEMessages.DELSPAWN_CONFIRMATION.sender()
				.replace("<spawn>", () -> this.getButtonSpawn(spawn_name, spawn.get()))
				.replace("<confirmation>", () -> this.getButtonConfirmation(spawn_name))
				.sendTo(player);
		// Le serveur n'a pas de spawn qui porte ce nom
		} else {
			EEMessages.DELSPAWN_INCONNU.sender()
				.replace("<name>", spawn_name)
				.sendTo(player);
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandDeleteSpawnConfirmation(final EPlayer player, final String spawn_name) throws ServerDisableException {
		Optional<Transform<World>> spawn = this.plugin.getManagerServices().getSpawn().get(spawn_name);
		
		// Le serveur n'a pas de spawn qui porte ce nom
		if (!spawn.isPresent()) {
			EEMessages.DELSPAWN_INCONNU.sender()
				.replace("<name>", spawn_name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}

		// Le spawn n'a pas été supprimer
		if (!this.plugin.getManagerServices().getSpawn().remove(spawn_name)) {
			EAMessages.COMMAND_ERROR.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.DELSPAWN_DELETE.sender()
			.replace("<spawn>", this.getButtonSpawn(spawn_name, spawn.get()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonSpawn(final String name, final Transform<World> location){
		return EEMessages.DELSPAWN_NAME.getFormat().toText("<name>", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.DELSPAWN_NAME_HOVER.getFormat().toText(
							"<name>", name,
							"<world>", location.getExtent().getName(),
							"<x>", String.valueOf(location.getLocation().getBlockX()),
							"<y>", String.valueOf(location.getLocation().getBlockY()),
							"<z>", String.valueOf(location.getLocation().getBlockZ()))))
					.build();
	}
	
	private Text getButtonConfirmation(final String name){
		return EEMessages.DELSPAWN_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.DELSPAWN_CONFIRMATION_VALID_HOVER.getFormat()
							.toText("<name>", name)))
					.onClick(TextActions.runCommand("/delspawn \"" + name + "\" confirmation"))
					.build();
	}
}
