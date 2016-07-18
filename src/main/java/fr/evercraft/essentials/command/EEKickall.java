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

public class EEKickall extends ECommand<EverEssentials> {
	
	public EEKickall(final EverEssentials plugin) {
        super(plugin, "kickall");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.KICKALL.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.KICKALL_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/kickall <" + EAMessages.ARGS_REASON.get() +">")
				.onClick(TextActions.suggestCommand("/kickall "))
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
		if(args.size() >= 1) {
			resultat = commandKick(source, EChat.of(getMessage(args)));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandKick(final CommandSource staff, final Text message) throws CommandException {
		Text raison = ETextBuilder.toBuilder(EEMessages.KICKALL_MESSAGE.get()
							.replaceAll("<staff>", staff.getName()))
						.replace("<message>", message)
						.build();
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			player.kick(raison);
		}
		return true;
	}
}
