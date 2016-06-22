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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import fr.evercraft.everapi.text.ETextBuilder;

public class EEUuid extends ECommand<EverEssentials> {
	
	public EEUuid(final EverEssentials plugin) {
        super(plugin, "uuid");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.UUID.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.UUID_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.UUID_OTHERS.get())){
			return Text.builder("/uuid [joueur]").onClick(TextActions.suggestCommand("/uuid "))
					.color(TextColors.RED).build();
		}
		return Text.builder("/uuid").onClick(TextActions.suggestCommand("/uuid"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1 && source.hasPermission(EEPermissions.UUID_OTHERS.get())){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandUUID((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.UUID_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandUUIDOthers(source, optPlayer.get());
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
	
	public boolean commandUUID(final EPlayer player) {
		player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
				.append(EEMessages.UUID_PLAYER.get())
				.replace("<uuid>", getButtonUUID(player.getUniqueId()))
				.build());
		return true;
	}
	
	public boolean commandUUIDOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){				
			staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.UUID_PLAYER_OTHERS.get()
					.replaceAll("<player>", player.getName()))
					.replace("<uuid>", getButtonUUID(player.getUniqueId()))
					.build());				
			return true;
		// La source et le joueur sont identique
		} else {
			return commandUUID(player);
		}
	}
	
	public Text getButtonUUID(final UUID uuid){
		return EChat.of(EEMessages.UUID_NAME.get().replace("<uuid>", uuid.toString())).toBuilder()
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
					.onClick(TextActions.suggestCommand(uuid.toString()))
					.onShiftClick(TextActions.insertText(uuid.toString()))
					.build();
	}
}

