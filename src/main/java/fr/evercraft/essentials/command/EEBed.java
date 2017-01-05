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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.RespawnLocation;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

/*
 * Pas encore implémentée
 */

public class EEBed extends ECommand<EverEssentials> {
	
	public EEBed(final EverEssentials plugin) {
        super(plugin, "bed");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BED.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.BED_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.BED_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "]")
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
		List<String> suggest = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.BED_OTHERS.get())){
			suggest.addAll(this.getAllPlayers(source));
		}
		return suggest;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandBed((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// On connais le joueur
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.BED_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (optPlayer.isPresent()){
					resultat = commandBedOthers(source, optPlayer.get());
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
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandBed(final EPlayer player) {
		Optional<Map<UUID, RespawnLocation>> spawn = player.get(Keys.RESPAWN_LOCATIONS);
		if (spawn.isPresent()){
			player.sendMessage(spawn.get().toString());
		} else {
			player.sendMessage("En attente d'implémentation");
		}
		return true;
	}
	
	private boolean commandBedOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if (!player.equals(staff)) {
			return true;
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
	}
}