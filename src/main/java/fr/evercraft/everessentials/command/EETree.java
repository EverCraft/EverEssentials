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
package fr.evercraft.everessentials.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.gen.PopulatorObject;
import org.spongepowered.api.world.gen.PopulatorObjects;

import com.flowpowered.math.vector.Vector3i;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EETree extends ECommand<EverEssentials> {
	
	public EETree(final EverEssentials plugin) {
        super(plugin, "tree");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TREE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.TREE_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		Builder build = Text.builder("/" + this.getName() + " <");
		
		List<Text> populator = new ArrayList<Text>();
		for (CatalogType type : this.plugin.getGame().getRegistry().getAllOf(PopulatorObject.class)){
			populator.add(Text.builder(type.getId().replaceAll("minecraft:", ""))
								.onClick(TextActions.suggestCommand("/" + this.getName() + " " + type.getId().replaceAll("minecraft:", "").toUpperCase()))
								.build());
		}
		build.append(Text.joinWith(Text.of("|"), populator));
		return build.append(Text.of(">"))
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			Set<String> suggests = new TreeSet<String>();
			if(args.get(0).startsWith("minecraft")) {
				for (CatalogType type : this.plugin.getGame().getRegistry().getAllOf(PopulatorObject.class)) {
					suggests.add(type.getId().toUpperCase());
				}
			} else {
				for (CatalogType type : this.plugin.getGame().getRegistry().getAllOf(PopulatorObject.class)) {
					suggests.add(type.getId().replaceAll("minecraft:", "").toUpperCase());
				}
			}
			return suggests;
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandTree((EPlayer) source, PopulatorObjects.OAK);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		// On connais le joueur
		} else if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<PopulatorObject> generator = this.plugin.getGame().getRegistry().getType(PopulatorObject.class, args.get(0));
				if (generator.isPresent()){
					return this.commandTree((EPlayer) source, generator.get());
				} else {
					EEMessages.TREE_INCONNU.sender()
						.replace("{type}", args.get(0))
						.sendTo(source);
				}
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
	
	private CompletableFuture<Boolean> commandTree(final EPlayer player, PopulatorObject generator) throws CommandException {
		Optional<Vector3i> block = player.getViewBlock();
		
		// Aucun block
		if (!block.isPresent()) {
			EAMessages.PLAYER_NO_LOOK_BLOCK.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Vector3i location = block.get().add(0, 1, 0);
		
		// Impossible de le placer
		if (!generator.canPlaceAt(player.getWorld(), location.getX(), location.getY(), location.getZ())) {
			if (!generator.equals(PopulatorObjects.DESERT_WELL)) {
				EEMessages.TREE_NO_CAN_DIRT.sendTo(player);
			} else {
				EEMessages.TREE_NO_CAN_SAND.sendTo(player);
			}
		}
			
		generator.placeObject(player.getWorld(), player.getRandom(), location.getX(), location.getY(), location.getZ());
		return CompletableFuture.completedFuture(true);
	}
}
