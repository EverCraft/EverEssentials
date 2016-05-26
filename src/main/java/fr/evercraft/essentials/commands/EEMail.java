/**
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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.ESubject;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.Mail;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEMail extends ECommand<EverEssentials> {
	
	public EEMail(final EverEssentials plugin) {
        super(plugin, "mail");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("MAIL"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("MAIL_DESCRIPTION");
	}
	
	public Text help(final CommandSource source) {
		Builder build = Text.builder("/" + this.getName() + " <");
		build = build.append(Text.builder("read").onClick(TextActions.suggestCommand("/" + this.getName() + " read")).build());
		build = build.append(Text.of("|"));
		build = build.append(Text.builder("clear").onClick(TextActions.suggestCommand("/" + this.getName() + " clear")).build());
		
		if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SENDALL"))){
			build = build.append(Text.of("|"));
			build = build.append(Text.builder("send <*|player> <message>").onClick(TextActions.suggestCommand("/" + this.getName() + " send ")).build());
		} else if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
			build = build.append(Text.of("|"));
			build = build.append(Text.builder("send <player> <message>").onClick(TextActions.suggestCommand("/" + this.getName() + " send ")).build());
		}
		return build.append(Text.of(">")).color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("read");
			suggests.add("clear");
			if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
				suggests.add("send");
			}
		} else if (args.size() == 2) {
			if(args.get(0).equalsIgnoreCase("send") && source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))) {
				if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SENDALL"))){
					suggests.add("*");
					for(Player player : this.plugin.getGame().getServer().getOnlinePlayers()) {
						suggests.add(player.getName());
					}
				} else {
					suggests = null;
				}
			}
		} else if (args.size() == 3) {
			if(args.get(0).equalsIgnoreCase("send") && source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
				suggests.add("Hello world");
			}
		}
		return suggests;
	}
	
	protected List<String> getArg(final String arg) {
		List<String> args = super.getArg(arg);
		// Le message est transformer en un seul argument
		if(args.size() > 3 && args.get(0).equalsIgnoreCase("send")) {
			List<String> args_send = new ArrayList<String>();
			args_send.add(args.get(0));
			args_send.add(args.get(1));
			if(args.get(1).equalsIgnoreCase("*")) {
				args_send.add(Pattern.compile("^[ \"]*" + args.get(0) + "[ \"]*\\*[ \"][ ]*").matcher(arg).replaceAll(""));
			} else {
				args_send.add(Pattern.compile("^[ \"]*" + args.get(0) + "[ \"]*" + args.get(1) + "[ \"][ ]*").matcher(arg).replaceAll(""));
			}
			return args_send;
		}
		return args;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 1) {
			if(args.get(0).equalsIgnoreCase("read")) {
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = commandRead((EPlayer) source);
				// La source n'est pas un joueur
				} else {
					source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
				}
			} else if(args.get(0).equalsIgnoreCase("clear")) {
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = commandClear((EPlayer) source);
				// La source n'est pas un joueur
				} else {
					source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
				}
			} else {
				source.sendMessage(help(source));
			}
		} else if(args.size() == 2) {
			if(args.get(0).equalsIgnoreCase("read")) {
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = commandRead((EPlayer) source, args.get(1));
				// La source n'est pas un joueur
				} else {
					source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
				}
			} else if(args.get(0).equalsIgnoreCase("delete")) {
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = commandDelete((EPlayer) source, args.get(1));
				// La source n'est pas un joueur
				} else {
					source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
				}
			} else if(args.get(0).equalsIgnoreCase("sendall")) {
				// Si il a la permission
				if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SENDALL"))){
					resultat = commandSendAll(source, args.get(1));
				// Il n'a pas la permission
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			} else {
				source.sendMessage(help(source));
			}
		} else if(args.size() == 3) {
			if(args.get(0).equalsIgnoreCase("send")) {
				// Si il a la permission
				if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))) {
					if(args.get(1).equalsIgnoreCase("*")) {
						// Si il a la permission
						if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SENDALL"))){
							resultat = commandSendAll(source, args.get(2));
						// Il n'a pas la permission
						} else {
							source.sendMessage(EAMessages.NO_PERMISSION.getText());
						}
					} else {
						Optional<User> optUser = this.plugin.getEServer().getUser(args.get(1));
						// Le joueur existe
						if(optUser.isPresent()){
							resultat = commandSend(source, optUser.get(), args.get(2));
						// Le joueur est introuvable
						} else {
							source.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getText("PLAYER_NOT_FOUND")));
						}
					}
				// Il n'a pas la permission
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			} else if(args.get(0).equalsIgnoreCase("delete") && args.get(2).equalsIgnoreCase("confirmation")) {
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = commandDeleteConfirmation((EPlayer) source, args.get(1));
				// La source n'est pas un joueur
				} else {
					source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
				}
			} else {
				source.sendMessage(help(source));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	/*
	 * Read
	 */

	private boolean commandRead(EPlayer player) {
		Set<Mail> mails = player.getMails();
		if(mails.size() == 0) {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("MAIL_READ_EMPTY"));
		} else {
			List<Text> lists = new ArrayList<Text>();
			
			TreeMap<Long, Mail> noread = new TreeMap<Long, Mail>();
			TreeMap<Long, Mail> read = new TreeMap<Long, Mail>();
			for(Mail mail : mails) {
				if(mail.isRead()) {
					read.put(mail.getDateTime(), mail);
				} else {
					noread.put(mail.getDateTime(), mail);
				}
			}
			
			for(Mail mail : noread.descendingMap().values()) {
				lists.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("MAIL_READ_LINE_NO_READ")
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))
						.replace("<read>", getButtonRead(mail))
						.replace("<delete>", getButtonDelete(mail))
						.build());
			}
			
			for(Mail mail : read.descendingMap().values()) {
				lists.add(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("MAIL_READ_LINE_READ")
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))
						.replace("<read>", getButtonRead(mail))
						.replace("<delete>", getButtonDelete(mail))
						.build());
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(this.plugin.getMessages().getText("MAIL_READ_TITLE").toBuilder()
					.onClick(TextActions.runCommand("/mail read")).build(), lists, player);
		}
		return true;
	}
	
	public Text getButtonRead(final Mail mail){
		return this.plugin.getMessages().getText("MAIL_BUTTOM_READ").toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("MAIL_BUTTOM_READ_HOVER"))))
					.onClick(TextActions.runCommand("/mail read " + mail.getID()))
					.build();
	}
	
	public Text getButtonDelete(final Mail mail){
		return this.plugin.getMessages().getText("MAIL_BUTTON_DELETE").toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("MAIL_BUTTON_DELETE_HOVER"))))
					.onClick(TextActions.runCommand("/mail delete " + mail.getID()))
					.build();
	}
	
	private boolean commandRead(EPlayer player, String id_string) {
		try {
			Optional<Mail> mail = player.readMail(Integer.parseInt(id_string));
			if(mail.isPresent()) {
				BookView.Builder book = BookView.builder();
				book = book.addPage(mail.get().getText());
				player.sendBookView(book.build());
				return true;
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("MAIL_READ_ERROR")
						.replaceAll("<id>", id_string));
			}
		} catch (NumberFormatException e){
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("IS_NOT_NUMBER")
					.replaceAll("<number>", id_string)));
		}
		return false;
	}
	
	/*
	 * Delete
	 */
	
	private boolean commandDelete(EPlayer player, String id_string) {
		try {
			Optional<Mail> mail = player.getMail(Integer.parseInt(id_string));
			if(mail.isPresent()) {	
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("MAIL_DELETE")
							.replaceAll("<id>", String.valueOf(mail.get().getID()))
							.replaceAll("<player>", mail.get().getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.get().getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.get().getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.get().getDateTime())))
						.replace("<mail>", getButtomDeleteMail(mail.get()))
						.replace("<confirmation>", getButtonDeleteConfirmation(mail.get()))
						.build());
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("MAIL_DELETE_ERROR")
						.replaceAll("<id>", id_string));
			}
		} catch (NumberFormatException e){
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("IS_NOT_NUMBER")
					.replaceAll("<number>", id_string)));
		}
		return false;
	}
	
	private boolean commandDeleteConfirmation(EPlayer player, String id_string) {
		try {
			Optional<Mail> mail = player.removeMail(Integer.parseInt(id_string));
			if(mail.isPresent()) {	
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("MAIL_DELETE_CONFIRMATION")
							.replaceAll("<id>", String.valueOf(mail.get().getID()))
							.replaceAll("<player>", mail.get().getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.get().getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.get().getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.get().getDateTime())))
						.replace("<mail>", getButtomDeleteMail(mail.get()))
						.build());
				return true;
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("MAIL_DELETE_ERROR")
						.replaceAll("<id>", id_string));
			}
		} catch (NumberFormatException e){
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("IS_NOT_NUMBER")
					.replaceAll("<number>", id_string)));
		}
		return false;
	}
	
	private Text getButtonDeleteConfirmation(final Mail mail){
		return this.plugin.getMessages().getText("MAIL_DELETE_VALID").toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("MAIL_DELETE_VALID_HOVER"))))
					.onClick(TextActions.runCommand("/mail delete " + mail.getID() + " confirmation"))
					.build();
	}
	
	private Text getButtomDeleteMail(final Mail mail) {
		return EChat.of(this.plugin.getMessages().getMessage("MAIL_DELETE_MAIL")).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("MAIL_DELETE_MAIL_HOVER")
							.replaceAll("<id>", String.valueOf(mail.getID()))
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<time>", this.plugin.getEverAPI().getManagerUtils().getDate().parseTime(mail.getDateTime()))
							.replaceAll("<date>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDate(mail.getDateTime()))
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))))
					.build();
	}

	/*
	 * Clear
	 */
	
	private boolean commandClear(EPlayer player) {
		if(player.clearMails()) {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("MAIL_CLEAR")));
			return true;
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("MAIL_CLEAR_ERROR")));
		}
		return false;
	}
	
	/*
	 * Send
	 */
	
	private boolean commandSend(CommandSource staff, User player, String message) {
		ESubject subject = this.plugin.getManagerServices().getEssentials().get(player.getUniqueId());
		if(subject != null) {
			if(subject.addMail(staff.getIdentifier(), message)) {
				if(staff.getIdentifier().equals(player.getIdentifier())) {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("MAIL_SEND")
						.replaceAll("<player>", player.getName())));
				} else {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("MAIL_SEND_EQUALS")
							.replaceAll("<player>", player.getName())));
				}
			} else {
				staff.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
			}
		} else {
			staff.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getText("PLAYER_NOT_FOUND")));
		}
		return true;
	}

	/*
	 * SendAll
	 */
	
	private boolean commandSendAll(CommandSource player, String message) {
		this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().sendAllMail(player.getIdentifier(), message));
		player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("MAIL_SENDALL")));
		return true;
	}
}
