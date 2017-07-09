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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
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
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source instanceof Player) {
			Optional<ItemStack> item = ((Player) source).getItemInHand(HandTypes.MAIN_HAND);
			if(item.isPresent()) {
				Optional<List<Text>> lore = item.get().get(Keys.ITEM_LORE);
				if(lore.isPresent()) {
					if(lore.get().size() >= 1) {
						return Arrays.asList("1", String.valueOf(lore.get().size()));
					} else {
						return Arrays.asList("1");
					}
				}
			}
		}
		return Arrays.asList();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + "<" + EAMessages.ARGS_LINE.getString() +  ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if(args.size() == 1){
			if(source instanceof EPlayer){
				return this.commandItemLoreRemove((EPlayer) source, args.get(0));
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandItemLoreRemove(final EPlayer player, final String line_name) {
		Optional<Integer> line = UtilsInteger.parseInt(line_name);
		if (line.isPresent()) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<number>", line_name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<ItemStack> item = player.getItemInMainHand();
		if(!item.isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
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
		return CompletableFuture.completedFuture(true);
	}
}