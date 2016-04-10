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
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEMe extends ECommand<EverEssentials> {
	
	public EEMe(final EverEssentials plugin) {
        super(plugin ,"me", "action", "describe");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("ME"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("ME_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		Text help = Text.builder("/me <message>").onClick(TextActions.suggestCommand("/me "))
					.color(TextColors.RED).build();
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() > 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandMe((EPlayer) source, getMessage(args));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandMe(final EPlayer player, String message) {
		player.broadcast(this.plugin.getMessages().getMessage("ME_PREFIX").replaceAll("<player>", player.getName()) + message);
		return true;
	}
	
	public String getMessage(final List<String> args){
		return String.join(" ", args);
	}
}