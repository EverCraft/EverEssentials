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
import java.util.Optional;

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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

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
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source instanceof Player) {
			suggests.addAll(this.getAllPlayers(source));
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 1) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					resultat = this.commandTeleportation((EPlayer) source, player.get());
				// Joueur introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
				
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		
		return resultat;
	}
	
	private boolean commandTeleportation(EPlayer player, EPlayer destination) {
		// La source et le joueur sont identique
		if (player.equals(destination)) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_ERROR_EQUALS.get());
			return false;
		}
		
		// Le joueur ignore la destination
		if (player.ignore(destination)) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_IGNORE_PLAYER.get()
					.replaceAll("<player>", destination.getName()));
			return false;
		}
		
		// La destination ignore le joueur
		if (destination.ignore(player)) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_IGNORE_DESTINATION.get()
					.replaceAll("<player>", destination.getName()));
			return false;
		}
					
		// La destination n'accepte pas les demandes de téléportation
		if (!destination.isToggle()) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TOGGLE_DISABLED.get()
					.replaceAll("<player>", destination.getName()));
			return false;
		}
		
		long delay = this.plugin.getConfigs().getTpaAcceptCancellation();
		String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(System.currentTimeMillis() + delay);
						
		// Il y a déjà une demande de téléportation en cours
		if (!destination.addTeleportAsk(player.getUniqueId(), delay)) {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_ERROR_DELAY.get()
					.replaceAll("<player>", destination.getName()));
			return false;
		}
		
		player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPA_STAFF_QUESTION.get()
				.replaceAll("<player>", destination.getName())
				.replaceAll("<delay>", delay_format));
		destination.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
						.append(EEMessages.TPA_PLAYER_QUESTION.get()
							.replaceAll("<player>", player.getName())
							.replaceAll("<delay>", delay_format))
						.replace("<accept>", EETeleportationAsk.getButtonAccept(player.getName()))
						.replace("<deny>", EETeleportationAsk.getButtonDeny(player.getName()))
						.build());
		return true;
	}
	
	public static Text getButtonPosition(final String player, final Location<World> location){
		return EChat.of(EEMessages.TPA_DESTINATION.get().replaceAll("<player>", player)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.TPA_DESTINATION_HOVER.get()
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
	
	public static Text getButtonAccept(final String player){
		return EChat.of(EEMessages.TPA_PLAYER_QUESTION_ACCEPT.get().replaceAll("<player>", player)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.TPA_PLAYER_QUESTION_ACCEPT_HOVER.get()
							.replaceAll("<player>", player))))
					.onClick(TextActions.runCommand("/tpaccept " + player))
					.build();
	}
	
	public static Text getButtonDeny(final String player){
		return EChat.of(EEMessages.TPA_PLAYER_QUESTION_DENY.get().replaceAll("<player>", player)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.TPA_PLAYER_QUESTION_DENY_HOVER.get()
							.replaceAll("<player>", player))))
					.onClick(TextActions.runCommand("/tpdeny " + player))
					.build();
	}
}