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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everapi.sponge.UtilsNetwork;
import fr.evercraft.everapi.text.ETextBuilder;

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
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_PLAYER.get() + "|"  + EAMessages.ARGS_IP.get() + "]")
				.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		suggests.addAll(this.getAllUsers());
		return suggests;
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
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			}
		
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		
		return resultat;
	}
	
	private boolean commandSeen(final EPlayer player) {
		player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText()).append(EEMessages.SEEN_IP.get())
				.replace("<ip>", getButtomIP(UtilsNetwork.getHostString(player.getConnection().getAddress().getAddress())))
				.build());
		return true;
	}
	
	private boolean commandSeenOthers(final CommandSource staff, final EUser user) throws CommandException {
		// La source et le joueur sont identique
		if (user.equals(staff)) {
			return this.commandSeen((EPlayer) user);
		}
		if(user.getLastIP().isPresent()){
			staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText()).append(EEMessages.SEEN_IP_OTHERS.get())
					.replace("<player>", user.getName())
					.replace("<ip>", getButtomIP(UtilsNetwork.getHostString(user.getLastIP().get())))
				.build());
		} else {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SEEN_IP_OTHERS_NO_IP.get()
				.replaceAll("<player>", user.getDisplayName())));
		}
		return true;
	}
	
	private boolean commandSeenOthers(final CommandSource staff, final String address) throws CommandException {
		List<Text> lists = new ArrayList<Text>();
		Optional<List<UUID>> uuids = this.plugin.getDataBases().getPlayersWithSameIP(address);
		lists.add(EChat.of(EEMessages.SEEN_IP_MESSAGE.get()
				.replaceAll("<ip>", address)));
		if(uuids.isPresent()){
			for(UUID uuid : uuids.get()){
				Optional<EUser> player = this.plugin.getEServer().getEUser(uuid);
				if(player.isPresent()){
					lists.add(ETextBuilder.toBuilder(EEMessages.SEEN_IP_LIST.get())
							.replace("<player>", getButtomUser(player.get()))
						.build());
				}
			}
		} else {
			lists.add(EEMessages.SEEN_IP_NO_PLAYER.getText());
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EChat.of(EEMessages.SEEN_IP_TITLE.get().replace("<ip>", address)).toBuilder()
					.onClick(TextActions.runCommand("/s \"" + address + "\""))
					.build(), 
				lists, staff);
		return false;
	}
	
	private Text getButtomIP(final String address){
		return EChat.of(EEMessages.SEEN_IP_STYLE.get()
				.replaceAll("<ip>", address)).toBuilder()
			.onHover(TextActions.showText(EChat.of(EAMessages.HOVER_COPY.get())))
			.onClick(TextActions.suggestCommand(address))
			.onShiftClick(TextActions.insertText(address))
			.build();
	}
	
	private Text getButtomUser(final EUser player){
		return EChat.of(EEMessages.SEEN_PLAYER_STYLE.get()
				.replaceAll("<player>", player.getDisplayName())).toBuilder()
			.onHover(TextActions.showText(EChat.of(EAMessages.HOVER_COPY.get())))
			.onClick(TextActions.suggestCommand(player.getName()))
			.onShiftClick(TextActions.insertText(player.getName()))
			.build();
	}
}