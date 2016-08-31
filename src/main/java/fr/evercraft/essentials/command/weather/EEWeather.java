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
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.sponge.UtilsTick;

public class EEWeather extends ECommand<EverEssentials> {

	public EEWeather(final EverEssentials plugin) {
		super(plugin, "weather");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WEATHER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WEATHER_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " ")
				.append(Text.of("<"))
				.append(Text.builder("sun").onClick(TextActions.suggestCommand("/" + this.getName() + " sun")).build())
				.append(Text.of("|"))
				.append(Text.builder("rain").onClick(TextActions.suggestCommand("/" + this.getName() + " rain")).build())
				.append(Text.of("|"))
				.append(Text.builder("storm").onClick(TextActions.suggestCommand("/" + this.getName() + " storm")).build())
				.append(Text.of("> [" + EAMessages.ARGS_WORLD.get() + "] [" + EAMessages.ARGS_MINUTES.get() + "]"))
				.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
				.color(TextColors.RED)
				.build();
	}

	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("sun");
			suggests.add("rain");
			suggests.add("storm");
		} else if (args.size() == 2) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
					if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
						suggests.add(world.getName());
					}
				}
			}
		} else if (args.size() == 3) {
			suggests.add("60");
		}
		return suggests;
	}

	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			// Si la source a un monde
			if (source instanceof Locatable) {
				resultat = this.commandWeather(source, getWeather(args.get(0)), ((Locatable) source).getWorld());
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// On connais le joueur
		} else if (args.size() == 2) {
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(1));
			// Si le monde existe
			if (world.isPresent()) {
				resultat = this.commandWeather(source, getWeather(args.get(0)), world.get());
			} else {
				try {
					int time = Integer.parseInt(args.get(1));
					// Si la source a un monde
					if (source instanceof Locatable) {
						resultat = this.commandWeather(source, getWeather(args.get(0)), ((Locatable) source).getWorld(), time);
					// La source n'est pas un joueur
					} else {
						source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
					}
				} catch (NumberFormatException e) {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
							.replaceAll("<world>", args.get(1))));
				}
			}
		// On connais le joueur
		} else if (args.size() == 3) {
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(1));
			// Si le monde existe
			if (world.isPresent()) {
				try {
					resultat = this.commandWeather(source, getWeather(args.get(0)), world.get(), Integer.parseInt(args.get(2)));
				} catch (NumberFormatException e) {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
							.replaceAll("<number>", args.get(2))));
				}
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
						.replaceAll("<world>", args.get(1))));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		
		return resultat;
	}

	private boolean commandWeather(final CommandSource player, final Optional<Weather> weather, final World world) {
		if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world)) {
			if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {			
				if (weather.isPresent()) {
					world.setWeather(weather.get());
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + getMessage(weather.get())
								.replaceAll("<world>", world.getName())
								.replaceAll("<weather>", weather.get().getName())));
					return true;
				} else {
					player.sendMessage(this.help(player));
				}
			} else {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WEATHER_ERROR.get()));
			}
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.NO_PERMISSION_WORLD.get()
					.replaceAll("<world>", world.getName())));
		}
		return false;
	}
	
	private boolean commandWeather(final CommandSource player, final Optional<Weather> weather, final World world, final int duration) {
		if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world)) {
			if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {			
				if (weather.isPresent()) {
					world.setWeather(weather.get(), UtilsTick.parseMinutes(duration));
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + getMessageDuration(weather.get())
								.replaceAll("<world>", world.getName())
								.replaceAll("<duration>", String.valueOf(duration))
								.replaceAll("<weather>", weather.get().getName())));
				} else {
					player.sendMessage(this.help(player));
				}
			} else {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WEATHER_ERROR.get()));
			}
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.NO_PERMISSION_WORLD.get()
					.replaceAll("<world>", world.getName())));
		}
		return true;
	}
	
	private Optional<Weather> getWeather(final String weather_name) {
		Weather weather = null;
		if (weather_name.equalsIgnoreCase("sun")) {
			weather = Weathers.CLEAR;
		} else if (weather_name.equalsIgnoreCase("rain")) {
			weather = Weathers.RAIN;
		} else if (weather_name.equalsIgnoreCase("storm")) {
			weather = Weathers.THUNDER_STORM;
		}
		return Optional.ofNullable(weather);
	}
	
	private String getMessage(final Weather weather) {
		String message = null;
		if (weather.equals(Weathers.RAIN)) {
			message = EEMessages.WEATHER_RAIN.get();
		} else if (weather.equals(Weathers.THUNDER_STORM)) {
			message = EEMessages.WEATHER_STORM.get();
		} else {
			message = EEMessages.WEATHER_SUN.get();
		}
		return message;
	}
	
	private String getMessageDuration(final Weather weather) {
		String message = null;
		if (weather.equals(Weathers.RAIN)) {
			message = EEMessages.WEATHER_RAIN_DURATION.get();
		} else if (weather.equals(Weathers.THUNDER_STORM)) {
			message = EEMessages.WEATHER_STORM_DURATION.get();
		} else {
			message = EEMessages.WEATHER_SUN_DURATION.get();
		}
		return message;
	}
}
