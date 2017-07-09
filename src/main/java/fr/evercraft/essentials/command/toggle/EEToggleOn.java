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
package fr.evercraft.essentials.command.toggle;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;

public class EEToggleOn extends ESubCommand<EverEssentials> {
	
	public EEToggleOn(final EverEssentials plugin, final EEToggle command) {
        super(plugin, command, "on");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TOGGLE_ON_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.TOGGLE_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_USER.getString() + "]")
						.onClick(TextActions.suggestCommand("/" + this.getName()))
						.color(TextColors.RED)
						.build();
		} else {
			return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
		}
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.TOGGLE_OTHERS.get())){
			return this.getAllUsers(args.get(0), source);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			if (source instanceof EPlayer) {
				return this.commandToggleOn((EPlayer) source);
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.TOGGLE_OTHERS.get())){
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					return this.commandToggleOnOthers(source, user.get());
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandToggleOn(final EPlayer player) {
		// Toggle activé
		if (player.isToggle()) {
			EEMessages.TOGGLE_ON_PLAYER_ERROR.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!player.setAllowFlight(true)) {
			EEMessages.TOGGLE_ON_PLAYER_CANCEL.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.TOGGLE_ON_PLAYER.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandToggleOnOthers(final CommandSource staff, final EUser user) throws CommandException {
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && user.getIdentifier().equals(staff.getIdentifier())) {
			return this.commandToggleOn((EPlayer) staff);
		}
			
		// Toggle activé
		if (user.isToggle()) {
			EEMessages.TOGGLE_ON_OTHERS_ERROR.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!user.setAllowFlight(true)) {
			EEMessages.TOGGLE_ON_OTHERS_CANCEL.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.TOGGLE_ON_OTHERS_STAFF.sender()
			.replace("<player>", user.getName())
			.sendTo(staff);
		
		if (user instanceof EPlayer) {
			EEMessages.TOGGLE_ON_OTHERS_PLAYER.sender()
				.replace("<staff>", staff.getName())
				.sendTo((EPlayer) user);
		}
		return CompletableFuture.completedFuture(true);
	}
}