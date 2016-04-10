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
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEBook extends ECommand<EverEssentials> {
	
	public EEBook(final EverEssentials plugin) {
        super(plugin, "book");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("BOOK"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("BOOK_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/book ").onClick(TextActions.suggestCommand("/book "))
				.append(Text.of("["))
				.append(Text.builder("title").onClick(TextActions.suggestCommand("/book title")).build())
				.append(Text.of("|"))
				.append(Text.builder("author").onClick(TextActions.suggestCommand("/book author")).build())
				.append(Text.of("] [nom]"))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("title");
			suggests.add("author");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandBook((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				if(args.get(0).equalsIgnoreCase("title")){
					args.remove(0);
					resultat = commandBookTitle((EPlayer) source, getMessage(args));
				} else if(args.get(0).equalsIgnoreCase("author")){
					args.remove(0);
					resultat = commandBookAuthor((EPlayer) source, getMessage(args));
				} else {
					source.sendMessage(help(source));
				}
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandBook(EPlayer player) {
		// Si le joueur a bien un item dans la main
		if(player.getItemInHand().isPresent()) {
			ItemStack item = player.getItemInHand().get();
			if(item.getItem().equals(ItemTypes.WRITTEN_BOOK) && item.get(Keys.BOOK_PAGES).isPresent()) {
				// Nouveau livre
				ItemStack book = ItemStack.of(ItemTypes.WRITABLE_BOOK, 1);
				
				// Ajoute les pages
				book.offer(Keys.BOOK_PAGES, item.get(Keys.BOOK_PAGES).get());
				
				// Remplace le livre
				player.setItemInHand(book);
				
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("BOOK_WRITABLE"));
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("BOOK_NO_WRITTEN"));
			}
		// Le joueur a aucun item dans la main
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("EMPTY_ITEM_IN_HAND"));
		}
		return false;
	}

	private boolean commandBookTitle(EPlayer player, String message) {
		// Si le joueur a bien un item dans la main
		if(player.getItemInHand().isPresent()) {
			//ItemStack item = player.getItemInHand().get();
		// Le joueur a aucun item dans la main
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("EMPTY_ITEM_IN_HAND"));
		}
		return false;
	}

	private boolean commandBookAuthor(EPlayer player, String message) {
		// Si le joueur a bien un item dans la main
		if(player.getItemInHand().isPresent()) {
			//ItemStack item = player.getItemInHand().get();
		// Le joueur a aucun item dans la main
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("EMPTY_ITEM_IN_HAND"));
		}
		return false;
	}
}
