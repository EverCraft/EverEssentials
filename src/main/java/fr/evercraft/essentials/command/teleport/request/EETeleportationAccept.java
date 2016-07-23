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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.TeleportRequest;
import fr.evercraft.everapi.services.essentials.TeleportRequest.Type;
import fr.evercraft.everapi.text.ETextBuilder;

public class EETeleportationAccept extends ECommand<EverEssentials> {
	
	public EETeleportationAccept(final EverEssentials plugin) {
        super(plugin, "tpaccept", "tpyes");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPACCEPT.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TPACCEPT_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if(args.size() == 0) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandTeleportationAccept((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 1) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandTeleportationAccept((EPlayer) source, optPlayer.get());
				// Joueur introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	private boolean commandTeleportationAccept(EPlayer player) {
		Map<UUID, TeleportRequest> teleports = player.getAllTeleportsAsk();
		List<Text> lists = new ArrayList<Text>();
		
		Optional<EPlayer> one_player = Optional.empty();
		
		for(Entry<UUID, TeleportRequest> teleport : teleports.entrySet()) {
			Optional<EPlayer> player_request = this.plugin.getEServer().getEPlayer(teleport.getKey());
			
			if(player_request.isPresent()) {
				one_player = player_request;
				if(teleport.getValue().getType().equals(Type.TPA)) {
					lists.add(ETextBuilder.toBuilder(EEMessages.TPA_PLAYER_LIST_LINE.get()
							.replaceAll("<player>", player_request.get().getName()))
						.replace("<accept>", EETeleportationAsk.getButtonAccept(player_request.get().getName()))
						.replace("<deny>", EETeleportationAsk.getButtonDeny(player_request.get().getName()))
						.build());
				} else if(teleport.getValue().getType().equals(Type.TPAHERE)) {
					lists.add(ETextBuilder.toBuilder(EEMessages.TPA_PLAYER_LIST_LINE.get()
							.replaceAll("<player>", player_request.get().getName()))
						.replace("<accept>", EETeleportationAskHere.getButtonAccept(player_request.get().getName()))
						.replace("<deny>", EETeleportationAskHere.getButtonDeny(player_request.get().getName()))
						.build());
				}
			}
		}

		if(!(lists.size() == 1 && one_player.isPresent())) {
			if(lists.isEmpty()) {
				lists.add(EEMessages.TPA_PLAYER_LIST_EMPTY.getText());
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.TPA_PLAYER_LIST_TITLE.getText().toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName())).build(), lists, player);
		} else {
			return this.commandTeleportationAccept(player, one_player.get());
		}
		
		return true;
	}
	

	private boolean commandTeleportationAccept(final EPlayer player, final EPlayer player_request) {
		Optional<TeleportRequest> teleports = player.getTeleportAsk(player_request.getUniqueId());
		
		// Il y a une demande de téléportation
		if(teleports.isPresent()) {
			// La demande est toujours valide
			if(!teleports.get().isExpire()) {				
				player.removeTeleportAsk(player_request.getUniqueId());
				
				if(teleports.get().getType().equals(Type.TPA)) {
					this.commandTeleportationAcceptAsk(player, player_request, teleports.get());
				} else if(teleports.get().getType().equals(Type.TPAHERE)) {
					this.commandTeleportationAcceptAskHere(player, player_request, teleports.get());
				}
				
			// La demande a expiré
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_PLAYER_EXPIRE.get()
						.replaceAll("<player>", player_request.getName()));
			}
		// Aucune demande de téléportation
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_PLAYER_EMPTY.get()
					.replaceAll("<player>", player_request.getName()));
		}
		return false;
	}
	
	private boolean commandTeleportationAcceptAsk(final EPlayer player, final EPlayer player_request, final TeleportRequest teleport) {
		long delay = this.plugin.getConfigs().getTeleportDelay(player_request);
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(System.currentTimeMillis() + delay);
		
		final Transform<World> location = player.getTransform();
		
		if(player_request.getWorld().equals(location.getExtent()) || this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player_request, location.getExtent())) {
			if(delay > 0) {
				player_request.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_STAFF_ACCEPT.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<delay>", delay_format));
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_PLAYER_ACCEPT.get()
						.replaceAll("<player>", player_request.getName())
						.replaceAll("<delay>", delay_format));
			}
			
			player_request.setTeleport(delay, () -> this.teleportAsk(player_request, player, location), player_request.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		} else {
			player_request.sendMessage(EEMessages.PREFIX.get() + EAMessages.NO_PERMISSION_WORLD.get());
		}
		return false;
	}
	
	private boolean commandTeleportationAcceptAskHere(final EPlayer player, final EPlayer player_request, final TeleportRequest teleport) {
		long delay = this.plugin.getConfigs().getTeleportDelay(player);
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(System.currentTimeMillis() + delay);
		
		final Transform<World> location = teleport.getLocation().orElse(player_request.getTransform());
		
		if(player.getWorld().equals(location.getExtent()) || this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, location.getExtent())) {
			if(delay > 0) {
				player_request.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPAHERE_STAFF_ACCEPT.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<delay>", delay_format));
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPAHERE_PLAYER_ACCEPT.get()
						.replaceAll("<player>", player_request.getName())
						.replaceAll("<delay>", delay_format));
			}
			
			player.setTeleport(delay, () -> this.teleportAskHere(player_request, player, location), player_request.hasPermission(EEPermissions.TELEPORT_BYPASS_MOVE.get()));
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.NO_PERMISSION_WORLD.get());
		}
		return false;
	}
	
	public void teleportAsk(final EPlayer player_request, final EPlayer player, final Transform<World> teleport) {
		if(player_request.teleportSafe(teleport)) {
			player_request.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.TPA_STAFF_TELEPORT.get()
						.replaceAll("<player>", player.getName()))
					.replace("<destination>", EETeleportationAsk.getButtonPosition(player.getName(), teleport.getLocation()))
					.build());
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.TPA_PLAYER_TELEPORT.get())
					.replace("<player>", EETeleportationAsk.getButtonPosition(player_request.getName(), teleport.getLocation()))
					.build());
		} else {
			player_request.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_ERROR_LOCATION.get()
						.replaceAll("<player>", player_request.getName()));
		}
	}
	
	public void teleportAskHere(final EPlayer player_request, final EPlayer player, final Transform<World> teleport) {
		if(player.teleportSafe(teleport)) {
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.TPAHERE_PLAYER_TELEPORT.get()
						.replaceAll("<player>", player.getName()))
					.replace("<destination>", EETeleportationAsk.getButtonPosition(player.getName(), teleport.getLocation()))
					.build());
			player_request.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.TPAHERE_STAFF_TELEPORT.get())
					.replace("<player>", EETeleportationAsk.getButtonPosition(player_request.getName(), teleport.getLocation()))
					.build());
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPAHERE_ERROR_LOCATION.get()
						.replaceAll("<player>", player_request.getName()));
		}
	}
}