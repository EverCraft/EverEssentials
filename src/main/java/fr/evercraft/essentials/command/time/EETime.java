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
package fr.evercraft.essentials.command.time;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EETime extends ECommand<EverEssentials> {
	
	private static final int MAX_TIME = 24000;
	private static final int DIFF_TIME = -6000;
	private static final int DIFF_HOURS_TIME = 1000;
	private static final double DIFF_MINUTES_TIME = 1000d/60d;
	private static final DecimalFormat FORMAT = new DecimalFormat("00");
	
	private static final long TIME_DAY = 0;
	private static final long TIME_NIGHT = 14000;
	private static final long TIME_DAWN = 23000;

	public EETime(final EverEssentials plugin) {
		super(plugin, "time");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TIME.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TIME_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " ")
				.append(Text.of("["))
				.append(Text.builder("day").onClick(TextActions.suggestCommand("/" + this.getName() + " day")).build())
				.append(Text.of("|"))
				.append(Text.builder("night").onClick(TextActions.suggestCommand("/" + this.getName() + " night")).build())
				.append(Text.of("|"))
				.append(Text.builder("dawn").onClick(TextActions.suggestCommand("/" + this.getName() + " dawn")).build())
				.append(Text.of("|"))
				.append(Text.builder("17:30").onClick(TextActions.suggestCommand("/" + this.getName() + " 17:30")).build())
				.append(Text.of("|"))
				.append(Text.builder("4000").onClick(TextActions.suggestCommand("/" + this.getName() + " 4000")).build())
				.append(Text.of("] [" + EAMessages.ARGS_WORLD.getString() + "|*]"))
				.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
				.color(TextColors.RED)
				.build();
	}

	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("day", "night", "dawn", "17:30", "4000");
		} else if (args.size() == 2) {
			List<String> suggests = new ArrayList<String>();
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getEssentials().hasPermissionWorld(source, world)) {
					if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
						suggests.add(world.getName());
					}
				}
			}
			suggests.add("*");
			return suggests;
		}
		return Arrays.asList();
	}

	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandTime((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// On connais le joueur
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof Locatable) {
				return this.commandTimeSet(source, parseTime(args.get(0)), ((Locatable) source).getWorld());
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// On connais le joueur
		} else if (args.size() == 2) {
			if (args.get(1).equals("*")){
				return this.commandTimeSetAll(source, parseTime(args.get(0)));
			} else {
				Optional<World> world = this.plugin.getEServer().getWorld(args.get(1));
				// Si le monde existe
				if (world.isPresent()) {
					return this.commandTimeSet(source, parseTime(args.get(0)), world.get());
				} else {
					EAMessages.WORLD_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{world}", args.get(1))
						.sendTo(source);
				}
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandTime(final EPlayer player) {
		EEMessages.TIME_INFORMATION.sender()
			.replace("{world}", player.getWorld().getName())
			.replace("{hours}", this.getTime(player.getWorld().getProperties().getWorldTime()))
			.replace("{ticks}", String.valueOf(player.getWorld().getProperties().getWorldTime()))
			.sendTo(player);
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandTimeSet(final CommandSource player, final Optional<Long> time, final World world) {
		if (!this.plugin.getEssentials().hasPermissionWorld(player, world)) {
			EAMessages.NO_PERMISSION_WORLD.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", world.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!time.isPresent()) {
			EEMessages.TIME_ERROR.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		this.setWorldTime(world.getProperties(), time.get());
		EEMessages.TIME_SET_WORLD.sender()
			.replace("{world}", world.getName())
			.replace("{hours}", this.getTime(time.get()))
			.replace("{ticks}", String.valueOf(time))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandTimeSetAll(final CommandSource player, final Optional<Long> time) {
		if (!time.isPresent()) {
			EEMessages.TIME_ERROR.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		for (World world : this.plugin.getEServer().getWorlds()) {
			if (this.plugin.getEssentials().hasPermissionWorld(player, world)) {
				if (world.getProperties().getDimensionType().equals(DimensionTypes.OVERWORLD)) {
					setWorldTime(world.getProperties(), time.get());
				}
			}
		}
		EEMessages.TIME_SET_ALL_WORLD.sender()
			.replace("{hours}", this.getTime(time.get()))
			.replace("{ticks}", String.valueOf(time.get()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private void setWorldTime(WorldProperties world, long time) {
		world.setWorldTime(((long) (Math.ceil(world.getTotalTime()/(double) MAX_TIME) * MAX_TIME)) + time);
	}
	
	private Optional<Long> parseTime(final String arg){
		if (arg.equalsIgnoreCase("day")) {
			return Optional.of(TIME_DAY);
		} else if (arg.equalsIgnoreCase("night")) {
			return Optional.of(TIME_NIGHT);
		} else if (arg.equalsIgnoreCase("dawn")) {
			return Optional.of(TIME_DAWN);
		} else if (arg.contains(":")) {
			String args[] = arg.split(":", 2);
			if (args.length == 2) {
				return this.parseTime(args[0], args[1]);
			}
		} else if (arg.contains("h")) {
			String args[] = arg.split("h", 2);
			if (args.length == 2) {
				return this.parseTime(args[0], args[1]);
			}
		} else {
			try {
				long ticks = Integer.parseInt(arg);
				return Optional.of(ticks);
			} catch (NumberFormatException e) {}
		}
		
		return Optional.empty();
	}	
	
	private Optional<Long> parseTime(final String name_hours, final String name_minutes){
		try {
			Integer hours = Integer.parseInt(name_hours);
			Integer minutes = Integer.parseInt(name_minutes);
			if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
				Double time = (hours * DIFF_HOURS_TIME) + (minutes * DIFF_MINUTES_TIME);
				time = time + DIFF_TIME;
				if (time < 0) {
					time = MAX_TIME + time;
				}
				return Optional.of(time.longValue());
			}
		} catch (NumberFormatException e) {}
		return Optional.empty();
	}
	
	private Text getTime(long ticks){
		ticks = ticks - DIFF_TIME;
		ticks = ticks % MAX_TIME;
		
		double minutes = ticks % DIFF_HOURS_TIME;
		double hours = Math.floor((ticks - minutes) / DIFF_HOURS_TIME);
		minutes = Math.floor(minutes / DIFF_MINUTES_TIME);
		
		return EEMessages.TIME_FORMAT.getFormat().toText(
				"{hours}", FORMAT.format(hours),
				"{minutes}", FORMAT.format(minutes));
	}
}
