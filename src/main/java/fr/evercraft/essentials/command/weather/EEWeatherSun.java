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
package fr.evercraft.essentials.command.weather;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.command.ECommand;

public class EEWeatherSun extends ECommand<EverEssentials> {

	public EEWeatherSun(final EverEssentials plugin) {
		super(plugin, "sun");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WEATHER.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.WEATHER_SUN_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/sun").onClick(TextActions.suggestCommand("/sun")).color(TextColors.RED).build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			resultat = commandWeatherSun(source);
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	public boolean commandWeatherSun(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "weather sun");
		return false;
	}
}
