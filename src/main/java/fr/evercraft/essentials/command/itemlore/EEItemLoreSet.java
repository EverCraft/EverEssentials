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
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsInteger;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

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
		return EEMessages.ITEM_LORE_SET_DESCRIPTION.getText();
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			if(source instanceof Player){
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(((Player) source).getUniqueId());
				if(player.isPresent()){
					Optional<ItemStack> item = player.get().getItemInMainHand();
					if(item.isPresent()){
						suggests.add("1");
						Optional<List<Text>> lore = item.get().get(Keys.ITEM_LORE);
						if(lore.isPresent()){
							if(lore.get().size() > 1){
								suggests.add(String.valueOf(lore.get().size()));
							}
						}
					}
				}
			}
		} else if(args.size() == 2) {
			if(source instanceof Player){
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(((Player) source).getUniqueId());
				if(player.isPresent()){
					if(player.get().getItemInMainHand().isPresent()){
						suggests.add("&bHello world");
					}
				}
			}
		}
		return suggests;
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + "<" + EAMessages.ARGS_LINE.getString() +  "> <" + EAMessages.ARGS_DESCRIPTION.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		if(args.size() == 1){
			this.help(source);
			return false;
		} else if(args.size() == 2){
			if(source instanceof EPlayer){
				commandItemLoreSet((EPlayer) source, args.get(0), args.get(1));
				return true;
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
				return false;
			}
		} else {
			this.help(source);
			return false;
		}
	}

	private boolean commandItemLoreSet(final EPlayer player, final String line_name, final String description) {
		Optional<Integer> line = UtilsInteger.parseInt(line_name);
		if (line.isPresent()) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<number>", line_name)
				.sendTo(player);
			return false;
		}
		
		if (line.get() > 0) {
			EAMessages.NUMBER_INVALID.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return false;
		}
		
		Optional<ItemStack> item = player.getItemInMainHand();
		if(!item.isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return false;
		}

		List<Text> lore = new ArrayList<Text>();
		if(item.get().get(Keys.ITEM_LORE).isPresent()){
			lore = item.get().get(Keys.ITEM_LORE).get();
		}
		if(line.get() >= lore.size()){
			lore.add(EChat.of(description));
		} else {
			lore.set(line.get() - 1, EChat.of(description));
		}
		item.get().offer(Keys.ITEM_LORE, lore);
		EEMessages.ITEM_LORE_SET_LORE.sender()
			.replace("<item>", EChat.getButtomItem(item.get(), EEMessages.ITEM_LORE_SET_COLOR.getColor()))
			.replace("<line>", line.get().toString())
			.sendTo(player);
		item.get().offer(Keys.ITEM_LORE, lore);
		player.setItemInMainHand(item.get());
		return true;

	}
}