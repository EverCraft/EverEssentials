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

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EETeleportationAskAll extends ECommand<EverEssentials> {
	
	public EETeleportationAskAll(final EverEssentials plugin) {
        super(plugin, "tpaall");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPAALL.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TPAALL_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.TPAALL_OTHERS.get())){
			return Text.builder("/tpaall [joueur]")
					.onClick(TextActions.suggestCommand("/tpaall "))
					.color(TextColors.RED)
					.build();
		} 
		return Text.builder("/tpaall")
					.onClick(TextActions.suggestCommand("/tpaall"))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source.hasPermission(EEPermissions.TPAALL_OTHERS.get())){
			suggests = null;
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si connait que la location ou aussi peut être le monde
		if(args.size() == 0) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandTeleportationAll((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.TPAALL_OTHERS.get())) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandTeleportationAllOthers(source, optPlayer.get());
				// Joueur introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandTeleportationAll(EPlayer staff) {
		if(this.plugin.getEServer().getOnlinePlayers().size() > 1) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if(!staff.equals(player)) {
					if(player.isToggle()) {
						long delay = this.plugin.getConfigs().getTpaAcceptCancellation();
						String delay_format = this.plugin.getEverAPI().getManagerUtils().getDate().formatDateDiff(System.currentTimeMillis() + delay);
						
						if(player.addTeleportAskHere(staff.getUniqueId(), delay, staff.getTransform())) {							
							player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
										.append(EEMessages.TPAHERE_PLAYER_QUESTION.get()
											.replaceAll("<player>", staff.getName())
											.replaceAll("<delay>", delay_format))
										.replace("<accept>", EETeleportationAsk.getButtonAccept(staff.getName()))
										.replace("<deny>", EETeleportationAsk.getButtonDeny(staff.getName()))
										.build());
						}
					}
				}
			}
			staff.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPAALL_PLAYER.get());
			return true;
		} else {
			staff.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPAALL_ERROR.get());
		}
		return false;
	}

	private boolean commandTeleportationAllOthers(CommandSource staff, EPlayer destination) {
		return false;
	}
}