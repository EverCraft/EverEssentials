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
		return Text.builder("/" + this.getName() + "<" + EAMessages.ARGS_LINE.get() +  "> <" + EAMessages.ARGS_DESCRIPTION.get() + ">")
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
		try {
			int line = Integer.parseInt(line_name) ;
			if(line > 0){
				Optional<ItemStack> item = player.getItemInMainHand();
				if(item.isPresent()){
					List<Text> lore = new ArrayList<Text>();
					if(item.get().get(Keys.ITEM_LORE).isPresent()){
						lore = item.get().get(Keys.ITEM_LORE).get();
					}
					if(line >= lore.size()){
						lore.add(EChat.of(description));
					} else {
						lore.set(line - 1, EChat.of(description));
					}
					item.get().offer(Keys.ITEM_LORE, lore);
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get()).append(EEMessages.ITEM_LORE_SET_LORE.get())
							.replace("<item>", EChat.getButtomItem(item.get(), EChat.getTextColor(EEMessages.ITEM_LORE_SET_COLOR.get())))
							.replace("<line>", String.valueOf(line))
						.build());
					item.get().offer(Keys.ITEM_LORE, lore);
					player.setItemInMainHand(item.get());
					return true;
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
					return false;
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
						.replaceAll("<number>", line_name));
				return false;
			}
		} catch (NumberFormatException e) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", line_name));
			return false;
		}
	}
}