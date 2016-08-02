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
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.Enchantment;
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
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEnchantment;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEEnchant extends ECommand<EverEssentials> {
	
	public EEEnchant(final EverEssentials plugin) {
        super(plugin, "enchant");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ENCHANT.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.ENCHANT_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_ENCHANTMENT.get() + "> [" + EAMessages.ARGS_ENCHANTMENT.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (source instanceof Player){
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(((Player) source).getUniqueId());
			if(player.isPresent()) {
				if(args.size() == 1){
					if (player.get().getItemInMainHand().isPresent()){
						ItemStack item = player.get().getItemInMainHand().get();
						EnchantmentData enchantmentData = item.getOrCreate(EnchantmentData.class).get();
						if (!enchantmentData.enchantments().isEmpty()){
							for (Enchantment enchant : getApplicableEnchant(enchantmentData, item)){
								suggests.add(enchant.getId().replace("minecraft:", ""));
							}
						} else {
							for (Enchantment enchant : UtilsEnchantment.getEnchantments()){
								if (enchant.canBeAppliedByTable(item)){
									suggests.add(enchant.getId().replace("minecraft:", ""));
								}
							}
						}
					}
				} else if(args.size() == 2){
					if (player.get().getItemInMainHand().isPresent()){
						Optional<Enchantment> optEnchantment = UtilsEnchantment.getID("minecraft:" + args.get(0));
						if (optEnchantment.isPresent()){
							Enchantment enchantment = optEnchantment.get();
							for (int cpt = 1 ; cpt <= enchantment.getMaximumLevel() ; cpt++){
								suggests.add(String.valueOf(cpt));
							}
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
		if (args.size() == 1){
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandEnchant((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 2) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandEnchant((EPlayer) source, args.get(0), args.get(1));
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
	
	public boolean commandEnchant(final EPlayer player, String enchantname, String lvl) {
		if (player.getItemInMainHand().isPresent()){
			ItemStack item = player.getItemInMainHand().get();
			Optional<Enchantment> optEnchantment = UtilsEnchantment.getID("minecraft:" + enchantname);
			if (optEnchantment.isPresent()){
				Enchantment enchantment = optEnchantment.get();
				try {
					int level = Integer.parseInt(lvl);
					if (level > 0){
						if (level <= enchantment.getMaximumLevel()){
							ItemEnchantment itemEnchant = new ItemEnchantment(enchantment, level);
							EnchantmentData enchantmentData = item.getOrCreate(EnchantmentData.class).get();
							if (itemEnchant.getEnchantment().canBeAppliedToStack(item)){
								enchantmentData.set(enchantmentData.enchantments().add(itemEnchant));
								item.offer(enchantmentData);
								player.setItemInMainHand(item);
								player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.ENCHANT_SUCCESSFULL.getText()));
								return true;
							} else {
								player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
										.append(EEMessages.ENCHANT_INCOMPATIBLE.get())
										.replace("<item>", EChat.getButtomItem(item, 
												EChat.getTextColor(EEMessages.MORE_ITEM_COLOR.get())))
										.build());
								return false;
							}
						} else {
							player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ENCHANT_LEVEL_TOO_HIGHT.get());
							return false;
						}
					} else {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.NUMBER_INVALID.get());
						return false;
					}
				} catch (NumberFormatException e) {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
							.replaceAll("<number>", lvl));
					return false;
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ENCHANT_NOT_FOUND.get());
				return false;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
			return false;
		}
	}
	
	public boolean commandEnchant(final EPlayer player, String enchantname) {
		if (player.getItemInMainHand().isPresent()){
			ItemStack item = player.getItemInMainHand().get();
			Optional<Enchantment> optEnchantment = UtilsEnchantment.getID("minecraft:" + enchantname);
			if (optEnchantment.isPresent()){
				Enchantment enchantment = optEnchantment.get();
				ItemEnchantment itemEnchant = new ItemEnchantment(enchantment, enchantment.getMaximumLevel());
				EnchantmentData enchantmentData = item.getOrCreate(EnchantmentData.class).get();
				if (itemEnchant.getEnchantment().canBeAppliedToStack(item)){
					enchantmentData.set(enchantmentData.enchantments().add(itemEnchant));
					item.offer(enchantmentData);
					player.setItemInMainHand(item);
					player.sendMessage(EEMessages.PREFIX.get() 
							+ EEMessages.ENCHANT_SUCCESSFULL.get());
					return true;
				} else {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.ENCHANT_INCOMPATIBLE.get())
							.replace("<item>", EChat.getButtomItem(item, 
									EChat.getTextColor(EEMessages.MORE_ITEM_COLOR.get())))
							.build());
					return false;
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ENCHANT_NOT_FOUND.get());
				return false;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
			return false;
		}
	}
	
	private List<Enchantment> getApplicableEnchant(EnchantmentData enchantmentData, ItemStack item){
		List<Enchantment> list = UtilsEnchantment.getEnchantments();
		for (Enchantment enchant : UtilsEnchantment.getEnchantments()){
			for(ItemEnchantment ench : enchantmentData.enchantments()){
				if(!enchant.canBeAppliedToStack(item) || !enchant.isCompatibleWith(ench.getEnchantment())){
					list.remove(enchant);
				}
			}
		}
		return list;
	}
}
