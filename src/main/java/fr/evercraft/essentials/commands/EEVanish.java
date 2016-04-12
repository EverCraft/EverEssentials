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

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEVanish extends ECommand<EverEssentials> {
	
	public EEVanish(final EverEssentials plugin) {
        super(plugin, "vanish", "v");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("VANISH"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("VANISH_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		Text help;
		if(source.hasPermission(this.plugin.getPermissions().get("VANISH_OTHERS"))){
			help = Text.builder("/vanish").onClick(TextActions.suggestCommand("/vanish "))
					.append(Text.builder(" [joueur [on|off]]").build())
					.color(TextColors.RED).build();
		} else {
			help = Text.builder("/vanish").onClick(TextActions.suggestCommand("/vanish"))
					.color(TextColors.RED).build();
		}
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source.hasPermission(this.plugin.getPermissions().get("VANISH_OTHERS"))){
			suggests = null;
		} else if(args.size() == 2 && source.hasPermission(this.plugin.getPermissions().get("VANISH_OTHERS"))){
			suggests.add("on");
			suggests.add("off");
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
				resultat = commandVanish((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("VANISH_OTHERS"))){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandVanishOthers(source, optPlayer.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(this.plugin.getPermissions().noPermission());
			}
		// On connais le joueur et si on doit lui activé ou lui désactivé le vanish
		} else if(args.size() == 2) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("VANISH_OTHERS"))){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					if(args.get(1).equalsIgnoreCase("on")) {
						resultat = commandVanishOthers(source, optPlayer.get(), true);
					} else if(args.get(1).equalsIgnoreCase("off")) {
						resultat = commandVanishOthers(source, optPlayer.get(), false);
					} else {
						source.sendMessage(help(source));
					}
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
	
	public boolean commandVanish(final EPlayer player) {
		boolean vanish = player.isVanish();
		if(player.setVanish(!vanish)) {
			// Si le vanish est déjà activé
			if(vanish){
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_PLAYER_DISABLE"));
			// Vanish est déjà désactivé
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_PLAYER_ENABLE"));
			}
			return false;
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
		}
		return true;
	}
	
	public boolean commandVanishOthers(final CommandSource staff, final EPlayer player) throws CommandException {
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			boolean vanish = player.isVanish();
			if(player.setVanish(!vanish)) {
				// Si le vanish est déjà activé
				if(vanish){
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_PLAYER_DISABLE")
							.replaceAll("<staff>", staff.getName()));
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_STAFF_DISABLE")
							.replaceAll("<player>", player.getName())));
				// Vanish est déjà désactivé
				} else {
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_PLAYER_ENABLE")
							.replaceAll("<staff>", staff.getName()));
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_STAFF_ENABLE")
							.replaceAll("<player>", player.getName())));
				}
				return true;
			} else {
				player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
			}
		// La source et le joueur sont identique
		} else {
			return execute(staff, new ArrayList<String>());
		}
		return false;
	}
	
	public boolean commandVanishOthers(final CommandSource staff, final EPlayer player, final boolean etat) throws CommandException {
		boolean vanish = player.isVanish();
		// La source et le joueur sont différent
		if(!player.equals(staff)){
			if(etat) {
				// Si le Vanish est déjà activé
				if(vanish){
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_STAFF_ENABLE_ERROR")
							.replaceAll("<player>", player.getName())));
				// Vanish est désactivé
				} else {
					if(player.setVanish(etat)) {
						player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_PLAYER_ENABLE")
								.replaceAll("<staff>", staff.getName()));
						staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_STAFF_ENABLE")
								.replaceAll("<player>", player.getName())));
						return true;
					} else {
						player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
					}
				}
			} else {
				// Si le Vanish est déjà activé
				if(vanish){
					if(player.setVanish(etat)) {
						player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_PLAYER_DISABLE")
								.replaceAll("<staff>", staff.getName()));
						staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_STAFF_DISABLE")
								.replaceAll("<player>", player.getName())));
						return true;
					} else {
						player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
					}
				// Vanish est désactivé
				} else {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_OTHERS_STAFF_DISABLE_ERROR")
							.replaceAll("<player>", player.getName())));
				}
			}
		// La source et le joueur sont identique
		} else {
			if(etat) {
				// Si le vanish est déjà activé
				if(vanish){
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_PLAYER_ENABLE_ERROR"));
				// Vanish est désactivé
				} else {
					if(player.setVanish(etat)) {
						player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_PLAYER_ENABLE"));
						return true;
					} else {
						player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
					}
				}
			} else {
				// Si le vanish est déjà activé
				if(vanish){
					if(player.setVanish(etat)) {
						player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_PLAYER_DISABLE"));
						return true;
					} else {
						player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getEverAPI().getMessages().getCommandError()));
					}
				// Vanish est désactivé
				} else {
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("VANISH_PLAYER_DISABLE_ERROR"));
				}
			}
		}
		return false;
	}
}
