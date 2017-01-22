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
import java.util.Collection;
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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEClearInventory extends ECommand<EverEssentials> {
	
	public EEClearInventory(final EverEssentials plugin) {
        super(plugin, "clearinventory", "ci", "clearinvent");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.CLEARINVENTORY.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.CLEARINVENTORY_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.CLEARINVENTORY_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.CLEARINVENTORY_OTHERS.get())){
			suggests.addAll(this.getAllPlayers(source));
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandClearInventory((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.CLEARINVENTORY_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (optPlayer.isPresent()){
					resultat = this.commandClearInventory(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
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
		
		return resultat;
	}
	
	private boolean commandClearInventory(final EPlayer player){
		int total = player.getInventory().totalItems();
		
		// Il n'y a pas d'item
		if (total == 0) {
			EEMessages.CLEARINVENTORY_NOITEM.sendTo(player);
			return false;
		}
		
		player.getInventory().clear();
		
		EEMessages.CLEARINVENTORY_PLAYER.sender()
			.replace("<amount>", String.valueOf(total))
			.sendTo(player);
		return true;
	}
	
	private boolean commandClearInventory(final CommandSource staff, final EPlayer player) {
		// La source et le joueur sont identique
		if (player.equals(staff)) {
			return this.commandClearInventory(player);
		}
		
		int total = player.getInventory().totalItems();

		// Il n'y a pas d'item
		if (total == 0) {
			EEMessages.CLEARINVENTORY_OTHERS_NOITEM.sendTo(staff);
			return false;
		}
		
		player.getInventory().clear();
		
		EEMessages.CLEARINVENTORY_OTHERS_STAFF.sender()
			.replace("<player>", player.getName())
			.replace("<amount>", String.valueOf(total))
			.sendTo(staff);
		EEMessages.CLEARINVENTORY_OTHERS_PLAYER.sender()
			.replace("<staff>", staff.getName())
			.replace("<amount>", String.valueOf(total))
			.sendTo(player);
		return true;
	}
}
