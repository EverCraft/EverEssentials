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
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EETeleportationAll extends ECommand<EverEssentials> {
	
	public EETeleportationAll(final EverEssentials plugin) {
        super(plugin, "tpall");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TPALL.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TPALL_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(EEPermissions.TPALL_OTHERS.get())){
			return Text.builder("/tpall [joueur]").onClick(TextActions.suggestCommand("/tpall "))
					.color(TextColors.RED).build();
		} 
		return Text.builder("/tpall").onClick(TextActions.suggestCommand("/tpall"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source.hasPermission(EEPermissions.TPALL_OTHERS.get())){
			suggests = null;
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si connait que la location ou aussi peut être le monde
		if(args.size() == 0) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = commandTeleportationAll((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.TPALL_OTHERS.get())) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandTeleportationAllOthers(source, optPlayer.get());
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
	
	private boolean commandTeleportationAll(EPlayer staff) {
		Optional<Transform<World>> optTransform = teleport(staff);
		if(optTransform.isPresent()) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if(!staff.equals(player)) {
					if(player.getWorld().equals(optTransform.get().getExtent()) || 
							this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, optTransform.get().getExtent())) {
						player.setTransform(optTransform.get());
						player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(EEMessages.TPALL_PLAYER.get()
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(staff.getName(), player.getLocation()))
								.build());
					}
				}
			}
			staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.TPALL_STAFF.get())
					.replace("<destination>", getButtonPosition(staff.getName(), optTransform.get().getLocation()))
					.build());
			return true;
		} else {
			staff.sendMessage(EEMessages.PREFIX.get() + EEMessages.TPALL_ERROR.get());
		}
		return false;
	}

	private boolean commandTeleportationAllOthers(CommandSource staff, EPlayer destination) {
		if(!destination.equals(staff)) {
			Optional<Transform<World>> optTransform = teleport(destination);
			if(optTransform.isPresent()) {
				for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
					if(!destination.equals(player)) {
						if(player.getWorld().equals(optTransform.get().getExtent()) || 
								this.plugin.getManagerServices().getEssentials().hasPermissionWorld(player, optTransform.get().getExtent())) {
							player.setTransform(optTransform.get());
							if(!player.equals(staff)) {
								player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
										.append(EEMessages.TPALL_OTHERS_PLAYER.get()
												.replaceAll("<staff>", staff.getName()))
										.replace("<destination>", getButtonPosition(destination.getName(), optTransform.get().getLocation()))
										.build());
							}
						}
					}
				}
				staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(EEMessages.TPALL_OTHERS_STAFF.get()
								.replaceAll("<staff>", staff.getName()))
						.replace("<destination>", getButtonPosition(destination.getName(), optTransform.get().getLocation()))
						.build());
				return true;
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TPALL_ERROR.get()));
			}
		} else {
			return commandTeleportationAll(destination);
		}
		return false;
	}
	
	private Optional<Transform<World>> teleport(EPlayer destination){
		if(destination.isFlying()) {
			return this.plugin.getEverAPI().getManagerUtils().getLocation().getBlockSafe(destination.getTransform());
		} else {
			return Optional.of(destination.getTransform());
		}
	}
	
	public Text getButtonPosition(final String player, final Location<World> location){
		return EChat.of(EEMessages.TPALL_DESTINATION.get().replaceAll("<player>", player)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.TPALL_DESTINATION_HOVER.get()
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}