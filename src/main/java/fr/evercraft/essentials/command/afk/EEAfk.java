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
package fr.evercraft.essentials.command.afk;

import java.util.List;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEAfk extends EParentCommand<EverEssentials> {
	
	public EEAfk(final EverEssentials plugin) {
        super(plugin, "afk", "away");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.AFK.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.AFK_DESCRIPTION.getText();
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
			resultat = this.commandAfk((EPlayer) source);
		// La source n'est pas un joueur
		} else {
			EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(source);
		}
		
		return resultat;
	}
	
	private boolean commandAfk(final EPlayer player) {
		boolean afk = !player.isAfk();
		if (player.setAfk(afk)) {
			if (afk) {
				EEMessages.AFK_ON_PLAYER.sendTo(player);
				EEMessages.AFK_ON_ALL.sender()
					.replace(player.getReplacesAll())
					.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> !other.equals(player));
			} else {
				EEMessages.AFK_OFF_PLAYER.sendTo(player);
				EEMessages.AFK_OFF_ALL.sender()
					.replace(player.getReplacesAll())
					.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> !other.equals(player));
			}
			return true;
		} else {
			if (afk) {
				EEMessages.AFK_ON_PLAYER_CANCEL.sendTo(player);
			} else {
				EEMessages.AFK_OFF_PLAYER_CANCEL.sendTo(player);
			}
		}
		return false;
	}
}