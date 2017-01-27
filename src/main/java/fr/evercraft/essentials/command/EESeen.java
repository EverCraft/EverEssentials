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
package fr.evercraft.essentials.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import com.google.common.net.InetAddresses;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.sponge.UtilsNetwork;

public class EESeen extends ECommand<EverEssentials> {
	
	public EESeen(final EverEssentials plugin) {
        super(plugin, "seen");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SEEN.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SEEN_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_USER.getString() + "|"  + EAMessages.ARGS_IP.getString() + "]")
				.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.getAllUsers(args.get(0), source);
		}
		return Arrays.asList();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandSeen((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			if(InetAddresses.isInetAddress(args.get(0))){
				resultat = this.commandSeenOthers(source, args.get(0));
			} else { 
				Optional<EUser> optUser = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (optUser.isPresent()){
					resultat = this.commandSeenOthers(source, optUser.get());
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			}
		
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandSeen(final EPlayer player) {
		EEMessages.SEEN_IP.sender()
			.replace("<ip>", getButtomIP(UtilsNetwork.getHostString(player.getConnection().getAddress().getAddress())))
			.sendTo(player);
		return true;
	}
	
	private boolean commandSeenOthers(final CommandSource staff, final EUser user) throws CommandException {
		// La source et le joueur sont identique
		if (user.equals(staff)) {
			return this.commandSeen((EPlayer) user);
		}
		if(user.getLastIP().isPresent()) {
			EEMessages.SEEN_IP_OTHERS.sender()
				.replace("<player>", user.getDisplayName())
				.replace("<ip>", getButtomIP(UtilsNetwork.getHostString(user.getLastIP().get())))
				.sendTo(staff);
		} else {
			EEMessages.SEEN_IP_OTHERS_NO_IP.sender()
				.replace("<player>", user.getDisplayName())
				.sendTo(staff);
		}
		return true;
	}
	
	private boolean commandSeenOthers(final CommandSource staff, final String address) throws CommandException {
		List<Text> lists = new ArrayList<Text>();
		Optional<List<UUID>> uuids = this.plugin.getDataBases().getPlayersWithSameIP(address);
		lists.add(EEMessages.SEEN_IP_MESSAGE.getFormat().toText("<ip>", address));
		if(uuids.isPresent()){
			for(UUID uuid : uuids.get()){
				Optional<EUser> player = this.plugin.getEServer().getEUser(uuid);
				if(player.isPresent()){
					lists.add(EEMessages.SEEN_IP_LIST.getFormat().toText("<player>", getButtomUser(player.get())));
				}
			}
		} else {
			lists.add(EEMessages.SEEN_IP_NO_PLAYER.getText());
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EEMessages.SEEN_IP_TITLE.getFormat().toText("<ip>", address).toBuilder()
					.onClick(TextActions.runCommand("/s \"" + address + "\""))
					.build(), 
				lists, staff);
		return false;
	}
	
	private Text getButtomIP(final String address){
		return EEMessages.SEEN_IP_STYLE.getFormat().toText("<ip>", address).toBuilder()
			.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
			.onClick(TextActions.suggestCommand(address))
			.onShiftClick(TextActions.insertText(address))
			.build();
	}
	
	private Text getButtomUser(final EUser player){
		return EEMessages.SEEN_PLAYER_STYLE.getFormat().toText("<player>", player.getDisplayName()).toBuilder()
			.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
			.onClick(TextActions.suggestCommand(player.getName()))
			.onShiftClick(TextActions.insertText(player.getName()))
			.build();
	}
}