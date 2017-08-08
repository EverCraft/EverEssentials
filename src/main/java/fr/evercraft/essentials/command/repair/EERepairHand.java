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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}

	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandRepair((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandRepair(final EPlayer player) {
		Optional<ItemStack> optItem = player.getItemInMainHand();
		if (!optItem.isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		ItemStack item = optItem.get();
		Optional<Integer> durability = item.get(Keys.ITEM_DURABILITY);
		if (!durability.isPresent()) {
			EEMessages.REPAIR_HAND_ERROR.sender()
	        	.replace("{item}", EChat.getButtomItem(item, EEMessages.REPAIR_HAND_ITEM_COLOR.getColor()))
	        	.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		item.offer(Keys.ITEM_DURABILITY, Integer.MAX_VALUE);
		if (item.get(Keys.ITEM_DURABILITY).get() == durability.get()) {
			EEMessages.REPAIR_HAND_MAX_DURABILITY.sender()
	        	.replace("{item}", EChat.getButtomItem(item, EEMessages.REPAIR_HAND_ITEM_COLOR.getColor()))
	        	.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
        player.setItemInMainHand(item);
        EEMessages.REPAIR_HAND_PLAYER.sender()
        	.replace("{item}", EChat.getButtomItem(item, EEMessages.REPAIR_HAND_ITEM_COLOR.getColor()))
        	.sendTo(player);
        return CompletableFuture.completedFuture(true);
	}
}
