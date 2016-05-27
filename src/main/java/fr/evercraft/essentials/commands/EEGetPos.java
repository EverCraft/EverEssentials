/**
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
package fr.evercraft.essentials.commands;

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

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEGetPos extends ECommand<EverEssentials> {
	
	public EEGetPos(final EverEssentials plugin) {
        super(plugin, "getpos");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.GETPOS.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("GETPOS_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.GETPOS_OTHERS.get())){
			return Text.builder("/getpos [joueur]").onClick(TextActions.suggestCommand("/getpos "))
					.color(TextColors.RED).build();
		}
		return Text.builder("/getpos").onClick(TextActions.suggestCommand("/getpos"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1 && source.hasPermission(EEPermissions.GETPOS_OTHERS.get())){
			return null;
		}
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandGetPos((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.GETPOS_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandGetPosOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
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
	
	public boolean commandGetPos(final EPlayer player) {
		player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
				.append(this.plugin.getMessages().getMessage("GETPOS_MESSAGE"))
				.replace("<position>", getButtonPos(player.getLocation()))
				.build());
		return true;
	}
	
	public boolean commandGetPosOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
				.append(this.plugin.getMessages().getMessage("GETPOS_MESSAGE_OTHERS")
						.replaceAll("<player>", player.getName())
						.replaceAll("<world>", player.getWorld().getName())
						.replaceAll("<x>", String.valueOf(player.getLocation().getBlockX()))
						.replaceAll("<y>", String.valueOf(player.getLocation().getBlockY()))
						.replaceAll("<z>", String.valueOf(player.getLocation().getBlockZ())))
				.replace("<position>", getButtonPos(player.getLocation()))
				.build());
		return true;
	}
	
	public Text getButtonPos(final Location<World> location){
		return EChat.of(this.plugin.getMessages().getMessage("GETPOS_POTISITON_NAME")).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("GETPOS_POSITION_HOVER")
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}
