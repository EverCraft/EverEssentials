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

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EETeleportation extends ECommand<EverEssentials> {
	
	public EETeleportation(final EverEssentials plugin) {
        super(plugin, "tp", "teleport", "tp2p", "tele");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("TP"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("TP_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(this.plugin.getPermissions().get("TP_OTHERS"))){
			return Text.builder("/tp [joueur|destination <joueur>]").onClick(TextActions.suggestCommand("/tp "))
					.color(TextColors.RED).build();
		} 
		return Text.builder("/tp <joueur>").onClick(TextActions.suggestCommand("/tp "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests = null;
		} else if (args.size() == 2 && source.hasPermission(this.plugin.getPermissions().get("TP_OTHERS"))){
			suggests = null;
		}
		return suggests;
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
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
				}
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if(args.size() == 2) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("TP_OTHERS"))) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					Optional<EPlayer> optDestination = this.plugin.getEServer().getEPlayer(args.get(1));
					// Le joueur existe
					if(optPlayer.isPresent()){
						resultat = commandTeleportation(source, optPlayer.get(), optDestination.get());
					// Joueur introuvable
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				// Joueur introuvable
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
	
	private boolean commandTeleportation(EPlayer player, EPlayer destination) {
		if(!player.equals(destination)) {
			if(player.getWorld().equals(destination.getWorld()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
					player.hasPermission(this.plugin.getEverAPI().getPermissions().get("WORLDS") + "." + destination.getWorld().getName())) {
				if(teleport(player, destination)) {
					player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("TP_PLAYER"))
							.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
							.build());
					return true;
				} else {
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TP_ERROR_LOCATION"));
				}
			} else {
				player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NO_PERMISSION_WORLD"));
			}
		} else {
			player.teleportSafe(player.getTransform());
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TP_PLAYER_EQUALS"));
		}
		return false;
	}
	
	private boolean commandTeleportation(CommandSource staff, EPlayer player, EPlayer destination) {
		if(player.getWorld().equals(destination.getWorld()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
				player.hasPermission(this.plugin.getEverAPI().getPermissions().get("WORLDS") + "." + destination.getWorld().getName())) {
			if(destination.equals(player)) {
				if(destination.equals(staff)){
					player.teleportSafe(player.getTransform());
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TP_PLAYER_EQUALS"));
					return true;
				} else {
					if(teleport(player, player)) {
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TP_OTHERS_PLAYER_REPOSITION")
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(player.getName(), player.getLocation()))
								.build());
						staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TP_OTHERS_STAFF_REPOSITION")
										.replaceAll("<player>", player.getName()))
								.replace("<destination>", getButtonPosition(player.getName(), player.getLocation()))
								.build());
						return true;
					} else {
						staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TP_ERROR_LOCATION")));
					}
				}
			} else {
				if(teleport(player, destination)) {
					if(player.equals(staff)) {
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TP_PLAYER"))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
					} else if(destination.equals(staff)) {
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TP_OTHERS_STAFF_EQUALS_DESTINATION_PLAYER")
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
						staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TP_OTHERS_STAFF_EQUALS_DESTINATION_STAFF")
										.replaceAll("<player>", player.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
					} else {
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TP_OTHERS_PLAYER")
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
						staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TP_OTHERS_STAFF")
										.replaceAll("<player>", player.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
					}
					return true;
				} else {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TP_ERROR_LOCATION")));
				}
			}
		} else {
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NO_PERMISSION_WORLD_OTHERS")));
		}
		return false;
	}
	
	private boolean teleport(EPlayer player, EPlayer destination){
		if(destination.isFlying()) {
			return player.teleportSafe(destination.getTransform());
		} else {
			player.setTransform(destination.getTransform());
		}
		return true;
	}
	
	public Text getButtonPosition(final String player, final Location<World> location){
		return EChat.of(this.plugin.getMessages().getMessage("TP_DESTINATION").replaceAll("<player>", player)).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("TP_DESTINATION_HOVER")
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}