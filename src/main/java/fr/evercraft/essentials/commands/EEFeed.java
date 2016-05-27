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

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEFeed extends ECommand<EverEssentials> {
	
	public EEFeed(final EverEssentials plugin) {
        super(plugin, "feed", "eat");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.FEED.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("FEED_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.FEED_OTHERS.get())){
			return Text.builder("/feed").onClick(TextActions.suggestCommand("/feed "))
					.append(Text.builder(" [joueur|*]").build())
					.color(TextColors.RED).build();
		}
		return Text.builder("/feed").onClick(TextActions.suggestCommand("/feed"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if(args.size() == 1 && source.hasPermission(EEPermissions.FEED_OTHERS.get())){
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
				resultat = commandFeed((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.FEED_OTHERS.get())){
				// Pour tous les joueurs
				if(args.get(0).equals("*")) {
					resultat = commandFeedAll(source);
				// Pour un joueur
				} else {
					Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
					// Le joueur existe
					if(optPlayer.isPresent()){
						resultat = commandFeedOthers(source, optPlayer.get());
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
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
	
	public boolean commandFeed(final EPlayer player) {
		player.setFood(20);
		player.setSaturation(20);
		player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("FEED_PLAYER"));
		return true;
	}
	
	public boolean commandFeedAll(final CommandSource staff) {
		// Pour tous les joueurs connecté
		for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()){
			player.setFood(20);
			player.setSaturation(20);
			// La source et le joueur sont différent
			if(!staff.equals(player)){
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("FEED_OTHERS_PLAYER")
						.replaceAll("<staff>", staff.getName()));
			}
		}
		staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("FEED_ALL_STAFF")));
		return true;
	}
	
	public boolean commandFeedOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			player.setFood(20);
			player.setSaturation(20);
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("FEED_OTHERS_PLAYER")
					.replaceAll("<staff>", staff.getName()));
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("FEED_OTHERS_STAFF")
					.replaceAll("<player>", player.getName())));
			return true;
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
	}
}
