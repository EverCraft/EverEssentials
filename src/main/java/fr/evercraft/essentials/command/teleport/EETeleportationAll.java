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
package fr.evercraft.essentials.command.teleport;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EETeleportationAll extends ECommand<EverEssentials> {
	
	public EETeleportationAll(final EverEssentials plugin) {
        super(plugin, "tpall");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPALL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPALL_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.TPALL_OTHERS.get())){
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
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source.hasPermission(EEPermissions.TPALL_OTHERS.get())){
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				return this.commandTeleportationAll((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.TPALL_OTHERS.get())) {
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					return this.commandTeleportationAllOthers(source, player.get());
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
	
	private CompletableFuture<Boolean> commandTeleportationAll(EPlayer staff) {
		Optional<Transform<World>> transform = this.teleport(staff);
		if (!transform.isPresent()) {
			EEMessages.TPALL_ERROR.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if (!staff.equals(player)) {
				if (player.getWorld().equals(transform.get().getExtent()) || 
						this.plugin.getEverAPI().hasPermissionWorld(player, transform.get().getExtent())) {
					
					player.teleport(transform.get(), true);
					
					EEMessages.TPALL_PLAYER.sender()
						.replace("{staff}", staff.getName())
						.replace("{destination}", () -> this.getButtonPosition(staff.getName(), player.getLocation()))
						.sendTo(player);
				}
			}
		}
		
		EEMessages.TPALL_STAFF.sender()
			.replace("{destination}", () -> this.getButtonPosition(staff.getName(), transform.get().getLocation()))
			.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandTeleportationAllOthers(CommandSource staff, EPlayer destination) {
		if (destination.equals(staff)) {
			return this.commandTeleportationAll(destination);
		}
			
		Optional<Transform<World>> transform = teleport(destination);
		if (!transform.isPresent()) {
			EEMessages.TPALL_ERROR.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if (!destination.equals(player)) {
				
				if (player.getWorld().equals(transform.get().getExtent()) || 
						this.plugin.getEverAPI().hasPermissionWorld(player, transform.get().getExtent())) {
					
					player.teleport(transform.get(), true);
					if (!player.equals(staff)) {
						EEMessages.TPALL_OTHERS_PLAYER.sender()
							.replace("{staff}", staff.getName())
							.replace("{destination}", () -> this.getButtonPosition(staff.getName(), player.getLocation()))
							.sendTo(player);
					}
				}
				
			}
		}
		EEMessages.TPALL_OTHERS_STAFF.sender()
			.replace("{staff}", staff.getName())
			.replace("{destination}", () -> this.getButtonPosition(destination.getName(), transform.get().getLocation()))
			.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}
	
	private Optional<Transform<World>> teleport(EPlayer destination) {
		if (destination.isFlying()) {
			return this.plugin.getEverAPI().getManagerUtils().getLocation().getBlock(destination.getTransform());
		} else {
			return Optional.of(destination.getTransform());
		}
	}
	
	private Text getButtonPosition(final String player, final Location<World> location) {
		return EEMessages.TPALL_DESTINATION.getFormat().toText("{player}", player).toBuilder()
					.onHover(TextActions.showText(EEMessages.TPALL_DESTINATION_HOVER.getFormat().toText(
							"{world}", location.getExtent().getName(),
							"{x}", String.valueOf(location.getBlockX()),
							"{y}", String.valueOf(location.getBlockY()),
							"{z}", String.valueOf(location.getBlockZ()))))
					.build();
	}
}
