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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.Mail;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEMailDelete extends ESubCommand<EverEssentials> {
	public EEMailDelete(final EverEssentials plugin, final EEMail command) {
        super(plugin, command, "delete");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.MAIL_DELETE_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("1");
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName() + "<ID>"))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 1){
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandDelete((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandDeleteConfirmation((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	/*
	 * Delete
	 */
	
	private boolean commandDelete(EPlayer player, String id_string) {
		try {
			Optional<Mail> mail = player.getMail(Integer.parseInt(id_string));
			if(mail.isPresent()) {	
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.MAIL_DELETE_MESSAGE.get()
							.replaceAll("<id>", String.valueOf(mail.get().getID()))
							.replaceAll("<player>", mail.get().getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.get().getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.get().getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.get().getDateTime())))
						.replace("<mail>", getButtomDeleteMail(mail.get()))
						.replace("<confirmation>", getButtonDeleteConfirmation(mail.get()))
						.build());
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.MAIL_DELETE_ERROR.get()
						.replaceAll("<id>", id_string));
			}
		} catch (NumberFormatException e){
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", id_string)));
		}
		return false;
	}
	
	private boolean commandDeleteConfirmation(EPlayer player, String id_string) {
		try {
			Optional<Mail> mail = player.removeMail(Integer.parseInt(id_string));
			if(mail.isPresent()) {	
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.MAIL_DELETE_CONFIRMATION.get()
							.replaceAll("<id>", String.valueOf(mail.get().getID()))
							.replaceAll("<player>", mail.get().getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.get().getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.get().getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.get().getDateTime())))
						.replace("<mail>", getButtomDeleteMail(mail.get()))
						.build());
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.MAIL_DELETE_ERROR.get()
						.replaceAll("<id>", id_string));
			}
		} catch (NumberFormatException e){
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", id_string)));
		}
		return false;
	}
	
	private Text getButtonDeleteConfirmation(final Mail mail){
		return EEMessages.MAIL_DELETE_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.MAIL_DELETE_VALID_HOVER.getText()))
					.onClick(TextActions.runCommand("/mail delete " + mail.getID() + " confirmation"))
					.build();
	}
	
	private Text getButtomDeleteMail(final Mail mail) {
		return EEMessages.MAIL_DELETE_MAIL.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.MAIL_DELETE_MAIL_HOVER.get()
							.replaceAll("<id>", String.valueOf(mail.getID()))
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))))
					.build();
	}
}
