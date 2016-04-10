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
import org.spongepowered.api.entity.Transform;
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

public class EETeleportationAll extends ECommand<EverEssentials> {
	
	public EETeleportationAll(final EverEssentials plugin) {
        super(plugin, "tpall");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("TPALL"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("TPALL_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		if(source.hasPermission(this.plugin.getPermissions().get("TPALL_OTHERS"))){
			return Text.builder("/tpall <joueur>").onClick(TextActions.suggestCommand("/tpall "))
					.color(TextColors.RED).build();
		} 
		return Text.builder("/tpall").onClick(TextActions.suggestCommand("/tpall"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source.hasPermission(this.plugin.getPermissions().get("TPALL_OTHERS"))){
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
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("TPALL_OTHERS"))) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandTeleportationAllOthers(source, optPlayer.get());
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
	
	private boolean commandTeleportationAll(EPlayer staff) {
		Optional<Transform<World>> optTransform = teleport(staff);
		if(optTransform.isPresent()) {
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if(!staff.equals(player)) {
					if(player.getWorld().equals(optTransform.get().getExtent()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
							player.hasPermission(this.plugin.getEverAPI().getPermissions().get("WORLDS") + "." + optTransform.get().getExtent().getName())) {
						player.setTransform(optTransform.get());
						player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
								.append(this.plugin.getMessages().getMessage("TPALL_PLAYER")
										.replaceAll("<staff>", staff.getName()))
								.replace("<destination>", getButtonPosition(staff.getName(), player.getLocation()))
								.build());
					}
				}
			}
			staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
					.append(this.plugin.getMessages().getMessage("TPALL_STAFF"))
					.replace("<destination>", getButtonPosition(staff.getName(), optTransform.get().getLocation()))
					.build());
			return true;
		} else {
			staff.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TPALL_ERROR"));
		}
		return false;
	}

	private boolean commandTeleportationAllOthers(CommandSource staff, EPlayer destination) {
		if(!destination.equals(staff)) {
			Optional<Transform<World>> optTransform = teleport(destination);
			if(optTransform.isPresent()) {
				for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
					if(!destination.equals(player)) {
						if(player.getWorld().equals(optTransform.get().getExtent()) || !this.plugin.getConfigs().isWorldTeleportPermissions() ||
								player.hasPermission(this.plugin.getEverAPI().getPermissions().get("WORLDS") + "." + optTransform.get().getExtent().getName())) {
							player.setTransform(optTransform.get());
							if(!player.equals(staff)) {
								player.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
										.append(this.plugin.getMessages().getMessage("TPALL_OTHERS_PLAYER")
												.replaceAll("<staff>", staff.getName()))
										.replace("<destination>", getButtonPosition(destination.getName(), optTransform.get().getLocation()))
										.build());
							}
						}
					}
				}
				staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
						.append(this.plugin.getMessages().getMessage("TPALL_OTHERS_STAFF")
								.replaceAll("<staff>", staff.getName()))
						.replace("<destination>", getButtonPosition(destination.getName(), optTransform.get().getLocation()))
						.build());
				return true;
			} else {
				staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("TPALL_ERROR")));
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
		return EChat.of(this.plugin.getMessages().getMessage("TPALL_DESTINATION").replaceAll("<player>", player)).toBuilder()
					.onHover(TextActions.showText(EChat.of(this.plugin.getMessages().getMessage("TPALL_DESTINATION_HOVER")
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}