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
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEHat extends ECommand<EverEssentials> {
    
	public EEHat(EverEssentials plugin) {
        super(plugin ,"hat", "head");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("HAT"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("HAT_DESCRIPTION");
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
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if (args.size() == 1 && args.get(0).equalsIgnoreCase("remove")) {	
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandHatRemove((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandHat(final EPlayer player){
		// Si le joueur a un objet dans la main
		if (player.getItemInHand().isPresent()){
			ItemStack item = player.getItemInHand().get();
			// Si l'objet est un bloc
			if (item.getItem().getBlock().isPresent()){
				// Si le joueur a un item sur la tête
				if (player.getHelmet().isPresent() && !player.getHelmet().get().getItem().getBlock().isPresent()){
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
							.append(this.plugin.getMessages().getMessage("HAT_NO_EMPTY"))
							.replace("<item>", EChat.getButtomItem(player.getHelmet().get(), EChat.getTextColor(this.plugin.getMessages().getMessage("HAT_ITEM_COLOR"))))
							.build());
				// Le joueur peut avoir l'ojet sur la tête
				} else {
					if(player.getHelmet().isPresent()) {
						player.giveItemAndDrop(player.getHelmet().get());
					}
					
					ItemStack stack = player.getItemInHand().get();
					if (stack.getQuantity() > 1){
			            stack.setQuantity(stack.getQuantity() - 1);
			            player.setItemInHand(stack);
					} else {
						player.setItemInHand(null);
					}
			        stack.setQuantity(1);
			        player.setHelmet(stack);
			        player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
							.append(this.plugin.getMessages().getMessage("HAT_IS_HAT"))
							.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("HAT_ITEM_COLOR"))))
							.build());
			        return true;
				}
			// L'objet est un item
			} else {
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
						.append(this.plugin.getMessages().getMessage("HAT_IS_NOT_HAT"))
						.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("HAT_ITEM_COLOR"))))
						.build());
			}
		// Le jouer n'a pas d'objet dans la main
		} else {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("HAT_NULL")));
		}
		return false;
	}
	
	public boolean commandHatRemove(final EPlayer player) {
		// Le joueur a un objet sur la tête
		if(player.getHelmet().isPresent() && player.getHelmet().get().getItem().getBlock().isPresent()) {
			ItemStack item = player.getHelmet().get();
			player.setHelmet(null);
			player.giveItemAndDrop(item);
			player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
					.append(this.plugin.getMessages().getMessage("HAT_REMOVE"))
					.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("HAT_ITEM_COLOR"))))
					.build());
		// Le joueur n'a pas d'objet sur la tête
		} else {
			player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("HAT_REMOVE_EMPTY")));
		}
		return false;
	}
}
