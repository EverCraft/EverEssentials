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

public class EEItemLoreRemove extends ESubCommand<EverEssentials> {
	public EEItemLoreRemove(final EverEssentials plugin, final EEItemLore command) {
        super(plugin, command, "remove");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM_LORE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.ITEM_LORE_REMOVE_DESCRIPTION.get());
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
						Optional<List<Text>> lore = item.get().get(Keys.ITEM_LORE);
						this.plugin.getEServer().broadcast("test 1 ");
						if(lore.isPresent()){
							this.plugin.getEServer().broadcast("test 2 ");
							suggests.add("1");
							if(lore.get().size() >= 1){
								suggests.add(String.valueOf(lore.get().size()));
							}
						}
					}
				}
			}
		}
		return suggests;
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + "<" + EAMessages.ARGS_LINE.get() +  ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		if(args.size() == 1){
			if(source instanceof EPlayer){
				commandItemLoreRemove((EPlayer) source, args.get(0));
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

	private boolean commandItemLoreRemove(final EPlayer player, final String line_name) {
		try {
			int line = Integer.parseInt(line_name);
			Optional<ItemStack> item = player.getItemInMainHand();
			if(item.isPresent()){
				List<Text> lore = new ArrayList<Text>();
				if(item.get().get(Keys.ITEM_LORE).isPresent()){
					lore = item.get().get(Keys.ITEM_LORE).get();
				}
				if(line <= lore.size() && line > 0){
					lore.remove(line - 1);
					item.get().offer(Keys.ITEM_LORE, lore);
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get()).append(EEMessages.ITEM_LORE_REMOVE_LORE.get())
							.replace("<item>", EChat.getButtomItem(item.get(), EChat.getTextColor(EEMessages.ITEM_LORE_REMOVE_COLOR.get())))
							.replace("<line>", String.valueOf(line))
						.build());
					item.get().offer(Keys.ITEM_LORE, lore);
					player.setItemInMainHand(item.get());
					return true;
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_LORE_REMOVE_ERROR.get()
							.replace("<max>", String.valueOf(lore.size())));
					return false;
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
				return false;
			}
		} catch (NumberFormatException e) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", line_name));
			return false;
		}
	}
}