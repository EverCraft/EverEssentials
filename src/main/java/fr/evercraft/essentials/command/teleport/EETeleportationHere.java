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

public class EETeleportationHere extends ECommand<EverEssentials> {
	
	public EETeleportationHere(final EverEssentials plugin) {
        super(plugin, "tphere");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPHERE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TPHERE_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.addAll(this.getAllPlayers());
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 1) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if (player.isPresent()){
					resultat = this.commandTeleportationHere((EPlayer) source, player.get());
				// Joueur introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandTeleportationHere(EPlayer staff, EPlayer player) {
		if (player.equals(staff)) {
			player.reposition();
			EEMessages.TPHERE_EQUALS.sendTo(player);
			return false;
		}
		
		if (!player.getWorld().equals(staff.getWorld()) && !this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, staff.getWorld())) {
			EAMessages.NO_PERMISSION_WORLD_OTHERS.sender()
				.replace("<world>", staff.getWorld().getName())
				.sendTo(staff);
			return false;
		}
			
		if (!this.teleport(player, staff)) {
			EEMessages.TPHERE_ERROR.sendTo(staff);
			return false;
		}
		
		EEMessages.TPHERE_PLAYER.sender()
			.replace("<staff>", staff.getName())
			.replace("<destination>", () -> this.getButtonPosition(staff.getName(), player.getLocation()))
			.sendTo(player);
		EEMessages.TPHERE_STAFF.sender()
			.replace("<player>", player.getName())
			.replace("<destination>", () -> this.getButtonPosition(staff.getName(), player.getLocation()))
			.sendTo(staff);
		return true;
	}
	
	private boolean teleport(EPlayer player, EPlayer destination){
		if (destination.isFlying()) {
			return player.teleportSafeZone(destination.getTransform(), true);
		} else {
			player.setTransform(destination.getTransform());
		}
		return true;
	}
	
	private Text getButtonPosition(final String player, final Location<World> location){
		return EEMessages.TPHERE_DESTINATION.getFormat().toText("<player>", player).toBuilder()
					.onHover(TextActions.showText(EEMessages.TPHERE_DESTINATION_HOVER.getFormat().toText(
							"<world>", location.getExtent().getName(),
							"<x>", String.valueOf(location.getBlockX()),
							"<y>", String.valueOf(location.getBlockY()),
							"<z>", String.valueOf(location.getBlockZ()))))
					.build();
	}
}