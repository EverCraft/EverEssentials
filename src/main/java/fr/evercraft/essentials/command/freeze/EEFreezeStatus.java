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
package fr.evercraft.essentials.command.freeze;

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

public class EEFreezeStatus extends ESubCommand<EverEssentials> {
	
	public EEFreezeStatus(final EverEssentials plugin, final EEFreeze command) {
        super(plugin, command, "status");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.FREEZE_STATUS_DESCRIPTION.get());
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.FREEZE_OTHERS.get())){
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
		if (args.size() == 1 && source.hasPermission(EEPermissions.FREEZE_OTHERS.get())){
			suggests.addAll(this.getAllUsers());
		}
		return suggests;
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			if (source instanceof EPlayer) {
				resultat = this.commandFreezeStatus((EPlayer) source);
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.FREEZE_OTHERS.get())){
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					resultat = this.commandFreezeStatusOthers(source, user.get());
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

	private boolean commandFreezeStatus(final EPlayer player) {
		// Si le freeze est déjà activé
		if (player.isFreeze()){
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FREEZE_STATUS_PLAYER_ON.get()
					.replaceAll("<player>", player.getName())));
		// Freeze est déjà désactivé
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FREEZE_STATUS_PLAYER_OFF.get()
					.replaceAll("<player>", player.getName())));
		}
		return true;
	}
	
	private boolean commandFreezeStatusOthers(final CommandSource staff, final EUser user) {
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && user.getIdentifier().equals(staff.getIdentifier())) {
			return this.commandFreezeStatus((EPlayer) staff);
			
		// La source et le joueur sont différent
		} else {
			// Freeze activé
			if (user.isFreeze()){
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FREEZE_STATUS_OTHERS_ON.get()
						.replaceAll("<player>", user.getName())));
			// Freeze désactivé
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.FREEZE_STATUS_OTHERS_OFF.get()
						.replaceAll("<player>", user.getName())));
			}
			return true;
		}
	}
}
