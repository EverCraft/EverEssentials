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
package fr.evercraft.everessentials.command.teleport;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EETeleportation extends ECommand<EverEssentials> {
	
	public EETeleportation(final EverEssentials plugin) {
        super(plugin, "tp", "teleport", "tp2p", "tele");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TP.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TP_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.TP_OTHERS.get())){
			return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_RECIPIENT.getString() + "] <" + EAMessages.ARGS_PLAYER.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		} 
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.getAllPlayers(source, false);
		} else if (args.size() == 2 && source.hasPermission(EEPermissions.TP_OTHERS.get())){
			return this.getAllPlayers(source, false);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si connait que la location ou aussi peut être le monde
		if (args.size() == 1) {
			
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (optPlayer.isPresent()){
					return this.commandTeleportation((EPlayer) source, optPlayer.get());
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
			
		} else if (args.size() == 2) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.TP_OTHERS.get())) {
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					
					Optional<EPlayer> destination = this.plugin.getEServer().getEPlayer(args.get(1));
					// Le joueur existe
					if (destination.isPresent()){
						return this.commandTeleportation(source, player.get(), destination.get());
					// Joueur introuvable
					} else {
						EAMessages.PLAYER_NOT_FOUND.sender()
							.prefix(EEMessages.PREFIX)
							.replace("{player}", args.get(1))
							.sendTo(source);
					}
					
				// Joueur introuvable
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
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandTeleportation(EPlayer player, EPlayer destination) {
		if (player.equals(destination)) {
			player.reposition();
			EEMessages.TP_PLAYER_EQUALS.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
			
		if (!player.getWorld().equals(destination.getWorld()) && 
			!this.plugin.getEverAPI().hasPermissionWorld(player, destination.getWorld())) {
			
			EAMessages.NO_PERMISSION_WORLD.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", destination.getWorld().getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!this.teleport(player, destination)) {
			EEMessages.TP_ERROR_LOCATION.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.TP_PLAYER.sender()
			.replace("{destination}", this.getButtonPosition(destination.getName(), player.getLocation()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> commandTeleportation(CommandSource staff, EPlayer player, EPlayer destination) {
		if (!player.getWorld().equals(destination.getWorld()) && 
			!this.plugin.getEverAPI().hasPermissionWorld(player, destination.getWorld())) {
			
			EAMessages.NO_PERMISSION_WORLD_OTHERS.sender()
				.replace("{world}", destination.getWorld().getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.put("{staff}", EReplace.of(staff.getName()));
		replaces.put("{player}", EReplace.of(player.getName()));
		replaces.put("{destination}", EReplace.of(() -> this.getButtonPosition(player.getName(), player.getLocation())));
			
		// Reposition
		if (destination.equals(player)) {
			player.reposition();
			if (destination.equals(staff)) {
				EEMessages.TP_PLAYER_EQUALS.sendTo(player);
			} else {
				EEMessages.TP_OTHERS_PLAYER_REPOSITION.sender()
					.replaceString(replaces)
					.sendTo(player);
				EEMessages.TP_OTHERS_STAFF_REPOSITION.sender()
					.replaceString(replaces)
					.sendTo(staff);
			}
		// Teleport
		} else {
			if (this.teleport(player, destination)) {
				EEMessages.TP_ERROR_LOCATION.sendTo(staff);
				return CompletableFuture.completedFuture(false);
			}
			
			if (player.equals(staff)) {
				EEMessages.TP_PLAYER.sender()
					.replaceString(replaces)
					.sendTo(player);
			} else if (destination.equals(staff)) {
				EEMessages.TP_OTHERS_STAFF_EQUALS_DESTINATION_PLAYER.sender()
					.replaceString(replaces)
					.sendTo(player);
				EEMessages.TP_OTHERS_STAFF_EQUALS_DESTINATION_STAFF.sender()
					.replaceString(replaces)
					.sendTo(staff);
			} else {
				EEMessages.TP_OTHERS_PLAYER.sender()
					.replaceString(replaces)
					.sendTo(player);
				EEMessages.TP_OTHERS_STAFF.sender()
					.replaceString(replaces)
					.sendTo(staff);
			}
		}
		return CompletableFuture.completedFuture(true);
	}
	
	private boolean teleport(final EPlayer player, final EPlayer destination) {
		if (destination.isFlying()) {
			return player.teleportSafeZone(destination.getTransform(), true);
		} else {
			return player.setTransform(destination.getTransform());
		}
	}
	
	private Text getButtonPosition(final String player, final Location<World> location){
		return EEMessages.TP_DESTINATION.getFormat().toText("{player}", player).toBuilder()
					.onHover(TextActions.showText(EEMessages.TP_DESTINATION_HOVER.getFormat().toText(
							"{world}", location.getExtent().getName(),
							"{x}", String.valueOf(location.getBlockX()),
							"{y}", String.valueOf(location.getBlockY()),
							"{z}", String.valueOf(location.getBlockZ()))))
					.build();
	}
}
