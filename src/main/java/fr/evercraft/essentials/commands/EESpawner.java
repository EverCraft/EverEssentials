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

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.MobSpawner;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.MobSpawnerData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntity;

public class EESpawner extends ECommand<EverEssentials> {
	
	public EESpawner(final EverEssentials plugin) {
        super(plugin, "spawner", "mobspawner", "changems");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPAWNER.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SPAWNER_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/spawner <créature> [délais]").onClick(TextActions.suggestCommand("/spawner "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(UtilsEntity type : UtilsEntity.values()){
				suggests.add(type.getName());
			}
		} else if(args.size() == 2){
			suggests.add("60");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le joueur
		if(args.size() == 1) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				Optional<UtilsEntity> optEntity = UtilsEntity.get(args.get(0));
				if (optEntity.isPresent()){
					resultat = commandSpawner((EPlayer) source, optEntity.get());
				} else {
					//source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPAWNER_ERROR_MOB.get()));
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
	
	public boolean commandSpawner(final EPlayer player, UtilsEntity entity) {
		Optional<Vector3i> optBlock = player.getViewBlock();
		if(optBlock.isPresent()) {
			Location<World> location = player.getWorld().getLocation(optBlock.get());
			if(location.getBlock().getType().equals(BlockTypes.MOB_SPAWNER)) {				
				if(location.getTileEntity().isPresent()) {
					MobSpawner spawner = (MobSpawner) location.getTileEntity().get();
					if(spawner.getOrCreate(MobSpawnerData.class).isPresent()) {
						player.sendMessage("MobSpawner : present");
					} else {
						player.sendMessage("MobSpawner : no present");
					}
					if(spawner.offer(spawner.getMobSpawnerData().nextEntityToSpawn().set(entity.getType(), null)).isSuccessful()) {
						player.sendMessage("MobSpawner : add");
					} else {
						player.sendMessage("MobSpawner : error");
					}
					
					if(spawner.offer(Keys.SPAWNABLE_ENTITY_TYPE, entity.getType()).isSuccessful()) {
						player.sendMessage("MobSpawner : add");
					} else {
						player.sendMessage("MobSpawner : error");
					}
				} else {
					player.sendMessage("MobSpawner : no");
				}
			} else {
				player.sendMessage("is not mobspawner");
			}
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_NO_LOOK_BLOCK.get());
		}
		return false;
	}
}
