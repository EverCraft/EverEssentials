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
/**
 * Authors : Rexbut, Lesbleu
 */
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.ESubject;
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
					source.sendMessage(this.plugin.getPermissions().noPermission());
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
							resultat = commandSendAll(source, args.get(1));
						// Il n'a pas la permission
						} else {
							source.sendMessage(this.plugin.getPermissions().noPermission());
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
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			} else {
				source.sendMessage(help(source));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	private boolean commandRead(EPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean commandRead(EPlayer player, String string) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean commandDelete(EPlayer player, String id_string) {
		try {
			Optional<Mail> mail = player.removeMail(Integer.parseInt(id_string));
			if(mail.isPresent()) {	
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("MAIL_DELETE_PLAYER"))
						.replace("<mail>", getButtomDelete(mail.get()))
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
	
	private Text getButtomDelete(Mail mail) {
		return EChat.of(this.plugin.getMessages().getMessage("MAIL_DELETE_MAIL")).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("MAIL_DELETE_MAIL_HOVER")
							.replaceAll("<id>", String.valueOf(mail.getID()))
							.replaceAll("<player>", mail.getToName())
							.replaceAll("<datetime>", this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(mail.getDateTime())))))
					.build();
	}

	private boolean commandClear(EPlayer player) {
		if(player.clearMails()) {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("MAIL_CLEAR_PLAYER")));
			return true;
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("MAIL_CLEAR_ERROR")));
		}
		return false;
	}
	
	private boolean commandSend(CommandSource staff, User player, String message) {
		ESubject subject = this.plugin.getManagerServices().getEssentials().get(player.getUniqueId());
		if(subject != null) {
			if(subject.receiveMail(staff.getIdentifier(), message)) {
				
			} else {
				staff.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
			}
		} else {
			staff.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getText("PLAYER_NOT_FOUND")));
		}
		return false;
	}

	private boolean commandSendAll(CommandSource player, String string) {

		return false;
	}
}
