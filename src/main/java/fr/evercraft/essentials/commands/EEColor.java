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
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEColor extends ECommand<EverEssentials> {
	
	public EEColor(final EverEssentials plugin) {
        super(plugin, "color");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("COLOR"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("COLOR_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/color").onClick(TextActions.suggestCommand("/color"))
					.color(TextColors.RED).build();
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
				resultat = commandColor((EPlayer) source);
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
	
	public boolean commandColor(final EPlayer player) {
		List<Text> texts = new ArrayList<Text>();
		texts.add(this.plugin.getMessages().getText("COLOR_LIST_TITLE"));
		
		texts.add(this.getButtomColor("0", TextColors.BLACK));
		texts.add(this.getButtomColor("1", TextColors.DARK_BLUE));
		texts.add(this.getButtomColor("2", TextColors.DARK_GREEN));
		texts.add(this.getButtomColor("3", TextColors.DARK_AQUA));
		texts.add(this.getButtomColor("4", TextColors.DARK_RED));
		texts.add(this.getButtomColor("5", TextColors.DARK_PURPLE));
		texts.add(this.getButtomColor("6", TextColors.GOLD));
		texts.add(this.getButtomColor("7", TextColors.GRAY));
		texts.add(this.getButtomColor("8", TextColors.DARK_GRAY));
		texts.add(this.getButtomColor("9", TextColors.BLUE));
		texts.add(this.getButtomColor("a", TextColors.GREEN));
		texts.add(this.getButtomColor("b", TextColors.AQUA));
		texts.add(this.getButtomColor("c", TextColors.RED));
		texts.add(this.getButtomColor("d", TextColors.LIGHT_PURPLE));
		texts.add(this.getButtomColor("e", TextColors.YELLOW));
		texts.add(this.getButtomColor("f", TextColors.WHITE));
		
		player.sendBookView(BookView.builder().addPage(Text.joinWith(Text.of("\n"), texts)).build());
		return true;
	}
	
	private Text getButtomColor(String id, TextColor text) {
		return ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("COLOR_LIST_MESSAGE")
						.replaceAll("<color>", "&" + id)
						.replaceAll("<name>", this.plugin.getEverAPI().getMessages().getColor(text)))
				.replace("<id>", Text.of("&" + id))
				.build();
	}
}