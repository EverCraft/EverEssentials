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
package fr.evercraft.essentials.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

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

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

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
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		Set<String> suggests = new TreeSet<String>();
		if (args.size() == 1) {
			if(args.get(0).startsWith("minecraft")) {
				for (CatalogType type : this.plugin.getGame().getRegistry().getAllOf(PopulatorObject.class)) {
					suggests.add(type.getId().toUpperCase());
				}
			} else {
				for (CatalogType type : this.plugin.getGame().getRegistry().getAllOf(PopulatorObject.class)) {
					suggests.add(type.getId().replaceAll("minecraft:", "").toUpperCase());
				}
			}
		}
		return new ArrayList<String>(suggests);
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandTree((EPlayer) source, PopulatorObjects.OAK);
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
					resultat = this.commandTree((EPlayer) source, generator.get());
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TREE_INCONNU.get()
							.replaceAll("<type>", args.get(0))));
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
		
		return resultat;
	}
	
	private boolean commandTree(final EPlayer player, PopulatorObject generator) throws CommandException {
		Optional<Vector3i> block = player.getViewBlock();
		
		// Aucun block
		if (!block.isPresent()) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_NO_LOOK_BLOCK.get());
			return false;
		}
		
		Vector3i location = block.get().add(0, 1, 0);
		
		// Impossible de le placer
		if (!generator.canPlaceAt(player.getWorld(), location.getX(), location.getY(), location.getZ())) {
			if (!generator.equals(PopulatorObjects.DESERT_WELL)) {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TREE_NO_CAN_DIRT.get());
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TREE_NO_CAN_SAND.get());
			}
		}
			
		generator.placeObject(player.getWorld(), player.getRandom(), location.getX(), location.getY(), location.getZ());
		return true;
	}
}
