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
import java.util.concurrent.CompletableFuture;

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
import fr.evercraft.everapi.services.essentials.SubjectUserEssentials;

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
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_HOME.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1 && source instanceof Player){
			Optional<SubjectUserEssentials> player = this.plugin.getManagerServices().getEssentials().get(((Player) source).getUniqueId());
			// Le joueur existe
			if (player.isPresent()) {
				List<String> suggests = new ArrayList<String>();
				for (String home : player.get().getHomes().keySet()){
					suggests.add(home);
				}
				return suggests;
			}
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Nom du home inconnu
		if (args.size() == 0) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandHomeList((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nom du home connu
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandHomeTeleport((EPlayer) source, args.get(0));
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandHomeList(final EPlayer player) throws CommandException {
		Map<String, Transform<World>> homes = player.getHomes();
		
		// Le joueur n'as pas de home
		if (homes.size() == 0) {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.HOME_EMPTY.getText()));
			return CompletableFuture.completedFuture(false);
		}
		
		// Le joueur a un home
		if (homes.size() == 1) {
			return this.commandHomeTeleport(player, homes.entrySet().iterator().next().getKey());
		}
		
		// Le joueur a plusieurs home
		Optional<EUserSubject> subject = this.plugin.getManagerServices().getEssentials().getSubject(player.getUniqueId());
		if (!subject.isPresent()) {
			EAMessages.PLAYER_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<player>", player.getName())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
				
		List<Text> lists = new ArrayList<Text>();
		for (Entry<String, VirtualTransform> home : (new TreeMap<String, VirtualTransform>(subject.get().getAllHomes())).entrySet()) {
			Optional<World> world = home.getValue().getWorld();
			if (world.isPresent()){
				lists.add(EEMessages.HOME_LIST_LINE.getFormat().toText(
					"<home>", this.getButtonHome(home.getKey(), home.getValue()),
					"<teleport>", this.getButtonTeleport(home.getKey(), home.getValue()),
					"<delete>", this.getButtonDelete(home.getKey(), home.getValue())));
			} else {
				lists.add(EEMessages.HOME_LIST_LINE_ERROR_WORLD.getFormat().toText(
					"<home>", this.getButtonHome(home.getKey(), home.getValue()),
					"<delete>", this.getButtonDelete(home.getKey(), home.getValue())));
			}
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.HOME_LIST_TITLE.getText().toBuilder()
				.onClick(TextActions.runCommand("/home")).build(), lists, player);
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandHomeTeleport(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().getMaxCaractere());
		
		Optional<Transform<World>> home = player.getHome(home_name);
		// Le joueur n'a pas de home qui porte ce nom
		if (!home.isPresent()) {
			EEMessages.HOME_INCONNU.sender()
				.replace("<home>", name)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		if (!player.teleport(home.get(), true)) {
			EAMessages.PLAYER_ERROR_TELEPORT.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		EEMessages.HOME_TELEPORT.sender()
			.replace("<home>", this.getButtonHome(name, home.get()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonTeleport(final String name, final VirtualTransform location){
		return EEMessages.HOME_LIST_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.HOME_LIST_TELEPORT_HOVER.getFormat()
							.toText("<home>", name)))
					.onClick(TextActions.runCommand("/home \"" + name+ "\""))
					.build();
	}
	
	private Text getButtonDelete(final String name, final VirtualTransform location){
		return EEMessages.HOME_LIST_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.HOME_LIST_DELETE_HOVER.getFormat()
							.toText("<home>", name)))
					.onClick(TextActions.runCommand("/delhome \"" + name+ "\""))
					.build();
	}
	
	private Text getButtonHome(final String name, final VirtualTransform location){
		return EEMessages.HOME_NAME.getFormat().toText("<name>", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.HOME_NAME_HOVER.getFormat().toText(
							"<home>", name,
							"<world>", location.getWorldName(),
							"<x>", String.valueOf(location.getPosition().getX()),
							"<y>", String.valueOf(location.getPosition().getY()),
							"<z>", String.valueOf(location.getPosition().getZ()))))
					.build();
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
}
