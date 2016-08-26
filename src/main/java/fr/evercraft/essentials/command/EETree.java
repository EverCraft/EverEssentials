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
			populator.add(Text.builder(type.getName())
								.onClick(TextActions.suggestCommand("/" + this.getName() + " " + type.getName()))
								.build());
		}
		
		return build.append(Text.of(">"))
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (CatalogType type : this.plugin.getGame().getRegistry().getAllOf(PopulatorObject.class)){
				suggests.add(type.getName());
			}
		}
		return suggests;
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
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// On connais le joueur
		} else if (args.size() == 1) {
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<PopulatorObject> optGenerator = this.plugin.getGame().getRegistry().getType(PopulatorObject.class, args.get(0));
				if (optGenerator.isPresent()){
					resultat = this.commandTree((EPlayer) source, optGenerator.get());
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.TREE_INCONNU.get()
							.replaceAll("<type>", args.get(0))));
				}
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
	
	public boolean commandTree(final EPlayer player, PopulatorObject generator) throws CommandException {
		Optional<Vector3i> optBlock = player.getViewBlock();
		if (optBlock.isPresent()) {
			Vector3i block = optBlock.get().add(0, 1, 0);
			if (generator.canPlaceAt(player.getWorld(), block.getX(), block.getY(), block.getZ())) {
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
}
