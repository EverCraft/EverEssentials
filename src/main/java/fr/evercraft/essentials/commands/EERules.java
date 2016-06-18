/**
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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.command.ECommand;

public class EERules extends ECommand<EverEssentials> {
	
	public EERules(final EverEssentials plugin) {
        super(plugin, "rules", "rule");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.RULES.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.RULES_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/rules").onClick(TextActions.suggestCommand("/rules"))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du warp inconnu
		if (args.size() == 0) {
			resultat = commandRules(source);
		// Nom du warp connu
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandRules(final CommandSource player){			
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				this.plugin.getRules().getTitle(),
				this.plugin.getRules().getList(),
				player);
		return true;
	}
}