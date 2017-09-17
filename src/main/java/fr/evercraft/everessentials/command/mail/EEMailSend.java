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
package fr.evercraft.everessentials.command.mail;

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

public class EEMailSend extends ESubCommand<EverEssentials> {
	
	public EEMailSend(final EverEssentials plugin, final EEMail command) {
        super(plugin, command, "send");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL_SEND.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.MAIL_SEND_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.getAllUsers(args.get(0), source);
		} else if (args.size() == 2) {
			return Arrays.asList("Hello world");
		}
		return Arrays.asList();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName()+ " <" + EAMessages.ARGS_USER.getString() + "> <" + EAMessages.ARGS_MESSAGE.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 2){
			// Si il a la permission
			if (source.hasPermission(EEPermissions.MAIL_SEND.get())) {
				if (args.get(0).equalsIgnoreCase("*") || args.get(0).equalsIgnoreCase("all")) {
					
					// Si il a la permission
					if (source.hasPermission(EEPermissions.MAIL_SENDALL.get())){
						return this.commandSendAll(source, args.get(1));
					// Il n'a pas la permission
					} else {
						EAMessages.NO_PERMISSION.sender()
							.prefix(EEMessages.PREFIX)
							.sendTo(source);
					}
					
				} else {
					
					Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
					// Le joueur existe
					if (user.isPresent()){
						return this.commandSend(source, user.get(), args.get(1));
					// Le joueur est introuvable
					} else {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
							.replace("{player}", args.get(0))
							.sendTo(source);
					}
					
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

	private CompletableFuture<Boolean> commandSend(CommandSource staff, EUser user, String message) {
		// Le staff ignore le joueur
		if (staff instanceof EPlayer && ((EPlayer) staff).ignore(user)) {
			EEMessages.MAIL_SEND_IGNORE_PLAYER.sender()
				.replace("{player}", user.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le joueur vous ignore
		if (staff instanceof EPlayer && user.ignore((EPlayer) staff)) {
			EEMessages.MAIL_SEND_IGNORE_RECEIVE.sender()
				.replace("{player}", user.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// Event cancel
		if (!user.addMail(staff, message)) {
			EEMessages.MAIL_SEND_CANCEL.sender()
				.replace("{player}", user.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!staff.getIdentifier().equals(user.getIdentifier())) {
			EEMessages.MAIL_SEND_MESSAGE.sender()
				.replace("{player}", user.getName())
				.sendTo(staff);
		} else {
			EEMessages.MAIL_SEND_EQUALS.sender()
				.replace("{player}", user.getName())
				.sendTo(staff);
		}
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandSendAll(CommandSource player, String message) {
		this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().sendAllMail(player.getIdentifier(), message));
		EEMessages.MAIL_SEND_ALL.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
}
