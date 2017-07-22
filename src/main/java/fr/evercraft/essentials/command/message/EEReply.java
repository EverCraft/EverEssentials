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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEReply extends ECommand<EverEssentials> {
	
	public EEReply(final EverEssentials plugin) {
        super(plugin , "reply", "r");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.REPLY.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.REPLY_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		Text help = Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_MESSAGE.getString() + ">")
						.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
						.color(TextColors.RED)
						.build();
		return help;
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("Hello world");
		}
		return Arrays.asList();
	}
	
	@Override
	protected List<String> getArg(final String arg){
		if (arg.isEmpty()) {
			return Arrays.asList();
		}
		return Arrays.asList(arg);
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			String message = EEMsg.replaceMessage(this.plugin.getChat(), source, args.get(0));
			
			// La source est un joueur
			if (source instanceof EPlayer) {
				return this.commandReply(source, ((EPlayer) source).getReplyTo(), message);
			// La source est une console
			} else if (source instanceof ConsoleSource) {
				return this.commandReply(source, this.plugin.getManagerServices().getEssentials().getConsole().getReplyTo(), message);
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandReply(final CommandSource player, final Optional<String> receive, final String message) {
		if (!receive.isPresent()) {
			EEMessages.REPLY_EMPTY.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le destinataire est le console
		if (receive.get().equalsIgnoreCase(EEMsg.CONSOLE)) {
			// La source est un joueur
			if (player instanceof EPlayer) {
				return this.commandMsgConsole((EPlayer) player, this.plugin.getEServer().getConsole(), message);
			// La source est une console
			} else {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(player);
			}
		// Le destinataire est un joueur
		} else {
			try {
				Optional<EPlayer> replyTo = this.plugin.getEServer().getEPlayer(UUID.fromString(receive.get()));
				if (replyTo.isPresent()) {
					// La source est un joueur
					if (player instanceof EPlayer) {
						return this.commandMsgPlayer((EPlayer) player, replyTo.get(), message);
					// La source est une console
					} else {
						return this.commandMsgConsole(player, replyTo.get(), message);
					}
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("<player>", receive.get())
						.sendTo(player);
				}
			} catch(IllegalArgumentException e) {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(player);
			}
		}
		return CompletableFuture.completedFuture(false);
	}
	
	/*
	 * Un joueur parle à un autre joueur
	 */
	private CompletableFuture<Boolean> commandMsgPlayer(final EPlayer player, final EPlayer receive, final String message) {
		if (receive.ignore(player)) {
				EEMessages.REPLY_IGNORE_RECEIVE.sender()
				.replace("<message>", message)
				.replace("<player>", receive.getName())
				.sendTo(player);
				return CompletableFuture.completedFuture(false);
		}
		
		if (player.ignore(receive)) {
			EEMessages.REPLY_IGNORE_PLAYER.sender()
				.replace("<message>", message)
				.replace("<player>", receive.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.put(Pattern.compile("<message>"), EReplace.of(message));
		
		replaces.putAll(player.getReplaces());
		receive.sendMessage(EEMessages.REPLY_PLAYER_RECEIVE.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + player.getName() + " "))
					.build());
		
		replaces.putAll(receive.getReplaces());
		player.sendMessage(EEMessages.REPLY_PLAYER_SEND.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.REPLY_PLAYER_SEND_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + receive.getName() + " "))
					.build());
		return CompletableFuture.completedFuture(true);
	}
	
	/*
	 * La console envoye un message à joueur
	 */
	private CompletableFuture<Boolean> commandMsgConsole(final CommandSource player, final EPlayer receive, final String message) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.put(Pattern.compile("<message>"), EReplace.of(message));
		
		receive.sendMessage(EEMessages.REPLY_CONSOLE_SEND.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.REPLY_CONSOLE_SEND_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + player.getName() + " "))
					.build());
		
		replaces.putAll(receive.getReplaces());
		player.sendMessage(EEMessages.REPLY_PLAYER_RECEIVE.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE + " "))
					.build());
		return CompletableFuture.completedFuture(true);
	}
	
	/*
	 * Un joueur envoye un message à la console
	 */
	private CompletableFuture<Boolean> commandMsgConsole(final EPlayer player, final CommandSource receive, final String message) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.put(Pattern.compile("<message>"), EReplace.of(message));
		
		player.sendMessage(EEMessages.REPLY_CONSOLE_SEND.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.REPLY_CONSOLE_SEND_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + receive.getName() + " "))
					.build());
		
		replaces.putAll(player.getReplaces());
		receive.sendMessage(EEMessages.REPLY_PLAYER_RECEIVE.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE + " "))
					.build());
		return CompletableFuture.completedFuture(true);
	}
}