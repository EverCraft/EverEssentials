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
package fr.evercraft.essentials.command.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWhitelistList extends ESubCommand<EverEssentials> {
	public EEWhitelistList(final EverEssentials plugin, final EEWhitelist command) {
        super(plugin, command, "list");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHITELIST.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WHITELIST_LIST_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0) {
			resultat = commandWhitelistList(source);
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWhitelistList(final CommandSource player) {
		Optional<WhitelistService> optWhitelist = this.plugin.getEverAPI().getManagerService().getWhitelist();
		if(optWhitelist.isPresent()){
			List<Text> lists = new ArrayList<Text>();
			WhitelistService whitelist = optWhitelist.get();
			if(!whitelist.getWhitelistedProfiles().isEmpty()){
				if(player.hasPermission(EEPermissions.WHITELIST_MANAGE.get())) {
					for(GameProfile profile : whitelist.getWhitelistedProfiles()) {
						String name = profile.getName().orElse(profile.getUniqueId().toString());
						lists.add(ETextBuilder.toBuilder(EEMessages.WHITELIST_LIST_LINE.get()
									.replaceAll("<player>", name))
								.replace("<delete>", getButtonDelete(name))
								.build());
					}
				} else {
					for(GameProfile profile : whitelist.getWhitelistedProfiles()) {
						lists.add(ETextBuilder.toBuilder(EEMessages.WHITELIST_LIST_LINE.get()
									.replaceAll("<player>", profile.getName().orElse(profile.getUniqueId().toString())))
								.replace("<delete>", "")
								.build());
						}
				}
			} else {
				lists.add(EEMessages.WHITELIST_LIST_NO_PLAYER.getText());
			}
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.WHITELIST_LIST_TITLE.getText().toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName())).build(), lists, player);
		}
		return true;
	}
	
	public Text getButtonDelete(final String name){
		return EEMessages.WHITELIST_LIST_REMOVE.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.WHITELIST_LIST_REMOVE_HOVER.get()
							.replaceAll("<player>", name))))
					.onClick(TextActions.runCommand("/whitelist remove " + name))
					.build();
	}
}
