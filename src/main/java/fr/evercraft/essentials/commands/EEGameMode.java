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
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsGameMode;

public class EEGameMode extends ECommand<EverEssentials> {
	
	public EEGameMode(final EverEssentials plugin) {
        super(plugin, "gamemode", "gm");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("GAMEMODE"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("GAMEMODE_DESCRIPTION");
	}
	
	public Text help(final CommandSource source) {
		if(source.hasPermission(this.plugin.getPermissions().get("PING_OTHERS"))){
			return Text.builder("/gm <")
					.append(Text.builder("survival").onClick(TextActions.suggestCommand("/gm survival ")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("creative").onClick(TextActions.suggestCommand("/gm creative ")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("adventure").onClick(TextActions.suggestCommand("/gm adventure ")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("spectator").onClick(TextActions.suggestCommand("/gm spectator ")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("empty").onClick(TextActions.suggestCommand("/gm empty ")).build())
					.append(Text.builder("> [joueur]").build())
					.color(TextColors.RED).build();
		}
		return Text.builder("/gm <")
					.append(Text.builder("survival").onClick(TextActions.suggestCommand("/gm survival")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("creative").onClick(TextActions.suggestCommand("/gm creative")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("adventure").onClick(TextActions.suggestCommand("/gm adventure")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("spectator").onClick(TextActions.suggestCommand("/gm spectator")).build())
					.append(Text.builder("|").build())
					.append(Text.builder("empty").onClick(TextActions.suggestCommand("/gm empty")).build())
					.append(Text.builder(">").build())
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("survival");
			suggests.add("creative");
			suggests.add("adventure");
			suggests.add("spectator");
			suggests.add("empty");
		} else if(args.size() == 2 && source.hasPermission(this.plugin.getPermissions().get("GAMEMODE_OTHERS"))){
			suggests = null;
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on connait que le gamemode
		if(args.size() == 1) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandGameMode((EPlayer) source, args.get(0));
				// Si la source est une console ou un commande block
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
			
			// Si on connait le gamemode et le joueur
		} else if(args.size() == 2) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("GAMEMODE_OTHERS"))){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(1));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandGameModeOthers(source, optPlayer.get(), args.get(0));
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
	
	public boolean commandGameMode(final EPlayer player, final String gamemode_name) {
		Optional<GameMode> optGamemode = UtilsGameMode.getGameMode(gamemode_name); 
		// Si gamemode est correct
		if(optGamemode.isPresent()) {
			GameMode gamemode = optGamemode.get();
			// Si le nouveau gamemode est différent à celui du joueur
			if(!gamemode.equals(player.getGameMode())) {
				player.setGameMode(gamemode);
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GAMEMODE_PLAYER_CHANGE")
						.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode)));
				return true;
			// Gamemode identique à celui du joueur
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GAMEMODE_PLAYER_EQUAL")
						.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode)));
			}
		// Nom du gamemode inconnue
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GAMEMODE_ERROR_NAME"));
		}
		return false;
	}
	
	public boolean commandGameModeOthers(final CommandSource staff, final EPlayer player, final String gamemode_name) throws CommandException {
		Optional<GameMode> optGamemode = UtilsGameMode.getGameMode(gamemode_name); 
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			// Si gamemode est correct
			if(optGamemode.isPresent()) {
				GameMode gamemode = optGamemode.get();
				// Si le nouveau gamemode est différent à celui du joueur
				if(!gamemode.equals(player.getGameMode())) {
					player.setGameMode(gamemode);
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GAMEMODE_OTHERS_STAFF_CHANGE")
							.replaceAll("<player>", player.getName())
							.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode))));
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GAMEMODE_OTHERS_PLAYER_CHANGE")
							.replaceAll("<staff>", staff.getName())
							.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode)));
					return true;
					// Gamemode identique à celui du joueur
				} else {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GAMEMODE_OTHERS_EQUAL")
							.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode))
							.replaceAll("<player>", player.getName())));
				}
			// Nom du gamemode inconnue
			} else {
				staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GAMEMODE_ERROR_NAME")));
			}
		// La source et le joueur sont identique
		} else {
			List<String> args = new ArrayList<String>();
			args.add(gamemode_name);
			return execute(staff, args);
		}
		return false;
	}
}
