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

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntity;

public class EESpawnMob extends EReloadCommand<EverEssentials> {
	
	private int limit;
	
	public EESpawnMob(final EverEssentials plugin) {
        super(plugin, "spawnmob");
        reload();
    }
	
	@Override
	public void reload() {
		this.limit = this.plugin.getConfigs().get("spawnmob.limit").getInt();
	}

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPAWNMOB.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SPAWNMOB_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/spawnmob <" + EAMessages.ARGS_ENTITY + "> [quantité]").onClick(TextActions.suggestCommand("/spawnmob "))
				.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(UtilsEntity type : UtilsEntity.values()){
				suggests.add(type.getName());
			}
		} else if(args.size() == 2){
			suggests.add("1");
			suggests.add(String.valueOf(this.limit));
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
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPAWNMOB_ERROR_MOB.get()));
				}
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 2){
			if(source instanceof EPlayer) {
				Optional<UtilsEntity> optEntity = UtilsEntity.get(args.get(0));
				if (optEntity.isPresent()){
					try {
						int amount = Math.min(Integer.parseInt(args.get(1)), this.limit);
						resultat = commandSpawnMob((EPlayer) source, optEntity.get(), amount);						
					} catch (NumberFormatException e){
						source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
								.replaceAll("<number>", args.get(1))));
					}
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.SPAWNMOB_ERROR_MOB.get()));
				}
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
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
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SPAWNMOB_MOB.get()
					.replaceAll("<nb>", String.valueOf(amount))
					.replaceAll("<entity>", StringUtils.capitalize(utilsEntity.getName())));
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.PLAYER_NO_LOOK_BLOCK.get());
		}
		return false;
	}
}
