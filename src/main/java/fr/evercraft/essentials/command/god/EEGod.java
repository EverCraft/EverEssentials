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
package fr.evercraft.essentials.command.god;

import java.util.List;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEGod extends EParentCommand<EverEssentials> {
	
	public EEGod(final EverEssentials plugin) {
        super(plugin, "god", "godmode");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.GOD.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.GOD_DESCRIPTION.get());
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return true;
	}
	
	@Override
	protected boolean commandDefault(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
				
		// Si la source est un joueur
		if (source instanceof EPlayer) {
			resultat = this.commandGod((EPlayer) source);
		// La source n'est pas un joueur
		} else {
			source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
		}
		
		return resultat;
	}
	
	private boolean commandGod(final EPlayer player) {
		boolean god = !player.isGod();
		if (player.setGod(god)) {
			player.heal();
			if (god) {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.GOD_ON_PLAYER.getText()));
			} else {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.GOD_OFF_PLAYER.getText()));
			}
			return true;
		} else {
			if (god) {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.GOD_ON_PLAYER_CANCEL.getText()));
			} else {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.GOD_OFF_PLAYER_CANCEL.getText()));
			}
		}
		return false;
	}
}