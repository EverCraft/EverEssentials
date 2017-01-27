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
import java.util.Set;
import java.util.TreeMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.BookView;
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

public class EEMailRead extends ESubCommand<EverEssentials> {
	
	public EEMailRead(final EverEssentials plugin, final EEMail mail) {
        super(plugin, mail, "read");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.MAIL_READ_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer((Player) source);
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
		
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandRead((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandRead((EPlayer) source, args.get(0));
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

	private boolean commandRead(EPlayer player) {
		Set<Mail> mails = player.getMails();
		
		if (mails.size() == 0) {
			EEMessages.MAIL_READ_EMPTY.sendTo(player);
			return false;
		}
		
		List<Text> lists = new ArrayList<Text>();
		
		TreeMap<Long, Mail> noread = new TreeMap<Long, Mail>();
		TreeMap<Long, Mail> read = new TreeMap<Long, Mail>();
		for (Mail mail : mails) {
			if (mail.isRead()) {
				read.put(mail.getDateTime(), mail);
			} else {
				noread.put(mail.getDateTime(), mail);
			}
		}
		
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		
		// Mail non lu
		for (Mail mail : noread.descendingMap().values()) {
			replaces.put("<id>", EReplace.of(String.valueOf(mail.getID())));
			replaces.put("<player>", EReplace.of(mail.getToName()));
			replaces.put("<time>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime())));
			replaces.put("<date>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime())));
			replaces.put("<datetime>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())));
			replaces.put("<read>", EReplace.of(() -> this.getButtonRead(mail)));
			replaces.put("<delete>", EReplace.of(() -> this.getButtonDelete(mail)));
			
			lists.add(EEMessages.MAIL_READ_LINE_NO_READ.getFormat().toText(replaces));
		}
		
		// Mail lu
		for (Mail mail : read.descendingMap().values()) {
			replaces.put("<id>", EReplace.of(String.valueOf(mail.getID())));
			replaces.put("<player>", EReplace.of(mail.getToName()));
			replaces.put("<time>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime())));
			replaces.put("<date>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime())));
			replaces.put("<datetime>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())));
			replaces.put("<read>", EReplace.of(() -> this.getButtonRead(mail)));
			replaces.put("<delete>", EReplace.of(() -> this.getButtonDelete(mail)));
			
			lists.add(EEMessages.MAIL_READ_LINE_READ.getFormat().toText(replaces));
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.MAIL_READ_TITLE.getText().toBuilder()
				.onClick(TextActions.runCommand("/mail read")).build(), lists, player);
		return true;
	}
	
	private boolean commandRead(EPlayer player, String id_string) {
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
		replaces.put("<read>", EReplace.of(() -> this.getButtomReadMail(mail.get())));
		
		if (!player.readMail(mail.get())) {
			EEMessages.MAIL_READ_CANCEL.sender()
				.replace(replaces)
				.sendTo(player);
			return false;
		}
					
		player.sendBookView(BookView.builder()
									.addPage(mail.get().getText())
									.build());
		
		return true;
	}
	
	private Text getButtonRead(final Mail mail){
		return EEMessages.MAIL_BUTTON_READ.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.MAIL_BUTTON_READ_HOVER.getText()))
					.onClick(TextActions.runCommand("/mail read " + mail.getID()))
					.build();
	}
	
	private Text getButtonDelete(final Mail mail){
		return EEMessages.MAIL_BUTTON_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.MAIL_BUTTON_DELETE_HOVER.getText()))
					.onClick(TextActions.runCommand("/mail delete " + mail.getID()))
					.build();
	}
	
	private Text getButtomReadMail(final Mail mail) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<id>", EReplace.of(String.valueOf(mail.getID())));
		replaces.put("<player>", EReplace.of(mail.getToName()));
		replaces.put("<time>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime())));
		replaces.put("<date>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime())));
		replaces.put("<datetime>", EReplace.of(() -> this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())));
		
		return EEMessages.MAIL_READ_MAIL.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.MAIL_READ_MAIL_HOVER.getFormat().toText(replaces)))
					.build();
	}
}