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
package fr.evercraft.essentials.command.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;

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
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.addAll(this.getAllUsers());
		} else if (args.size() == 2) {
			suggests.add("Hello world");
		}
		return suggests;
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName()+ " <" + EAMessages.ARGS_PLAYER.getString() + "> <" + EAMessages.ARGS_MESSAGE.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 2){
			// Si il a la permission
			if (source.hasPermission(EEPermissions.MAIL_SEND.get())) {
				if (args.get(0).equalsIgnoreCase("*") || args.get(0).equalsIgnoreCase("all")) {
					
					// Si il a la permission
					if (source.hasPermission(EEPermissions.MAIL_SENDALL.get())){
						resultat = this.commandSendAll(source, args.get(1));
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
						resultat = this.commandSend(source, user.get(), args.get(1));
					// Le joueur est introuvable
					} else {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
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
		
		return resultat;
	}

	private boolean commandSend(CommandSource staff, EUser user, String message) {
		// Le staff ignore le joueur
		if (staff instanceof EPlayer && ((EPlayer) staff).ignore(user)) {
			EEMessages.MAIL_SEND_IGNORE_PLAYER.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			return false;
		}
		
		// Le joueur vous ignore
		if (staff instanceof EPlayer && user.ignore((EPlayer) staff)) {
			EEMessages.MAIL_SEND_IGNORE_RECEIVE.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			return false;
		}
		
		// Event cancel
		if (!user.addMail(staff, message)) {
			EEMessages.MAIL_SEND_CANCEL.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			return false;
		}
		
		if (!staff.getIdentifier().equals(user.getIdentifier())) {
			EEMessages.MAIL_SEND_MESSAGE.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
		} else {
			EEMessages.MAIL_SEND_EQUALS.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
		}
		return true;
	}
	
	private boolean commandSendAll(CommandSource player, String message) {
		this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().sendAllMail(player.getIdentifier(), message));
		EEMessages.MAIL_SEND_ALL.sendTo(player);
		return true;
	}
}
