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
package fr.evercraft.everessentials.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EESpeed extends ECommand<EverEssentials> {
	
	private static final String WALK = "walk";
	private static final String FLY = "fly";
	
	private static final double MAX_SPEED = 10;
	private static final double MIN_SPEED = 0.001;
	
	public EESpeed(final EverEssentials plugin) {
		super(plugin, "speed");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPEED.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SPEED_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		boolean walk = source.hasPermission(EEPermissions.SPEED_WALK.get());
		boolean fly = source.hasPermission(EEPermissions.SPEED_FLY.get());
		
		Builder build = Text.builder("/" + this.getName());
		
		if (walk || fly) {
			if (walk && fly) {
				build.append(Text.of(" [" + EAMessages.ARGS_SPEED.getString() + "] [walk|fly]"));
			} else if (fly) {
				build.append(Text.of(" [" + EAMessages.ARGS_SPEED.getString() + "] [fly]"));
			} else if (walk) {
				build.append(Text.of(" [" + EAMessages.ARGS_SPEED.getString() + "] [walk]"));
			}
			
			if(source.hasPermission(EEPermissions.SPEED_OTHERS.get())) {
				build.append(Text.of(" [" + EAMessages.ARGS_USER.getString() + "]"));
			}
		}
		
		return build.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("1", String.valueOf(EESpeed.MAX_SPEED));
		} else if (args.size() == 2) {
			List<String> suggests = new ArrayList<String>();
			if (source.hasPermission(EEPermissions.SPEED_WALK.get())) {
				suggests.add("walk");
			}
			if (source.hasPermission(EEPermissions.SPEED_FLY.get())) {
				suggests.add("fly");
			}
			return suggests;
		} else if (args.size() == 3 && source.hasPermission(EEPermissions.SPEED_OTHERS.get())) {
			return this.getAllUsers(args.get(2), source);
		}
		return Arrays.asList();
	}

	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandSpeedInfo((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<Double> speed = this.getSpeed(args.get(0));
				if (speed.isPresent()) {
					return this.commandSpeed((EPlayer) source, speed.get());
				} else {
					EAMessages.IS_NOT_NUMBER.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{number}", args.get(0))
						.sendTo(source);
				}
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 2) {
			
			if (source instanceof EPlayer) {
				Optional<Double> speed = this.getSpeed(args.get(0));
				if (speed.isPresent()) {
					if (args.get(1).equalsIgnoreCase(EESpeed.WALK)) {
						return this.commandSpeedWalk((EPlayer) source, speed.get());
					} else if (args.get(1).equalsIgnoreCase(EESpeed.FLY)) {
						return this.commandSpeedFly((EPlayer) source, speed.get());
					} else {
						source.sendMessage(this.help(source));
					}
				} else {
					EAMessages.IS_NOT_NUMBER.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{number}", args.get(0))
						.sendTo(source);
				}
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 3) {
			
			if (source.hasPermission(EEPermissions.SPEED_OTHERS.get())) {
				Optional<Double> speed = this.getSpeed(args.get(0));
				if (speed.isPresent()) {
					Optional<EUser> player = this.plugin.getEServer().getEUser(args.get(2));
					if (player.isPresent()){
						if (args.get(1).equalsIgnoreCase(EESpeed.WALK)) {
							return this.commandSpeedWalkOthers(source, player.get(), speed.get());
						} else if (args.get(1).equalsIgnoreCase(EESpeed.FLY)) {
							return this.commandSpeedFlyOthers(source, player.get(), speed.get());
						} else {
							source.sendMessage(this.help(source));
						}
					} else {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
							.replace("{player}", args.get(2))
							.sendTo(source);
					}
				} else {
					EAMessages.IS_NOT_NUMBER.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{number}", args.get(0))
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSpeedInfo(final EPlayer player) {
		if (player.isFlying()) {
			EEMessages.SPEED_INFO_FLY.sender()
				.replace("{speed}", UtilsDouble.round(player.getFlySpeed() / EPlayer.CONVERSION_FLY, 3).toString())
				.sendTo(player);
		} else {
			EEMessages.SPEED_INFO_WALK.sender()
				.replace("{speed}", UtilsDouble.round(player.getWalkSpeed() / EPlayer.CONVERSION_WALF, 3).toString())
				.sendTo(player);
		}
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandSpeed(final EPlayer player, final Double speed) {
		if (player.isFlying()) {
			return this.commandSpeedFly(player, speed);
		} else {
			return this.commandSpeedWalk(player, speed);
		}
	}

	private CompletableFuture<Boolean> commandSpeedWalk(final EPlayer player, final Double speed) {
		// Le joueur n'a pas la permission
		if (!player.hasPermission(EEPermissions.SPEED_WALK.get())) {
			EAMessages.NO_PERMISSION.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		player.setWalkSpeed(speed * EPlayer.CONVERSION_WALF);
		EEMessages.SPEED_PLAYER_WALK.sender()
			.replace("{speed}", speed.toString())
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandSpeedFly(final EPlayer player, final Double speed) {
		// Le joueur n'a pas la permission
		if (!player.hasPermission(EEPermissions.SPEED_FLY.get())) {
			EAMessages.NO_PERMISSION.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		player.setFlySpeed(speed * EPlayer.CONVERSION_FLY);
		EEMessages.SPEED_PLAYER_FLY.sender()
			.replace("{speed}", speed.toString())
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandSpeedWalkOthers(final CommandSource staff, final EUser user, final Double speed) {
		// Le joueur n'a pas la permission
		if (!staff.hasPermission(EEPermissions.SPEED_WALK.get())) {
			EAMessages.NO_PERMISSION.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		user.setWalkSpeed(speed * EPlayer.CONVERSION_WALF);
		EEMessages.SPEED_OTHERS_STAFF_WALK.sender()
			.replace("{speed}", speed.toString())
			.replace("{player}", user.getName())
			.sendTo(staff);
		
		user.getPlayer().ifPresent(player -> EEMessages.SPEED_OTHERS_PLAYER_WALK.sender()
				.replace("{speed}", speed.toString())
				.replace("{staff}", staff.getName())
				.sendTo(player));
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandSpeedFlyOthers(CommandSource staff, EUser user, Double speed) {
		// Le joueur n'a pas la permission
		if (!staff.hasPermission(EEPermissions.SPEED_FLY.get())) {
			EAMessages.NO_PERMISSION.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		user.setFlySpeed(speed * EPlayer.CONVERSION_FLY);
		EEMessages.SPEED_OTHERS_STAFF_FLY.sender()
			.replace("{speed}", speed.toString())
			.replace("{player}", user.getName())
			.sendTo(staff);

		user.getPlayer().ifPresent(player -> EEMessages.SPEED_OTHERS_PLAYER_FLY.sender()
				.replace("{speed}", speed.toString())
				.replace("{staff}", staff.getName())
				.sendTo(player));
		return CompletableFuture.completedFuture(true);
	}
	
	private Optional<Double> getSpeed(String arg) {
		try {
			double speed = Double.parseDouble(arg);
			speed = UtilsDouble.round(speed, 3);
			speed = Math.min(MAX_SPEED, speed);
			speed = Math.max(MIN_SPEED, speed);
			return Optional.of(speed);
		} catch (NumberFormatException e) {}
		return Optional.empty();
	}
}
