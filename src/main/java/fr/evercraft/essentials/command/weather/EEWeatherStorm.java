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
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEWeatherStorm extends ECommand<EverEssentials> {

	public EEWeatherStorm(final EverEssentials plugin) {
		super(plugin, "storm");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WEATHER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WEATHER_STORM_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
					suggests.add(world.getName());
				}
			}
		} else if (args.size() == 3) {
			suggests.add("60");
		}
		return suggests;
	}

	@Override
	public boolean execute(CommandSource source, final List<String> args) throws CommandException {
		// Erreur : Context 
		if(source instanceof EPlayer) {
			source = ((EPlayer) source).get();
		}
		
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			resultat = this.commandWeatherStorm(source, "");
		} else if (args.size() == 1) {
			resultat = this.commandWeatherStorm(source, "\"" + args.get(0) + "\"");
		} else if (args.size() == 2) {
			resultat = this.commandWeatherStorm(source, "\"" + args.get(0) + "\" \"" + args.get(1) + "\"");
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	public boolean commandWeatherStorm(final CommandSource player, final String arg) {
		this.plugin.getGame().getCommandManager().process(player, "weather storm " + arg);
		return false;
	}
}
