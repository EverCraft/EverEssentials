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
package fr.evercraft.essentials.command.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.subject.EUserSubject;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.VirtualTransform;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;

public class EEHomeOthers extends ECommand<EverEssentials> {
	
	public EEHomeOthers(final EverEssentials plugin) {
        super(plugin, "homeothers", "homesothers");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.HOME_OTHERS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.HOMEOTHERS_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_USER.getString() + "> [" + EAMessages.ARGS_HOME.getString() + " [delete]]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source instanceof Player){
			return this.getAllUsers(args.get(0), source);
		} else if (args.size() == 2) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()) {
				List<String> suggests = new ArrayList<String>();
				for (String home : optPlayer.get().getHomes().keySet()){
					suggests.add(home);
				}
				return suggests;
			}
		} else if (args.size() == 3) {
			if (source.hasPermission(EEPermissions.HOME_OTHERS_DELETE.get())) {
				return Arrays.asList("delete");
			}
		}
		return Arrays.asList();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Nom du home inconnu
		if (args.size() == 1) {
			
			Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
			// Le joueur existe
			if (user.isPresent()){
				resultat = this.commandHomeList(source, user.get());
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 2) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					resultat = this.commandHomeTeleport((EPlayer) source, user.get(), args.get(1));
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 3) {
			if (args.get(2).equalsIgnoreCase("delete")) {
				
				// Si il a la permission
				if (source.hasPermission(EEPermissions.HOME_OTHERS_DELETE.get())) {
					Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
					// Le joueur existe
					if (user.isPresent()){
						resultat = this.commandHomeDelete(source, user.get(), args.get(1));
					// Le joueur est introuvable
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
				
			} else {
				source.sendMessage(this.help(source));
			}
		} else if (args.size() == 4) {
			if(args.get(2).equalsIgnoreCase("delete") && args.get(3).equalsIgnoreCase("confirmation")) {

				// Si il a la permission
				if (source.hasPermission(EEPermissions.HOME_OTHERS_DELETE.get())) {
					Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(0));
					// Le joueur existe
					if (user.isPresent()) {
						resultat = this.commandHomeDeleteConfirmation(source, user.get(), args.get(1));
					// Le joueur est introuvable
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
				
			} else {
				source.sendMessage(this.help(source));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandHomeList(final CommandSource staff, final EUser user){
		Map<String, Transform<World>> homes = user.getHomes();
		
		// Le joueur n'as pas de home
		if (homes.size() == 0) {
			EEMessages.HOMEOTHERS_EMPTY.sender()
				.replace("<player>", user.getName())
				.sendTo(staff);
			return false;
		}
		
		List<Text> lists = new ArrayList<Text>();
		for (Entry<String, Transform<World>> home : (new TreeMap<String, Transform<World>>(homes)).entrySet()) {
			lists.add(EEMessages.HOMEOTHERS_LIST_LINE.getFormat().toText(
						"<player>", user.getName(),
						"<home>", this.getButtonHome(home.getKey(), home.getValue()),
						"<teleport>", this.getButtonTeleport(user.getName(), home.getKey()),
						"<delete>", this.getButtonDelete(user.getName(), home.getKey())));
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.HOMEOTHERS_LIST_TITLE.getFormat()
				.toText("<player>", user.getName()).toBuilder()
				.onClick(TextActions.runCommand("/homeothers " + user.getName())).build(), lists, staff);
		return true;
	}
	
	private boolean commandHomeTeleport(final EPlayer staff, final EUser player, final String home_name){
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<Transform<World>> home = player.getHome(name);
		// Le joueur n'a pas de home qui porte ce nom
		if (!home.isPresent()) {
			EEMessages.HOMEOTHERS_INCONNU.sender()
				.replace("<player>", player.getName())
				.replace("<home>", name)
				.sendTo(staff);
			return false;
		}
		
		staff.teleport(home.get(), true);
		EEMessages.HOMEOTHERS_TELEPORT.sender()
			.replace("<player>", player.getName())
			.replace("<home>", this.getButtonHome(name, home.get()))
			.sendTo(staff);
		return true;
	}
	
	private boolean commandHomeDelete(final CommandSource staff, final EUser user, final String home_name){
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());

		Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(user.getUniqueId());
		if (!subject.isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
			
		Optional<VirtualTransform> home = subject.get().getHomeLocation(name);
		// Le n'a pas de home qui porte ce nom
		if (!home.isPresent()) {
			EEMessages.HOMEOTHERS_INCONNU.sender()
				.replace("<player>", user.getName())
				.replace("<home>", name)
				.sendTo(staff);
			return false;
		}
		
		EEMessages.HOMEOTHERS_DELETE_CONFIRMATION.sender()
			.replace("<player>", user.getName())
			.replace("<home>", this.getButtonHome(name, home.get()))
			.replace("<confirmation>", this.getButtonConfirmation(user.getName(), name))
			.sendTo(staff);
		return false;
	}
	
	private boolean commandHomeDeleteConfirmation(final CommandSource staff, final EUser user, final String home_name){
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(user.getUniqueId());
		if (!subject.isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
		
		Optional<VirtualTransform> home = subject.get().getHomeLocation(name);
		// Le n'a pas de home qui porte ce nom
		if (!home.isPresent()) {
			EEMessages.DELHOME_INCONNU.sender()
				.replace("<home>", name)
				.sendTo(staff);
			return false;
		}
				
		// Le home n'a pas été supprimer
		if (!user.removeHome(name)) {
			EAMessages.COMMAND_ERROR.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
		
		EEMessages.HOMEOTHERS_DELETE.sender()
			.replace("<player>", user.getName())
			.replace("<home>", getButtonHome(name, home.get()))
			.sendTo(staff);
		return true;
	}
	
	private Text getButtonHome(final String name, final Transform<World> location) {
		return EEMessages.HOME_NAME.getFormat().toText("<name>", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.HOME_NAME_HOVER.getFormat().toText(
								"<home>", name,
								"<world>", location.getExtent().getName(),
								"<x>", String.valueOf(location.getLocation().getBlockX()),
								"<y>", String.valueOf(location.getLocation().getBlockY()),
								"<z>", String.valueOf(location.getLocation().getBlockZ()))))
					.build();
	}
	
	private Text getButtonHome(final String name, final VirtualTransform location) {
		return EEMessages.HOME_NAME.getFormat().toText("<name>", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.HOME_NAME_HOVER.getFormat().toText(
								"<home>", name,
								"<world>", location.getWorldName(),
								"<x>", String.valueOf(location.getPosition().getFloorX()),
								"<y>", String.valueOf(location.getPosition().getFloorY()),
								"<z>", String.valueOf(location.getPosition().getFloorZ()))))
					.build();
	}
	
	private Text getButtonTeleport(final String player, final String name) {
		return EEMessages.HOMEOTHERS_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.HOMEOTHERS_LIST_TELEPORT_HOVER.getFormat().toText(
								"<player>", player,
								"<home>", name)))
					.onClick(TextActions.runCommand("/homeothers " + player + " \"" + name + "\""))
					.build();
	}
	
	private Text getButtonDelete(final String player, final String name) {
		return EEMessages.HOMEOTHERS_LIST_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.HOMEOTHERS_LIST_DELETE_HOVER.getFormat().toText(
								"<player>", player,
								"<home>", name)))
					.onClick(TextActions.runCommand("/homeothers " + player + " \"" + name + "\" delete"))
					.build();
	}
	
	private Text getButtonConfirmation(final String player, final String name) {
		return EEMessages.HOMEOTHERS_DELETEE_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.HOMEOTHERS_DELETE_CONFIRMATION_VALID_HOVER.getFormat().toText(
								"<player>", player,
								"<home>", name)))
					.onClick(TextActions.runCommand("/homeothers " + player + " \"" + name + "\" delete confirmation"))
					.build();
	}
}
