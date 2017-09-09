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
package fr.evercraft.essentials.command.worldborder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;

public class EEWorldborderSet extends ESubCommand<EverEssentials> {
	
	public EEWorldborderSet(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "set");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WORLDBORDER_SET_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return Arrays.asList("100", "1000");
		} else if (args.size() == 2){
			List<String> suggests = new ArrayList<String>();
			suggests.add("30");
			suggests.add("60");
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getEverAPI().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
			return suggests;
		} else if (args.size() == 3){
			List<String> suggests = new ArrayList<String>();
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getEverAPI().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
			return suggests;
		}
		return Arrays.asList();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_BLOCK.getString() + "> [" + EAMessages.ARGS_SECONDS.getString() + "] [" 
				+ EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 1){
			if (source instanceof Locatable) {
				return this.commandWorldborderSet(source, ((Locatable) source).getWorld(), args.get(0));
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 2){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(1));
			if (world.isPresent()){
				return this.commandWorldborderSet(source, world.get(), args.get(0));
			} else {
				if (source instanceof Locatable) {
					return this.commandWorldborderSet(source, ((Locatable) source).getWorld(), args.get(0), args.get(1));
				} else {
					EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			}
		} else if (args.size() == 3){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(2));
			if (world.isPresent()){
				return this.commandWorldborderSet(source, world.get(), args.get(0), args.get(1));
			} else {
				EAMessages.WORLD_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{world}", args.get(2))
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandWorldborderSet(CommandSource source, World world, String diameter_string) {
		try {
			double diameter = Integer.parseInt(diameter_string);
			
			world.getWorldBorder().setDiameter(diameter);
			EEMessages.WORLDBORDER_SET_BORDER.sender()
				.replace("{world}", world.getName())
				.replace("{amount}", String.valueOf(diameter))
				.sendTo(source);
			
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", diameter_string)
				.sendTo(source);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandWorldborderSet(CommandSource source, World world, String diameter_string, String time_string) {
		try {
			double diameter = Integer.parseInt(diameter_string);
			try {
				double time = Integer.parseInt(time_string);
				
				world.getWorldBorder().setDiameter(world.getWorldBorder().getDiameter(), diameter, (long) (time * 1000));
				
				EEMessages message;
				if (world.getWorldBorder().getDiameter() > diameter){
					message = EEMessages.WORLDBORDER_SET_BORDER_DECREASE;
				} else {
					message = EEMessages.WORLDBORDER_SET_BORDER_INCREASE;
				}
				
				message.sender()
					.replace("{world}", world.getName())
					.replace("{amount}", String.valueOf(diameter))
					.replace("{time}", String.valueOf(time))
					.sendTo(source);
				
				return CompletableFuture.completedFuture(true);
			} catch (NumberFormatException e) {
				EAMessages.IS_NOT_NUMBER.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{number}", time_string)
					.sendTo(source);
			}
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", diameter_string)
				.sendTo(source);
		}
		return CompletableFuture.completedFuture(false);
	}
}
