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
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.BookView.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEColor extends ECommand<EverEssentials> {
	
	public EEColor(final EverEssentials plugin) {
        super(plugin, "color");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.COLOR.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.COLOR_DESCRIPTION.getText();
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
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandColor((EPlayer) source);
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
		
		return resultat;
	}
	
	private boolean commandColor(final EPlayer player) {
		Builder book = BookView.builder();
		
		List<Text> page = new ArrayList<Text>();
		page.add(EEMessages.COLOR_LIST_TITLE.getText());
		page.add(this.getButtomColor("0", TextColors.BLACK));
		page.add(this.getButtomColor("1", TextColors.DARK_BLUE));
		page.add(this.getButtomColor("2", TextColors.DARK_GREEN));
		page.add(this.getButtomColor("3", TextColors.DARK_AQUA));
		page.add(this.getButtomColor("4", TextColors.DARK_RED));
		page.add(this.getButtomColor("5", TextColors.DARK_PURPLE));
		page.add(this.getButtomColor("6", TextColors.GOLD));
		page.add(this.getButtomColor("7", TextColors.GRAY));
		page.add(this.getButtomColor("8", TextColors.DARK_GRAY));
		page.add(this.getButtomColor("9", TextColors.BLUE));
		page.add(this.getButtomColor("a", TextColors.GREEN));
		page.add(this.getButtomColor("b", TextColors.AQUA));
		page.add(this.getButtomColor("c", TextColors.RED));
		book.addPage(Text.joinWith(Text.of("\n"), page));
		
		
		page.clear();
		page.add(this.getButtomColor("d", TextColors.LIGHT_PURPLE));
		page.add(this.getButtomColor("e", TextColors.YELLOW));
		page.add(this.getButtomColor("f", TextColors.WHITE));
		book.addPage(Text.joinWith(Text.of("\n"), page));
		
		player.sendBookView(book.build());
		return true;
	}
	
	private Text getButtomColor(String id, TextColor text) {
		Optional<EAMessages> color = EAMessages.getColor(text);
		if (color.isPresent()) {
			return EEMessages.COLOR_LIST_MESSAGE.getFormat().toText(
					"<color>", "&" + id,
					"<name>", color.get().getString(),
					"<id>", Text.of("&" + id));
		}
		return Text.EMPTY;
	}
}