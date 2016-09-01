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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.EReloadCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEGenerate extends EReloadCommand<EverEssentials> {
	
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
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_WORLD.get() + ">")
				.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
				.color(TextColors.RED)
				.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if (this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
		} else if (args.size() == 2){
			suggests.add("confirmation");
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 0) {
			
			if (source instanceof EPlayer) {
				EPlayer player = (EPlayer) source;
				resultat = this.commandGenerate(player, player.getWorld());
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
			
		} else if (args.size() == 1) {
			
			Optional<World> world = this.plugin.getEServer().getEWorld(args.get(0));
			if (world.isPresent()) {
				resultat = this.commandGenerate(source, world.get());
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", args.get(0))));
			}
			
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			
			Optional<World> world = this.plugin.getEServer().getEWorld(args.get(0));
			if (world.isPresent()) {
				resultat = this.commandGenerateConfirmation(source, world.get());
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", args.get(0))));
			}
			
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandGenerate(final CommandSource source, final World world) {
		int chunk = (int) Math.round(Math.pow((world.getWorldBorder().getDiameter() / 16), 2)); 
		
		source.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
			.append(EEMessages.GENERATE_WARNING.get()
				.replaceAll("<world>", world.getName())
				.replaceAll("<chunk>", String.valueOf(chunk)))
				.replace("<confirmation>", this.getButtonConfirmation(world))
			.build());
		return true;
	}

	private boolean commandGenerateConfirmation(final CommandSource source, World world) {
		world.getWorldBorder()
			.newChunkPreGenerate(world)
			.logger(this.plugin.getLogger().getLogger())
			.owner(this.plugin)
			.tickPercentLimit(this.tickPercentLimit)
			.tickInterval(this.tickInterval)
			.chunksPerTick(this.chunksPerTick)
			.start();
		
		source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GENERATE_LAUNCH.get()
				.replaceAll("<world>", world.getName())));
		return true;
	}
	
	private Text getButtonConfirmation(final World world){
		return EEMessages.GENERATE_WARNING_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.GENERATE_WARNING_VALID_HOVER.get()
							.replaceAll("<world>", world.getName()))))
					.onClick(TextActions.runCommand("/generate \"" + world.getUniqueId() + "\" confirmation"))
					.build();
	}
}