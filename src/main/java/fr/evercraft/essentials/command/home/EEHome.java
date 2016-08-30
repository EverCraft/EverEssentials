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
import fr.evercraft.essentials.service.subject.EUserSubject;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEHome extends ECommand<EverEssentials> {
	
	public EEHome(final EverEssentials plugin) {
        super(plugin, "home", "homes", "residence", "residences");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.HOME.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.HOME_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_HOME.get() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source instanceof Player){
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer((Player) source);
			// Le joueur existe
			if (player.isPresent()) {
				for (String home : player.get().getHomes().keySet()){
					suggests.add(home);
				}
			}
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Nom du home inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandHomeList((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// Nom du home connu
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandHomeTeleport((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandHomeList(final EPlayer player) throws CommandException {
		Map<String, Transform<World>> homes = player.getHomes();
		
		// Le joueur n'as pas de home
		if (homes.size() == 0) {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.HOME_EMPTY.getText()));
			
		// Le joueur a un home
		} else if (homes.size() == 1) {
			return this.commandHomeTeleport(player, homes.entrySet().iterator().next().getKey());
			
		// Le joueur a plusieurs home
		} else {
			Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
			if (subject.isPresent()) {
				
				List<Text> lists = new ArrayList<Text>();
				for (Entry<String, LocationSQL> home : (new TreeMap<String, LocationSQL>(subject.get().getAllHomes())).entrySet()) {
					Optional<World> world = home.getValue().getWorld();
					if (world.isPresent()){
						lists.add(ETextBuilder.toBuilder(EEMessages.HOME_LIST_LINE.get())
							.replace("<home>", this.getButtonHome(home.getKey(), home.getValue()))
							.replace("<teleport>", this.getButtonTeleport(home.getKey(), home.getValue()))
							.replace("<delete>", this.getButtonDelete(home.getKey(), home.getValue()))
							.build());
					} else {
						lists.add(ETextBuilder.toBuilder(EEMessages.HOME_LIST_LINE_ERROR_WORLD.getText())
							.replace("<home>", this.getButtonHome(home.getKey(), home.getValue()))
							.replace("<delete>", this.getButtonDelete(home.getKey(), home.getValue()))
							.build());
					}
				}
				
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.HOME_LIST_TITLE.getText().toBuilder()
						.onClick(TextActions.runCommand("/home")).build(), lists, player);
			} else {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
			}
			
		}
		return false;
	}
	
	private boolean commandHomeTeleport(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<Transform<World>> home = player.getHome(home_name);
		// Le joueur a home qui porte ce nom
		if (home.isPresent()) {
			player.teleport(home.get());
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
					.append(EEMessages.HOME_TELEPORT.get())
					.replace("<home>", this.getButtonHome(name, home.get()))
					.build());
			return true;
		// Le joueur n'a pas de home qui porte ce nom
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.HOME_INCONNU.get()
					.replaceAll("<home>", name));
		}
		return false;
	}
	
	private Text getButtonTeleport(final String name, final LocationSQL location){
		return EEMessages.HOME_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_LIST_TELEPORT_HOVER.get()
							.replaceAll("<home>", name))))
					.onClick(TextActions.runCommand("/home \"" + name+ "\""))
					.build();
	}
	
	private Text getButtonDelete(final String name, final LocationSQL location){
		return EEMessages.HOME_LIST_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_LIST_DELETE_HOVER.get()
							.replaceAll("<home>", name))))
					.onClick(TextActions.runCommand("/delhome \"" + name+ "\""))
					.build();
	}
	
	private Text getButtonHome(final String name, final LocationSQL location){
		return EChat.of(EEMessages.HOME_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_NAME_HOVER.get()
							.replaceAll("<home>", name)
							.replaceAll("<world>", location.getWorldName())
							.replaceAll("<x>", location.getX().toString())
							.replaceAll("<y>", location.getY().toString())
							.replaceAll("<z>", location.getZ().toString()))))
					.build();
	}
	
	private Text getButtonHome(final String name, final Transform<World> location){
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
