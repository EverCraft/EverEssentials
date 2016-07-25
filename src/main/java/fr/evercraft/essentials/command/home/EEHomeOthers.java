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
import fr.evercraft.essentials.service.subject.ESubject;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEHomeOthers extends ECommand<EverEssentials> {
	
	public EEHomeOthers(final EverEssentials plugin) {
        super(plugin, "homeothers", "homesothers");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.HOME_OTHERS.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.HOMEOTHERS_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/homeothers <" + EAMessages.ARGS_PLAYER.get() + "> [" + EAMessages.ARGS_HOME.get() + " [delete]]")
				.onClick(TextActions.suggestCommand("/homeothers "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source instanceof Player){
			suggests = null;
		} else if(args.size() == 2) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()) {
				for(String home : optPlayer.get().getHomes().keySet()){
					suggests.add(home);
				}
			}
		} else if(args.size() == 3) {
			suggests.add("delete");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Nom du home inconnu
		if (args.size() == 1) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = commandHomeList(source, optPlayer.get());
			// Le joueur est introuvable
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
		} else if(args.size() == 2) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur existe
				if(optPlayer.isPresent()){
					resultat = commandHomeTeleport((EPlayer) source, optPlayer.get(), args.get(1));
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 3 && args.get(2).equalsIgnoreCase("delete")) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = commandHomeDelete(source, optPlayer.get(), args.get(1));
			// Le joueur est introuvable
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
		} else if(args.size() == 4 && args.get(2).equalsIgnoreCase("delete") && args.get(3).equalsIgnoreCase("confirmation")) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = commandHomeDeleteConfirmation(source, optPlayer.get(), args.get(1));
			// Le joueur est introuvable
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandHomeList(final CommandSource staff, final EPlayer player){
		Optional<ESubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if(subject.isPresent()) {
			Map<String, LocationSQL> homes = subject.get().getAllHomes();
			// Le joueur n'as pas de home
			if(homes.size() == 0){
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.HOMEOTHERS_EMPTY.get().replaceAll("<player>", player.getName())));
			// Le joueur au moins un home
			} else {
				List<Text> lists = new ArrayList<Text>();
				for (Entry<String, LocationSQL> home : (new TreeMap<String, LocationSQL>(homes)).entrySet()) {
					lists.add(ETextBuilder.toBuilder(EEMessages.HOMEOTHERS_LIST_LINE.get()
									.replaceAll("<player>", player.getName()))
								.replace("<home>", getButtonHome(home.getKey(), home.getValue()))
								.replace("<teleport>", getButtonTeleport(player.getName(), home.getKey()))
								.replace("<delete>", getButtonDelete(player.getName(), home.getKey()))
								.build());
				}
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(EEMessages.HOMEOTHERS_LIST_TITLE.get()
						.replaceAll("<player>", player.getName())).toBuilder()
						.onClick(TextActions.runCommand("/homeothers " + player.getName())).build(), lists, staff);
				return true;
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get());
		}
		return false;
	}
	
	public boolean commandHomeTeleport(final EPlayer staff, final EPlayer player, final String home_name){
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> home = player.getHome(name);
		// Le joueur a home qui porte ce nom
		if(home.isPresent()) {
			staff.setBack();
			staff.setTransform(home.get());
			staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
								.append(EEMessages.HOMEOTHERS_TELEPORT.get()
										.replaceAll("<player>", player.getName()))
								.replace("<home>", getButtonHome(name, home.get()))
								.build());
			return true;
		// Le joueur n'a pas de home qui porte ce nom
		} else {
			staff.sendMessage(EEMessages.PREFIX.get() + EEMessages.HOMEOTHERS_INCONNU.get()
					.replaceAll("<player>", player.getName())
					.replaceAll("<home>", name));
		}
		return false;
	}
	
	public boolean commandHomeDelete(final CommandSource staff, final EPlayer player, final String home_name){
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<ESubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if(subject.isPresent()) {
			Optional<LocationSQL> home = subject.get().getHomeLocation(name);
			// Le joueur a bien un home qui porte ce nom
			if(home.isPresent()) {
				staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
						.append(EEMessages.HOMEOTHERS_DELETE_CONFIRMATION.get()
								.replaceAll("<player>", player.getName()))
						.replace("<home>", getButtonHome(name, home.get()))
						.replace("<confirmation>", getButtonConfirmation(player.getName(), name))
						.build());
			// Le n'a pas de home qui porte ce nom
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.HOMEOTHERS_INCONNU.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<home>", name)));
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
		}
		return false;
	}
	
	public boolean commandHomeDeleteConfirmation(final CommandSource staff, final EPlayer player, final String home_name){
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<ESubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if(subject.isPresent()) {
			Optional<LocationSQL> home = subject.get().getHomeLocation(name);
			// Le joueur a bien un home qui porte ce nom
			if(home.isPresent()) {
				// Si le home a bien été supprimer
				if(player.removeHome(name)) {
					staff.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.HOMEOTHERS_DELETE.get()
									.replaceAll("<player>", player.getName()))
							.replace("<home>", getButtonHome(name, home.get()))
							.build());
					return true;
				// Le home n'a pas été supprimer
				} else {
					staff.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR.getText()));
				}
			// Le n'a pas de home qui porte ce nom
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.DELHOME_INCONNU.get().replaceAll("<home>", name)));
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
		}
		return false;
	}
	
	public Text getButtonHome(final String name, final Transform<World> location) {
		return EChat.of(EEMessages.HOME_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_NAME_HOVER.get()
							.replaceAll("<home>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getLocation().getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getLocation().getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getLocation().getBlockZ())))))
					.build();
	}
	
	public Text getButtonHome(final String name, final LocationSQL location) {
		return EChat.of(EEMessages.HOME_NAME.get().replaceAll("<name>", name)).toBuilder()
				.onHover(TextActions.showText(EChat.of(EEMessages.HOME_NAME_HOVER.get()
						.replaceAll("<home>", name)
						.replaceAll("<world>", location.getWorldName())
						.replaceAll("<x>", location.getX().toString())
						.replaceAll("<y>", location.getY().toString())
						.replaceAll("<z>", location.getZ().toString()))))
				.build();
	}
	
	public Text getButtonTeleport(final String player, final String name) {
		return EEMessages.HOMEOTHERS_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOMEOTHERS_LIST_TELEPORT_HOVER.get()
							.replaceAll("<player>", player)
							.replaceAll("<home>", name))))
					.onClick(TextActions.runCommand("/homeothers " + player + " \"" + name + "\""))
					.build();
	}
	
	public Text getButtonDelete(final String player, final String name) {
		return EEMessages.HOMEOTHERS_LIST_DELETE.getText().toBuilder()
				.onHover(TextActions.showText(EChat.of(EEMessages.HOMEOTHERS_LIST_DELETE_HOVER.get()
						.replaceAll("<player>", player)
						.replaceAll("<home>", name))))
				.onClick(TextActions.runCommand("/homeothers " + player + " \"" + name + "\" delete"))
				.build();
	}
	
	public Text getButtonConfirmation(final String player, final String name) {
		return EEMessages.HOMEOTHERS_DELETEE_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOMEOTHERS_DELETE_CONFIRMATION_VALID_HOVER.get()
							.replaceAll("<player>", player)
							.replaceAll("<home>", name))))
					.onClick(TextActions.runCommand("/homeothers " + player + " \"" + name + "\" delete confirmation"))
					.build();
	}
}
