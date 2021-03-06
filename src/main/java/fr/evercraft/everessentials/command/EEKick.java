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
package fr.evercraft.everessentials.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEKick extends ECommand<EverEssentials> {
	
	public EEKick(final EverEssentials plugin) {
        super(plugin, "kick");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.KICK.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.KICK_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + "> [" + EAMessages.ARGS_REASON.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			List<String> suggests = new ArrayList<String>();
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if (!player.equals(source) && !player.hasPermission(EEPermissions.KICK_BYPASS.get())) {
					suggests.add(player.getName());
				}
			}
			return suggests;
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
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()) {
				return this.commandKick(source, optPlayer.get(), EEMessages.KICK_DEFAULT_REASON.getText());
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{player}", args.get(0))
					.sendTo(source);
			}
			
		} else if (args.size() == 2) {
			
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()) {
				return this.commandKick(source, optPlayer.get(), EChat.of(args.get(1)));
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{player}", args.get(0))
					.sendTo(source);
			}
		
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandKick(final CommandSource staff, final EPlayer player, final Text message) throws CommandException {
		// Le joueur a la permission bypass
		if(player.hasPermission(EEPermissions.KICK_BYPASS.get())) {
			EEMessages.KICK_BYPASS.sender()
				.replace("{player}", player.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		player.kick(EEMessages.KICK_MESSAGE.getFormat().toText(
			"{staff}", staff.getName(),
			"{reason}", message));
		return CompletableFuture.completedFuture(true);
	}
}
