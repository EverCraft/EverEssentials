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
import java.util.Collection;
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
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;

public class EEWhitelistAdd extends ESubCommand<EverEssentials> {
	
	public EEWhitelistAdd(final EverEssentials plugin, final EEWhitelist command) {
        super(plugin, command, "add");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHITELIST_MANAGE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WHITELIST_ADD_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (GameProfile player : this.plugin.getEServer().getGameProfileManager().getCache().getProfiles()) {
				if (player.getName().isPresent()) {
					suggests.add(player.getName().orElse(player.getUniqueId().toString()));
				}
			}
		}
		return suggests;
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		if (args.size() == 1) {
			this.plugin.getGame().getScheduler().createTaskBuilder()
												.async()
												.execute(() -> this.commandWhitelistAdd(source, args.get(0)))
												.submit(this.plugin);
			return true;
		} else {
			source.sendMessage(this.help(source));
		}
		return false;
	}

	private boolean commandWhitelistAdd(final CommandSource player, final String identifier) {
		Optional<GameProfile> gameprofile = this.plugin.getEServer().getGameProfile(identifier);
		// Le joueur existe
		if (!gameprofile.isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return false;
		}
		
		Optional<WhitelistService> whitelist = this.plugin.getEverAPI().getManagerService().getWhitelist();
		if (!whitelist.isPresent()) {	
			EAMessages.COMMAND_ERROR.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return false;
		}
				
		if (whitelist.get().addProfile(gameprofile.get())) {
			EEMessages.WHITELIST_ADD_ERROR.sender()
				.replace("<player>", gameprofile.get().getName().orElse(identifier))
				.sendTo(player);
			return false;
		}
		
		EEMessages.WHITELIST_ADD_PLAYER.sender()
			.replace("<player>", gameprofile.get().getName().orElse(identifier))
			.sendTo(player);
		return true;
	}
}