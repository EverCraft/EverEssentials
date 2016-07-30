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
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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

public class EEMsg extends ECommand<EverEssentials> {
	
	public final static String CONSOLE = "console";
	
	public EEMsg(final EverEssentials plugin) {
        super(plugin ,"msg", "tell", "m", "t", "whisper");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.REPLY.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.MSG_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		Text help = Text.builder("/msg <" + EAMessages.ARGS_PLAYER.get() + "> <" + EAMessages.ARGS_MESSAGE.get() + ">")
						.onClick(TextActions.suggestCommand("/msg "))
						.color(TextColors.RED)
						.build();
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if(!player.equals(source)) {
					suggests.add(player.getName());
				}
			}
			suggests.add(EEMsg.CONSOLE);
		}
		return suggests;
	}
	
	@Override
	protected List<String> getArg(final String arg) {
		List<String> args = super.getArg(arg);
		// Le message est transformer en un seul argument
		if(args.size() > 2) {
			List<String> args_send = new ArrayList<String>();
			args_send.add(args.get(0));
			args_send.add(Pattern.compile("^[ \"]*" + args.get(0) + "[ \"]*\\*[ \"][ ]*").matcher(arg).replaceAll(""));
			return args_send;
		}
		return args;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 2) {
			if(args.get(0).equalsIgnoreCase(EEMsg.CONSOLE)) {
				if(source instanceof EPlayer) {
					resultat = this.commandMsgConsole((EPlayer) source, this.plugin.getEServer().getConsole(), args.get(1));
				} else if(this.plugin.getEServer().getConsole().equals(source)) {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				} else if(source.getIdentifier().equals("@")) {
					this.plugin.getEServer().getConsole().sendMessage(EChat.of(EEMessages.MSG_COMMANDBLOCK_RECEIVE.get().replaceAll("<message>", args.get(1))));
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			} else {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()) {
					if(source instanceof EPlayer) {
						resultat = commandMsgPlayer((EPlayer) source, optPlayer.get(), args.get(1));
					} else if(this.plugin.getEServer().getConsole().equals(source)) {
						resultat = commandMsgConsole(source, optPlayer.get(), args.get(1));
					} else if(source.getIdentifier().equals("@")) {
						optPlayer.get().sendMessage(EEMessages.MSG_COMMANDBLOCK_RECEIVE.get().replaceAll("<message>", args.get(1)));
					} else {
						source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
					}
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandMsgPlayer(final EPlayer player, final EPlayer receive, final String message) {
		receive.sendMessage(player.replaceVariable(EEMessages.MSG_PLAYER_RECEIVE.get()
				.replaceAll("<message>", message))
			.toBuilder()
			.onHover(TextActions.showText(player.replaceVariable(EEMessages.MSG_PLAYER_RECEIVE_HOVER.get())))
			.onClick(TextActions.suggestCommand("/msg " + player.getName()))
			.build());

		player.sendMessage(receive.replaceVariable(EEMessages.MSG_PLAYER_SEND.get()
						.replaceAll("<message>", message))
					.toBuilder()
					.onHover(TextActions.showText(receive.replaceVariable(EEMessages.MSG_PLAYER_SEND_HOVER.get())))
					.onClick(TextActions.suggestCommand("/msg " + receive.getName()))
					.build());
		
		receive.setReplyTo(player.getIdentifier());
		player.setReplyTo(receive.getIdentifier());
		return true;
	}
	
	/*
	 * L
	 */
	public boolean commandMsgConsole(final CommandSource player, final EPlayer receive, final String message) {
		player.sendMessage(receive.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE.get()
				.replaceAll("<message>", message))
			.toBuilder()
			.onHover(TextActions.showText(receive.replaceVariable(EEMessages.REPLY_PLAYER_RECEIVE_HOVER.get())))
			.onClick(TextActions.suggestCommand("/msg " + receive.getName()))
			.build());

		receive.sendMessage(EChat.of(EEMessages.REPLY_CONSOLE_SEND.get()
						.replaceAll("<message>", message))
					.toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.REPLY_CONSOLE_SEND_HOVER.get())))
					.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE))
					.build());
		
		receive.setReplyTo(player.getIdentifier());
		this.plugin.getManagerServices().getEssentials().getConsole().setReplyTo(receive.getIdentifier());
		return true;
	}
	
	public boolean commandMsgConsole(final EPlayer player, final CommandSource receive, final String message) {
		receive.sendMessage(player.replaceVariable(EEMessages.REPLY_CONSOLE_RECEIVE.get()
				.replaceAll("<message>", message))
			.toBuilder()
			.onHover(TextActions.showText(player.replaceVariable(EEMessages.REPLY_CONSOLE_RECEIVE_HOVER.get())))
			.onClick(TextActions.suggestCommand("/msg " + EEMsg.CONSOLE))
			.build());

		player.sendMessage(EChat.of(EEMessages.REPLY_PLAYER_SEND.get()
						.replaceAll("<message>", message))
					.toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.REPLY_PLAYER_SEND_HOVER.get())))
					.onClick(TextActions.suggestCommand("/msg " + receive.getName()))
					.build());
		return true;
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
						.onClick(TextActions.suggestCommand("/msg console"))
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