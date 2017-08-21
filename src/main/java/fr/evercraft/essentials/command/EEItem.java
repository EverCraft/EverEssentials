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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.item.ItemType;
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
import fr.evercraft.everapi.plugin.command.ReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsItemStack;
import fr.evercraft.everapi.sponge.UtilsItemType;

public class EEItem extends ECommand<EverEssentials> implements ReloadCommand {
	
	private Collection<ItemType> items;
	private Collection<ItemType> blacklist; 

	public EEItem(final EverEssentials plugin) {
        super(plugin, "item", "i");
        
        this.reload();
    }
	
	@Override
	public void reload() {
		this.items = UtilsItemType.getItems();
		this.blacklist = this.getBlacklist();
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.ITEM_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_ITEM.getString() + "> [" + EAMessages.ARGS_TYPE.getString() +"] "
				+ "[" + EAMessages.ARGS_AMOUNT.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			if(args.get(0).startsWith("minecraft")) {
				for (ItemType type : this.items) {
					suggests.add(type.getName().toUpperCase());
				}
			} else {
				for (ItemType type : this.items) {
					suggests.add(type.getName().replaceAll("minecraft:", "").toUpperCase());
				}
			}
		} else if (args.size() == 2) { 
			Optional<ItemStack> optItem = UtilsItemStack.getItem(args.get(0));
			if (optItem.isPresent()) {
				Optional<Class<? extends CatalogType>> catalogType = UtilsItemType.getCatalogType(optItem.get());
				if (catalogType.isPresent()) {
					for (CatalogType type : this.plugin.getGame().getRegistry().getAllOf(catalogType.get())){
						suggests.add(type.getName());
					}
				} else {
					suggests.add(String.valueOf(optItem.get().getMaxStackQuantity()));
					if (!suggests.contains("1")){
						suggests.add("1");
					}
				}
			}
		} else if (args.size() == 3) {
			Optional<ItemStack> optItem = UtilsItemStack.getItem(args.get(0));
			if (optItem.isPresent()) {
				Optional<Class<? extends CatalogType>> catalogType = UtilsItemType.getCatalogType(optItem.get());
				if (catalogType.isPresent()) {
					suggests.add(String.valueOf(optItem.get().getMaxStackQuantity()));
					if (!suggests.contains("1")){
						suggests.add("1");
					}
				}
			}
		}
		return suggests;
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si la source est un joueur
		if (source instanceof EPlayer) {
			if (args.size() == 1) {
				return this.commandItem((EPlayer) source, args.get(0));
			} else if (args.size() == 2) {
				return this.commandItem((EPlayer) source, args.get(0), args.get(1));
			} else if (args.size() == 3) {
				return this.commandItem((EPlayer) source, args.get(0), args.get(1), args.get(2));
			// Nombre d'argument incorrect
			} else {
				source.sendMessage(this.help(source));
			}
		// La source n'est pas un joueur
		} else {
			EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(source);
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandItem(final EPlayer player, String type_string) {
		Optional<ItemType> type = UtilsItemType.getItemType(type_string);
		
		// Le type n'existe pas
		if (!type.isPresent()) {
			EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.sender()
				.replace("{item}", type_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// L'item est dans la BlackList
		if (this.blacklist.contains(type.get())) {
			EEMessages.ITEM_ERROR_ITEM_BLACKLIST.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		this.commandGive(player, ItemStack.of(type.get(), 1), type.get().getMaxStackQuantity());
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandItem(final EPlayer player, String type_string, String value) {
		Optional<ItemType> type = UtilsItemType.getItemType(type_string);
		
		// Le type n'existe pas
		if (!type.isPresent()) {
			EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.sender()
				.replace("{item}", type_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// L'item est dans la BlackList
		if (this.blacklist.contains(type.get())) {
			EEMessages.ITEM_ERROR_ITEM_BLACKLIST.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		ItemStack item = ItemStack.of(type.get(), 1);
		int quantity = type.get().getMaxStackQuantity();
		Optional<ItemStack> item_data = UtilsItemType.getCatalogType(item, value);
		
		// Si la valeur est une data
		if (item_data.isPresent()){
			return this.commandGive(player, item_data.get(), quantity);
		}
		
		// La quantité n'est pas un nombre
		try {
			quantity = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", value)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		// La valeur n'est pas correcte
		if (quantity < 1 && quantity > type.get().getMaxStackQuantity()) {
			EEMessages.ITEM_ERROR_QUANTITY.sender()
				.replace("{amount}", String.valueOf(type.get().getMaxStackQuantity()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		return this.commandGive(player, item, quantity);
	}

	private CompletableFuture<Boolean> commandItem(final EPlayer player, String type_string, String data_string, String quantity_string) {
		Optional<ItemType> type = UtilsItemType.getItemType(type_string);
		
		// Le type n'existe pas
		if (!type.isPresent()) {
			EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.sender()
				.replace("{item}", type_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// L'item est dans la BlackList
		if (this.blacklist.contains(type.get())) {
			EEMessages.ITEM_ERROR_ITEM_BLACKLIST.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		ItemStack item = ItemStack.of(type.get(), 1);
		int quantity = type.get().getMaxStackQuantity();
		Optional<ItemStack> item_data = UtilsItemType.getCatalogType(item, data_string);
		
		// Si la valeur est une data
		if (!item_data.isPresent()) {
			EEMessages.ITEM_ERROR_DATA.sender()
				.replace("{item}", data_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// La quantité n'est pas un nombre
		try {
			quantity = Integer.parseInt(quantity_string);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", quantity_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		// La valeur n'est pas correcte
		if (quantity < 1 && quantity > type.get().getMaxStackQuantity()) {
			EEMessages.ITEM_ERROR_DATA.sender()
				.replace("{amount}", String.valueOf(type.get().getMaxStackQuantity()))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		return this.commandGive(player, item_data.get(), quantity);
	}
	
	private CompletableFuture<Boolean> commandGive(final EPlayer player, ItemStack item, Integer quantity) {
		item.setQuantity(quantity);
		Text itemText = EChat.getButtomItem(item, EEMessages.ITEM_GIVE_COLOR.getColor());
		
		if(player.giveItem(item).isPresent()) {
			EAMessages.PLAYER_INVENTORY_FULL_AND_DROP.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{item}", itemText)
				.sendTo(player);
		}
		
		EEMessages.ITEM_GIVE.sender()
			.replace("{quantity}", quantity.toString())
			.replace("{item}", itemText)
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private Collection<ItemType> getBlacklist(){
		Collection<ItemType> blacklist = new ArrayList<ItemType>();
		
		this.plugin.getConfigs().getListString("blacklist").forEach(item -> {
			Optional<ItemType> type = UtilsItemType.getItemType(item);
			if (type.isPresent()){
				blacklist.add(type.get());
			} else {
				this.plugin.getELogger().warn("BlackList error : '" + item + "' is not a name of an object minecraft.");
			}
		});
		return blacklist;
	}
}
