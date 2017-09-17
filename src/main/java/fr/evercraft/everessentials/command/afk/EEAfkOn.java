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
package fr.evercraft.everessentials.command.afk;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEAfkOn extends ESubCommand<EverEssentials> {
	
	public EEAfkOn(final EverEssentials plugin, final EEAfk command) {
        super(plugin, command, "on");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return true;
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.AFK_ON_DESCRIPTION.getText();
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
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.AFK_OTHERS.get())){
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			if (source instanceof EPlayer) {
				return this.commandAfkOn((EPlayer) source);
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.AFK_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (optPlayer.isPresent()){
					return this.commandAfkOnOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(0))
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
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandAfkOn(final EPlayer player) {
		boolean afk = player.isAfk();
		// Si le mode afk est déjà activé
		if (!afk){
			if (player.setAfk(true)) {
				EEMessages.AFK_ON_PLAYER.sendTo(player);
				EEMessages.AFK_ON_ALL.sender()
					.replace(player.getReplaces())
					.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> !other.equals(player));
				return CompletableFuture.completedFuture(true);
			} else {
				EEMessages.AFK_ON_PLAYER_CANCEL.sendTo(player);
			}
		// Mode afk est déjà désactivé
		} else {
			EEMessages.AFK_ON_PLAYER_ERROR.sendTo(player);
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandAfkOnOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if (!player.equals(staff)){
			boolean afk = player.isAfk();
			// Si le mode afk est déjà activé
			if (!afk) {
				if (player.setAfk(true)) {
					EEMessages.AFK_ON_OTHERS_PLAYER.sender()
						.replace("{staff}", staff.getName())
						.sendTo(player);
					EEMessages.AFK_ON_OTHERS_STAFF.sender()
						.replace("{player}", player.getName())
						.sendTo(player);
					EEMessages.AFK_ON_ALL.sender()
						.replace(player.getReplaces())
						.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> !other.equals(player) && other.equals(staff));
					return CompletableFuture.completedFuture(true);
				} else {
					EEMessages.AFK_ON_OTHERS_CANCEL.sender()
						.replace("{player}", player.getName())
						.sendTo(staff);
				}
			// Mode afk est déjà désactivé
			} else {
				EEMessages.AFK_ON_OTHERS_ERROR.sender()
					.replace("{player}", player.getName())
					.sendTo(staff);
			}
		// La source et le joueur sont identique
		} else {
			return this.commandAfkOn(player);
		}
		return CompletableFuture.completedFuture(false);
	}
}
