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

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEMore extends ECommand<EverEssentials> {
	
	public EEMore(final EverEssentials plugin) {
        super(plugin, "more");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.MORE.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("MORE_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		Text help = Text.builder("/more").onClick(TextActions.suggestCommand("/more"))
				.color(TextColors.RED).build();
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandMore((EPlayer) source);
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
	
	public boolean commandMore(final EPlayer player) {
		// Si le joueur a bien un item dans la main
		if(player.getItemInHand().isPresent()) {
			ItemStack item = player.getItemInHand().get();
			
			Integer max = item.getMaxStackQuantity();
			/*if(player.hasPermission(EEPermissions.MORE_UNLIMITED"))) {
				max = 64;
			} else {
				max = item.getMaxStackQuantity();
			}*/
			
			if(item.getQuantity() < max) {
				item.setQuantity(max);
				player.setItemInHand(item);		
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("MORE_PLAYER")
								.replaceAll("<quantity>", max.toString()))
						.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("MORE_ITEM_COLOR"))))
						.build());
				return true;
			} else {
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("MORE_MAX_QUANTITY")
								.replaceAll("<quantity>", max.toString()))
						.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(this.plugin.getMessages().getMessage("MORE_ITEM_COLOR"))))
						.build());
			}
		// Le joueur a aucun item dans la main
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("EMPTY_ITEM_IN_HAND"));
		}
		return false;
	}
}
