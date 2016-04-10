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
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EERepairHand extends ECommand<EverEssentials> {

	public EERepairHand(final EverEssentials plugin) {
		super(plugin, "repairhand");
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("REPAIR_HAND"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("REPAIR_HAND_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/repairhand").onClick(TextActions.suggestCommand("/repairhand")).color(TextColors.RED).build();
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
				resultat = commandRepair((EPlayer) source);
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

	public boolean commandRepair(final EPlayer player) {
		if (player.getItemInHand().isPresent()){
			ItemStack item = player.getItemInHand().get();
			Optional<Integer> data = item.get(Keys.ITEM_DURABILITY);
			if(data.isPresent()){
				int value = data.get();
				item.offer(Keys.ITEM_DURABILITY, Integer.MAX_VALUE);
				if(item.get(Keys.ITEM_DURABILITY).get() != value){
		            player.setItemInHand(item);
		            player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
		            		.append(this.plugin.getMessages().getMessage("REPAIR_HAND_PLAYER"))
		            		.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("REPAIR_HAND_ITEM_COLOR"))))
		            		.build());
		            return true;
				} else {
		            player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
		            		.append(this.plugin.getMessages().getMessage("REPAIR_HAND_MAX_DURABILITY"))
		            		.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("REPAIR_HAND_ITEM_COLOR"))))
		            		.build());
					return false;
				}
			} else {
	            player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
	            		.append(this.plugin.getMessages().getMessage("REPAIR_HAND_ERROR"))
	            		.replace("<item>",  EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("REPAIR_HAND_ITEM_COLOR"))))
	            		.build());
				return false;
			}
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("EMPTY_ITEM_IN_HAND"));
			return false;
		}
	}
}
