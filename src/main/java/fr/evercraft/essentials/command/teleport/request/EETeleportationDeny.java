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
package fr.evercraft.essentials.command.teleport.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.TeleportRequest;
import fr.evercraft.everapi.services.essentials.TeleportRequest.Type;

public class EETeleportationDeny extends ECommand<EverEssentials> {
	
	public EETeleportationDeny(final EverEssentials plugin) {
        super(plugin, "tpdeny", "tpno");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPDENY.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPDENY_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source instanceof Player) {
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandTeleportationDeny((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					resultat = this.commandTeleportationDeny((EPlayer) source, player.get());
				// Joueur introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandTeleportationDeny(final EPlayer player) {
		Map<UUID, TeleportRequest> teleports = player.getAllTeleportsAsk();
		List<Text> lists = new ArrayList<Text>();
		Optional<EPlayer> one_player = Optional.empty();
		
		for (Entry<UUID, TeleportRequest> teleport : teleports.entrySet()) {
			Optional<EPlayer> player_request = this.plugin.getEServer().getEPlayer(teleport.getKey());
			
			if (player_request.isPresent()) {
				one_player = player_request;
				if (teleport.getValue().getType().equals(Type.TPA)) {
					lists.add(EEMessages.TPA_PLAYER_LIST_LINE.getFormat().toText(
						"<player>", EReplace.of(player_request.get().getName()),
						"<accept>", EReplace.of(() -> EETeleportationAsk.getButtonAccept(player_request.get().getName())),
						"<deny>", EReplace.of(() -> EETeleportationAsk.getButtonDeny(player_request.get().getName()))));
				} else if (teleport.getValue().getType().equals(Type.TPAHERE)) {
					lists.add(EEMessages.TPA_PLAYER_LIST_LINE.getFormat().toText(
						"<player>", EReplace.of(player_request.get().getName()),
						"<accept>", EReplace.of(() -> EETeleportationAskHere.getButtonAccept(player_request.get().getName())),
						"<deny>", EReplace.of(() -> EETeleportationAskHere.getButtonDeny(player_request.get().getName()))));
				}
			}
		}

		if (!(lists.size() == 1 && one_player.isPresent())) {
			if (lists.isEmpty()) {
				lists.add(EEMessages.TPA_PLAYER_LIST_EMPTY.getText());
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.TPA_PLAYER_LIST_TITLE.getText().toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName())).build(), lists, player);
		} else {
			return this.commandTeleportationDeny(player, one_player.get());
		}
		
		return true;
	}
	
	private boolean commandTeleportationDeny(final EPlayer player, final EPlayer player_request) {
		Optional<TeleportRequest> teleports = player.getTeleportAsk(player_request.getUniqueId());
		
		// Il y a une demande de téléportation
		if (teleports.isPresent()) {
			// La demande est toujours valide
			if (!teleports.get().isExpire()) {
				player.removeTeleportAsk(player_request.getUniqueId());
				
				if (teleports.get().getType().equals(Type.TPA)) {
					EEMessages.TPA_PLAYER_DENY.sender()
						.replace("<player>", player_request.getName())
						.sendTo(player);
					EEMessages.TPA_STAFF_DENY.sender()
						.replace("<player>", player.getName())
						.sendTo(player_request);
				} else if (teleports.get().getType().equals(Type.TPAHERE)) {
					EEMessages.TPAHERE_PLAYER_DENY.sender()
						.replace("<player>", player_request.getName())
						.sendTo(player);
					EEMessages.TPAHERE_STAFF_DENY.sender()
						.replace("<player>", player.getName())
						.sendTo(player_request);
				}				
			// La demande a expiré
			} else {
				if (teleports.get().getType().equals(Type.TPA)) {
					EEMessages.TPA_PLAYER_EXPIRE.sender()
						.replace("<player>", player_request.getName())
						.sendTo(player);
				} else if (teleports.get().getType().equals(Type.TPAHERE)) {
					EEMessages.TPAHERE_PLAYER_EXPIRE.sender()
						.replace("<player>", player_request.getName())
						.sendTo(player);
				}
			}
		// Aucune demande de téléportation
		} else {
			EEMessages.TPA_PLAYER_EMPTY.sender()
				.replace("<player>", player_request.getName())
				.sendTo(player);
		}
		return false;
	}
}