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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.BookView.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEFormat extends ECommand<EverEssentials> {
	
	public EEFormat(final EverEssentials plugin) {
        super(plugin, "format");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.FORMAT.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.FORMAT_DESCRIPTION.getText();
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
				return this.commandFormat((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandFormat(final EPlayer player) {
		Builder book = BookView.builder();
		
		List<Text> page = new ArrayList<Text>();
		page.add(EEMessages.FORMAT_LIST_TITLE.getText());
		page.add(this.getButtomColor("k", EEMessages.FORMAT_OBFUSCATED.getText()));
		page.add(this.getButtomColor("l", EEMessages.FORMAT_BOLD.getText()));
		page.add(this.getButtomColor("m", EEMessages.FORMAT_STRIKETHROUGH.getText()));
		page.add(this.getButtomColor("n", EEMessages.FORMAT_UNDERLINE.getText()));
		page.add(this.getButtomColor("o", EEMessages.FORMAT_ITALIC.getText()));
		page.add(this.getButtomColor("r", EEMessages.FORMAT_RESET.getText()));
		book.addPage(Text.joinWith(Text.of("\n"), page));
		player.sendBookView(book.build());
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtomColor(String id, Text text) {
		return EEMessages.FORMAT_LIST_MESSAGE.getFormat().toText(
					"{format}", "&" + id,
					"{name}", text,
					"{id}", Text.of("&" + id));
	}
}
