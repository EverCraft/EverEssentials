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

public class EETeleportationAskHere extends ECommand<EverEssentials> {
	
	public EETeleportationAskHere(final EverEssentials plugin) {
        super(plugin, "tpahere");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPAHERE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPAHERE_DESCRIPTION.getText();
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
		// Si connait que la location ou aussi peut être le monde
		if (args.size() == 1) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (optPlayer.isPresent()) {
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
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandTeleportation(EPlayer staff, EPlayer player) {
		// La source et le joueur sont identique
		if (staff.equals(player)) {
			EEMessages.TPAHERE_ERROR_EQUALS.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le staff ignore le joueur
		if (staff.ignore(player)) {
			EEMessages.TPAHERE_IGNORE_STAFF.sender()
				.replace("{player}", player.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le joueur ignore le staff
		if (player.ignore(staff)) {
			EEMessages.TPAHERE_IGNORE_PLAYER.sender()
				.replace("{player}", player.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
					
		// La destination n'accepte pas les demandes de téléportation
		if (!player.isToggle()) {
			EEMessages.TOGGLE_DISABLED.sender()
				.replace("{player}", player.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// Le joueur n'a pas la permission
		if (!player.getWorld().equals(staff.getWorld()) && !this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, staff.getWorld())) {
			EAMessages.NO_PERMISSION_WORLD_OTHERS.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{world}", staff.getWorld().getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		long delay = this.plugin.getConfigs().getTpaAcceptCancellation();
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDate(System.currentTimeMillis() + delay);
				
		// Il y a déjà une demande de téléportation en cours
		if (!player.addTeleportAskHere(staff.getUniqueId(), delay, staff.getTransform())) {
			EEMessages.TPAHERE_ERROR_DELAY.sender()
				.replace("{player}", player.getName())
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.TPAHERE_STAFF_QUESTION.sender()
			.replace("{player}", player.getName())
			.replace("{delay}", delay_format)
			.sendTo(staff);
		EEMessages.TPAHERE_PLAYER_QUESTION.sender()
			.replace("{player}", staff.getName())
			.replace("{delay}", delay_format)
			.replace("{accept}", EETeleportationAskHere.getButtonAccept(staff.getName()))
			.replace("{deny}", EETeleportationAskHere.getButtonDeny(staff.getName()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	public static Text getButtonPosition(final String player, final Location<World> location){
		return EEMessages.TPAHERE_DESTINATION.getFormat().toText("{player}", player).toBuilder()
					.onHover(TextActions.showText(EEMessages.TPAHERE_DESTINATION_HOVER.getFormat().toText(
							"{world}", location.getExtent().getName(),
							"{x}", String.valueOf(location.getBlockX()),
							"{y}", String.valueOf(location.getBlockY()),
							"{z}", String.valueOf(location.getBlockZ()))))
					.build();
	}
	
	public static Text getButtonAccept(final String player){
		return EEMessages.TPAHERE_PLAYER_QUESTION_ACCEPT.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.TPAHERE_PLAYER_QUESTION_ACCEPT_HOVER.getFormat()
							.toText("{player}", player)))
					.onClick(TextActions.runCommand("/tpaccept " + player))
					.build();
	}
	
	public static Text getButtonDeny(final String player){
		return EEMessages.TPAHERE_PLAYER_QUESTION_DENY.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.TPAHERE_PLAYER_QUESTION_DENY_HOVER.getFormat()
							.toText("{player}", player)))
					.onClick(TextActions.runCommand("/tpdeny " + player))
					.build();
	}
}
