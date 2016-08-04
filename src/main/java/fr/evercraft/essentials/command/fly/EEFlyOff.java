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
package fr.evercraft.essentials.command.fly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEFlyOff extends ESubCommand<EverEssentials> {
	public EEFlyOff(final EverEssentials plugin, final EEFly command) {
        super(plugin, command, "off");
    }
	
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.FLY_OFF_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(!(args.size() == 1 && source.hasPermission(EEPermissions.FLY_OTHERS.get()))){
			suggests = null;
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.FLY_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "]")
						.onClick(TextActions.suggestCommand("/" + this.getName()))
						.color(TextColors.RED)
						.build();
		} else {
			return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
		}
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0) {
			if(source instanceof EPlayer) {
				resultat = commandFlyOff((EPlayer) source);
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.FLY_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandFlyOffOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	public boolean commandFlyOff(final EPlayer player) {
		boolean fly = player.getAllowFlight();
		// Fly activé
		if(fly){
			if(!player.isCreative()){
				if(player.setAllowFlight(false)) {
					player.setFlying(false);
					player.teleportBottom();
					player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_OFF_PLAYER.getText()));
				} else {
					player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_OFF_PLAYER_CANCEL.getText()));
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_OFF_PLAYER_CREATIVE.getText()));
			}
		// Fly désactivé
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.FLY_OFF_PLAYER_ERROR.getText()));
		}
		return true;
	}
	
	public boolean commandFlyOffOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			boolean fly = player.getAllowFlight();
			// Fly activé
			if(fly){
				if(!player.isCreative()){
					if(player.setAllowFlight(false)) {
						player.setFlying(false);
						player.teleportBottom();
						player.sendMessage(EEMessages.PREFIX.get() + EEMessages.FLY_OFF_OTHERS_PLAYER.get()
								.replaceAll("<staff>", staff.getName()));
						staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OFF_OTHERS_STAFF.get()
								.replaceAll("<player>", player.getName())));
						return true;
					} else {
						staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OFF_OTHERS_CANCEL.get()
								.replaceAll("<player>", player.getName())));
					}
				} else {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OFF_OTHERS_CREATIVE.get()
							.replaceAll("<player>", player.getName())));
				}
			// Fly désactivé
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FLY_OFF_OTHERS_ERROR.get()
						.replaceAll("<player>", player.getName())));
			}
			return false;
		// La source et le joueur sont identique
		} else {
			return commandFlyOff(player);
		}
	}
}
