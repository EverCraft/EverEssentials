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
/**
 * Authors : Rexbut, Lesbleu
 */
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEMail extends ECommand<EverEssentials> {
	
	public EEMail(final EverEssentials plugin) {
        super(plugin, "mail");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("MAIL"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("MAIL_DESCRIPTION");
	}
	
	public Text help(final CommandSource source) {
		Builder build = Text.builder("/" + this.getName() + " <");
		build = build.append(Text.builder("read").onClick(TextActions.suggestCommand("/" + this.getName() + " read")).build());
		build = build.append(Text.of("|"));
		build = build.append(Text.builder("clear").onClick(TextActions.suggestCommand("/" + this.getName() + " clear")).build());
		
		if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
			build = build.append(Text.of("|"));
			build = build.append(Text.builder("send <to> <message>").onClick(TextActions.suggestCommand("/" + this.getName() + " send ")).build());
		}
		
		if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SENDALL"))){
			build = build.append(Text.of("|"));
			build = build.append(Text.builder("sendall <message>").onClick(TextActions.suggestCommand("/" + this.getName() + " sendall ")).build());
		}
		return build.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("read");
			suggests.add("clear");
			if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
				suggests.add("send");
			}
			if(source.hasPermission(this.plugin.getPermissions().get("MAIL_SENDALL"))){
				suggests.add("sendall");
			}
		} else if (args.size() == 2) {
			if(args.get(0).equalsIgnoreCase("send") && source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
				suggests = null;
			} else if(args.get(0).equalsIgnoreCase("send") && source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
				suggests.add("Hello world");
			}
		} else if (args.size() == 3) {
			if(args.get(0).equalsIgnoreCase("send") && source.hasPermission(this.plugin.getPermissions().get("MAIL_SEND"))){
				suggests.add("Hello world");
			}
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandPing((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("PING_OTHERS"))){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandPingOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(this.plugin.getPermissions().noPermission());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandPing(final EPlayer player) {
		player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("PING_PLAYER")
				.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency())));
		return true;
	}
	
	public boolean commandPingOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("PING_OTHERS")
					.replaceAll("<player>", player.getName())
					.replaceAll("<ping>", String.valueOf(player.getConnection().getLatency()))));
			return true;
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
	}
}
