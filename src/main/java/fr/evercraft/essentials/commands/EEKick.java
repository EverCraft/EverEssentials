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
package fr.evercraft.essentials.commands;

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
import fr.evercraft.everapi.text.ETextBuilder;

public class EEKick extends ECommand<EverEssentials> {
	
	public EEKick(final EverEssentials plugin) {
        super(plugin, "kick");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.KICK.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.KICK_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/kick <joueur> <raison>").onClick(TextActions.suggestCommand("/kick "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() >= 2) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				args.remove(0);
				resultat = commandKick(source, optPlayer.get(), EChat.of(getMessage(args)));
			// Le joueur est introuvable
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandKick(final CommandSource staff, final EPlayer player, final Text message) throws CommandException {
		player.kick(ETextBuilder.toBuilder(EEMessages.KICK_MESSAGE.get()
								.replaceAll("<staff>", staff.getName()))
							.replace("<message>", message)
							.build());
		return true;
	}
}
