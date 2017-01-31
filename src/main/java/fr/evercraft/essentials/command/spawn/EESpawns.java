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
import java.util.Arrays;
import java.util.Collection;
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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.location.VirtualLocation;

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
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
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
		TreeMap<String, VirtualLocation> spawns = new TreeMap<String, VirtualLocation>(this.plugin.getManagerServices().getSpawn().getAllSQL());
		
		List<Text> lists = new ArrayList<Text>();
		if (player.hasPermission(EEPermissions.DELSPAWN.get())) {
			
			for (Entry<String, VirtualLocation> spawn : spawns.entrySet()) {
				Optional<World> world = spawn.getValue().getWorld();
				if (world.isPresent()){
					lists.add(EEMessages.SPAWNS_LINE_DELETE.getFormat().toText(
							"<spawn>", () -> this.getButtonSpawn(spawn.getKey(), spawn.getValue()),
							"<teleport>", () -> this.getButtonTeleport(spawn.getKey(), spawn.getValue()),
							"<delete>", () -> this.getButtonDelete(spawn.getKey(), spawn.getValue())));
				} else {
					lists.add(EEMessages.SPAWNS_LINE_DELETE_ERROR_WORLD.getFormat().toText(
							"<spawn>", () -> this.getButtonSpawn(spawn.getKey(), spawn.getValue()),
							"<delete>", () -> this.getButtonDelete(spawn.getKey(), spawn.getValue())));
				}
			}
			
		} else {
			
			for (Entry<String, VirtualLocation> spawn : spawns.entrySet()) {
				Optional<World> world = spawn.getValue().getWorld();
				if (world.isPresent()){
					lists.add(EEMessages.SPAWNS_LINE.getFormat().toText(
							"<spawn>", () -> this.getButtonSpawn(spawn.getKey(), spawn.getValue()),
							"<teleport>", () -> this.getButtonTeleport(spawn.getKey(), spawn.getValue())));
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
	
	private Text getButtonTeleport(final String name, final VirtualLocation location){
		return EEMessages.SPAWNS_TELEPORT.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.SPAWNS_TELEPORT_HOVER.getFormat()
							.toText("<name>", name)))
					.onClick(TextActions.runCommand("/spawn \"" + name + "\""))
					.build();
	}
	
	private Text getButtonDelete(final String name, final VirtualLocation location){
		return EEMessages.SPAWNS_DELETE.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.SPAWNS_DELETE_HOVER.getFormat()
							.toText("<name>", name)))
					.onClick(TextActions.runCommand("/delspawn \"" + name + "\""))
					.build();
	}
	
	private Text getButtonSpawn(final String name, final VirtualLocation location){
		return EEMessages.SPAWNS_NAME.getFormat().toText("<name>", name).toBuilder()
					.onHover(TextActions.showText(EEMessages.SPAWNS_NAME_HOVER.getFormat().toText(
							"<name>", name,
							"<world>", location.getWorldName(),
							"<x>", location.getFloorX().toString(),
							"<y>", location.getFloorY().toString(),
							"<z>", location.getFloorZ().toString())))
					.build();
	}
}
