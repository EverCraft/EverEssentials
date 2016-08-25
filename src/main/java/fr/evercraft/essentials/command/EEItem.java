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
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsItemStack;
import fr.evercraft.everapi.sponge.UtilsItemType;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEItem extends EReloadCommand<EverEssentials> {
	
	private Collection<ItemType> items;
	private Collection<ItemType> blacklist; 

	public EEItem(final EverEssentials plugin) {
        super(plugin, "item");
        
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
		return Text.builder("/" + this.getName() + " <" +  EAMessages.ARGS_ITEM.get() + "> [" + EAMessages.ARGS_TYPE.get() 
				+"] [" + EAMessages.ARGS_AMOUNT.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			if(args.get(0).startsWith("minecraft")) {
				for (ItemType type : this.items) {
					suggests.add(type.getName());
				}
			} else {
				for (ItemType type : this.items) {
					suggests.add(type.getName().replaceAll("minecraft:", ""));
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
			if (optItem.isPresent()){
				suggests.add(String.valueOf(optItem.get().getMaxStackQuantity()));
				if (!suggests.contains("1")){
					suggests.add("1");
				}
			}
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si la source est un joueur
		if (source instanceof EPlayer) {
			if (args.size() == 1) {
				resultat = this.commandItem((EPlayer) source, args.get(0));
			} else if (args.size() == 2) {
				resultat = this.commandItem((EPlayer) source, args.get(0), args.get(1));
			} else if (args.size() == 3) {
				resultat = this.commandItem((EPlayer) source, args.get(0), args.get(1), args.get(2));
			// Nombre d'argument incorrect
			} else {
				source.sendMessage(this.help(source));
			}
		// La source n'est pas un joueur
		} else {
			source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
		}
		
		return resultat;
	}
	
	private boolean commandItem(final EPlayer player, String item_name) {
		Optional<ItemType> type = UtilsItemType.getItemType(item_name);
		if (type.isPresent()){
			if (!this.blacklist.contains(type.get())){
				this.commandGive(player, ItemStack.of(type.get(), 1), type.get().getMaxStackQuantity());
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_BLACKLIST.get());
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.get()
					.replaceAll("<item>", item_name));
		}
		return false;
	}

	private boolean commandItem(final EPlayer player, String type_string, String value) {
		Optional<ItemType> type = UtilsItemType.getItemType(type_string);
		// Le type existe
		if (type.isPresent()) {
			// L'item n'est pas dans la blacklist
			if (!this.blacklist.contains(type.get())) {
				ItemStack item = ItemStack.of(type.get(), 1);
				int quantity = type.get().getMaxStackQuantity();
				
				Optional<ItemStack> item_data = UtilsItemType.getCatalogType(item, value);
				// Si la valeur est une data
				if (item_data.isPresent()){
					return this.commandGive(player, item_data.get(), quantity);
				// La valeur n'est pas une data
				} else {
					try {
						quantity = Integer.parseInt(value);
						if (quantity <= type.get().getMaxStackQuantity() && quantity > 0){
							return this.commandGive(player, item_data.get(), quantity);
						} else {
							player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_QUANTITY.get()
									.replaceAll("<amount>", String.valueOf(type.get().getMaxStackQuantity())));
						}
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get().replaceAll("<number>", value));
					}
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_BLACKLIST.get());
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.get()
					.replaceAll("<item>", type_string));
		}
		return false;
	}

	private boolean commandItem(final EPlayer player, String type_string, String data_string, String quantity_string) {
		Optional<ItemType> type = UtilsItemType.getItemType(type_string);
		// Le type existe
		if (type.isPresent()) {
			// L'item n'est pas dans la blacklist
			if (!this.blacklist.contains(type.get())) {
				ItemStack item = ItemStack.of(type.get(), 1);
				
				Optional<ItemStack> item_data = UtilsItemType.getCatalogType(item, data_string);
				// Si la valeur est une data
				if (item_data.isPresent()){
					try {
						int quantity = Integer.parseInt(quantity_string);
						if (quantity <= type.get().getMaxStackQuantity() && quantity > 0){
							return this.commandGive(player, item_data.get(), quantity);
						} else {
							player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_QUANTITY.get()
									.replaceAll("<amount>", String.valueOf(type.get().getMaxStackQuantity())));
						}
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
								.replaceAll("<number>", quantity_string));
					}
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_DATA.get()
							.replaceAll("<item>", data_string));
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_BLACKLIST.get());
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.get()
					.replaceAll("<item>", type_string));
		}
		return false;
	}
	
	private boolean commandGive(final EPlayer player, ItemStack item, Integer quantity) {
		item.setQuantity(quantity);
		
		if(!player.giveItem(item).isPresent()) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
				.append(EEMessages.ITEM_GIVE.get().replaceAll("<quantity>", quantity.toString()))
					.replace("<item>", EChat.getButtomItem(item, EEMessages.ITEM_GIVE_COLOR.getColor()))
				.build());
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_INVENTORY_FULL.get());
		}
		return true;
	}
	
	private Collection<ItemType> getBlacklist(){
		Collection<ItemType> blacklist = new ArrayList<ItemType>();
		for (String item : this.plugin.getConfigs().getListString("blacklist")){
			Optional<ItemType> type = UtilsItemType.getItemType(item);
			if (type.isPresent()){
				blacklist.add(type.get());
			} else {
				this.plugin.getLogger().warn("BlackList error : '" + item + "' is not a name of an object minecraft.");
			}
		}
		return blacklist;
	}
}