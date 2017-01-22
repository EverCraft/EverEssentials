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
package fr.evercraft.essentials.command.teleport;

import java.util.ArrayList;
import java.util.Collection;
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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsLocation.LocationResult;

public class EETeleportationPosition extends ECommand<EverEssentials> {
	
	public EETeleportationPosition(final EverEssentials plugin) {
        super(plugin, "tppos");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPPOS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPPOS_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		if (source.hasPermission(EEPermissions.TPPOS_OTHERS.get())){
			return Text.builder("/" + this.getName() + " <x> <y> <z> [" + EAMessages.ARGS_WORLD.getString() + " [" + EAMessages.ARGS_PLAYER.getString() + "]]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
		} 
		return Text.builder("/" + this.getName() + " <x> <y> <z> [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() >= 1 && args.size() <= 3) {
			suggests.add("1");
		} else if (args.size() == 4) {
			suggests.addAll(this.getAllWorlds());
		} else if (args.size() == 5 && source.hasPermission(EEPermissions.TPPOS_OTHERS.get())){
			suggests.addAll(this.getAllPlayers());
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si connait que la location ou aussi peut être le monde
		if (args.size() == 3) {
			
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandTeleportationPosition((EPlayer) source, args.get(0), args.get(1), args.get(2));
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 4) {
			
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandTeleportationPosition((EPlayer) source, args.get(0), args.get(1), args.get(2), args.get(3));
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Pour téléporter un autre joueur
		} else if (args.size() == 5) {
			
			// Si il a la permission
			if (source.hasPermission(EEPermissions.TPPOS_OTHERS.get())){
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(4));
				// Le joueur existe
				if (player.isPresent()){
					resultat = this.commandTeleportationPositionOthers(source, player.get(), args.get(0), args.get(1), args.get(2), args.get(3));
				// Joueur introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandTeleportationPosition(final EPlayer player, final String x, final String y, final String z) {
		LocationResult location = this.plugin.getEverAPI().getManagerUtils().getLocation().getLocation(player, x, y, z);
		// Si les coordonnées sont valides
		if (!location.isError()) {
			if (player.teleportSafeZone(player.getWorld().getLocation(location.getLocation().get()), true)) {
				EEMessages.TPPOS_PLAYER.sender()
					.replace("<position>", this.getButtonPosition(player.getLocation()))
					.sendTo(player);
				return true;
			} else {
				EEMessages.TPPOS_PLAYER_ERROR.sender()
					.replace("<position>", this.getButtonPosition(player.getWorld().getLocation(location.getLocation().get())))
					.sendTo(player);
			}
		}
		return false;
	}
	
	private boolean commandTeleportationPosition(final EPlayer player, final String x, final String y, final String z, final String world_name) {
		LocationResult location = this.plugin.getEverAPI().getManagerUtils().getLocation().getLocation(player, x, y, z);
		
		// Si les coordonnées sont valides
		if (location.isError()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(player);
			return false;
		}
		
		Optional<World> world =  this.plugin.getEServer().getWorld(world_name);
		// Monde introuvable
		if (!world.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(player);
			return false;
		}
		
		if (!player.getWorld().equals(world.get()) && !this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world.get())) {
			EAMessages.NO_PERMISSION_WORLD.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(player);
			return false;
		}
		
		if (!player.teleportSafeZone(world.get().getLocation(location.getLocation().get()), true)) {
			EEMessages.TPPOS_PLAYER_ERROR.sender()
				.replace("<position>", () -> this.getButtonPosition(world.get().getLocation(location.getLocation().get())))
				.sendTo(player);
			return false;
		}
		
		EEMessages.TPPOS_PLAYER.sender()
			.replace("<position>", () -> this.getButtonPosition(player.getLocation()))
			.sendTo(player);
		return true;
	}
	
	private boolean commandTeleportationPositionOthers(final CommandSource staff, final EPlayer player, final String x, final String y, final String z, final String world_name) {
		LocationResult location = this.plugin.getEverAPI().getManagerUtils().getLocation().getLocation(player, x, y, z);
		
		// Si les coordonnées sont valides
		if (location.isError()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(staff);
			return false;
		}
		
		Optional<World> world =  this.plugin.getEServer().getWorld(world_name);
		// Si le monde existe
		if (!world.isPresent()) {
			location.getError().get()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
		
		if (!player.getWorld().equals(world.get()) && !this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, world.get())) {
			EAMessages.NO_PERMISSION_WORLD_OTHERS.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<world>", world_name)
				.sendTo(staff);
			return false;
		}
		
		if (!player.teleportSafeZone(world.get().getLocation(location.getLocation().get()), true)) {
			EEMessages.TPPOS_OTHERS_ERROR.sender()
				.replace("<player>", player.getName())
				.replace("<position>", this.getButtonPosition(world.get().getLocation(location.getLocation().get())))
				.sendTo(staff);
			return false;
		}
		
		EEMessages.TPPOS_OTHERS_PLAYER.sender()
			.replace("<staff>", staff.getName())
			.replace("<position>", () -> this.getButtonPosition(player.getLocation()))
			.sendTo(player);
		EEMessages.TPPOS_OTHERS_STAFF.sender()
			.replace("<player>", player.getName())
			.replace("<position>", () -> this.getButtonPosition(player.getLocation()))
			.sendTo(staff);
		 return true;
	}
	
	private Text getButtonPosition(final Location<World> location){
		return EEMessages.TPPOS_POSITION.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.TPPOS_POSITION_HOVER.getFormat().toText(
							"<world>", location.getExtent().getName(),
							"<x>", String.valueOf(location.getBlockX()),
							"<y>", String.valueOf(location.getBlockY()),
							"<z>", String.valueOf(location.getBlockZ()))))
					.build();
	}
}