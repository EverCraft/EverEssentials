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
import java.util.Optional;

import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsTick;

public class EEWeather extends ECommand<EverEssentials> {

	public EEWeather(final EverEssentials plugin) {
		super(plugin, "weather");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WEATHER.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.WEATHER_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/weather ").onClick(TextActions.suggestCommand("/weather "))
				.append(Text.of("<"))
				.append(Text.builder("sun").onClick(TextActions.suggestCommand("/weather sun")).build())
				.append(Text.of("|"))
				.append(Text.builder("rain").onClick(TextActions.suggestCommand("/weather rain")).build())
				.append(Text.of("|"))
				.append(Text.builder("storm").onClick(TextActions.suggestCommand("/weather storm")).build())
				.append(Text.of("> [monde [minutes]]"))
				.color(TextColors.RED).build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("sun");
			suggests.add("rain");
			suggests.add("storm");
		} else if (args.size() == 2) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
					suggests.add(world.getName());
				}
			}
		} else if (args.size() == 3) {
			suggests.add("10");
		}
		return suggests;
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandWeather(source, getWeather(args.get(0)), ((EPlayer) source).getWorld());
			} else if (source instanceof CommandBlock) {
				resultat = commandWeather(source, getWeather(args.get(0)), ((CommandBlock) source).getWorld());
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if (args.size() == 2) {
			Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(1));
			// Si le monde existe
			if (optWorld.isPresent()) {
				resultat = commandWeather(source, getWeather(args.get(0)), optWorld.get());
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()));
			}
		// On connais le joueur
		} else if (args.size() == 3) {
			Optional<World> optWorld = this.plugin.getEServer().getWorld(args.get(1));
			// Si le monde existe
			if (optWorld.isPresent()) {
				resultat = commandWeatherDuration(source, getWeather(args.get(0)), optWorld.get(), args.get(2));
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	public boolean commandWeather(final CommandSource player, final Optional<Weather> weather, final World world) {
		if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {			
			if(weather.isPresent()) {
				world.setWeather(weather.get());
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + getMessage(weather.get())
							.replaceAll("<world>", world.getName())
							.replaceAll("<weather>", weather.get().getName())));
			} else {
				player.sendMessage(help(player));
			}
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WEATHER_ERROR.get()));
		}
		return false;
	}
	
	public boolean commandWeatherDuration(final CommandSource player, final Optional<Weather> weather, final World world, final String name_duration) {
		try {
			int duration = Integer.parseInt(name_duration);
			if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {			
				if(weather.isPresent()) {
					world.setWeather(weather.get(), UtilsTick.parseMinutes(duration));
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + getMessageDuration(weather.get())
								.replaceAll("<world>", world.getName())
								.replaceAll("<duration>", String.valueOf(duration))
								.replaceAll("<weather>", weather.get().getName())));
				} else {
					player.sendMessage(help(player));
				}
			} else {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WEATHER_ERROR.get()));
			}
		} catch (NumberFormatException e) {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", name_duration)));
		}
		return false;
	}
	
	public Optional<Weather> getWeather(final String weather_name) {
		Weather weather = null;
		if(weather_name.equalsIgnoreCase("sun")) {
			weather = Weathers.CLEAR;
		} else if(weather_name.equalsIgnoreCase("rain")) {
			weather = Weathers.RAIN;
		} else if(weather_name.equalsIgnoreCase("storm")) {
			weather = Weathers.THUNDER_STORM;
		}
		return Optional.ofNullable(weather);
	}
	
	public String getMessage(final Weather weather) {
		String message = null;
		if(weather.equals(Weathers.RAIN)) {
			message = EEMessages.WEATHER_RAIN.get();
		} else if(weather.equals(Weathers.THUNDER_STORM)) {
			message = EEMessages.WEATHER_STORM.get();
		} else {
			message = EEMessages.WEATHER_SUN.get();
		}
		return message;
	}
	
	public String getMessageDuration(final Weather weather) {
		String message = null;
		if(weather.equals(Weathers.RAIN)) {
			message = EEMessages.WEATHER_RAIN_DURATION.get();
		} else if(weather.equals(Weathers.THUNDER_STORM)) {
			message = EEMessages.WEATHER_STORM_DURATION.get();
		} else {
			message = EEMessages.WEATHER_SUN_DURATION.get();
		}
		return message;
	}
}
