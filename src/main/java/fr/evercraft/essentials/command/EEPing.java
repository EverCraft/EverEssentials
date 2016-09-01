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

public class EEPing extends ECommand<EverEssentials> {
	
	public EEPing(final EverEssentials plugin) {
        super(plugin, "ping");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.PING.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.PING_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.PING_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.PING_OTHERS.get())){
			return null;
		}
		return new ArrayList<String>();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandPing((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.PING_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (optPlayer.isPresent()){
					resultat = this.commandPingOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		
		return resultat;
	}
	
	private boolean commandPing(final EPlayer player) {
		player.sendMessage(EEMessages.PREFIX.get() + EEMessages.PING_PLAYER.get()
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency())));
		return true;
	}
	
	private boolean commandPingOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont identique
		if (player.equals(staff)) {
			return this.commandPing(player);
		}
		
		staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.PING_OTHERS.get()
				.replaceAll("<player>", player.getName())
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency()))));
		return true;
	}
}