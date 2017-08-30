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

public class EEWorldborderDamage extends ESubCommand<EverEssentials> {
	
	public EEWorldborderDamage(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "damage");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WORLDBORDER_DAMAGE_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return Arrays.asList("buffer", "amount");
		} else if (args.size() == 2){
			return Arrays.asList("1", "5", "10");
		} else if (args.size() == 3){
			List<String> suggests = new ArrayList<String>();
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
			return suggests;
		}
		return Arrays.asList();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <buffer|amount> <" + EAMessages.ARGS_VALUE.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpAmount(final CommandSource source) {
		return Text.builder("/" + this.getName() + " amount <" + EAMessages.ARGS_DAMAGE.getString() + "> [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " amount "))
					.color(TextColors.RED)
					.build();
	}
	
	public Text helpBuffer(final CommandSource source) {
		return Text.builder("/" + this.getName() + " buffer <" + EAMessages.ARGS_BLOCK.getString() + "> [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " buffer "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 1){
			if (args.get(0).equalsIgnoreCase("amount")){
				source.sendMessage(this.helpAmount(source));
			} else if (args.get(0).equalsIgnoreCase("buffer")){
				source.sendMessage(this.helpBuffer(source));
			} else {
				source.sendMessage(this.help(source));
			}
		} else if (args.size() == 2){
			if (source instanceof Locatable) {
				if (args.get(0).equalsIgnoreCase("amount")){
					return this.commandWorldborderDamageAmount(source, ((Locatable) source).getWorld(), args.get(0));
				} else if (args.get(0).equalsIgnoreCase("buffer")){
					return this.commandWorldborderDamageBuffer(source, ((Locatable) source).getWorld(), args.get(0));
				} else {
					source.sendMessage(this.help(source));
				}
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 3){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(2));
			if (world.isPresent()){
				if (args.get(0).equalsIgnoreCase("amount")){
					return this.commandWorldborderDamageAmount(source, world.get(), args.get(0));
				} else if (args.get(0).equalsIgnoreCase("buffer")){
					return this.commandWorldborderDamageBuffer(source, world.get(), args.get(0));
				} else {
					source.sendMessage(this.help(source));
				}
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

	private CompletableFuture<Boolean> commandWorldborderDamageAmount(CommandSource source, World world, String value_string) {
		try {
			double value = Integer.parseInt(value_string);
			
			world.getWorldBorder().setDamageAmount(value);
			EEMessages.WORLDBORDER_DAMAGE_AMOUNT.sender()
				.replace("{amount}", String.valueOf(value))
				.replace("{world}", world.getName())
				.sendTo(source);
			return CompletableFuture.completedFuture(true);
			
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", value_string)
				.sendTo(source);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandWorldborderDamageBuffer(CommandSource source, World world, String value_string) {
		try {
			double value = Integer.parseInt(value_string);

			world.getWorldBorder().setDamageThreshold(value);
			EEMessages.WORLDBORDER_DAMAGE_BUFFER.sender()
				.replace("{amount}", String.valueOf(value))
				.replace("{world}", world.getName())
				.sendTo(source);
			return CompletableFuture.completedFuture(true);
			
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", value_string)
				.sendTo(source);
			return CompletableFuture.completedFuture(false);
		}
	}
}
