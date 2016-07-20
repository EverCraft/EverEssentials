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
		return Text.builder("/item <objet> [quantité] [type]").onClick(TextActions.suggestCommand("/item"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(ItemType type : this.items){
				suggests.add(type.getName().replaceAll("minecraft:", ""));
			}
		} else if(args.size() == 2){
			suggests.add("1");
			Optional<ItemType> optItem = UtilsItemTypes.getItemType(args.get(0));
			if(optItem.isPresent()){
				suggests.add(String.valueOf(optItem.get().getMaxStackQuantity()));
			}
		} else if(args.size() == 3){
			
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
				resultat = commandItem((EPlayer) source, args.get(0), args.get(1));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 3) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandItem((EPlayer) source, args.get(0), args.get(1));
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
				player.giveItem(item);
				player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.ITEM_GIVE.getText())
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
	
	public boolean commandItem(final EPlayer player, String item_name, String arg) {
		Optional<ItemType> optItem = UtilsItemTypes.getItemType(item_name);
		if(optItem.isPresent()){
			ItemType type = optItem.get();
			if(!this.blacklist.contains(type)){
				try {
					int quantity = Integer.parseInt(arg);
					if(quantity < type.getMaxStackQuantity()){
						ItemStack item = ItemStack.of(type, quantity);
						player.giveItem(item);
						player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
							.append(EEMessages.ITEM_GIVE.getText())
								.replace("<item>", EChat.getButtomItem(item, EEMessages.ITEM_GIVE_COLOR.getColor()))
							.build());
						return true;
					} else {
						player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ITEM_ERROR_ITEM_BLACKLIST.get()
								.replaceAll("<nb>", String.valueOf(type.getMaxStackQuantity())));
						return false;
					}
				} catch (NumberFormatException e) {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get().replaceAll("<number>", arg));
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
				this.plugin.getLogger().warn("Erreur : " + bl + "n'est pas un nom d'un item de minecraft.");
			}
		}
		return blacklist;
	}
}