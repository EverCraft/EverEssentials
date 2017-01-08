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
		return EEMessages.ITEM_LORE_REMOVE_DESCRIPTION.getText();
	}
	
	@Override
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			if(source instanceof Player) {
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(((Player) source).getUniqueId());
				if(player.isPresent()) {
					Optional<ItemStack> item = player.get().getItemInMainHand();
					if(item.isPresent()) {
						Optional<List<Text>> lore = item.get().get(Keys.ITEM_LORE);
						if(lore.isPresent()) {
							suggests.add("1");
							if(lore.get().size() >= 1) {
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
		return Text.builder("/" + this.getName() + "<" + EAMessages.ARGS_LINE.getString() +  ">")
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

	private boolean commandItemLoreRemove(final EPlayer player, final String line_name) {
		Optional<Integer> line = UtilsInteger.parseInt(line_name);
		if (line.isPresent()) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<number>", line_name)
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
		
		if(line.get() > lore.size() || line.get() == 0) {
			EEMessages.ITEM_LORE_REMOVE_ERROR.sender()
				.replace("<max>", String.valueOf(lore.size()))
				.sendTo(player);
		}
		
		lore.remove(line.get() - 1);
		item.get().offer(Keys.ITEM_LORE, lore);
		EEMessages.ITEM_LORE_REMOVE_LORE.sender()
			.replace("<item>", EChat.getButtomItem(item.get(), EEMessages.ITEM_LORE_REMOVE_COLOR.getColor()))
			.replace("<line>", String.valueOf(line.get()))
			.sendTo(player);
		item.get().offer(Keys.ITEM_LORE, lore);
		player.setItemInMainHand(item.get());
		return true;

	}
}