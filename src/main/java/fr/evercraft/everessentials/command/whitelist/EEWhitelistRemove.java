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
package fr.evercraft.everessentials.command.whitelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEWhitelistRemove extends ESubCommand<EverEssentials> {
	
	public EEWhitelistRemove(final EverEssentials plugin, final EEWhitelist command) {
        super(plugin, command, "remove");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHITELIST_MANAGE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WHITELIST_REMOVE_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			List<String> suggests = new ArrayList<String>();
			for (GameProfile player :this.plugin.getEverAPI().getManagerService().getWhitelist().getWhitelistedProfiles()) {
				if (player.getName().isPresent()) {
					suggests.add(player.getName().orElse(player.getUniqueId().toString()));
				}
			}
			return suggests;
		}
		return Arrays.asList();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_USER.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 1) {
			this.plugin.getGame().getScheduler().createTaskBuilder()
												.async()
												.execute(() -> this.commandWhitelistRemove(source, args.get(0)))
												.submit(this.plugin);
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandWhitelistRemove(final CommandSource player, final String identifier) {
		Optional<GameProfile> gameprofile = this.plugin.getEServer().getGameProfile(identifier);
		// Le joueur existe
		if (!gameprofile.isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{player}", identifier)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!this.plugin.getEverAPI().getManagerService().getWhitelist().removeProfile(gameprofile.get())) {
			EEMessages.WHITELIST_REMOVE_ERROR.sender()
				.replace("{player}", gameprofile.get().getName().orElse(identifier))
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.WHITELIST_REMOVE_PLAYER.sender()
			.replace("{player}", gameprofile.get().getName().orElse(identifier))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
}
