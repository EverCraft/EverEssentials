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
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPA.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TPA_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/tpa <" + EAMessages.ARGS_PLAYER.get() + ">").onClick(TextActions.suggestCommand("/tpa "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si connait que la location ou aussi peut être le monde
		if(args.size() == 1) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandTeleportation((EPlayer) source, optPlayer.get());
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
	
	private boolean commandTeleportation(EPlayer player, EPlayer destination) {
		if(!player.equals(destination)) {
			if(destination.isToggle()) {
				long delay = this.plugin.getConfigs().getTpaAcceptCancellation();
				String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(System.currentTimeMillis() + delay);
				
				if(destination.addTeleportAsk(player.getUniqueId(), delay)) {
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TPA_STAFF_QUESTION.get()
							.replaceAll("<player>", destination.getName())
							.replaceAll("<delay>", delay_format)));
					
					destination.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
									.append(EEMessages.TPA_PLAYER_QUESTION.get()
										.replaceAll("<player>", player.getName())
										.replaceAll("<delay>", delay_format))
									.replace("<accept>", EETeleportationAsk.getButtonAccept(player.getName()))
									.replace("<deny>", EETeleportationAsk.getButtonDeny(player.getName()))
									.build());
				} else {
					player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TPA_ERROR_DELAY.get()
							.replaceAll("<player>", destination.getName())));
				}
			} else {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TOGGLE_DISABLED.get()
						.replaceAll("<player>", destination.getName())));
			}
		} else {
			player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TPA_ERROR_EQUALS.get()));
		}
		return false;
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