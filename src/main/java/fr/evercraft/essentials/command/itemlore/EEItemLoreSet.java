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
package fr.evercraft.essentials.command.itemlore;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEItemLoreSet extends ESubCommand<EverEssentials> {
	public EEItemLoreSet(final EverEssentials plugin, final EEItemLore command) {
        super(plugin, command, "set");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM_LORE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.ITEM_LORE_SET_DESCRIPTION.get());
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			if(source instanceof Player){
				Player player = (Player) source;
				if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent()){
					ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
					suggests.add("0");
					if(item.get(Keys.ITEM_LORE).isPresent()){
						suggests.add(String.valueOf(item.get(Keys.ITEM_LORE).get().size()));
					}
				}
			}
		} else if(args.size() == 2) {
			if(source instanceof Player){
				Player player = (Player) source;
				if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent()){
					suggests.add("&bHello world");
				}
			}
		}
		return suggests;
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + "<" + EAMessages.ARGS_LINE.get() +  "> <" + EAMessages.ARGS_DESCRIPTION.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		if(args.size() == 1){
			if(source instanceof EPlayer){
				commandItemLoreAdd((EPlayer) source, args.get(0));
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

	private boolean commandItemLoreAdd(final EPlayer player, final String name) {
		if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent()){
			ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).get();
			List<Text> lore = new ArrayList<Text>();
			if(item.get(Keys.ITEM_LORE).isPresent()){
				lore = item.get(Keys.ITEM_LORE).get();
			}
			lore.add(EChat.of(name));
			item.offer(Keys.ITEM_LORE, lore);
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get()).append(EEMessages.ITEM_LORE_ADD_LORE.get())
					.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(EEMessages.ITEM_LORE_ADD_COLOR.get())))
				.build());
			item.offer(Keys.ITEM_LORE, lore);
			player.setItemInHand(HandTypes.MAIN_HAND, item);
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
			return false;
		}
	}
}