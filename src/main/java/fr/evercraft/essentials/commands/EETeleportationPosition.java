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

import com.flowpowered.math.vector.Vector3i;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EETeleportationPosition extends ECommand<EverEssentials> {
	
	public EETeleportationPosition(final EverEssentials plugin) {
        super(plugin, "tppos");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPPOS.get());
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("TPPOS_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.TPPOS_OTHERS.get())){
			return Text.builder("/tppos <x> <y> <z> [monde [joueur]]").onClick(TextActions.suggestCommand("/tppos "))
					.color(TextColors.RED).build();
		} 
		return Text.builder("/tppos <x> <y> <z> [monde]").onClick(TextActions.suggestCommand("/tppos "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() >= 1 && args.size() <= 3){
			suggests.add("1");
		} else if (args.size() == 4){
			for(World world : this.plugin.getEServer().getWorlds()){
				suggests.add(world.getProperties().getWorldName());
			}
		} else if (args.size() == 5 && source.hasPermission(EEPermissions.TPPOS_OTHERS.get())){
			suggests = null;
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si connait que la location ou aussi peut être le monde
		if(args.size() == 3) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandTeleportationPosition((EPlayer) source, args.get(0), args.get(1), args.get(2));
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if(args.size() == 4) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandTeleportationPosition((EPlayer) source, args.get(0), args.get(1), args.get(2), args.get(3));
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		// Pour téléporter un autre joueur
		} else if (args.size() == 5){
			// Si il a la permission
			if (source.hasPermission(EEPermissions.TPPOS_OTHERS.get())){
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(4));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandTeleportationPositionOthers(source, optPlayer.get(), args.get(0), args.get(1), args.get(2), args.get(3));
				// Joueur introuvable
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
	
	public boolean commandTeleportationPosition(final EPlayer player, final String x, final String y, final String z) {
		Optional<Vector3i> optLocation = this.plugin.getEverAPI().getManagerUtils().getLocation().getLocation(player, x, y, z);
		// Si les coordonnées sont valides
		if(optLocation.isPresent()) {
			if(player.teleportSafe(player.getWorld().getLocation(optLocation.get()))) {
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("TPPOS_PLAYER"))
						.replace("<position>", getButtonPosition(player.getLocation()))
						.build());
				return true;
			} else {
				player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("TPPOS_PLAYER_ERROR"))
						.replace("<position>", getButtonPosition(player.getWorld().getLocation(optLocation.get())))
						.build());
			}
		}
		return false;
	}
	
	public boolean commandTeleportationPosition(final EPlayer player, final String x, final String y, final String z, final String world_name) {
		Optional<Vector3i> optLocation = this.plugin.getEverAPI().getManagerUtils().getLocation().getLocation(player, x, y, z);
		// Si les coordonnées sont valides
		if(optLocation.isPresent()) {
			Optional<World> optWorld =  this.plugin.getEServer().getWorld(world_name);
			// Si le monde existe
			if(optWorld.isPresent()) {
				if(player.getWorld().equals(optWorld.get()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
						player.hasPermission(this.plugin.getEverAPI().getPermissions().get("WORLDS") + "." + optWorld.get().getName())) {
					if(player.teleportSafe(optWorld.get().getLocation(optLocation.get()))) {
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TPPOS_PLAYER"))
								.replace("<position>", getButtonPosition(player.getLocation()))
								.build());
						return true;
					} else {
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TPPOS_PLAYER_ERROR"))
								.replace("<position>", getButtonPosition(optWorld.get().getLocation(optLocation.get())))
								.build());
					}
				} else {
					player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NO_PERMISSION_WORLD"));
				}
			// Monde introuvable
			} else {
				player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getText("WORLD_NOT_FOUND")));
			}
		}
		return false;
	}
	
	public boolean commandTeleportationPositionOthers(final CommandSource staff, final EPlayer player, final String world_name, final String x, final String y, final String z) {
		Optional<Vector3i> optLocation = this.plugin.getEverAPI().getManagerUtils().getLocation().getLocation(player, x, y, z);
		// Si les coordonnées sont valides
		if(optLocation.isPresent()) {
			Optional<World> optWorld =  this.plugin.getEServer().getWorld(world_name);
			// Si le monde existe
			if(optWorld.isPresent()) {
				if(player.getWorld().equals(optWorld.get()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
						player.hasPermission(this.plugin.getEverAPI().getPermissions().get("WORLDS") + "." + optWorld.get().getName())) {
					if(player.teleportSafe(optWorld.get().getLocation(optLocation.get()))) {
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TPPOS_OTHERS_PLAYER")
										.replaceAll("<staff>", staff.getName()))
								.replace("<position>", getButtonPosition(player.getLocation()))
								.build());
						staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TPPOS_OTHERS_STAFF")
										.replaceAll("<player>", player.getName()))
								.replace("<position>", getButtonPosition(player.getLocation()))
								.build());
						 return true;
					} else {
						staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TPPOS_OTHERS_ERROR")
										.replaceAll("<player>", player.getName()))
								.replace("<position>", getButtonPosition(optWorld.get().getLocation(optLocation.get())))
								.build());
					}
				} else {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NO_PERMISSION_WORLD_OTHERS")));
				}
			// Monde introuvable
			} else {
				staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getText("WORLD_NOT_FOUND")));
			}
		}
		return false;
	}
	
	public Text getButtonPosition(final Location<World> location){
		return EChat.of(this.plugin.getMessages().getMessage("TPPOS_POSITION")).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("TPPOS_POSITION_HOVER")
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}