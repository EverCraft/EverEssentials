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
package fr.evercraft.everessentials.command.itemname;

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

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEItemNameSet extends ESubCommand<EverEssentials> {
	private int max_displayname;
	
	public EEItemNameSet(final EverEssentials plugin, final EEItemName command) {
        super(plugin, command, "set");
        this.reload();
    }
	
	public void reload() {
		this.max_displayname = this.plugin.getConfigs().getMaxDisplayname();	
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM_NAME.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.ITEM_NAME_SET_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source instanceof Player){
			if(((Player) source).getItemInHand(HandTypes.MAIN_HAND).isPresent()){
				return Arrays.asList("&bHello world");
			}
		}
		return Arrays.asList();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_NAME.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if(args.size() == 1){
			if(source instanceof EPlayer){
				return this.commandItemName((EPlayer) source, args.get(0));
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

	private CompletableFuture<Boolean> commandItemName(final EPlayer player, final String name) {
		this.plugin.getEServer().broadcast("" + this.max_displayname);
		if(name.length() > this.max_displayname) {
			EEMessages.ITEM_NAME_SET_ERROR.sender()
				.replace("{amount}", String.valueOf(this.max_displayname))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<ItemStack> item = player.getItemInMainHand();
		if(!player.getItemInMainHand().isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		item.get().offer(Keys.DISPLAY_NAME, EChat.of(name));
		EEMessages.ITEM_NAME_SET_NAME.sender()
			.replace("{item-before}", EChat.getButtomItem(item.get(), EEMessages.ITEM_NAME_SET_COLOR.getColor()))
			.replace("{item-after}", EChat.getButtomItem(item.get(), EEMessages.ITEM_NAME_SET_COLOR.getColor()))
			.sendTo(player);
		player.setItemInMainHand(item.get());
		return CompletableFuture.completedFuture(true);
	}
}
