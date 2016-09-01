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
import java.util.Optional;

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

public class EEInfo extends ECommand<EverEssentials> {
	
	public EEInfo(final EverEssentials plugin) {
        super(plugin, "info");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.INFO.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.INFO_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		Text help = Text.builder("/" + this.getName())
						.onClick(TextActions.suggestCommand("/" + this.getName()))
						.color(TextColors.RED)
						.build();
		return help;
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandInfo((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandInfo(final EPlayer player) {
		Optional<ItemStack> item = player.getItemInMainHand();
		
		// Le joueur a aucun item dans la main
		if (!item.isPresent()) {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.EMPTY_ITEM_IN_HAND.getText()));
			return false;
		}
		
		player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
				.append(EEMessages.INFO_PLAYER.get()
						.replaceAll("<type>", item.get().getItem().getName().replaceAll("minecraft:", "").toUpperCase()))
				.replace("<item>", EChat.getButtomItem(item.get(), EChat.getTextColor(EEMessages.INFO_ITEM_COLOR.get())))
				.build());
		return true;
	}
}
