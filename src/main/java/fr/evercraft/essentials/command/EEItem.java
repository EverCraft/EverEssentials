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
import fr.evercraft.everapi.sponge.UtilsItemTypes;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEItem extends EReloadCommand<EverEssentials> {
	
	private Collection<ItemType> items;
	private Collection<ItemType> blacklist; 

	public EEItem(final EverEssentials plugin) {
        super(plugin, "item");
        reload();
    }
	
	@Override
	public void reload() {
		this.items = UtilsItemTypes.getItems();
		this.blacklist = getBlacklist();
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ITEM.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.ITEM_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" +  EAMessages.ARGS_ITEM.get() + "> [" + EAMessages.ARGS_TYPE.get() +"] [" + EAMessages.ARGS_AMOUNT.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(ItemType type : this.items){
				suggests.add(type.getName().replaceAll("minecraft:", ""));
			}
		} else if(args.size() == 2){
			Optional<ItemStack> optItem = UtilsItemStack.getItem(args.get(0));
			if(optItem.isPresent()){
				Optional<Class<? extends CatalogType>> catalogType = UtilsItemTypes.getCatalogType(optItem.get());
				if(catalogType.isPresent()) {
					for(CatalogType type : this.plugin.getGame().getRegistry().getAllOf(catalogType.get())){
						suggests.add(type.getId());
						suggests.add(type.getName());
					}
				} else {
					suggests.add(String.valueOf(optItem.get().getMaxStackQuantity()));
					if(!suggests.contains("1")){
						suggests.add("1");
					}
				}
			}
		} else if(args.size() == 3){
			Optional<ItemStack> optItem = UtilsItemStack.getItem(args.get(0));
			if(optItem.isPresent()){
				Optional<Class<? extends CatalogType>> catalogType = UtilsItemTypes.getCatalogType(optItem.get());
				if(catalogType.isPresent()) {
					for(CatalogType type : this.plugin.getGame().getRegistry().getAllOf(catalogType.get())){
						if(type.getName().equalsIgnoreCase(args.get(1))){
							suggests.add("1");
							suggests.add(String.valueOf(optItem.get().getMaxStackQuantity()));
						}
					}
				}
			}
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 1) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandItem((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if(args.size() == 2) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandItem((EPlayer) source, args.get(0),  args.get(1));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 3) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandItem((EPlayer) source, args.get(0), args.get(1), args.get(2));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandItem(final EPlayer player, String item_name) {
		Optional<ItemType> optItem = UtilsItemTypes.getItemType(item_name);
		if(optItem.isPresent()){
			ItemType type = optItem.get();
			if(!this.blacklist.contains(type)){
				ItemStack item = ItemStack.of(type, optItem.get().getMaxStackQuantity());
				int quantity = item.getQuantity(); 
				player.giveItem(item);
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.ITEM_GIVE.get().replaceAll("<quantity>", String.valueOf(quantity)))
						.replace("<item>", EChat.getButtomItem(item, EEMessages.ITEM_GIVE_COLOR.getColor()))
					.build());
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_BLACKLIST.get());
				return false;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.get()
					.replaceAll("<item>", item_name));
			return false;
		}
	}

	public boolean commandItem(final EPlayer player, String item_name, String value) {
		Optional<ItemType> optItemType = UtilsItemTypes.getItemType(item_name);
		if(optItemType.isPresent()){
			ItemType itemType = optItemType.get();
			if(!this.blacklist.contains(itemType)){
				ItemStack item = ItemStack.of(itemType, optItemType.get().getMaxStackQuantity());
				Optional<ItemStack> optItemStack = UtilsItemTypes.getCatalogType(item, value);
				int quantity;
				if(optItemStack.isPresent()){
					item = optItemStack.get();
				} else {
					try {
						quantity = Integer.parseInt(value);
						if(quantity <= itemType.getMaxStackQuantity() && quantity > 0){
							item = ItemStack.of(itemType, quantity);
						} else {
							player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_QUANTITY.get()
									.replaceAll("<nb>", String.valueOf(itemType.getMaxStackQuantity())));
							return false;
						}
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get().replaceAll("<number>", value));
						return false;
					}
				}
				quantity = item.getQuantity();
				player.giveItem(item);
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.ITEM_GIVE.get().replaceAll("<quantity>", String.valueOf(quantity)))
						.replace("<item>", EChat.getButtomItem(item, EEMessages.ITEM_GIVE_COLOR.getColor()))
					.build());
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_BLACKLIST.get());
				return false;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.get()
					.replaceAll("<item>", item_name));
			return false;
		}
	}

	public boolean commandItem(final EPlayer player, String item_name, String data, String item_quantity) {
		Optional<ItemType> optItem = UtilsItemTypes.getItemType(item_name);
		if(optItem.isPresent()){
			ItemType itemType = optItem.get();
			if(!this.blacklist.contains(itemType)){
				ItemStack item = ItemStack.of(itemType, itemType.getMaxStackQuantity());
				Optional<ItemStack> optItemStack = UtilsItemTypes.getCatalogType(item, data);
				if(optItemStack.isPresent()){
					item = optItemStack.get();
					try {
						int quantity = Integer.parseInt(item_quantity);
						if(quantity <= itemType.getMaxStackQuantity() && quantity > 0){
							item.setQuantity(quantity);
							player.giveItem(item);
							player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.ITEM_GIVE.get().replaceAll("<quantity>", String.valueOf(quantity)))
									.replace("<item>", EChat.getButtomItem(item, EEMessages.ITEM_GIVE_COLOR.getColor()))
								.build());
							return true;
						} else {
							player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_QUANTITY.get()
									.replaceAll("<nb>", String.valueOf(itemType.getMaxStackQuantity())));
							return false;
						}
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get().replaceAll("<number>", item_quantity));
						return false;
					}
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_TYPE.get());
					return false;
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_BLACKLIST.get());
				return false;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_NOT_FOUND.get()
					.replaceAll("<item>", item_name));
			return false;
		}
	}
	
	private Collection<ItemType> getBlacklist(){
		Collection<ItemType> blacklist = new ArrayList<ItemType>();
		for(String bl : this.plugin.getConfigs().getListString("blacklist")){
			Optional<ItemType> optItemType = UtilsItemTypes.getItemType(bl);
			if(optItemType.isPresent()){
				blacklist.add(optItemType.get());
			} else {
				this.plugin.getLogger().warn("Erreur : " + bl + "n'est pas un nom d'un objet de minecraft.");
			}
		}
		return blacklist;
	}
}