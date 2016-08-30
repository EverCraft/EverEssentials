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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.Mail;
import fr.evercraft.everapi.text.ETextBuilder;

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
		return EChat.of(EEMessages.MAIL_READ_DESCRIPTION.get());
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer((Player) source);
			// Le joueur existe
			if (player.isPresent()) {
				for (Mail mail : player.get().getMails()){
					suggests.add(String.valueOf(mail.getID()));
				}
			}
		}
		return suggests;
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
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
			
		} else if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandRead((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
			
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandRead(EPlayer player) {
		Set<Mail> mails = player.getMails();
		
		if (mails.size() == 0) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.MAIL_READ_EMPTY.get());
		} else {
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
			
			// Mail non lu
			for (Mail mail : noread.descendingMap().values()) {
				lists.add(ETextBuilder.toBuilder(EEMessages.MAIL_READ_LINE_NO_READ.get()
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))
						.replace("<read>", this.getButtonRead(mail))
						.replace("<delete>", this.getButtonDelete(mail))
						.build());
			}
			
			// Mail lu
			for (Mail mail : read.descendingMap().values()) {
				lists.add(ETextBuilder.toBuilder(EEMessages.MAIL_READ_LINE_READ.get()
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))
						.replace("<read>", this.getButtonRead(mail))
						.replace("<delete>", this.getButtonDelete(mail))
						.build());
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.MAIL_READ_TITLE.getText().toBuilder()
					.onClick(TextActions.runCommand("/mail read")).build(), lists, player);
		}
		return true;
	}
	
	private boolean commandRead(EPlayer player, String id_string) {
		try {
			Optional<Mail> mail = player.getMail(Integer.parseInt(id_string));
			if (mail.isPresent()) {
				
				if (player.readMail(mail.get())) {
					
					player.sendBookView(BookView.builder()
												.addPage(mail.get().getText())
												.build());
					
					return true;
				} else {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.MAIL_READ_CANCEL.get()
								.replaceAll("<id>", String.valueOf(mail.get().getID()))
								.replaceAll("<player>", mail.get().getToName())
								.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.get().getDateTime()))
								.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.get().getDateTime()))
								.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.get().getDateTime())))
							.replace("<mail>", getButtomReadMail(mail.get()))
							.build());
				}
				
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.MAIL_READ_ERROR.get()
						.replaceAll("<id>", id_string));
			}
		} catch (NumberFormatException e){
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", id_string)));
		}
		return false;
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
		return EEMessages.MAIL_READ_MAIL.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.MAIL_READ_MAIL_HOVER.get()
							.replaceAll("<id>", String.valueOf(mail.getID()))
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))))
					.build();
	}
}