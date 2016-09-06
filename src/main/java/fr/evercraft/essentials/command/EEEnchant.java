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

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.ENCHANT.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.ENCHANT_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_ENCHANTMENT.get() + "> [" + EAMessages.ARGS_LEVEL.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (source instanceof Player){
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(((Player) source).getUniqueId());
			if (player.isPresent() && player.get().getItemInMainHand().isPresent()) {
				if (args.size() == 1) {
					ItemStack item = player.get().getItemInMainHand().get();
					EnchantmentData enchantmentData = item.getOrCreate(EnchantmentData.class).get();
					
					// Si il y a plusieurs enchantements
					if (!enchantmentData.enchantments().isEmpty()) {
						for (Enchantment enchant : UtilsEnchantment.getEnchantments()) {
							if (enchant.canBeAppliedToStack(item)) {
								for (ItemEnchantment ench : enchantmentData.enchantments()) {
									if (enchant.isCompatibleWith(ench.getEnchantment())){
										suggests.add(enchant.getId().toLowerCase().replace("minecraft:", ""));
									}
								}
							}
						}
					// Il y a un seul enchantements
					} else {
						for (Enchantment enchant : UtilsEnchantment.getEnchantments()) {
							if (enchant.canBeAppliedByTable(item)) {
								suggests.add(enchant.getId().toLowerCase().replace("minecraft:", ""));
							}
						}
					}
				} else if (args.size() == 2){
					Optional<Enchantment> enchantment = this.getEnchantment(args.get(0));
					if (enchantment.isPresent()) {
						for (int cpt = enchantment.get().getMinimumLevel() ; cpt <= enchantment.get().getMaximumLevel() ; cpt++){
							suggests.add(String.valueOf(cpt));
						}
					}
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
			EPlayer player = (EPlayer) source;
			
			if (args.size() == 1) {
				Optional<Enchantment> enchantment = this.getEnchantment(args.get(0));
				
				// Si l'enchantement existe
				if (enchantment.isPresent()) {
					resultat = this.commandEnchant(player, enchantment.get(), enchantment.get().getMaximumLevel());
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ENCHANT_NOT_FOUND.get());
				}
			} else if (args.size() == 2) {
				Optional<Enchantment> enchantment = this.getEnchantment(args.get(0));
				
				// Si l'enchantement existe
				if (enchantment.isPresent()) {
					try {
						int level = Integer.parseInt(args.get(1));
						resultat = this.commandEnchant(player, enchantment.get(), level);
						
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
								.replaceAll("<number>", args.get(1)));
						return false;
					}
				// L'enchantement n'existe pas
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ENCHANT_NOT_FOUND.get());
				}
			// Nombre d'argument incorrect
			} else {
				source.sendMessage(this.help(source));
			}
		// La source n'est pas un joueur
		} else {
			source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
		}
		return resultat;
	}
	
	private boolean commandEnchant(final EPlayer player, Enchantment enchantment, int level) {
		// Le joueur n'a pas d'item dans la main
		if (!player.getItemInMainHand().isPresent()) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.EMPTY_ITEM_IN_HAND.get());
			return false;
		}
		
		ItemStack item = player.getItemInMainHand().get();
			
		// Le level est trop faible
		if (level < enchantment.getMinimumLevel()) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ENCHANT_LEVEL_TOO_LOW.get()
					.replaceAll("<number>", String.valueOf(level)));
			return false;
		}
		
		if (level > enchantment.getMaximumLevel()) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.ENCHANT_LEVEL_TOO_HIGHT.get()
					.replaceAll("<number>", String.valueOf(level)));
			return false;
		}

		EnchantmentData enchantment_data = item.getOrCreate(EnchantmentData.class).get();
					
		// L'enchantement n'est pas applicable sur cet item
		if (!UtilsEnchantment.canBeAppliedToItemStack(item, enchantment)) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.ENCHANT_INCOMPATIBLE.get()
							.replaceAll("<enchantment>", enchantment.getId().toLowerCase()
									.replace("minecraft:", "")
									.replaceAll(" ", ""))
							.replaceAll("<level>", String.valueOf(level)))
					.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(EEMessages.ENCHANT_ITEM_COLOR.get())))
					.build());
			return false;
		}
		
		enchantment_data.set(enchantment_data.enchantments().add(new ItemEnchantment(enchantment, level)));
		item.offer(enchantment_data);
		player.setItemInMainHand(item);
		
		player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
				.append(EEMessages.ENCHANT_SUCCESSFULL.get())
						.replace("<enchantment>", enchantment.getTranslation())
						.replace("<level>", String.valueOf(level))
				.replace("<item>", EChat.getButtomItem(item, EChat.getTextColor(EEMessages.ENCHANT_ITEM_COLOR.get())))
				.build());
		return true;
	}
	
	private Optional<Enchantment> getEnchantment(String enchant) {
		return UtilsEnchantment.getID("minecraft:" + enchant.toLowerCase().replace("minecraft:", ""));
	}
}
