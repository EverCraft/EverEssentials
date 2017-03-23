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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsInteger;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.Mail;
import fr.evercraft.everapi.services.essentials.SubjectUserEssentials;

public class EEMailDelete extends ESubCommand<EverEssentials> {
	
	public EEMailDelete(final EverEssentials plugin, final EEMail command) {
        super(plugin, command, "delete");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.MAIL_DELETE_DESCRIPTION.getText();
	}
	
	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_ID.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			Optional<SubjectUserEssentials> player = this.plugin.getManagerServices().getEssentials().get(((Player) source).getUniqueId());
			// Le joueur existe
			if (player.isPresent()) {
				List<String> suggests = new ArrayList<String>();
				for (Mail mail : player.get().getMails()){
					suggests.add(String.valueOf(mail.getID()));
				}
				return suggests;
			}
		}
		return Arrays.asList();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 1){
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandDelete((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandDeleteConfirmation((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
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
		Optional<Integer> id = UtilsInteger.parseInt(id_string);
		if (!id.isPresent()) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<number>", id_string)
				.sendTo(player);
			return false;
		}
		
		Optional<Mail> mail = player.getMail(id.get());
		if (!mail.isPresent()) {
			EEMessages.MAIL_DELETE_ERROR.sender()
				.replace("<number>", id_string)
				.sendTo(player);
			return false;
		}
		
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<id>", EReplace.of(String.valueOf(mail.get().getID())));
		replaces.put("<player>", EReplace.of(mail.get().getToName()));
		replaces.put("<time>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.get().getDateTime())));
		replaces.put("<date>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.get().getDateTime())));
		replaces.put("<datetime>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.get().getDateTime())));
		replaces.put("<mail>", EReplace.of(() -> this.getButtomDeleteMail(mail.get())));
		replaces.put("<confirmation>", EReplace.of(() -> this.getButtonDeleteConfirmation(mail.get())));
		
		EEMessages.MAIL_DELETE_MESSAGE.sender()
			.replaceString(replaces)
			.sendTo(player);
		return true;
	}
	
	private boolean commandDeleteConfirmation(EPlayer player, String id_string) {
		Optional<Integer> id = UtilsInteger.parseInt(id_string);
		if (!id.isPresent()) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<number>", id_string)
				.sendTo(player);
			return false;
		}
		
		Optional<Mail> mail = player.getMail(id.get());
		if (!mail.isPresent()) {
			EEMessages.MAIL_DELETE_ERROR.sender()
				.replace("<number>", id_string)
				.sendTo(player);
			return false;
		}
		
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<id>", EReplace.of(String.valueOf(mail.get().getID())));
		replaces.put("<player>", EReplace.of(mail.get().getToName()));
		replaces.put("<time>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.get().getDateTime())));
		replaces.put("<date>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.get().getDateTime())));
		replaces.put("<datetime>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.get().getDateTime())));
		replaces.put("<mail>", EReplace.of(() -> this.getButtomDeleteMail(mail.get())));
		
		if (!player.removeMail(mail.get())) {
			EEMessages.MAIL_DELETE_CONFIRMATION.sender()
				.replaceString(replaces)
				.sendTo(player);
			return false;
		}
		
		EEMessages.MAIL_DELETE_CANCEL.sender()
			.replaceString(replaces)
			.sendTo(player);
		return true;
	}
	
	private Text getButtonDeleteConfirmation(final Mail mail){
		return EEMessages.MAIL_DELETE_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.MAIL_DELETE_VALID_HOVER.getText()))
					.onClick(TextActions.runCommand("/mail delete " + mail.getID() + " confirmation"))
					.build();
	}
	
	private Text getButtomDeleteMail(final Mail mail) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<id>", EReplace.of(String.valueOf(mail.getID())));
		replaces.put("<player>", EReplace.of(mail.getToName()));
		replaces.put("<time>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime())));
		replaces.put("<date>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime())));
		replaces.put("<datetime>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())));
		
		return EEMessages.MAIL_DELETE_MAIL.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.MAIL_DELETE_MAIL_HOVER.getFormat().toText2(replaces)))
					.build();
	}
}
