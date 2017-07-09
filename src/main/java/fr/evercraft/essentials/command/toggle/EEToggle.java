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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEToggle extends EParentCommand<EverEssentials> {
	
	public EEToggle(final EverEssentials plugin) {
        super(plugin, "tptoggle");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TOGGLE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TOGGLE_DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return true;
	}
	
	@Override
	protected CompletableFuture<Boolean> commandDefault(final CommandSource source, final List<String> args) {
		// Si la source est un joueur
		if (source instanceof EPlayer) {
			return this.commandToggle((EPlayer) source);
		// La source n'est pas un joueur
		} else {
			EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(source);
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandToggle(final EPlayer player) {
		boolean toggle = !player.isToggle();
		
		if (player.setToggle(toggle)) {
			if (toggle) {
				EEMessages.TOGGLE_ON_PLAYER.sendTo(player);
			} else {
				EEMessages.TOGGLE_OFF_PLAYER.sendTo(player);
			}
			return CompletableFuture.completedFuture(true);
		} else {
			if (toggle) {
				EEMessages.TOGGLE_ON_PLAYER_CANCEL.sendTo(player);
			} else {
				EEMessages.TOGGLE_OFF_PLAYER_CANCEL.sendTo(player);
			}
		}
		return CompletableFuture.completedFuture(false);
	}
}