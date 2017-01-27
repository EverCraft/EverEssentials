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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEMsg extends ECommand<EverEssentials> {
	
	public final static String CONSOLE = "console";
	
	public EEMsg(final EverEssentials plugin) {
        super(plugin ,"tell", "msg", "m", "t", "whisper");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MSG.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.MSG_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		Text help = Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + "> <" + EAMessages.ARGS_MESSAGE.getString() + ">")
						.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
						.color(TextColors.RED)
						.build();
		return help;
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.getAllPlayers(source, true);
		} else if (args.size() == 2) {
			return Arrays.asList("Hello world");
		}
		return Arrays.asList();
	}
	
	@Override
	protected List<String> getArg(final String arg) {
		List<String> args = super.getArg(arg);
		// Le message est transformer en un seul argument
		if (args.size() > 2) {
			List<String> args_send = new ArrayList<String>();
			args_send.add(args.get(0));
			args_send.add(Pattern.compile("^[ \"]*" + args.get(0) + "[ \"][ ]*").matcher(arg).replaceAll(""));
			return args_send;
		}
		return args;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 2) {
			String message = EEMsg.replaceMessage(this.plugin.getChat(), source, args.get(1));
			
			// Le destinataire est la console
			if (args.get(0).equalsIgnoreCase(EEMsg.CONSOLE)) {
				
				// La source est un joueur
				if (source instanceof EPlayer) {
					resultat = this.commandMsgConsole((EPlayer) source, this.plugin.getEServer().getConsole(), message);
				// La source est la console
				} else if (source instanceof ConsoleSource) {
					EEMessages.MSG_CONSOLE_ERROR.sendTo(source);
				// La source est un commande block
				} else if (source instanceof CommandBlockSource) {
					resultat = this.commandMsgCommandBlock(source, this.plugin.getEServer().getConsole(), message);
				// La source est inconnue
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
				
			// Le destinataire est un joueur
			} else {
				
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (optPlayer.isPresent()) {
					// La source est un joueur
					if (source instanceof EPlayer) {
						resultat = this.commandMsgPlayer((EPlayer) source, optPlayer.get(), message);
					// La source est la console
					} else if (source instanceof ConsoleSource) {
						resultat = this.commandMsgConsole(source, optPlayer.get(), message);
					// La source est un commande block
					} else if (source instanceof CommandBlockSource) {
						resultat = this.commandMsgCommandBlock(source, optPlayer.get(), message);
					// La source est inconnue
					} else {
						EAMessages.COMMAND_ERROR.sender()
							.prefix(EEMessages.PREFIX)
							.sendTo(source);
					}
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
				
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	/*
	 * Un joueur parle à un autre joueur
	 */
	private boolean commandMsgPlayer(final EPlayer player, final EPlayer receive, final String message) {
		if (receive.ignore(player)) {
			EEMessages.MSG_IGNORE_RECEIVE.sender()
				.replace("<message>", message)
				.replace("<player>", receive.getName())
				.sendTo(player);
			return false;
		}
		
		if (player.ignore(receive)) {
			EEMessages.MSG_IGNORE_PLAYER.sender()
				.replace("<message>", message)
				.replace("<player>", receive.getName())
				.sendTo(player);
			return false;
		}
		
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<message>", EReplace.of(message));
		
		replaces.putAll(player.getReplacesAll());
		receive.sendMessage(EEMessages.MSG_PLAYER_RECEIVE.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.MSG_PLAYER_RECEIVE_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + player.getName() + " "))
					.build());
		
		replaces.putAll(receive.getReplacesAll());
		player.sendMessage(EEMessages.MSG_PLAYER_SEND.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.MSG_PLAYER_SEND_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + receive.getName() + " "))
					.build());
		if(receive.isAfk()){
			EEMessages.MSG_PLAYER_SEND_IS_AFK.sender()
				.replace("<player>", receive.getDisplayName())
				.sendTo(player);
		}
		receive.setReplyTo(player.getIdentifier());
		player.setReplyTo(receive.getIdentifier());
		return true;
	}
	
	/*
	 * La console envoye un message à joueur
	 */
	private boolean commandMsgConsole(final CommandSource player, final EPlayer receive, final String message) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<message>", EReplace.of(message));
		
		receive.sendMessage(EEMessages.MSG_CONSOLE_RECEIVE.getFormat().toText(replaces)
				.toBuilder()
				.onHover(TextActions.showText(EEMessages.MSG_CONSOLE_RECEIVE_HOVER.getFormat().toText(replaces)))
				.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE + " "))
				.build());
		
		replaces.putAll(receive.getReplacesAll());
		player.sendMessage(EEMessages.MSG_PLAYER_RECEIVE.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.MSG_PLAYER_RECEIVE_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + receive.getName() + " "))
					.build());
		
		receive.setReplyTo(player.getIdentifier());
		this.plugin.getManagerServices().getEssentials().getConsole().setReplyTo(receive.getIdentifier());
		return true;
	}
	
	/*
	 * Un joueur envoye un message à la console
	 */
	private boolean commandMsgConsole(final EPlayer player, final CommandSource receive, final String message) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("<message>", EReplace.of(message));
		
		player.sendMessage(EEMessages.MSG_CONSOLE_SEND.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.MSG_CONSOLE_SEND_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE + " "))
					.build());
		
		replaces.putAll(player.getReplacesAll());
		receive.sendMessage(EEMessages.MSG_PLAYER_RECEIVE.getFormat().toText(replaces)
					.toBuilder()
					.onHover(TextActions.showText(EEMessages.MSG_PLAYER_RECEIVE_HOVER.getFormat().toText(replaces)))
					.onClick(TextActions.suggestCommand("/msg " + player.getName() + " "))
					.build());
		
		player.setReplyTo(EEMsg.CONSOLE);
		this.plugin.getManagerServices().getEssentials().getConsole().setReplyTo(player.getIdentifier());
		return true;
	}
	
	/*
	 * Un commande block envoye un message à un joueur
	 */
	private boolean commandMsgCommandBlock(final CommandSource player, final EPlayer receive, final String message) {
		EEMessages.MSG_COMMANDBLOCK_RECEIVE.sender()
			.replace("<message>", message)
			.sendTo(receive);
		return true;
	}
	
	/*
	 * Un commande block envoye un message la console
	 */
	private boolean commandMsgCommandBlock(final CommandSource player, final CommandSource receive, final String message) {
		EEMessages.MSG_COMMANDBLOCK_RECEIVE.sender()
			.replace("<message>", message)
			.sendTo(receive);
		return true;
	}
	
	public static String replaceMessage(final EChat chat, final Subject player, String message) {
		if (!player.hasPermission(EEPermissions.MSG_COLOR.get())) {
			message = message.replaceAll(EChat.REGEX_COLOR, "");
		}
		if (!player.hasPermission(EEPermissions.MSG_FORMAT.get())) {
			message = message.replaceAll(EChat.REGEX_FORMAT, "");
		}
		if (!player.hasPermission(EEPermissions.MSG_MAGIC.get())) {
			message = message.replaceAll(EChat.REGEX_MAGIC, "");
		}
		if (player.hasPermission(EEPermissions.MSG_CHARACTER.get())) {
			message = chat.replaceCharacter(message);
		}
		if (player.hasPermission(EEPermissions.MSG_ICONS.get())) {
			message = chat.replaceIcons(message);
		}
		return message;
	}
}