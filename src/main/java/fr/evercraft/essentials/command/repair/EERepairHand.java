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
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
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

public class EERepairHand extends ECommand<EverEssentials> {

	public EERepairHand(final EverEssentials plugin) {
		super(plugin, "repairhand");
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.REPAIR_HAND.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.REPAIR_HAND_DESCRIPTION.getText();
	}
	
	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandRepair((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandRepair(final EPlayer player) {
		Optional<ItemStack> optItem = player.getItemInMainHand();
		if (optItem.isPresent()) {
			ItemStack item = optItem.get();
			Optional<Integer> durability = item.get(Keys.ITEM_DURABILITY);
			if (durability.isPresent()) {
				item.offer(Keys.ITEM_DURABILITY, Integer.MAX_VALUE);
				if (item.get(Keys.ITEM_DURABILITY).get() != durability.get()){
		            player.setItemInMainHand(item);
		            player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
		            		.append(EEMessages.REPAIR_HAND_PLAYER.get())
		            		.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(EEMessages.REPAIR_HAND_ITEM_COLOR.get())))
		            		.build());
		            return true;
				} else {
		            player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
		            		.append(EEMessages.REPAIR_HAND_MAX_DURABILITY.get())
		            		.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(EEMessages.REPAIR_HAND_ITEM_COLOR.get())))
		            		.build());
					return false;
				}
			} else {
	            player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
	            		.append(EEMessages.REPAIR_HAND_ERROR.get())
	            		.replace("<item>",  EChat.getButtomItem(item, EChat.getTextColor(EEMessages.REPAIR_HAND_ITEM_COLOR.get())))
	            		.build());
				return false;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
			return false;
		}
	}
}
