/**
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
package fr.evercraft.essentials.commands;

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

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEnchantment;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEEnchant extends ECommand<EverEssentials> {
	
	public EEEnchant(final EverEssentials plugin) {
        super(plugin, "enchant");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("ENCHANT"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("ENCHANT_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/enchant <enchantement> [niveau]").onClick(TextActions.suggestCommand("/enchant "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (source instanceof Player){
			Player player = (Player) source;
			if(args.size() == 1){
				if (player.getItemInHand().isPresent()){
					ItemStack item = player.getItemInHand().get();
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
				if (player.getItemInHand().isPresent()){
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
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if(args.size() == 2) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandEnchant((EPlayer) source, args.get(0), args.get(1));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandEnchant(final EPlayer player, String enchantname, String lvl) {
		if (player.getItemInHand().isPresent()){
			ItemStack item = player.getItemInHand().get();
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
								player.setItemInHand(item);
								player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") 
										+ this.plugin.getMessages().getMessage("ENCHANT_SUCCESSFULL"));
								return true;
							} else {
								player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
										.append(this.plugin.getMessages().getMessage("ENCHANT_INCOMPATIBLE"))
										.replace("<item>", EChat.getButtomItem(item, 
												EChat.getTextColor(this.plugin.getMessages().getMessage("MORE_ITEM_COLOR"))))
										.build());
								return false;
							}
						} else {
							player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("ENCHANT_LEVEL_TOO_HIGHT"));
							return false;
						}
					} else {
						player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NUMBER_INVALID"));
						return false;
					}
				} catch (NumberFormatException e) {
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("IS_NOT_NUMBER")
							.replaceAll("<number>", lvl));
					return false;
				}
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("ENCHANT_NOT_FOUND"));
				return false;
			}
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("EMPTY_ITEM_IN_HAND"));
			return false;
		}
	}
	
	public boolean commandEnchant(final EPlayer player, String enchantname) {
		if (player.getItemInHand().isPresent()){
			ItemStack item = player.getItemInHand().get();
			Optional<Enchantment> optEnchantment = UtilsEnchantment.getID("minecraft:" + enchantname);
			if (optEnchantment.isPresent()){
				Enchantment enchantment = optEnchantment.get();
				ItemEnchantment itemEnchant = new ItemEnchantment(enchantment, enchantment.getMaximumLevel());
				EnchantmentData enchantmentData = item.getOrCreate(EnchantmentData.class).get();
				if (itemEnchant.getEnchantment().canBeAppliedToStack(item)){
					enchantmentData.set(enchantmentData.enchantments().add(itemEnchant));
					item.offer(enchantmentData);
					player.setItemInHand(item);
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") 
							+ this.plugin.getMessages().getMessage("ENCHANT_SUCCESSFULL"));
					return true;
				} else {
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("ENCHANT_INCOMPATIBLE"))
							.replace("<item>", EChat.getButtomItem(item, 
									EChat.getTextColor(this.plugin.getMessages().getMessage("MORE_ITEM_COLOR"))))
							.build());
					return false;
				}
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("ENCHANT_NOT_FOUND"));
				return false;
			}
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("EMPTY_ITEM_IN_HAND"));
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
