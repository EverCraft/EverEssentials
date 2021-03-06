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
import fr.evercraft.everapi.sponge.UtilsItemType;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEHat extends ECommand<EverEssentials> {
    
	public EEHat(EverEssentials plugin) {
        super(plugin ,"hat", "head");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.HAT.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.HAT_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " ")
				.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
				.append(Text.builder("[remove]")
							.onClick(TextActions.suggestCommand("/" + this.getName() + " remove"))
							.build())
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("remove");
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si la source est un joueur
		if (source instanceof EPlayer) {
			// Nom du home inconnu
			if (args.size() == 0) {
				
				return this.commandHat((EPlayer) source);
				
			} else if (args.size() == 1) {
				
				if (args.get(0).equalsIgnoreCase("remove")) {	
					return this.commandHatRemove((EPlayer) source);
				} else {
					source.sendMessage(this.help(source));
				}
				
			// Nombre d'argument incorrect
			} else {
				source.sendMessage(this.help(source));
			}
		// La source n'est pas un joueur
		} else {
			EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(source);
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandHat(final EPlayer player) {
		Optional<ItemStack> item = player.getItemInMainHand();
		
		// Le jouer n'a pas d'objet dans la main
		if (!item.isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<ItemStack> helmet = player.getHelmet();
			
		// Le joueur a un casque sur la tête
		if (helmet.isPresent() && UtilsItemType.isHelmet(helmet.get().getType())) {
			EEMessages.HAT_NO_EMPTY.sender()
				.replace("{item}", EChat.getButtomItem(player.getHelmet().get(), EEMessages.HAT_ITEM_COLOR.getColor()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		// Le joueur a un item sur la tête
		if (helmet.isPresent()) {
			player.giveItemAndDrop(helmet.get());
		}
		
		ItemStack stack = player.getItemInMainHand().get();
		if (stack.getQuantity() > 1){
            stack.setQuantity(stack.getQuantity() - 1);
            player.setItemInMainHand(stack);
		} else {
			player.setItemInMainHand(null);
		}
		
        stack.setQuantity(1);
        player.setHelmet(stack);
        
        EEMessages.HAT_IS_HAT.sender()
			.replace("{item}", EChat.getButtomItem(item.get(), EEMessages.HAT_ITEM_COLOR.getColor()))
			.sendTo(player);
        return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandHatRemove(final EPlayer player) {
		Optional<ItemStack> helmet = player.getHelmet();
		
		// Le joueur n'a pas d'objet sur la tête
		if (!helmet.isPresent() || UtilsItemType.isHelmet(helmet.get().getType())) {
			EEMessages.HAT_REMOVE_EMPTY.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		player.setHelmet(null);
		player.giveItemAndDrop(helmet.get());
		
		EEMessages.HAT_REMOVE.sender()
			.replace("{item}", EChat.getButtomItem(helmet.get(), EEMessages.HAT_ITEM_COLOR.getColor()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
}
