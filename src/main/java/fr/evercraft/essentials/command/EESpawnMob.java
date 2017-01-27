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
import java.util.Arrays;
import java.util.Collection;
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
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntity;

public class EESpawnMob extends EReloadCommand<EverEssentials> {
	
	private int limit;
	
	public EESpawnMob(final EverEssentials plugin) {
        super(plugin, "spawnmob");
        this.reload();
    }
	
	@Override
	public void reload() {
		this.limit = this.plugin.getConfigs().getSpawnMobLimit();
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SPAWNMOB.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.SPAWNMOB_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_ENTITY.getString() + "> [" + EAMessages.ARGS_AMOUNT.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			List<String> suggests = new ArrayList<String>();
			for (UtilsEntity type : UtilsEntity.values()){
				suggests.add(type.getName());
			}
			return suggests;
		} else if (args.size() == 2){
			Arrays.asList("1", String.valueOf(this.limit));
		}
		return Arrays.asList();
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				Optional<UtilsEntity> optEntity = UtilsEntity.get(args.get(0));
				if (optEntity.isPresent()){
					resultat = this.commandSpawnMob((EPlayer) source, optEntity.get(), 1);
				} else {
					EEMessages.SPAWNMOB_ERROR_MOB.sender()
						.replace("<entity>", args.get(0))
						.sendTo(source);
				}
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 2) {
			
			if (source instanceof EPlayer) {
				Optional<UtilsEntity> optEntity = UtilsEntity.get(args.get(0));
				if (optEntity.isPresent()) {
					try {
						int amount = Math.max(Math.min(Integer.parseInt(args.get(1)), this.limit), 1);
						resultat = this.commandSpawnMob((EPlayer) source, optEntity.get(), amount);						
					} catch (NumberFormatException e) {
						EAMessages.IS_NOT_NUMBER.sender()
							.prefix(EEMessages.PREFIX)
							.replace("<number>", args.get(1))
							.sendTo(source);
					}
				} else {
					EEMessages.SPAWNMOB_ERROR_MOB.sender()
						.replace("<entity>", args.get(0))
						.sendTo(source);
				}
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandSpawnMob(final EPlayer player, UtilsEntity entity, int amount) {
		Optional<Vector3i> block = player.getViewBlock();
		
		// Aucun block
		if (!block.isPresent()) {
			EAMessages.PLAYER_NO_LOOK_BLOCK.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return false;
		}
		
		Location<World> spawnLocation = player.getWorld().getLocation(block.get().add(0, 1, 0));
		entity.spawnEntity(spawnLocation, amount);
		
		EEMessages.SPAWNMOB_MOB.sender()
			.replace("<amount>", String.valueOf(amount))
			.replace("<entity>", StringUtils.capitalize(entity.getName()))
			.sendTo(player);
		return true;
	}
}
