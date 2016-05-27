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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEBroadcast extends ECommand<EverEssentials> {
	
	public EEBroadcast(final EverEssentials plugin) {
        super(plugin ,"broadcast", "bcast");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BROADCAST.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("BROADCAST_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		Text help = Text.builder("/broadcast <message...>").onClick(TextActions.suggestCommand("/broadcast "))
					.color(TextColors.RED).build();
		return help;
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
		if(args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandBroadcastPlayer((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				resultat = commandBroadcastConsole(source, args.get(0));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandBroadcastPlayer(final EPlayer player, String message) {
		
		this.plugin.getEServer().getBroadcastChannel().send(EChat.of(this.plugin.getMessages().getMessage("BROADCAST_PREFIX_PLAYER").replaceAll("<player>", player.getName()) + message));
		return true;
	}
	
	public boolean commandBroadcastConsole(final CommandSource player, String message) {
		this.plugin.getEServer().getBroadcastChannel().send(EChat.of(this.plugin.getMessages().getMessage("BROADCAST_PREFIX_CONSOLE") + message));
		return true;
	}
}