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

public class EEExt extends ECommand<EverEssentials> {
	
	public EEExt(final EverEssentials plugin) {
        super(plugin, "ext", "extinguish");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EXT.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.EXT_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.EXT_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "|*]")
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
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.EXT_OTHERS.get())){
			suggests.addAll(this.getAllPlayers());
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandExt((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.EXT_OTHERS.get())){
				if (args.get(0).equals("*")) {
					resultat = this.commandExtAll(source);
				} else {
					Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
					// Le joueur existe
					if (optPlayer.isPresent()){
						resultat = this.commandExtOthers(source, optPlayer.get());
					// Le joueur est introuvable
					} else {
						source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
					}
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandExt(final EPlayer player) {
		// Le joueur n'est pas en feu
		if (player.getFireTicks() <= 0) {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.EXT_PLAYER_ERROR.getText()));
			return false;
		}
			
		player.setFireTicks(0);
		player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.EXT_PLAYER.getText()));
		return true;
	}
	
	private boolean commandExtOthers(final CommandSource staff, final EPlayer player) {
		// La source et le joueur sont identique
		if (player.equals(staff)) {
			return this.commandExt(player);
		}
		
		// Le joueur n'est pas en feu
		if (player.getFireTicks() <= 0) {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXT_OTHERS_ERROR.get()
					.replaceAll("<player>", player.getName())));
			return false;
		}
		
		
		player.setFireTicks(0);
		
		player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXT_OTHERS_PLAYER.get()
				.replaceAll("<staff>", player.getName())));
		staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXT_OTHERS_STAFF.get()
				.replaceAll("<player>", player.getName())));
		return true;
	}
	
	private boolean commandExtAll(final CommandSource staff) {
		// Pour tous les joueurs connecté
		this.plugin.getEServer().getOnlineEPlayers().forEach(player -> {
			if (player.getFireTicks() > 0) {
				player.setFireTicks(0);
				// La source et le joueur sont différent
				if (!staff.equals(player)) {
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXT_OTHERS_PLAYER.get()
							.replaceAll("<staff>", staff.getName())));
				}
			}
		});

		staff.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.EXT_ALL_STAFF.getText()));
		return true;
	}
}
