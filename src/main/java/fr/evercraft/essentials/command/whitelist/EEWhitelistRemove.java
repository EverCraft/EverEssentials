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
import java.util.concurrent.ExecutionException;

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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEWhitelistRemove extends ESubCommand<EverEssentials> {
	public EEWhitelistRemove(final EverEssentials plugin, final EEWhitelist command) {
        super(plugin, command, "remove");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WHITELIST_MANAGE.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WHITELIST_REMOVE_DESCRIPTION.get());
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
		if(args.size() == 1) {
			resultat = commandWhitelistRemove(source, args.get(0));
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandWhitelistRemove(final CommandSource source, final String arg) {
		Optional<EPlayer> optTarget = this.plugin.getEServer().getEPlayer(arg);
		// Le joueur existe
		if(optTarget.isPresent()){
			EPlayer target = optTarget.get();
			Optional<WhitelistService> optWhitelist = this.plugin.getEverAPI().getManagerService().getWhitelist();
			if(optWhitelist.isPresent()){
				WhitelistService whitelist = optWhitelist.get();
				try {
					GameProfile profile = this.plugin.getEServer().getGameProfileManager().get(target.getUniqueId()).get();
					if(whitelist.isWhitelisted(profile)){
						whitelist.removeProfile(this.plugin.getEServer().getGameProfileManager().get(target.getUniqueId()).get());
						source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WHITELIST_REMOVE_PLAYER.get()
								.replaceAll("<player>", target.getDisplayName())));
					} else {
						source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.WHITELIST_REMOVE_ERROR.get()
								.replaceAll("<player>", target.getDisplayName())));
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			} else {
				this.plugin.getLogger().error(EAMessages.COMMAND_ERROR.get());
			}
		// Le joueur est introuvable
		} else {
			source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
		}
		return true;
	}
}