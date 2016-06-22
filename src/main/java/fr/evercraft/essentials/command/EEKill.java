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

public class EEKill  extends ECommand<EverEssentials> {
	
	public EEKill(final EverEssentials plugin) {
        super(plugin, "kill");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.KILL.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.KILL_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		Text help;
		help = Text.builder("/kill <joueur>").onClick(TextActions.suggestCommand("/kill "))
				.color(TextColors.RED).build();
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1) {
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if(args.size() == 1) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			if(optPlayer.isPresent()){
				resultat = commandKill(source, optPlayer.get());
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandKill(final CommandSource staff, final EPlayer player) {
		if(!player.equals(staff)) {
			player.setHealth(0);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.KILL_PLAYER.get()
					.replaceAll("<staff>", staff.getName()));
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.KILL_STAFF.get()
					.replaceAll("<player>", player.getName())));
		} else {
			this.plugin.getGame().getCommandManager().process(staff, "suicide");
		}
		return true;
	}
}
