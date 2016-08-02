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
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;

public class EEStop extends ECommand<EverEssentials> {
	
	public EEStop(final EverEssentials plugin) {
        super(plugin ,"stop");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.STOP.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.STOP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_REASON.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args, String arg) throws CommandException {
		return execute(source, Arrays.asList(arg));
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
		if(args.size() == 0) {
			resultat = commandStop(source);
		} else {
			resultat = commandStop(source, args.get(0));
		}
		return resultat;
	}

	public boolean commandStop(final CommandSource player) {
		this.plugin.getLogger().info("Server shutdown by '" + player.getName() + "'");
		if(player instanceof ConsoleSource) {
			this.plugin.getGame().getServer().shutdown(
				EChat.of(this.plugin.getChat().replaceGlobal(
					EEMessages.STOP_CONSOLE_MESSAGE.get()
							.replaceAll("<staff>", player.getName()))));
		} else {
			this.plugin.getGame().getServer().shutdown(
					EChat.of(this.plugin.getChat().replaceGlobal(
						EEMessages.STOP_MESSAGE.get()
								.replaceAll("<staff>", player.getName()))));
		}
		return true;
	}
	
	public boolean commandStop(final CommandSource player, String message) {
		this.plugin.getLogger().info("Server shutdown by '" + player.getName() + "' (reason='" + message + "')");
		if(player instanceof ConsoleSource) {
			this.plugin.getGame().getServer().shutdown(
				EChat.of(this.plugin.getChat().replaceGlobal(
					EEMessages.STOP_CONSOLE_MESSAGE_REASON.get()
							.replaceAll("<staff>", player.getName())
							.replaceAll("<reason>", this.plugin.getChat().replace(message)))));
		} else {
			this.plugin.getGame().getServer().shutdown(
				EChat.of(this.plugin.getChat().replaceGlobal(
					EEMessages.STOP_MESSAGE_REASON.get()
							.replaceAll("<staff>", player.getName())
							.replaceAll("<reason>", this.plugin.getChat().replace(message)))));
		}		
		return true;
	}
}