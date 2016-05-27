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

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.ECommand;

public class EEWorldsEnd extends ECommand<EverEssentials> {
	
	public EEWorldsEnd(final EverEssentials plugin) {
		super(plugin, "end");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDS.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("WORLDS_END_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/end").onClick(TextActions.suggestCommand("/end")).color(TextColors.RED).build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = null;
		if(!(args.size() == 1 && source.hasPermission(EEPermissions.WORLDS_OTHERS.get()))){
			suggests = new ArrayList<String>();
		}
		return suggests;
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			resultat = commandEnd(source);
		} else if (args.size() == 1){
			resultat = commandEndOthers(source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	public boolean commandEnd(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "worlds DIM1");
		return false;
	}
	
	public boolean commandEndOthers(final CommandSource player, final String arg) {
		this.plugin.getGame().getCommandManager().process(player, "worlds DIM1 "+ arg);
		return false;
	}
}
