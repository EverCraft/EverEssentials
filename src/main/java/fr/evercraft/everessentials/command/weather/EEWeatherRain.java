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
package fr.evercraft.everessentials.command.weather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEWeatherRain extends ECommand<EverEssentials> {

	public EEWeatherRain(final EverEssentials plugin) {
		super(plugin, "rain");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WEATHER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WEATHER_RAIN_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			List<String> suggests = new ArrayList<String>();
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
					suggests.add(world.getName());
				}
			}
			return suggests;
		} else if (args.size() == 3) {
			return Arrays.asList("60");
		}
		return Arrays.asList();
	}

	@Override
	public CompletableFuture<Boolean> execute(CommandSource source, final List<String> args) throws CommandException {
		// Erreur : Context 
		if(source instanceof EPlayer) {
			source = ((EPlayer) source).get();
		}
		
		if (args.size() == 0) {
			return this.commandWeatherRain(source, "");
		} else if (args.size() == 1){
			return this.commandWeatherRain(source, "\"" + args.get(0) + "\"");
		} else if (args.size() == 2){
			return this.commandWeatherRain(source, "\"" + args.get(0) + "\" \"" + args.get(1) + "\"");
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	public CompletableFuture<Boolean> commandWeatherRain(final CommandSource player, final String arg) {
		this.plugin.getGame().getCommandManager().process(player, "weather rain " + arg);
		return CompletableFuture.completedFuture(false);
	}
}
