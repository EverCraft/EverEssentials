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
package fr.evercraft.everessentials.command.worldborder;

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

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEWorldborderCenter extends ESubCommand<EverEssentials> {
	
	public EEWorldborderCenter(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "center");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WORLDBORDER_CENTER_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return Arrays.asList("0", "100");
		} else if (args.size() == 2){
			return Arrays.asList("0", "100");
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
		return Text.builder("/" + this.getName() + " <x> <z> [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 2){
			if (source instanceof Locatable){
				return this.commandWorldborderCenter(source, ((Locatable) source).getWorld(), args.get(0), args.get(1));
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 3){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(2));
			if (world.isPresent()){
				return this.commandWorldborderCenter(source, world.get(), args.get(0), args.get(1));
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

	private CompletableFuture<Boolean> commandWorldborderCenter(CommandSource source, World world, String x_string, String y_string) {
		try {
			int x = Integer.parseInt(x_string);
			try {
				int z = Integer.parseInt(y_string);
				
				world.getWorldBorder().setCenter(x, z);
				EEMessages.WORLDBORDER_CENTER_MESSAGE.sender()
					.replace("{world}", world.getName())
					.replace("{x}", String.valueOf(x))
					.replace("{z}", String.valueOf(z))
					.sendTo(source);
				return CompletableFuture.completedFuture(true);
				
			} catch (NumberFormatException e) {
				EAMessages.IS_NOT_NUMBER.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{number}", y_string)
					.sendTo(source);
			}
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", x_string)
				.sendTo(source);
		}
		return CompletableFuture.completedFuture(false);
	}
}
