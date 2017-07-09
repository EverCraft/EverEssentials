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
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EETeleportationAskAll extends ECommand<EverEssentials> {
	
	public EETeleportationAskAll(final EverEssentials plugin) {
        super(plugin, "tpaall");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPAALL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPAALL_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.TPAALL_OTHERS.get())){
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
		if (args.size() == 1 && source.hasPermission(EEPermissions.TPAALL_OTHERS.get())){
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si connait que la location ou aussi peut être le monde
		if (args.size() == 0) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				return this.commandTeleportationAskAll((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.TPAALL_OTHERS.get())) {
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					return this.commandTeleportationAskAllOthers(source, player.get());
				// Joueur introuvable
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
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandTeleportationAskAll(EPlayer staff) {
		if (this.plugin.getEServer().getOnlinePlayers().size() == 1) {
			EEMessages.TPAALL_ERROR_EMPTY.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		Transform<World> location = staff.getTransform();
		
		if (!this.plugin.getEverAPI().getManagerUtils().getLocation().isPositionSafe(location)) {
			EEMessages.TPAALL_ERROR_PLAYER_LOCATION.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		long delay = this.plugin.getConfigs().getTpaAcceptCancellation();
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay);
		
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if (!staff.equals(player)) {
				if (!player.ignore(staff) && !staff.ignore(player) && player.isToggle()) {
					if (player.addTeleportAskHere(staff.getUniqueId(), delay, location)) {							
						EEMessages.TPAHERE_PLAYER_QUESTION.sender()
							.replace("<player>", staff.getName())
							.replace("<delay>", delay_format)
							.replace("<accept>", () -> EETeleportationAsk.getButtonAccept(staff.getName()))
							.replace("<deny>", () -> EETeleportationAsk.getButtonDeny(staff.getName()))
							.sendTo(player);
					}
				}
			}
		}
		EEMessages.TPAALL_PLAYER.sendTo(staff);
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandTeleportationAskAllOthers(CommandSource staff, EPlayer destination) {
		if (destination.equals(staff)) {
			return this.commandTeleportationAskAll(destination);
		}
			
		if (this.plugin.getEServer().getOnlinePlayers().size() == 1) {
			EEMessages.TPAALL_ERROR_EMPTY.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		Transform<World> location = destination.getTransform();
		
		if (this.plugin.getEverAPI().getManagerUtils().getLocation().isPositionSafe(location)) {
			EEMessages.TPAALL_ERROR_OTHERS_LOCATION.sender()
				.replace("<player>", destination.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		long delay = this.plugin.getConfigs().getTpaAcceptCancellation();
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay);
		
		EEMessages.TPAALL_OTHERS_STAFF.sender()
			.replace("<player>", destination.getName())
			.sendTo(staff);
		EEMessages.TPAALL_OTHERS_PLAYER.sender()
			.replace("<staff>", staff.getName())
			.sendTo(destination);
		
		for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
			if (!destination.equals(player)) {
				if (!(staff instanceof EPlayer) || (!player.ignore((EPlayer) staff) && !((EPlayer) staff).ignore(player))) {
					if (player.isToggle() && player.addTeleportAskHere(destination.getUniqueId(), delay, location)) {							
						EEMessages.TPAHERE_PLAYER_QUESTION.sender()
							.replace("<player>", destination.getName())
							.replace("<delay>", delay_format)
							.replace("<accept>", () -> EETeleportationAsk.getButtonAccept(destination.getName()))
							.replace("<deny>", () -> EETeleportationAsk.getButtonDeny(destination.getName()));
					}
				}
			}
		}
		return CompletableFuture.completedFuture(true);
	}
}