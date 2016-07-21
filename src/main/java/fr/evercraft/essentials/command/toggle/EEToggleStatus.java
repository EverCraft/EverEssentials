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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEToggleStatus extends ESubCommand<EverEssentials> {
	public EEToggleStatus(final EverEssentials plugin, final EEToggle command) {
        super(plugin, command, "status");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TOGGLE.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.TOGGLE_STATUS_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(source instanceof Player){
			if(this.plugin.getEServer().getEPlayer((Player) source).isPresent()){
				EPlayer player = this.plugin.getEServer().getEPlayer((Player) source).get();
				if(args.size() == 0) {
					resultat = commandToggleStatus(player);
				} else {
					source.sendMessage(this.help(player));
				}
			}
		}
		return resultat;
	}

	private boolean commandToggleStatus(final EPlayer player) {
		String message;
		if(player.isToggle()){
			message = EEMessages.TOGGLE_STATUS_ACTIVATED.get();
		} else {
			message = EEMessages.TOGGLE_STATUS_DISABLED.get();
		}
		player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TOGGLE_STATUS_MESSAGE.get()
				.replaceAll("<status>", message)));
		return true;
	}
}