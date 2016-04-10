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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntity;

public class EESpawnMob extends ECommand<EverEssentials> {
	
	public EESpawnMob(final EverEssentials plugin) {
        super(plugin, "spawnmob");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("SPAWNMOB"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("SPAWNMOB_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/spawnmob <créature> [quantité]").onClick(TextActions.suggestCommand("/spawnmob "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(UtilsEntity type : UtilsEntity.values()){
				suggests.add(type.getName());
			}
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
					resultat = commandSpawnMob((EPlayer) source, optEntity.get(), 1);
				} else {
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("SPAWNMOB_ERROR_MOB")));
				}
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else if(args.size() == 2){
			if(source instanceof EPlayer) {
				Optional<UtilsEntity> optEntity = UtilsEntity.get(args.get(0));
				if (optEntity.isPresent()){
					try {
						int amount = Integer.valueOf(args.get(1));
						resultat = commandSpawnMob((EPlayer) source, optEntity.get(), amount);
					} catch (Exception e){
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("IS_NOT_NUMBER")
								.replaceAll("<number>", args.get(1))));
					}
				} else {
					source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("SPAWNMOB_ERROR_MOB")));
				}
			// La source n'est pas un joueur
			} else {
				source.sendMessage(this.plugin.getEverAPI().getMessages().getText("COMMAND_ERROR_FOR_PLAYER"));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean commandSpawnMob(final EPlayer player, UtilsEntity utilsEntity, int amount) {
		Optional<Vector3i> optBlock = player.getViewBlock();
		if(optBlock.isPresent()) {
			Location<World> spawnLocation = player.getWorld().getLocation(optBlock.get().add(0, 1, 0));
			for (int cpt = 0; cpt < amount; cpt++){
				utilsEntity.spawnEntity(spawnLocation);
	    	}
		} else {
			player.sendMessage(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NO_LOOK_BLOCK"));
		}
		return false;
	}
}
