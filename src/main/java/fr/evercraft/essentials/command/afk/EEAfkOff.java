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
package fr.evercraft.essentials.command.afk;

import java.util.ArrayList;
import java.util.Collection;
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
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEAfkOff extends ESubCommand<EverEssentials> {
	
	public EEAfkOff(final EverEssentials plugin, final EEAfk command) {
        super(plugin, command, "off");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.AFK_OFF_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.AFK_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "]")
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
	
	@Override
	public Collection<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.AFK_OTHERS.get())){
			suggests.addAll(this.getAllPlayers(source));
		}
		return suggests;
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			if (source instanceof EPlayer) {
				resultat = this.commandAfkOff((EPlayer) source);
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.AFK_OTHERS.get())){
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()) {
					resultat = this.commandAfkOffOthers(source, player.get());
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandAfkOff(final EPlayer player) {
		boolean afk = player.isAfk();
		// Si le mode afk est déjà activé
		if (afk){
			if (player.setAfk(false)) {
				EEMessages.AFK_OFF_PLAYER.sendTo(player);
				EEMessages.AFK_OFF_ALL.sender()
					.replace(player.getReplacesAll())
					.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> !other.equals(player));
				return true;
			} else {
				EEMessages.AFK_OFF_PLAYER_CANCEL.sendTo(player);
			}
		// Le mode afk est déjà désactivé
		} else {
			EEMessages.AFK_OFF_PLAYER_ERROR.sendTo(player);
		}
		return false;
	}
	
	private boolean commandAfkOffOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if (!player.equals(staff)){
			boolean afk = player.isAfk();
			// Si le mode afk est déjà activé
			if (afk){
				if (player.setAfk(false)) {
					EEMessages.AFK_OFF_OTHERS_PLAYER.sender()
						.replace("<staff>", staff.getName())
						.sendTo(player);
					EEMessages.AFK_OFF_OTHERS_STAFF.sender()
						.replace("<player>", player.getName())
						.sendTo(player);
					EEMessages.AFK_OFF_ALL.sender()
						.replace(player.getReplacesAll())
						.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> !other.equals(player) && other.equals(staff));
					return true;
				} else {
					EEMessages.AFK_OFF_OTHERS_CANCEL.sender()
						.replace("<player>", player.getName())
						.sendTo(staff);
				}
			// Le mode afk est déjà désactivé
			} else {
				EEMessages.AFK_OFF_OTHERS_ERROR.sender()
					.replace("<player>", player.getName())
					.sendTo(staff);
			}
		// La source et le joueur sont identique
		} else {
			return this.commandAfkOff(player);
		}
		return false;
	}
}
