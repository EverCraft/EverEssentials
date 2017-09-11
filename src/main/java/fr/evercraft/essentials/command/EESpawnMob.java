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
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.entity.EntityTemplate;

public class EESpawnMob extends ECommand<EverEssentials> {
	
	private int limit;
	
	public EESpawnMob(final EverEssentials plugin) {
        super(plugin, "spawnmob");
        this.reload();
    }
	
	@Override
	public void reload() {
		super.reload();
		
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
		if (args.size() == 1) {
			List<String> suggests = new ArrayList<String>();
			this.plugin.getGame().getRegistry().getAllForMinecraft(EntityType.class).stream()
				.filter(entity -> !entity.equals(EntityTypes.UNKNOWN) && 
						(Creature.class.isAssignableFrom(entity.getEntityClass()) || Hostile.class.isAssignableFrom(entity.getEntityClass())))
				.forEach(entity -> suggests.add(entity.getId().replaceAll("minecraft:", "")));
			this.plugin.getGame().getRegistry().getAllOf(EntityType.class).stream()
				.filter(entity -> !entity.equals(EntityTypes.UNKNOWN) && 
						(Creature.class.isAssignableFrom(entity.getEntityClass()) || Hostile.class.isAssignableFrom(entity.getEntityClass())))
				.forEach(entity -> suggests.add(entity.getId()));
			this.plugin.getEverAPI().getManagerService().getEntity().getAll()
					.forEach(entity -> suggests.add(entity.getId()));
			
			return suggests;
		} else if (args.size() == 2){
			Arrays.asList("1", String.valueOf(this.limit));
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandSpawnMob((EPlayer) source, args.get(0), 1);
			// La source n'est pas un joueur
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 2) {
			
			if (source instanceof EPlayer) {
				try {
					int amount = Math.max(Math.min(Integer.parseInt(args.get(1)), this.limit), 1);
					return this.commandSpawnMob((EPlayer) source, args.get(0), amount);						
				} catch (NumberFormatException e) {
					EAMessages.IS_NOT_NUMBER.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{number}", args.get(1))
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
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSpawnMob(final EPlayer player, String entityString, int amount) {
		Optional<Vector3i> block = player.getViewBlock();
		// Aucun block
		if (!block.isPresent()) {
			EAMessages.PLAYER_NO_LOOK_BLOCK.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		Vector3d location = block.get().toDouble().add(0.5, 1, 0.5);
		
		if (!entityString.contains(":")) {
			entityString = "minecraft:" + entityString;
		}
		
		// EntityService
		Optional<EntityTemplate> format = this.plugin.getEverAPI().getManagerService().getEntity().getForAll(entityString);
		if (format.isPresent()) {
			return this.commandSpawnMob(player, format.get(), amount, location);
		}
		
		// Erreur
		EEMessages.SPAWNMOB_ERROR_MOB.sender()
			.replace("{entity}", entityString)
			.sendTo(player);
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandSpawnMob(final EPlayer player, EntityTemplate format, int amount, Vector3d location) {
		for(int cpt=0; cpt < amount; cpt++) {
			Entity entity = player.getWorld().createEntityNaturally(format.getType(), location);
			format.apply(entity, player.get());
			
			player.getWorld().spawnEntity(
					entity,
					Cause.source(this.plugin)
						.owner(player.get())
						.notifier(player.get())
						.build());
		}
		
		EEMessages.SPAWNMOB_MOB.sender()
			.replace("{amount}", String.valueOf(amount))
			.replace("{entity}", StringUtils.capitalize(format.getName()))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
}
