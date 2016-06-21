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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEFly extends ECommand<EverEssentials> {
	
	public EEFly(final EverEssentials plugin) {
        super(plugin, "fly");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.FLY.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.FLY_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		Text help;
		if(source.hasPermission(EEPermissions.FLY_OTHERS.get())){
			help = Text.builder("/fly").onClick(TextActions.suggestCommand("/fly "))
					.append(Text.builder(" [joueur [on|off]]").build())
					.color(TextColors.RED).build();
		} else {
			help = Text.builder("/fly").onClick(TextActions.suggestCommand("/fly"))
					.color(TextColors.RED).build();
		}
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source.hasPermission(EEPermissions.FLY_OTHERS.get())){
			suggests = null;
		} else if(args.size() == 2 && source.hasPermission(EEPermissions.FLY_OTHERS.get())){
			suggests.add("on");
			suggests.add("off");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandFly((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.FLY_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandFlyOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		// On connais le joueur et si on doit lui activé ou lui désactivé le fly
		} else if(args.size() == 2) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.FLY_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					if(args.get(1).equalsIgnoreCase("on")) {
						resultat = commandFlyOthers(source, optPlayer.get(), true);
					} else if(args.get(1).equalsIgnoreCase("off")) {
						resultat = commandFlyOthers(source, optPlayer.get(), false);
					} else {
						source.sendMessage(help(source));
					}
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
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
	
	public boolean commandFly(final EPlayer player) {
		// Si le fly est déjà activé
		if(player.getAllowFlight()) {
			if(!player.getGameMode().equals(GameModes.CREATIVE)) {
				player.setAllowFlight(false);
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_DISABLE.getText()));
			} else {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_ERROR_CREATIVE.getText()));
			}
		// Fly est déjà désactivé
		} else {
			player.setAllowFlight(true);
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_ENABLE.getText()));
		}
		return true;
	}
	
	public boolean commandFlyOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			// Si le fly est déjà activé
			if(player.getAllowFlight()) {
				if(!player.getGameMode().equals(GameModes.CREATIVE)) {
					player.setAllowFlight(false);
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_PLAYER_DISABLE.get()
							.replaceAll("<staff>", staff.getName()));
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_STAFF_DISABLE.get()
							.replaceAll("<player>", player.getName())));
				} else {
					player.setAllowFlight(false);
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_ERROR_CREATIVE.get()
							.replaceAll("<player>", player.getName())));
				}
			// Fly est déjà désactivé
			} else {
				player.setAllowFlight(true);
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_PLAYER_ENABLE.get()
						.replaceAll("<staff>", staff.getName()));
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_STAFF_ENABLE.get()
						.replaceAll("<player>", player.getName())));
			}
			return true;
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
	}
	
	public boolean commandFlyOthers(final CommandSource staff, final EPlayer player, final boolean etat) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			boolean canfly = player.getAllowFlight();
			if(etat) {
				// Si le fly est déjà activé
				if(canfly){
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_STAFF_ENABLE_ERROR.get()
							.replaceAll("<player>", player.getName())));
				// Fly est désactivé
				} else {
					player.setAllowFlight(etat);
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_PLAYER_ENABLE.get()
							.replaceAll("<staff>", staff.getName()));
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_STAFF_ENABLE.get()
							.replaceAll("<player>", player.getName())));
					return true;
				}
			} else {
				if(canfly){
					if(!player.getGameMode().equals(GameModes.CREATIVE)) {
						player.setAllowFlight(etat);
						player.sendMessage(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_PLAYER_DISABLE.get()
								.replaceAll("<staff>", staff.getName()));
						staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_STAFF_DISABLE.get()
								.replaceAll("<player>", player.getName())));
						return true;
					} else {
						staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_ERROR_CREATIVE.get()
								.replaceAll("<player>", player.getName())));
					}
				// Fly est désactivé
				} else {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OTHERS_STAFF_DISABLE_ERROR.get()
							.replaceAll("<player>", player.getName())));
				}
			}
		// La source et le joueur sont identique
		} else {
			boolean canfly = player.getAllowFlight();
			if(etat) {
				// Si le fly est déjà activé
				if(canfly){
					player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_ENABLE_ERROR.getText()));
				// Fly est désactivé
				} else {
					player.setAllowFlight(etat);
					player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_ENABLE.getText()));
					return true;
				}
			} else {
				if(canfly){
					if(!player.getGameMode().equals(GameModes.CREATIVE)) {
						player.setAllowFlight(etat);
						player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_DISABLE.getText()));
						return true;
					} else {
						player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_ERROR_CREATIVE.getText()));
					}
				// Fly est désactivé
				} else {
					player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_PLAYER_DISABLE_ERROR.getText()));
				}
			}
		}
		return false;
	}
}
