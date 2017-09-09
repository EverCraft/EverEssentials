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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.plugin.command.ReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEGenerate extends ECommand<EverEssentials> implements ReloadCommand {
	
	private float tickPercentLimit;
	private int tickInterval;
	private int chunksPerTick;
	
	public EEGenerate(final EverEssentials plugin) {
        super(plugin, "generate");
        this.reload();
    }
	
	@Override
	public void reload() {
		this.tickPercentLimit =  this.plugin.getConfigs().getGenerateTickPercentLimit();
		this.tickInterval =  this.plugin.getConfigs().getGenerateTickInterval();
		this.chunksPerTick =  this.plugin.getConfigs().getGenerateChuncksPerTick();
	}

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.GENERATE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.GENERATE_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_WORLD.getString() + ">")
				.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			List<String> suggests = new ArrayList<String>();
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getEverAPI().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
			return suggests;
		} else if (args.size() == 2){
			return Arrays.asList("confirmation");
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 0) {
			
			if (source instanceof EPlayer) {
				EPlayer player = (EPlayer) source;
				return this.commandGenerate(player, player.getWorld());
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
			
		} else if (args.size() == 1) {
			
			Optional<World> world = this.plugin.getEServer().getEWorld(args.get(0));
			if (world.isPresent()) {
				return this.commandGenerate(source, world.get());
			} else {
				EAMessages.WORLD_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{world}", args.get(0))
					.sendTo(source);
			}
			
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			
			Optional<World> world = this.plugin.getEServer().getEWorld(args.get(0));
			if (world.isPresent()) {
				return this.commandGenerateConfirmation(source, world.get());
			} else {
				EAMessages.WORLD_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{world}", args.get(0))
					.sendTo(source);
			}
			
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandGenerate(final CommandSource player, final World world) {
		int chunk = (int) Math.round(Math.pow((world.getWorldBorder().getDiameter() / 16), 2)); 
		
		EEMessages.GENERATE_WARNING.sender()
			.replace("{world}", world.getName())
			.replace("{chunk}", String.valueOf(chunk))
			.replace("{confirmation}", this.getButtonConfirmation(world))
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}

	private CompletableFuture<Boolean> commandGenerateConfirmation(final CommandSource player, World world) {
		world.getWorldBorder()
			.newChunkPreGenerate(world)
			.logger(this.plugin.getELogger().getLogger())
			.owner(this.plugin)
			.tickPercentLimit(this.tickPercentLimit)
			.tickInterval(this.tickInterval)
			.chunksPerTick(this.chunksPerTick)
			.start();
		
		EEMessages.GENERATE_LAUNCH.sender()
			.replace("{world}", world.getName())
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
	
	private Text getButtonConfirmation(final World world){
		return EEMessages.GENERATE_WARNING_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.GENERATE_WARNING_VALID_HOVER.getFormat()
							.toText("{world}", world.getName())))
					.onClick(TextActions.runCommand("/generate \"" + world.getUniqueId() + "\" confirmation"))
					.build();
	}
}
