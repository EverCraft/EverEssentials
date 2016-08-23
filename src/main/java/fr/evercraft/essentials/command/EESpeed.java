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
package fr.evercraft.essentials.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EESpeed extends ECommand<EverEssentials> {
	
	private static final double MAX_SPEED = 10;
	private static final double MIN_SPEED = 0.001;
	
	public EESpeed(final EverEssentials plugin) {
		super(plugin, "speed");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPEED.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SPEED_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.SPEED_WALK.get()) && source.hasPermission(EEPermissions.SPEED_FLY.get())) {
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_SPEED.get() + "] [walk|fly] [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		} else if (source.hasPermission(EEPermissions.SPEED_FLY.get())) {
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_SPEED.get() + "] [fly] [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		} else if (source.hasPermission(EEPermissions.SPEED_WALK.get())) {
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_SPEED.get() + "] [walk] [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("1");
		} else if (args.size() == 2) {
			if (source.hasPermission(EEPermissions.SPEED_WALK.get())) {
				suggests.add("walk");
			}
			if (source.hasPermission(EEPermissions.SPEED_FLY.get())) {
				suggests.add("fly");
			}
		} else if (args.size() == 3 && source.hasPermission(EEPermissions.SPEED_OTHERS.get())) {
			suggests = null;
		}
		return suggests;
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandSpeedInfo((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<Double> optSpeed = getSpeed(args.get(0));
				if (optSpeed.isPresent()) {
					resultat = commandSpeed((EPlayer) source, optSpeed.get());
				} else {
					source.sendMessage(EChat.of(EAMessages.IS_NOT_NUMBER.get().replaceAll("<number>", args.get(0))));
				}
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if (args.size() == 2) {
			if (source instanceof EPlayer) {
				Optional<Double> optSpeed = getSpeed(args.get(0));
				if (optSpeed.isPresent()) {
					if (args.get(1).equalsIgnoreCase("walk")) {
						resultat = commandSpeedWalk((EPlayer) source, optSpeed.get());
					} else if (args.get(1).equalsIgnoreCase("fly")) {
						resultat = commandSpeedFly((EPlayer) source, optSpeed.get());
					} else {
						source.sendMessage(help(source));
					}
				} else {
					source.sendMessage(EChat.of(EAMessages.IS_NOT_NUMBER.get().replaceAll("<number>", args.get(0))));
				}
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if (args.size() == 3) {
			if (source.hasPermission(EEPermissions.SPEED_OTHERS.get())) {
				Optional<Double> optSpeed = getSpeed(args.get(0));
				if (optSpeed.isPresent()) {
					Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(2));
					if (optPlayer.isPresent()){
						if (args.get(1).equalsIgnoreCase("walk")) {
							resultat = commandSpeedWalkOthers(source, optPlayer.get(), optSpeed.get());
						} else if (args.get(1).equalsIgnoreCase("fly")) {
							resultat = commandSpeedFlyOthers(source, optPlayer.get(), optSpeed.get());
						} else {
							source.sendMessage(help(source));
						}
					} else {
						source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
					}
				} else {
					source.sendMessage(EChat.of(EAMessages.IS_NOT_NUMBER.get().replaceAll("<number>", args.get(0))));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandSpeedInfo(EPlayer player) {
		if (player.isFlying()) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPEED_INFO_FLY.get()
					.replaceAll("<speed>", UtilsDouble.round(player.getFlySpeed() / EPlayer.CONVERSION_FLY, 3).toString()));
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPEED_INFO_WALK.get()
					.replaceAll("<speed>", UtilsDouble.round(player.getWalkSpeed() / EPlayer.CONVERSION_WALF, 3).toString()));
		}
		return false;
	}

	private boolean commandSpeed(EPlayer player, Double speed) {
		if (player.isFlying()) {
			return commandSpeedFly(player, speed);
		} else {
			return commandSpeedWalk(player, speed);
		}
	}

	private boolean commandSpeedWalk(EPlayer player, Double speed) {
		if (player.hasPermission(EEPermissions.SPEED_WALK.get())) {
			player.setWalkSpeed(speed * EPlayer.CONVERSION_WALF);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPEED_PLAYER_WALK.get()
					.replaceAll("<speed>", speed.toString()));
			return true;
		} else {
			player.sendMessage(EAMessages.NO_PERMISSION.getText());
		}
		return false;
	}

	private boolean commandSpeedFly(EPlayer player, Double speed) {
		if (player.hasPermission(EEPermissions.SPEED_FLY.get())) {
			player.setFlySpeed(speed * EPlayer.CONVERSION_FLY);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPEED_PLAYER_FLY.get()
					.replaceAll("<speed>", speed.toString()));
			return true;
		} else {
			player.sendMessage(EAMessages.NO_PERMISSION.getText());
		}
		return false;
	}

	private boolean commandSpeedWalkOthers(CommandSource staff, EPlayer player, Double speed) {
		if (staff.hasPermission(EEPermissions.SPEED_WALK.get())) {
			player.setWalkSpeed(speed * EPlayer.CONVERSION_WALF);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPEED_OTHERS_PLAYER_WALK.get()
					.replaceAll("<speed>", speed.toString())
					.replaceAll("<staff>", staff.getName()));
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPEED_OTHERS_STAFF_WALK.get()
					.replaceAll("<speed>", speed.toString())
					.replaceAll("<player>", player.getName())));
			return true;
		} else {
			staff.sendMessage(EAMessages.NO_PERMISSION.getText());
		}
		return false;
	}

	private boolean commandSpeedFlyOthers(CommandSource staff, EPlayer player, Double speed) {
		if (staff.hasPermission(EEPermissions.SPEED_FLY.get())) {
			player.setFlySpeed(speed * EPlayer.CONVERSION_FLY);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPEED_OTHERS_PLAYER_FLY.get()
					.replaceAll("<speed>", speed.toString())
					.replaceAll("<staff>", staff.getName()));
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPEED_OTHERS_STAFF_FLY.get()
					.replaceAll("<speed>", speed.toString())
					.replaceAll("<player>", player.getName())));
			return true;
		} else {
			staff.sendMessage(EAMessages.NO_PERMISSION.getText());
		}
		return false;
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
