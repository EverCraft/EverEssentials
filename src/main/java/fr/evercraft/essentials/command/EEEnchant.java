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
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.type.HandTypes;
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
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_ENCHANTMENT.getString() + "> [" + EAMessages.ARGS_LEVEL.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (source instanceof Player){
			Player player = (Player) source;
			ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
			if (item != null) {
				if (args.size() == 1) {
					EnchantmentData enchantmentData = item.getOrCreate(EnchantmentData.class).orElse(null);
					
					// Si il y a plusieurs enchantements
					if (enchantmentData != null && !enchantmentData.enchantments().isEmpty()) {
						for (Enchantment enchant : UtilsEnchantment.getAll()) {
							if(UtilsEnchantment.canBeAppliedToItemStack(item, enchant)){
								suggests.add(enchant.getId().toLowerCase().replace("minecraft:", ""));
							}
						}
					// Il y a un seul enchantements
					} else {
						for (Enchantment enchant : UtilsEnchantment.getAll()) {
							if(UtilsEnchantment.canBeAppliedToItemStack(item, enchant)){
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
					EEMessages.ENCHANT_NOT_FOUND.sendTo(player);
				}
			} else if (args.size() == 2) {
				Optional<Enchantment> enchantment = this.getEnchantment(args.get(0));
				
				// Si l'enchantement existe
				if (enchantment.isPresent()) {
					try {
						int level = Integer.parseInt(args.get(1));
						resultat = this.commandEnchant(player, enchantment.get(), level);
						
					} catch (NumberFormatException e) {
						EAMessages.IS_NOT_NUMBER.sender()
							.prefix(EEMessages.PREFIX)
							.replace("<number>", args.get(1))
							.sendTo(source);
						return false;
					}
				// L'enchantement n'existe pas
				} else {
					EEMessages.ENCHANT_NOT_FOUND.sendTo(player);
				}
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
		return resultat;
	}
	
	private boolean commandEnchant(final EPlayer player, Enchantment enchantment, int level) {
		// Le joueur n'a pas d'item dans la main
		if (!player.getItemInMainHand().isPresent()) {
			EAMessages.EMPTY_ITEM_IN_HAND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return false;
		}
		
		ItemStack item = player.getItemInMainHand().get();
			
		// Le level est trop faible
		if (level < enchantment.getMinimumLevel()) {
			EEMessages.ENCHANT_LEVEL_TOO_LOW.sender()
				.replace("<number>", String.valueOf(level))
				.sendTo(player);
			return false;
		}
		
		if (level > enchantment.getMaximumLevel()) {
			EEMessages.ENCHANT_LEVEL_TOO_HIGHT.sender()
				.replace("<number>", String.valueOf(level))
				.sendTo(player);
			return false;
		}

		EnchantmentData enchantment_data = item.getOrCreate(EnchantmentData.class).get();
					
		// L'enchantement n'est pas applicable sur cet item
		if (!UtilsEnchantment.canBeAppliedToItemStack(item, enchantment)) {
			EEMessages.ENCHANT_INCOMPATIBLE.sender()
				.replace("<enchantment>", () -> enchantment.getId().toLowerCase().replace("minecraft:", "").replaceAll(" ", ""))
				.replace("<level>", String.valueOf(level))
				.replace("<item>", () -> EChat.getButtomItem(item, EEMessages.ENCHANT_ITEM_COLOR.getColor()))
				.sendTo(player);
			return false;
		}
		
		enchantment_data.set(enchantment_data.enchantments().add(new ItemEnchantment(enchantment, level)));
		item.offer(enchantment_data);
		player.setItemInMainHand(item);
		
		EEMessages.ENCHANT_SUCCESSFULL.sender()
			.replace("<enchantment>", enchantment.getTranslation().get())
			.replace("<level>", String.valueOf(level))
			.replace("<item>", () -> EChat.getButtomItem(item, EEMessages.ENCHANT_ITEM_COLOR.getColor()))
			.sendTo(player);
		return true;
	}
	
	private Optional<Enchantment> getEnchantment(String enchant) {
		return UtilsEnchantment.getID("minecraft:" + enchant.toLowerCase().replace("minecraft:", ""));
	}
}
