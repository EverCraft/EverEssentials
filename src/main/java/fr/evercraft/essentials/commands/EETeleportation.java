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

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.EAPermissions;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EETeleportation extends ECommand<EverEssentials> {
	
	public EETeleportation(final EverEssentials plugin) {
        super(plugin, "tp", "teleport", "tp2p", "tele");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TP.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.TP_OTHERS.get())){
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
		} else if (args.size() == 2 && source.hasPermission(EEPermissions.TP_OTHERS.get())){
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
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 2) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.TP_OTHERS.get())) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					Optional<EPlayer> optDestination = this.plugin.getEServer().getEPlayer(args.get(1));
					// Le joueur existe
					if(optPlayer.isPresent()){
						resultat = commandTeleportation(source, optPlayer.get(), optDestination.get());
					// Joueur introuvable
					} else {
						source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
					}
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
	
	private boolean commandTeleportation(EPlayer player, EPlayer destination) {
		if(!player.equals(destination)) {
			if(player.getWorld().equals(destination.getWorld()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
					player.hasPermission(EAPermissions.WORLDS.get() + "." + destination.getWorld().getName())) {
				if(teleport(player, destination)) {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.TP_PLAYER.get())
							.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
							.build());
					return true;
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TP_ERROR_LOCATION.get());
				}
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.NO_PERMISSION_WORLD.get());
			}
		} else {
			player.teleportSafe(player.getTransform());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TP_PLAYER_EQUALS.get());
		}
		return false;
	}
	
	private boolean commandTeleportation(CommandSource staff, EPlayer player, EPlayer destination) {
		if(player.getWorld().equals(destination.getWorld()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
				player.hasPermission(EAPermissions.WORLDS.get() + "." + destination.getWorld().getName())) {
			if(destination.equals(player)) {
				if(destination.equals(staff)){
					player.teleportSafe(player.getTransform());
					player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TP_PLAYER_EQUALS.get());
					return true;
				} else {
					if(teleport(player, player)) {
						player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TP_OTHERS_PLAYER_REPOSITION.get()
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(player.getName(), player.getLocation()))
								.build());
						staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TP_OTHERS_STAFF_REPOSITION.get()
										.replaceAll("<player>", player.getName()))
								.replace("<destination>", getButtonPosition(player.getName(), player.getLocation()))
								.build());
						return true;
					} else {
						staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TP_ERROR_LOCATION.get()));
					}
				}
			} else {
				if(teleport(player, destination)) {
					if(player.equals(staff)) {
						player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TP_PLAYER.get())
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
					} else if(destination.equals(staff)) {
						player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TP_OTHERS_STAFF_EQUALS_DESTINATION_PLAYER.get()
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
						staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TP_OTHERS_STAFF_EQUALS_DESTINATION_STAFF.get()
										.replaceAll("<player>", player.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
					} else {
						player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TP_OTHERS_PLAYER.get()
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
						staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TP_OTHERS_STAFF.get()
										.replaceAll("<player>", player.getName()))
								.replace("<destination>", getButtonPosition(destination.getName(), player.getLocation()))
								.build());
					}
					return true;
				} else {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TP_ERROR_LOCATION.get()));
				}
			}
		} else {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.NO_PERMISSION_WORLD_OTHERS.get()));
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
		return EChat.of(EEMessages.TP_DESTINATION.get().replaceAll("<player>", player)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.TP_DESTINATION_HOVER.get()
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}