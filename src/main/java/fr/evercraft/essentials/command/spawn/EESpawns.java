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
package fr.evercraft.essentials.command.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.LocationSQL;
import fr.evercraft.everapi.text.ETextBuilder;

public class EESpawns extends ECommand<EverEssentials> {
		
	public EESpawns(final EverEssentials plugin) {
        super(plugin, "spawns");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPAWNS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SPAWNS_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;

		if (args.size() == 0) {
			resultat = this.commandSpawns(source);
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandSpawns(final CommandSource player) throws CommandException {
		TreeMap<String, LocationSQL> spawns = new TreeMap<String, LocationSQL>(this.plugin.getManagerServices().getSpawn().getAllSQL());
		
		List<Text> lists = new ArrayList<Text>();
		if (player.hasPermission(EEPermissions.DELSPAWN.get())) {
			
			for (Entry<String, LocationSQL> spawn : spawns.entrySet()) {
				Optional<World> world = spawn.getValue().getWorld();
				if (world.isPresent()){
					lists.add(ETextBuilder.toBuilder(EEMessages.SPAWNS_LINE_DELETE.get())
						.replace("<spawn>", this.getButtonSpawn(spawn.getKey(), spawn.getValue()))
						.replace("<teleport>", this.getButtonTeleport(spawn.getKey(), spawn.getValue()))
						.replace("<delete>", this.getButtonDelete(spawn.getKey(), spawn.getValue()))
						.build());
				} else {
					lists.add(ETextBuilder.toBuilder(EEMessages.SPAWNS_LINE_DELETE_ERROR_WORLD.get())
							.replace("<spawn>", this.getButtonSpawn(spawn.getKey(), spawn.getValue()))
							.replace("<delete>", this.getButtonDelete(spawn.getKey(), spawn.getValue()))
							.build());
				}
			}
			
		} else {
			
			for (Entry<String, LocationSQL> spawn : spawns.entrySet()) {
				Optional<World> world = spawn.getValue().getWorld();
				if (world.isPresent()){
					lists.add(ETextBuilder.toBuilder(EEMessages.SPAWNS_LINE.get())
						.replace("<spawn>", this.getButtonSpawn(spawn.getKey(), spawn.getValue()))
						.replace("<teleport>", this.getButtonTeleport(spawn.getKey(), spawn.getValue()))
						.build());
				}
			}
			
		}
		
		if (lists.isEmpty()) {
			lists.add(EEMessages.SPAWNS_EMPTY.getText());
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.SPAWNS_TITLE.getText().toBuilder()
				.onClick(TextActions.runCommand("/spawns")).build(), lists, player);		
		return false;
	}
	
	private Text getButtonTeleport(final String name, final LocationSQL location){
		return EEMessages.SPAWNS_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.SPAWNS_TELEPORT_HOVER.get()
							.replaceAll("<name>", name))))
					.onClick(TextActions.runCommand("/spawn \"" + name + "\""))
					.build();
	}
	
	private Text getButtonDelete(final String name, final LocationSQL location){
		return EEMessages.SPAWNS_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.SPAWNS_DELETE_HOVER.get()
							.replaceAll("<name>", name))))
					.onClick(TextActions.runCommand("/delspawn \"" + name + "\""))
					.build();
	}
	
	private Text getButtonSpawn(final String name, final LocationSQL location){
		return EChat.of(EEMessages.SPAWNS_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.SPAWNS_NAME_HOVER.get()
							.replaceAll("<name>", name)
							.replaceAll("<world>", location.getWorldName())
							.replaceAll("<x>", location.getX().toString())
							.replaceAll("<y>", location.getY().toString())
							.replaceAll("<z>", location.getZ().toString()))))
					.build();
	}
}
