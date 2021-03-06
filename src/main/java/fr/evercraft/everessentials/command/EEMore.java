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
package fr.evercraft.everessentials.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEMore extends ECommand<EverEssentials> {
	
	public EEMore(final EverEssentials plugin) {
        super(plugin, "more");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MORE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.MORE_DESCRIPTION.getText();
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
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandMore((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandMore(final EPlayer player) {
		Optional<ItemStack> item = player.getItemInMainHand();
		
		// Le joueur a aucun item dans la main
		if (!item.isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		Integer max = item.get().getMaxStackQuantity();
		
		if (player.hasPermission(EEPermissions.MORE_UNLIMITED.get())) {
			max = 64;
		} else {
			max = item.get().getMaxStackQuantity();
		}
		
		// La quantité est invalide
		if (item.get().getQuantity() > max) {
			EEMessages.MORE_MAX_QUANTITY.sender()
				.replace("{quantity}", max.toString())
				.replace("{item}", EChat.getButtomItem(item.get(), EEMessages.MORE_ITEM_COLOR.getColor()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		item.get().setQuantity(max);
		player.setItemInMainHand(item.get());
		
		EEMessages.MORE_PLAYER.sender()
			.replace("{quantity}", max.toString())
			.replace("{item}", EChat.getButtomItem(item.get(), EEMessages.MORE_ITEM_COLOR.getColor()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
}
