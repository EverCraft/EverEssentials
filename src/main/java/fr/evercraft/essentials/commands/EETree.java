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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.gen.PopulatorObject;
import org.spongepowered.api.world.gen.PopulatorObjects;
import org.spongepowered.api.world.gen.type.MushroomTypes;

import com.flowpowered.math.vector.Vector3i;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EETree extends ECommand<EverEssentials> {
	
	public EETree(final EverEssentials plugin) {
        super(plugin, "tree");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TREE.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TREE_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/tree ").onClick(TextActions.suggestCommand("/tree "))
				.append(Text.of("<"))
				.append(Text.builder("birch").onClick(TextActions.suggestCommand("/tree birch")).build())
				.append(Text.of("|"))
				.append(Text.builder("canopy").onClick(TextActions.suggestCommand("/tree canopy")).build())
				.append(Text.of("|"))
				.append(Text.builder("jungle").onClick(TextActions.suggestCommand("/tree jungle")).build())
				.append(Text.of("|"))
				.append(Text.builder("jungle_bush").onClick(TextActions.suggestCommand("/tree jungle_bush")).build())
				.append(Text.of("|"))
				.append(Text.builder("mega_birch").onClick(TextActions.suggestCommand("/tree mega_birch")).build())
				.append(Text.of("|"))
				.append(Text.builder("mega_jungle").onClick(TextActions.suggestCommand("/tree mega_jungle")).build())
				.append(Text.of("|"))
				.append(Text.builder("mega_oak").onClick(TextActions.suggestCommand("/tree mega_oak")).build())
				.append(Text.of("|"))
				.append(Text.builder("mega_pointy_taiga").onClick(TextActions.suggestCommand("/tree mega_pointy_taiga")).build())
				.append(Text.of("|"))
				.append(Text.builder("mega_tall_taiga").onClick(TextActions.suggestCommand("/tree mega_tall_taiga")).build())
				.append(Text.of("|"))
				.append(Text.builder("mushroom_brown").onClick(TextActions.suggestCommand("/tree mushroom_brown")).build())
				.append(Text.of("|"))
				.append(Text.builder("mushroom_red").onClick(TextActions.suggestCommand("/tree mushroom_red")).build())
				.append(Text.of("|"))
				.append(Text.builder("oak").onClick(TextActions.suggestCommand("/tree oak")).build())
				.append(Text.of("|"))
				.append(Text.builder("pointy_taiga").onClick(TextActions.suggestCommand("/tree pointy_taiga")).build())
				.append(Text.of("|"))
				.append(Text.builder("savanna").onClick(TextActions.suggestCommand("/tree savanna")).build())
				.append(Text.of("|"))
				.append(Text.builder("swamp").onClick(TextActions.suggestCommand("/tree swamp")).build())
				.append(Text.of("|"))
				.append(Text.builder("tall_taiga").onClick(TextActions.suggestCommand("/tree tall_taiga")).build())
				.append(Text.of(">"))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("birch");
			suggests.add("mushroom_brown");
			suggests.add("canopy");
			suggests.add("jungle");
			suggests.add("jungle_bush");
			suggests.add("mega_birch");
			suggests.add("mega_jungle");
			suggests.add("mega_oak");
			suggests.add("mega_pointy_taiga");
			suggests.add("mega_tall_taiga");
			suggests.add("oak");
			suggests.add("pointy_taiga");
			suggests.add("mushroom_red");
			suggests.add("savanna");
			suggests.add("swamp");
			suggests.add("tall_taiga");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandTree((EPlayer) source, PopulatorObjects.OAK);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// On connais le joueur
		} else if(args.size() == 1) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				Optional<PopulatorObject> optGenerator = getGenerator(args.get(0));
				if(optGenerator.isPresent()){
					resultat = commandTree((EPlayer) source, optGenerator.get());
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TREE_INCONNU.get()
							.replaceAll("<type>", args.get(0))));
				}
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
	
	public boolean commandTree(final EPlayer player, PopulatorObject generator) throws CommandException {
		Optional<Vector3i> optBlock = player.getViewBlock();
		if(optBlock.isPresent()) {
			Vector3i block = optBlock.get().add(0, 1, 0);
			if(generator.canPlaceAt(player.getWorld(), block.getX(), block.getY(), block.getZ())) {
				generator.placeObject(player.getWorld(), player.getRandom(), block.getX(), block.getY(), block.getZ());
				return true;
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TREE_NO_CAN.get());
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_NO_LOOK_BLOCK.get());
		}
		return false;
	}
	
	private Optional<PopulatorObject> getGenerator(String tree_name) {
		PopulatorObject generator = null;
		if(tree_name.equalsIgnoreCase("birch")) {
			generator = PopulatorObjects.BIRCH;
		} else if(tree_name.equalsIgnoreCase("mushroom_brown")) {
			generator = MushroomTypes.BROWN.getPopulatorObject();
		} else if(tree_name.equalsIgnoreCase("canopy")) {
			generator = PopulatorObjects.CANOPY;
		} else if(tree_name.equalsIgnoreCase("jungle")) {
			generator = PopulatorObjects.JUNGLE;
		} else if(tree_name.equalsIgnoreCase("jungle_bush")) {
			generator = PopulatorObjects.JUNGLE_BUSH;
		} else if(tree_name.equalsIgnoreCase("mega_birch")) {
			generator = PopulatorObjects.MEGA_BIRCH;
		} else if(tree_name.equalsIgnoreCase("mega_jungle")) {
			generator = PopulatorObjects.MEGA_JUNGLE;
		} else if(tree_name.equalsIgnoreCase("mega_oak")) {
			generator = PopulatorObjects.MEGA_OAK;
		} else if(tree_name.equalsIgnoreCase("mega_pointy_taiga")) {
			generator = PopulatorObjects.MEGA_POINTY_TAIGA;
		} else if(tree_name.equalsIgnoreCase("mega_tall_taiga")) {
			generator = PopulatorObjects.MEGA_TALL_TAIGA;
		} else if(tree_name.equalsIgnoreCase("oak")) {
			generator = PopulatorObjects.OAK;
		} else if(tree_name.equalsIgnoreCase("pointy_taiga")) {
			generator = PopulatorObjects.POINTY_TAIGA;
		} else if(tree_name.equalsIgnoreCase("mushroom_red")) {
			generator = MushroomTypes.RED.getPopulatorObject();
		} else if(tree_name.equalsIgnoreCase("savanna")) {
			generator = PopulatorObjects.SAVANNA;
		} else if(tree_name.equalsIgnoreCase("swamp")) {
			generator = PopulatorObjects.SWAMP;
		} else if(tree_name.equalsIgnoreCase("tall_taiga")) {
			generator = PopulatorObjects.TALL_TAIGA;
		}
		return Optional.ofNullable(generator);
	}
}
