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
import fr.evercraft.essentials.service.ESubject;
import fr.evercraft.essentials.service.warp.LocationSQL;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEHome extends ECommand<EverEssentials> {
	
	public EEHome(final EverEssentials plugin) {
        super(plugin, "home", "homes", "residence", "residences");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.HOME.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.HOME_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/home [home]").onClick(TextActions.suggestCommand("/home "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source instanceof Player){
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer((Player) source);
			// Le joueur existe
			if (optPlayer.isPresent()) {
				for(String home : optPlayer.get().getHomes().keySet()){
					suggests.add(home);
				}
			}
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Nom du home inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandHomeList((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nom du home connu
		} else if(args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = commandHomeTeleport((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandHomeList(final EPlayer player) throws CommandException {
		ESubject subject = this.plugin.getManagerServices().getEssentials().get(player.getUniqueId());
		if(subject != null) {
			Map<String, LocationSQL> homes = subject.getAllHomes();
			// Le joueur n'as pas de home
			if(homes.size() == 0) {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.HOME_EMPTY.getText()));
			// Le joueur a un home
			} else if(homes.size() == 1) {
				List<String> args = new ArrayList<String>();
				Entry<String, LocationSQL> home = homes.entrySet().iterator().next();
				if(home.getValue().getWorld().isPresent()) {
					args.add(homes.entrySet().iterator().next().getKey());
					return execute(player, args);
				} else {
					player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.HOME_EMPTY.getText()));
				}
			// Le joueur a plusieurs home
			} else {
				List<Text> lists = new ArrayList<Text>();
				for (Entry<String, LocationSQL> home : (new TreeMap<String, LocationSQL>(homes)).entrySet()) {
					Optional<World> world = home.getValue().getWorld();
					if(world.isPresent()){
						lists.add(ETextBuilder.toBuilder(EEMessages.HOME_LIST_LINE.get())
							.replace("<home>", getButtonHome(home.getKey(), home.getValue()))
							.replace("<teleport>", getButtonTeleport(home.getKey(), home.getValue()))
							.replace("<delete>", getButtonDelete(home.getKey(), home.getValue()))
							.build());
					} else {
						lists.add(ETextBuilder.toBuilder(EEMessages.HOME_LIST_LINE_ERROR_WORLD.getText())
							.replace("<home>", getButtonHome(home.getKey(), home.getValue()))
							.replace("<delete>", getButtonDelete(home.getKey(), home.getValue()))
							.build());
					}
				}
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.HOME_LIST_TITLE.getText().toBuilder()
						.onClick(TextActions.runCommand("/home")).build(), lists, player);
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
		}
		return false;
	}
	
	public boolean commandHomeTeleport(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		Optional<Transform<World>> home = player.getHome(home_name);
		// Le joueur a home qui porte ce nom
		if(home.isPresent()){
			player.setBack();
			player.setTransform(home.get());
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.HOME_TELEPORT.get())
					.replace("<home>", getButtonHome(name, home.get()))
					.build());
			return true;
		// Le joueur n'a pas de home qui porte ce nom
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.HOME_INCONNU.get()
					.replaceAll("<home>", name));
		}
		return false;
	}
	
	public Text getButtonTeleport(final String name, final LocationSQL location){
		return EEMessages.HOME_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_LIST_TELEPORT_HOVER.get()
							.replaceAll("<home>", name))))
					.onClick(TextActions.runCommand("/home \"" + name+ "\""))
					.build();
	}
	
	public Text getButtonDelete(final String name, final LocationSQL location){
		return EEMessages.HOME_LIST_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_LIST_DELETE_HOVER.get()
							.replaceAll("<home>", name))))
					.onClick(TextActions.runCommand("/delhome \"" + name+ "\""))
					.build();
	}
	
	public Text getButtonHome(final String name, final LocationSQL location){
		return EChat.of(EEMessages.HOME_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_NAME_HOVER.get()
							.replaceAll("<home>", name)
							.replaceAll("<world>", location.getWorldName())
							.replaceAll("<x>", location.getX().toString())
							.replaceAll("<y>", location.getY().toString())
							.replaceAll("<z>", location.getZ().toString()))))
					.build();
	}
	
	public Text getButtonHome(final String name, final Transform<World> location){
		return EChat.of(EEMessages.HOME_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_NAME_HOVER.get()
							.replaceAll("<home>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getLocation().getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getLocation().getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getLocation().getBlockZ())))))
					.build();
	}
}
