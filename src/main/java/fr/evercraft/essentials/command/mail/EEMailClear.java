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
package fr.evercraft.essentials.command.mail;

import java.util.ArrayList;
import java.util.List;
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
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEMailClear extends ESubCommand<EverEssentials> {
	public EEMailClear(final EverEssentials plugin, final EEMail command) {
        super(plugin, command, "clear");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MAIL.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.MAIL_CLEAR_DESCRIPTION.get());
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
		if(args.size() == 0){
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandClear((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	/*
	 * Clear
	 */
	
	private boolean commandClear(EPlayer player) {
		if(!player.getMails().isEmpty()) {
			if(player.clearMails()) {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.MAIL_CLEAR_MESSAGE.getText()));
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.MAIL_CLEAR_CANCEL.getText()));
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.MAIL_CLEAR_ERROR.getText()));
		}
		return false;
	}
}