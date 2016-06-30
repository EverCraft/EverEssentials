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

public class EEHat extends ECommand<EverEssentials> {
    
	public EEHat(EverEssentials plugin) {
        super(plugin ,"hat", "head");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.HAT.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.HAT_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/hat ").onClick(TextActions.suggestCommand("/hat "))
				.append(Text.builder("[remove]").onClick(TextActions.suggestCommand("/hat remove")).build())
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("remove");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du home inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandHat((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if (args.size() == 1 && args.get(0).equalsIgnoreCase("remove")) {	
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandHatRemove((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandHat(final EPlayer player){
		// Si le joueur a un objet dans la main
		if (player.getItemInMainHand().isPresent()){
			ItemStack item = player.getItemInMainHand().get();
			// Si le joueur a un item sur la tête
			if (player.getHelmet().isPresent() && !player.getHelmet().get().getItem().getBlock().isPresent()){
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.HAT_NO_EMPTY.get())
						.replace("<item>", EChat.getButtomItem(player.getHelmet().get(), EChat.getTextColor(EEMessages.HAT_ITEM_COLOR.get())))
						.build());
			// Le joueur peut avoir l'ojet sur la tête
			} else {
				if(player.getHelmet().isPresent()) {
					player.giveItemAndDrop(player.getHelmet().get());
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
		        player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.HAT_IS_HAT.get())
						.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(EEMessages.HAT_ITEM_COLOR.get())))
						.build());
		        return true;
			}
		// Le jouer n'a pas d'objet dans la main
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND));
		}
		return false;
	}
	
	public boolean commandHatRemove(final EPlayer player) {
		// Le joueur a un objet sur la tête
		if(player.getHelmet().isPresent() && player.getHelmet().get().getItem().getBlock().isPresent()) {
			ItemStack item = player.getHelmet().get();
			player.setHelmet(null);
			player.giveItemAndDrop(item);
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.HAT_REMOVE.get())
					.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(EEMessages.HAT_ITEM_COLOR.get())))
					.build());
		// Le joueur n'a pas d'objet sur la tête
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.HAT_REMOVE_EMPTY.get()));
		}
		return false;
	}
}
