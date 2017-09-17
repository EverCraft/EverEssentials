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
package fr.evercraft.everessentials.command.teleport.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.TeleportRequest;
import fr.evercraft.everapi.services.essentials.TeleportRequest.Type;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EETeleportationAccept extends ECommand<EverEssentials> {
	
	public EETeleportationAccept(final EverEssentials plugin) {
        super(plugin, "tpaccept", "tpyes");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPACCEPT.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPACCEPT_DESCRIPTION.getText();
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
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				return this.commandTeleportationAccept((EPlayer) source);
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
					return this.commandTeleportationAccept((EPlayer) source, player.get());
				// Joueur introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(0))
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
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandTeleportationAccept(EPlayer player) {
		Map<UUID, TeleportRequest> teleports = player.getAllTeleportsAsk();
		List<Text> lists = new ArrayList<Text>();
		Optional<EPlayer> one_player = Optional.empty();
		
		for (Entry<UUID, TeleportRequest> teleport : teleports.entrySet()) {
			Optional<EPlayer> player_request = this.plugin.getEServer().getEPlayer(teleport.getKey());
			
			if (player_request.isPresent()) {
				one_player = player_request;
				if (teleport.getValue().getType().equals(Type.TPA)) {
					lists.add(EEMessages.TPA_PLAYER_LIST_LINE.getFormat().toText(
						"{player}", player_request.get().getName(),
						"{accept}", EETeleportationAsk.getButtonAccept(player_request.get().getName()),
						"{deny}", EETeleportationAsk.getButtonDeny(player_request.get().getName())));
				} else if (teleport.getValue().getType().equals(Type.TPAHERE)) {
					lists.add(EEMessages.TPA_PLAYER_LIST_LINE.getFormat().toText(
						"{player}", player_request.get().getName(),
						"{accept}", EETeleportationAskHere.getButtonAccept(player_request.get().getName()),
						"{deny}", EETeleportationAskHere.getButtonDeny(player_request.get().getName())));
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
			return this.commandTeleportationAccept(player, one_player.get());
		}
		
		return CompletableFuture.completedFuture(true);
	}
	

	private CompletableFuture<Boolean> commandTeleportationAccept(final EPlayer player, final EPlayer player_request) {
		Optional<TeleportRequest> teleports = player.getTeleportAsk(player_request.getUniqueId());
		
		// Il y a une demande de téléportation
		if (!teleports.isPresent()) {
			EEMessages.TPA_PLAYER_EMPTY.sender()
				.replace("{player}", player_request.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// La demande a expiré
		if (teleports.get().isExpire()) {
			EEMessages.TPA_PLAYER_EXPIRE.sender()
				.replace("{player}", player_request.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		player.removeTeleportAsk(player_request.getUniqueId());
		
		if (teleports.get().getType().equals(Type.TPA)) {
			this.commandTeleportationAcceptAsk(player, player_request, teleports.get());
		} else if (teleports.get().getType().equals(Type.TPAHERE)) {
			this.commandTeleportationAcceptAskHere(player, player_request, teleports.get());
		}
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandTeleportationAcceptAsk(final EPlayer player, final EPlayer player_request, final TeleportRequest teleport) {
		long delay = this.plugin.getConfigs().getTeleportDelay(player_request);
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay);
		final Transform<World> location = player.getTransform();
		
		if (!player_request.getWorld().equals(location.getExtent()) && !this.plugin.getEverAPI().hasPermissionWorld(player_request, location.getExtent())) {
			EAMessages.NO_PERMISSION_WORLD.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", location.getExtent().getName())
				.sendTo(player_request);
			return CompletableFuture.completedFuture(false);
		}
		
		if (delay > 0) {
			EEMessages.TPA_STAFF_ACCEPT.sender()
				.replace("{player}", player.getName())
				.replace("{delay}", delay_format)
				.sendTo(player_request);
			EEMessages.TPA_PLAYER_ACCEPT.sender()
				.replace("{player}", player_request.getName())
				.replace("{delay}", delay_format)
				.sendTo(player);
		}
		
		player_request.setTeleport(delay, () -> this.teleportAsk(player_request, player, location), player_request.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandTeleportationAcceptAskHere(final EPlayer player, final EPlayer player_request, final TeleportRequest teleport) {
		long delay = this.plugin.getConfigs().getTeleportDelay(player);
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay);
		final Transform<World> location = teleport.getLocation().orElse(player_request.getTransform());
		
		if (!player.getWorld().equals(location.getExtent()) && !this.plugin.getEverAPI().hasPermissionWorld(player, location.getExtent())) {
			EAMessages.NO_PERMISSION_WORLD.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", location.getExtent().getName())
				.sendTo(player_request);
			return CompletableFuture.completedFuture(false);
		}
		
		if (delay > 0) {
			EEMessages.TPAHERE_STAFF_ACCEPT.sender()
				.replace("{player}", player.getName())
				.replace("{delay}", delay_format)
				.sendTo(player_request);
			EEMessages.TPAHERE_PLAYER_ACCEPT.sender()
				.replace("{player}", player_request.getName())
				.replace("{delay}", delay_format)
				.sendTo(player);
		}
		
		player.setTeleport(delay, () -> this.teleportAskHere(player_request, player, location), player_request.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		return CompletableFuture.completedFuture(false);
	}
	
	private void teleportAsk(final EPlayer player_request, final EPlayer player, final Transform<World> teleport) {
		if (player_request.isOnline() && player.isOnline()) {
			if (player_request.teleportSafe(teleport, true)) {
				EEMessages.TPA_STAFF_TELEPORT.sender()
					.replace("{player}", player.getName())
					.replace("{destination}", EETeleportationAsk.getButtonPosition(player.getName(), teleport.getLocation()))
					.sendTo(player_request);
				EEMessages.TPA_PLAYER_TELEPORT.sender()
					.replace("{player}", EETeleportationAsk.getButtonPosition(player_request.getName(), teleport.getLocation()))
					.sendTo(player);
			} else {
				EEMessages.TPA_ERROR_LOCATION.sender()
					.replace("{player}", player_request.getName())
					.sendTo(player_request);
			}
		}
	}
	
	private void teleportAskHere(final EPlayer player_request, final EPlayer player, final Transform<World> teleport) {
		if (player_request.isOnline() && player.isOnline()) {
			if (player.teleportSafe(teleport, true)) {
				EEMessages.TPAHERE_PLAYER_TELEPORT.sender()
					.replace("{player}", player.getName())
					.replace("{destination}", () -> EETeleportationAsk.getButtonPosition(player.getName(), teleport.getLocation()))
					.sendTo(player);
				EEMessages.TPAHERE_STAFF_TELEPORT.sender()
					.replace("{player}", EETeleportationAsk.getButtonPosition(player_request.getName(), teleport.getLocation()))
					.sendTo(player_request);
			} else {
				EEMessages.TPAHERE_ERROR_LOCATION.sender()
					.replace("{player}", player_request.getName())
					.sendTo(player);
			}
		}
	}
}
