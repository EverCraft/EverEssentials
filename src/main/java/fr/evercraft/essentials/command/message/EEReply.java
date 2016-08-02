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
package fr.evercraft.essentials.command.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEReply extends ECommand<EverEssentials> {
	
	public EEReply(final EverEssentials plugin) {
        super(plugin ,"r", "reply");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.REPLY.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.REPLY_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		Text help = Text.builder("/r <" + EAMessages.ARGS_MESSAGE.get() + ">")
						.onClick(TextActions.suggestCommand("/r "))
						.color(TextColors.RED)
						.build();
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	protected List<String> getArg(final String arg){
		if(arg.isEmpty()) {
			return Arrays.asList();
		}
		return Arrays.asList(arg);
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 1) {
			String message = EEMsg.replaceMessage(this.plugin.getChat(), source, args.get(0));
			
			// La source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandReply(source, ((EPlayer) source).getReplyTo(), message);
			// La source est une console
			} else if(source.equals(this.plugin.getGame().getServer().getConsole())) {
				resultat = commandReply(source, this.plugin.getManagerServices().getEssentials().getConsole().getReplyTo(), message);
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandReply(final CommandSource player, final Optional<String> receive, final String message) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if(receive.isPresent()) {
			// Le destinataire est le console
			if(receive.get().equalsIgnoreCase(EEMsg.CONSOLE)) {
				// La source est un joueur
				if(player instanceof EPlayer) {
					resultat = this.commandMsgConsole((EPlayer) player, this.plugin.getEServer().getConsole(), message);
				// La source est une console
				} else {
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get()));
				}
			// Le destinataire est un joueur
			} else {
				try {
					Optional<EPlayer> replyTo = this.plugin.getEServer().getEPlayer(UUID.fromString(receive.get()));
					if(replyTo.isPresent()) {
						// La source est un joueur
						if(player instanceof EPlayer) {
							resultat = this.commandMsgPlayer((EPlayer) player, replyTo.get(), message);
						// La source est une console
						} else {
							resultat = this.commandMsgConsole(player, replyTo.get(), message);
						}
					} else {
						player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
					}
				} catch(IllegalArgumentException e) {
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get()));
				}
			}
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.REPLY_EMPTY.get()));
		}
		return resultat;
	}
	
	/*
	 * Un joueur parle à un autre joueur
	 */
	public boolean commandMsgPlayer(final EPlayer player, final EPlayer receive, final String message) {
		receive.sendMessage(player.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE.get()
									.replaceAll("<message>", message))
								.toBuilder()
								.onHover(TextActions.showText(player.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.get())))
								.onClick(TextActions.suggestCommand("/msg " + player.getName() + " "))
								.build());
		
		player.sendMessage(receive.replaceVariable(EEMessages.REPLY_PLAYER_SEND.get()
									.replaceAll("<message>", message))
								.toBuilder()
								.onHover(TextActions.showText(receive.replaceVariable(EEMessages.REPLY_PLAYER_SEND_HOVER.get())))
								.onClick(TextActions.suggestCommand("/msg " + receive.getName() + " "))
								.build());
		return true;
	}
	
	/*
	 * La console envoye un message à joueur
	 */
	public boolean commandMsgConsole(final CommandSource player, final EPlayer receive, final String message) {
		player.sendMessage(receive.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE.get()
				.replaceAll("<message>", message))
			.toBuilder()
			.onHover(TextActions.showText(receive.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.get())))
			.onClick(TextActions.suggestCommand("/msg " + receive.getName() + " "))
			.build());

		receive.sendMessage(EChat.of(EEMessages.REPLY_CONSOLE_SEND.get()
						.replaceAll("<message>", message))
					.toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.REPLY_CONSOLE_SEND_HOVER.get())))
					.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE + " "))
					.build());
		return true;
	}
	
	/*
	 * Un joueur envoye un message à la console
	 */
	public boolean commandMsgConsole(final EPlayer player, final CommandSource receive, final String message) {
		player.sendMessage(EChat.of(EEMessages.REPLY_CONSOLE_SEND.get()
						.replaceAll("<message>", message))
					.toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.REPLY_CONSOLE_SEND_HOVER.get())))
					.onClick(TextActions.suggestCommand("/msg " + receive.getName() + " "))
					.build());
		receive.sendMessage(player.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE.get()
						.replaceAll("<message>", message))
					.toBuilder()
					.onHover(TextActions.showText(player.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.get())))
					.onClick(TextActions.suggestCommand("/msg " + player.getName() + " "))
					.build());
		return true;
	}
}