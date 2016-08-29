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
package fr.evercraft.essentials.command.repair;

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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EERepair extends ECommand<EverEssentials> {

	public EERepair(final EverEssentials plugin) {
		super(plugin, "repair", "fix");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.REPAIR.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.REPAIR_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " ").onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.append(Text.of("<"))
					.append(Text.builder("all").onClick(TextActions.suggestCommand("/" + this.getName() + " all")).build())
					.append(Text.of("|"))
					.append(Text.builder("hand").onClick(TextActions.suggestCommand("/" + this.getName() + " hand")).build())
					.append(Text.of("|"))
					.append(Text.builder("hotbar").onClick(TextActions.suggestCommand("/" + this.getName() + " hotbar")).build())
					.append(Text.of(">"))
					.color(TextColors.RED)
					.build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.add("all");
			suggests.add("hand");
			suggests.add("hotbar");
		}
		return suggests;
	}

	public boolean execute(CommandSource source, final List<String> args) throws CommandException {
		// Erreur : Context 
		if(source instanceof EPlayer) {
			source = ((EPlayer) source).get();
		}
		
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 1) {
			if (args.get(0).equalsIgnoreCase("all")) {
				resultat = this.commandRepairAll(source);
			} else if (args.get(0).equalsIgnoreCase("hand")) {
				resultat = this.commandRepairHand(source);
			} else if (args.get(0).equalsIgnoreCase("hotbar")) {
				resultat = this.commandRepairHotbar(source);
			} else {
				source.sendMessage(this.help(source));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	public boolean commandRepairAll(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "repairall");
		return false;
	}
	
	public boolean commandRepairHand(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "repairhand");
		return false;
	}
	
	public boolean commandRepairHotbar(final CommandSource player) {
		this.plugin.getGame().getCommandManager().process(player, "repairhotbar");
		return false;
	}
}