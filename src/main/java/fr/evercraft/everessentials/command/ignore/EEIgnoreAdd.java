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
package fr.evercraft.everessentials.command.ignore;

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

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEIgnoreAdd extends ESubCommand<EverEssentials> {
	
	public EEIgnoreAdd(final EverEssentials plugin, final EEIgnore command) {
        super(plugin, command, "add");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.IGNORE_ADD_DESCRIPTION.getText();
	}
	
	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_USER.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.getAllUsers(args.get(0), source);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					return this.commandIgnoreAdd((EPlayer) source, user.get());
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(0))
						.sendTo(source);
				}
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandIgnoreAdd(final EPlayer player, final EUser user) {
		if(player.ignore(user.getUniqueId())) {
			EEMessages.IGNORE_ADD_ERROR.sender()
				.replace("{player}", user.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (user.hasPermission(EEPermissions.IGNORE_BYPASS.get())) {
			EEMessages.IGNORE_ADD_BYPASS.sender()
				.replace("{player}", user.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!player.addIgnore(user.getUniqueId())) {
			EEMessages.IGNORE_ADD_CANCEL.sender()
				.replace("{player}", user.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.IGNORE_ADD_PLAYER.sender()
			.replace("{player}", user.getName())
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
}
