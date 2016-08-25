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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class EEBroadcast extends ECommand<EverEssentials> {
	
	public EEBroadcast(final EverEssentials plugin) {
        super(plugin ,"broadcast", "bcast");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BROADCAST.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.BROADCAST_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		Text help = Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_MESSAGE.get() + ">")
						.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
						.color(TextColors.RED)
						.build();
		return help;
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	@Override
	protected List<String> getArg(final String arg){
		if (arg.isEmpty()) {
			return Arrays.asList();
		}
		return Arrays.asList(arg);
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if (args.size() == 1) {
			resultat = this.commandBroadcast(args.get(0));
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandBroadcast(final String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EChat.of(EEMessages.BROADCAST_MESSAGE.get()
				.replaceAll("<message>", message)));
		return true;
	}
}