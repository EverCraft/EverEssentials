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
package fr.evercraft.essentials.command.itemname;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEItemNameClear extends ESubCommand<EverEssentials> {
	public EEItemNameClear(final EverEssentials plugin, final EEItemName command) {
        super(plugin, command, "clear");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM_NAME.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.ITEM_NAME_CLEAR_DESCRIPTION.get());
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		if(args.size() == 0){
			if(source instanceof EPlayer){
				commandItemNameClear((EPlayer) source);
				return true;
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
				return false;
			}
		} else {
			this.help(source);
			return false;
		}
	}

	private boolean commandItemNameClear(final EPlayer player) {
		if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent()){
			ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
			if(item.get(Keys.DISPLAY_NAME).isPresent()){
				item.offer(Keys.DISPLAY_NAME, Text.of(""));
				player.setItemInHand(HandTypes.MAIN_HAND, item);
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get()).append(EEMessages.ITEM_NAME_CLEAR_NAME.get())
						.replace("<item>", EChat.getButtomItem(player.getItemInHand(HandTypes.MAIN_HAND).get(), 
								EChat.getTextColor(EEMessages.ITEM_NAME_SET_COLOR.get())))
					.build());
				return true;
			} else {
				this.plugin.getEServer().broadcast("test");
				return false;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
			return false;
		}
	}
}