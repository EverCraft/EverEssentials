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
package fr.evercraft.essentials.command.toggle;

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
import fr.evercraft.everapi.server.user.EUser;

public class EEToggleOff extends ESubCommand<EverEssentials> {
	
	public EEToggleOff(final EverEssentials plugin, final EEToggle command) {
        super(plugin, command, "off");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.TOGGLE_OFF_DESCRIPTION.get());
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.TOGGLE_OTHERS.get())){
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
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.TOGGLE_OTHERS.get())){
			suggests.addAll(this.getAllUsers());
		}
		return suggests;
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			if (source instanceof EPlayer) {
				resultat = this.commandToggleOff((EPlayer) source);
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.TOGGLE_OTHERS.get())){
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					resultat = this.commandToggleOffOthers(source, user.get());
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

	private boolean commandToggleOff(final EPlayer player) {
		boolean toggle = player.isToggle();
		
		// Toggle activé
		if (toggle) {
			if (player.setToggle(false)) {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.TOGGLE_OFF_PLAYER.getText()));
			} else {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.TOGGLE_OFF_PLAYER_CANCEL.getText()));
			}
		// Toggle désactivé
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.TOGGLE_OFF_PLAYER_ERROR.getText()));
		}
		return true;
	}
	
	private boolean commandToggleOffOthers(final CommandSource staff, final EUser user) throws CommandException {
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && user.getIdentifier().equals(staff.getIdentifier())) {
			return this.commandToggleOff((EPlayer) staff);
			
		// La source et le joueur sont différent
		} else {
			boolean toggle = user.isToggle();
			// Toggle activé
			if (toggle) {
				if (user.setToggle(false)) {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TOGGLE_OFF_OTHERS_STAFF.get()
							.replaceAll("<player>", user.getName())));
					
					if(user instanceof EPlayer) {
						((EPlayer) user).sendMessage(EEMessages.PREFIX.get() + EEMessages.TOGGLE_OFF_OTHERS_PLAYER.get()
								.replaceAll("<staff>", staff.getName()));
					}
					return true;
				} else {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TOGGLE_OFF_OTHERS_CANCEL.get()
							.replaceAll("<player>", user.getName())));
				}
			// Toggle désactivé
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TOGGLE_OFF_OTHERS_ERROR.get()
						.replaceAll("<player>", user.getName())));
			}
			return false;
		}
	}
}
