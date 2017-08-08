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
package fr.evercraft.essentials.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

public class EEKickall extends ECommand<EverEssentials> {
	
	public EEKickall(final EverEssentials plugin) {
        super(plugin, "kickall");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.KICKALL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.KICKALL_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " {" + EAMessages.ARGS_REASON.getString() +"}")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	@Override
	protected List<String> getArg(final String arg) {
		if(!arg.isEmpty()) {
			return Arrays.asList(arg);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.commandKick(source, EChat.of(args.get(0)));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandKick(final CommandSource staff, final Text message) throws CommandException {
		Text raison = EEMessages.KICKALL_MESSAGE.getFormat().toText(
							"{staff}", staff.getName(),
							"{reason}", message);

		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if (!player.equals(staff) && !player.hasPermission(EEPermissions.KICK_BYPASS.get())) {
				player.kick(raison);
				return CompletableFuture.completedFuture(true);
			}
		}
		
		EEMessages.KICKALL_ERROR.sendTo(staff);
		return CompletableFuture.completedFuture(false);
	}
}
