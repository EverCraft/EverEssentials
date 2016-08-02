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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEExt extends ECommand<EverEssentials> {
	
	public EEExt(final EverEssentials plugin) {
        super(plugin, "ext", "extinguish");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EXT.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.EXT_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.EXT_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "]")
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
		if(args.size() == 1 && source.hasPermission(EEPermissions.EXT_OTHERS.get())){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandExt((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.EXT_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandExtOthers(source, optPlayer.get());
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
	
	public boolean commandExt(final EPlayer player) {
		if(player.getFireTicks() > 0) {
			player.setFireTicks(0);
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.EXT_PLAYER.getText()));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.EXT_PLAYER_ERROR.getText()));
		}
		return false;
	}
	
	public boolean commandExtOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			if(player.getFireTicks() > 0) {
				player.setFireTicks(0);
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXT_OTHERS_PLAYER.get()
						.replaceAll("<staff>", player.getName())));
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXT_OTHERS_STAFF.get()
						.replaceAll("<player>", player.getName())));
				return true;
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXT_OTHERS_ERROR.get()
						.replaceAll("<player>", player.getName())));
			}
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
		return false;
	}
}
