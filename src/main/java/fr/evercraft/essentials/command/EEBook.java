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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

/*
 * Pas encore implémentée
 */

public class EEBook extends ECommand<EverEssentials> {
	
	public EEBook(final EverEssentials plugin) {
        super(plugin, "book");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BOOK.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.BOOK_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/book ").onClick(TextActions.suggestCommand("/book "))
				.append(Text.of("["))
				.append(Text.builder("title").onClick(TextActions.suggestCommand("/book title")).build())
				.append(Text.of("|"))
				.append(Text.builder("author").onClick(TextActions.suggestCommand("/book author")).build())
				.append(Text.of("] [nom]"))
				.color(TextColors.RED).build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("title", "author");
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandBook((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// On connais le joueur
		} else if (args.size() == 1) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()) {
				if (args.get(0).equalsIgnoreCase("title")) {
					args.remove(0);
					return this.commandBookTitle((EPlayer) source, "");
				} else if (args.get(0).equalsIgnoreCase("author")) {
					args.remove(0);
					return this.commandBookAuthor((EPlayer) source, "");
				} else {
					source.sendMessage(this.help(source));
				}
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("<player>", args.get(0))
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandBook(EPlayer player) {
		// Si le joueur a bien un item dans la main
		if (player.getItemInMainHand().isPresent()) {
			ItemStack item = player.getItemInMainHand().get();
			if (item.getItem().equals(ItemTypes.WRITTEN_BOOK) && item.get(Keys.BOOK_PAGES).isPresent()) {
				// Nouveau livre
				ItemStack book = ItemStack.of(ItemTypes.WRITABLE_BOOK, 1);
				
				// Ajoute les pages
				book.offer(Keys.BOOK_PAGES, item.get(Keys.BOOK_PAGES).get());
				
				// Remplace le livre
				player.setItemInMainHand(book);
				
				EEMessages.BOOK_WRITABLE.sendTo(player);
			} else {
				EEMessages.BOOK_NO_WRITTEN.sendTo(player);
			}
		// Le joueur a aucun item dans la main
		} else {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
		}
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandBookTitle(EPlayer player, String message) {
		// Si le joueur a bien un item dans la main
		if (player.getItemInMainHand().isPresent()) {
			//ItemStack item = player.getItemInHand().get();
		// Le joueur a aucun item dans la main
		} else {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
		}
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandBookAuthor(EPlayer player, String message) {
		// Si le joueur a bien un item dans la main
		if (player.getItemInMainHand().isPresent()) {
			//ItemStack item = player.getItemInHand().get();
		// Le joueur a aucun item dans la main
		} else {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
		}
		return CompletableFuture.completedFuture(true);
	}
}
