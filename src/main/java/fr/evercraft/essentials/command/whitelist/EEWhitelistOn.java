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
package fr.evercraft.essentials.command.whitelist;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;

public class EEWhitelistOn extends ESubCommand<EverEssentials> {
	public EEWhitelistOn(final EverEssentials plugin, final EEWhitelist command) {
        super(plugin, command, "on");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHITELIST_MANAGE.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WHITELIST_ON_DESCRIPTION.get());
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
		if(args.size() == 0) {
			resultat = commandWhitelistOn(source);
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWhitelistOn(final CommandSource player) {
		if(!this.plugin.getEServer().hasWhitelist()){
			this.plugin.getEServer().setHasWhitelist(true);
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WHITELIST_ON_ACTIVATED.get()));
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WHITELIST_ON_ALREADY_ACTIVATED.get()));
		}
		return true;
	}
}