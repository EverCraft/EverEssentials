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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.services.essentials.EssentialsSubject;

public class EEMailSend extends ESubCommand<EverEssentials> {
	public EEMailSend(final EverEssentials plugin, final EEMail command) {
        super(plugin, command, "send");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL_SEND.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.MAIL_SEND_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests = null;
		} else if (args.size() == 2) {
			suggests.add("Hello world");
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName()+ " <" + EAMessages.ARGS_PLAYER.get() + "> <" + EAMessages.ARGS_MESSAGE.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 3){
			// Si il a la permission
			if(source.hasPermission(EEPermissions.MAIL_SEND.get())) {
				if(args.get(0).equalsIgnoreCase("*") || args.get(0).equalsIgnoreCase("all")) {
					// Si il a la permission
					if(source.hasPermission(EEPermissions.MAIL_SENDALL.get())){
						resultat = commandSendAll(source, args.get(1));
					// Il n'a pas la permission
					} else {
						source.sendMessage(EAMessages.NO_PERMISSION.getText());
					}
				} else {
					Optional<User> optUser = this.plugin.getEServer().getUser(args.get(0));
					// Le joueur existe
					if(optUser.isPresent()){
						resultat = commandSend(source, optUser.get(), args.get(1));
					// Le joueur est introuvable
					} else {
						source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
					}
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	/*
	 * Send
	 */
	
	private boolean commandSend(CommandSource staff, User player, String message) {
		Optional<EssentialsSubject> subject = this.plugin.getManagerServices().getEssentials().get(player.getUniqueId());
		if(subject.isPresent()) {
			if(subject.get().addMail(staff, message)) {
				if(staff.getIdentifier().equals(player.getIdentifier())) {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.MAIL_SEND_MESSAGE.get()
							.replaceAll("<player>", player.getName())));
				} else {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.MAIL_SEND_EQUALS.get()
							.replaceAll("<player>", player.getName())));
				}
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.MAIL_SEND_CANCEL.get()
						.replaceAll("<player>", player.getName())));
			}
		} else {
			staff.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
		}
		return true;
	}
	
	/*
	 * SendAll
	 */
	
	private boolean commandSendAll(CommandSource player, String message) {
		this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().sendAllMail(player.getIdentifier(), message));
		player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.MAIL_SEND_ALL.getText()));
		return true;
	}
}
