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
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsInventory;
import fr.evercraft.everapi.sponge.UtilsItemStack;

public class EERepairAll extends ECommand<EverEssentials> {

	public EERepairAll(final EverEssentials plugin) {
		super(plugin, "repairall");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.REPAIR_ALL.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("REPAIR_ALL_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/repairall").onClick(TextActions.suggestCommand("/repairall")).color(TextColors.RED).build();
	}

	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandRepairAll((EPlayer) source);
				// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
			// On connais le joueur
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	public boolean commandRepairAll(final EPlayer player) {
		UtilsInventory.repair(player.getInventory().query(Hotbar.class));
		UtilsInventory.repair(player.getInventory().query(GridInventory.class));
		
		if(player.getHelmet().isPresent()){
			player.setHelmet(UtilsItemStack.repairInventory(player.getHelmet().get()));
		}
		
		if(player.getChestplate().isPresent()){
			player.setChestplate(UtilsItemStack.repairInventory(player.getChestplate().get()));
		}
		
		if(player.getLeggings().isPresent()){
			player.setLeggings(UtilsItemStack.repairInventory(player.getLeggings().get()));
		}
		
		if(player.getBoots().isPresent()){
			player.setBoots(UtilsItemStack.repairInventory(player.getBoots().get()));
		}
		
		player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("REPAIR_ALL_PLAYER"));
		return false;
	}
}