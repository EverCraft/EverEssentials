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
import org.spongepowered.api.entity.living.player.Player;
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

public class EETeleportationAsk extends ECommand<EverEssentials> {
	
	public EETeleportationAsk(final EverEssentials plugin) {
        super(plugin, "tpa", "call", "tpask");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPA.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPA_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
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
		if (args.size() == 1) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					return this.commandTeleportation((EPlayer) source, player.get());
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
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandTeleportation(EPlayer player, EPlayer destination) {
		// La source et le joueur sont identique
		if (player.equals(destination)) {
			EEMessages.TPA_ERROR_EQUALS.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le joueur ignore la destination
		if (player.ignore(destination)) {
			EEMessages.TPA_IGNORE_PLAYER.sender()
				.replace("<player>", destination.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		// La destination ignore le joueur
		if (destination.ignore(player)) {
			EEMessages.TPA_IGNORE_DESTINATION.sender()
				.replace("<player>", destination.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
					
		// La destination n'accepte pas les demandes de téléportation
		if (!destination.isToggle()) {
			EEMessages.TOGGLE_DISABLED.sender()
				.replace("<player>", destination.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		long delay = this.plugin.getConfigs().getTpaAcceptCancellation();
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay);
						
		// Il y a déjà une demande de téléportation en cours
		if (!destination.addTeleportAsk(player.getUniqueId(), delay)) {
			EEMessages.TPA_ERROR_DELAY.sender()
				.replace("<player>", destination.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.TPA_STAFF_QUESTION.sender()
			.replace("<player>", destination.getName())
			.replace("<delay>", delay_format)
			.sendTo(player);
		EEMessages.TPA_PLAYER_QUESTION.sender()
			.replace("<player>", player.getName())
			.replace("<delay>", delay_format)
			.replace("<accept>", EETeleportationAsk.getButtonAccept(player.getName()))
			.replace("<deny>", EETeleportationAsk.getButtonDeny(player.getName()))
			.sendTo(destination);
		return CompletableFuture.completedFuture(true);
	}
	
	public static Text getButtonPosition(final String player, final Location<World> location){
		return EEMessages.TPA_DESTINATION.getFormat().toText("<player>", player).toBuilder()
					.onHover(TextActions.showText(EEMessages.TPA_DESTINATION_HOVER.getFormat().toText(
							"<world>", location.getExtent().getName(),
							"<x>", String.valueOf(location.getBlockX()),
							"<y>", String.valueOf(location.getBlockY()),
							"<z>", String.valueOf(location.getBlockZ()))))
					.build();
	}
	
	public static Text getButtonAccept(final String player){
		return EEMessages.TPA_PLAYER_QUESTION_ACCEPT.getFormat().toText("<player>", player).toBuilder()
					.onHover(TextActions.showText(EEMessages.TPA_PLAYER_QUESTION_ACCEPT_HOVER.getFormat()
							.toText("<player>", player)))
					.onClick(TextActions.runCommand("/tpaccept " + player))
					.build();
	}
	
	public static Text getButtonDeny(final String player){
		return EEMessages.TPA_PLAYER_QUESTION_DENY.getFormat().toText("<player>", player).toBuilder()
					.onHover(TextActions.showText(EEMessages.TPA_PLAYER_QUESTION_DENY_HOVER.getFormat()
							.toText("<player>", player)))
					.onClick(TextActions.runCommand("/tpdeny " + player))
					.build();
	}
}