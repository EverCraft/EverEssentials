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
			if(source instanceof EPlayer) {
				resultat = commandReply(source, ((EPlayer) source).getReplyTo(), args.get(0));
			} else {
				resultat = commandReply(source, this.plugin.getManagerServices().getEssentials().getConsole().getReplyTo(), args.get(0));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandReply(final CommandSource player, final Optional<String> receive, final String message) {
		if(receive.isPresent()) {
			String identifier = receive.get();
			try {
				Optional<EPlayer> replyTo = this.plugin.getEServer().getEPlayer(UUID.fromString(identifier));
				if(replyTo.isPresent()) {
					return this.commandReply(player, replyTo.get(), message);
				} else {
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.REPLY_ERROR.get()));
				}
			} catch(IllegalArgumentException e) {
				return this.commandReply(player, this.plugin.getEServer().getConsole(), message);
			}
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.REPLY_EMPTY.get()));
		}
		return false;
	}
	
	public boolean commandReply(final CommandSource source, final EPlayer receive, final String message) {
		if(source instanceof EPlayer) {
			EPlayer player = (EPlayer) source;
			receive.sendMessage(player.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE.get()
										.replaceAll("<message>", message))
									.toBuilder()
									.onHover(TextActions.showText(player.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.get())))
									.onClick(TextActions.suggestCommand("/msg " + player.getName()))
									.build());
			
			player.sendMessage(receive.replaceVariable(EEMessages.REPLY_PLAYER_SEND.get()
										.replaceAll("<message>", message))
									.toBuilder()
									.onHover(TextActions.showText(receive.replaceVariable(EEMessages.REPLY_PLAYER_SEND_HOVER.get())))
									.onClick(TextActions.suggestCommand("/msg " + receive.getName()))
									.build());
		} else {
			source.sendMessage(receive.replaceVariable(EEMessages.REPLY_CONSOLE_RECEIVE.get()
					.replaceAll("<message>", message))
				.toBuilder()
				.onHover(TextActions.showText(receive.replaceVariable(EEMessages.REPLY_CONSOLE_RECEIVE_HOVER.get())))
				.onClick(TextActions.suggestCommand("/msg " + receive.getName()))
				.build());
	
			receive.sendMessage(EChat.of(EEMessages.REPLY_CONSOLE_SEND.get()
							.replaceAll("<message>", message))
						.toBuilder()
						.onHover(TextActions.showText(EChat.of(EEMessages.REPLY_CONSOLE_SEND_HOVER.get())))
						.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE))
						.build());
		}
		return true;
	}
	
	public boolean commandReply(final CommandSource source, final CommandSource receive, final String message) {
		if(source instanceof EPlayer) {
			EPlayer player = (EPlayer) source;
			receive.sendMessage(player.replaceVariable(EEMessages.REPLY_CONSOLE_RECEIVE.get()
					.replaceAll("<message>", message))
				.toBuilder()
				.onHover(TextActions.showText(player.replaceVariable(EEMessages.REPLY_CONSOLE_RECEIVE_HOVER.get())))
				.onClick(TextActions.suggestCommand("/msg " + player.getName()))
				.build());
	
			player.sendMessage(EChat.of(EEMessages.REPLY_CONSOLE_SEND.get()
							.replaceAll("<message>", message))
						.toBuilder()
						.onHover(TextActions.showText(EChat.of(EEMessages.REPLY_CONSOLE_SEND_HOVER.get())))
						.onClick(TextActions.suggestCommand("/msg " + receive.getName()))
						.build());
		} else {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.REPLY_ERROR.get()));
		}
		return true;
	}
}