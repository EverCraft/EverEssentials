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
package fr.evercraft.essentials.command.god;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
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

public class EEGodStatus extends ESubCommand<EverEssentials> {
	public EEGodStatus(final EverEssentials plugin, final EEGod command) {
        super(plugin, command, "status");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.GOD.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.GOD_STATUS_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = null;
		if(!(args.size() == 1 && source.hasPermission(EEPermissions.GOD_OTHERS.get()))){
			suggests = new ArrayList<String>();
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.GOD_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "]")
						.onClick(TextActions.suggestCommand("/" + this.getName()))
						.color(TextColors.RED)
						.build();
		} else {
			return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
		}
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0) {
			if(source instanceof EPlayer) {
				resultat = commandGodStatus((EPlayer) source);
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.GOD_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandGodStatusOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	public boolean commandGodStatus(final EPlayer player) {
		boolean godMode = player.isGod();
		// Si le god mode est déjà activé
		if(godMode){
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GOD_STATUS_ON.get()
					.replaceAll("<player>", player.getDisplayName())));
		// God mode est déjà désactivé
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GOD_STATUS_OFF.get()
					.replaceAll("<player>", player.getDisplayName())));
		}
		return true;
	}
	
	public boolean commandGodStatusOthers(final CommandSource source, final EPlayer target) {
		boolean godMode = target.isGod();
		// Si le god mode est déjà activé
		if(godMode){
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GOD_STATUS_ON.get()
					.replaceAll("<player>", target.getDisplayName())));
		// God mode est déjà désactivé
		} else {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GOD_STATUS_OFF.get()
					.replaceAll("<player>", target.getDisplayName())));
		}
		return true;
	}
}