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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

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
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ESubCommand;

public class EEWorldborderInfo extends ESubCommand<EverEssentials> {
	
	public EEWorldborderInfo(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "info");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.WORLDBORDER_INFO_DESCRIPTION.getText();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
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
		return Text.builder("/" + this.getName() + " [" + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 0) {
			if (source instanceof Locatable) {
				return this.commandWorldborder(source, ((Locatable) source).getWorld());
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 1){
			Optional<World> world = this.plugin.getEServer().getWorld(args.get(0));
			if (world.isPresent()) {
				return this.commandWorldborder(source, world.get());
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
	
	private CompletableFuture<Boolean> commandWorldborder(final CommandSource source, final World world) {
		List<Text> lists = new ArrayList<Text>();
		
		lists.add(this.getLocation(world));
		lists.add(this.getBorder(world));
		lists.add(this.getDamageThreshold(world));
		lists.add(this.getDamageAmount(world));
		lists.add(this.getWarningDistance(world));
		lists.add(this.getWarningTime(world));
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.WORLDBORDER_INFO_TITLE.getFormat()
				.toText("{world}", world.getName()).toBuilder()
				.onClick(TextActions.runCommand("/" + this.getName()))
				.build(), lists, source);
		return CompletableFuture.completedFuture(true);
	}
	
	public Text getLocation(final World world){
		return EEMessages.WORLDBORDER_INFO_LOCATION.getFormat()
				.toText("{position}", this.getButtonLocation(world));
	}
	
	public Text getBorder(final World world){
		return EEMessages.WORLDBORDER_INFO_BORDER.getFormat()
				.toText("{amount}", UtilsDouble.getString(world.getWorldBorder().getDiameter()));
	}
	
	public Text getDamageThreshold(final World world){
		return EEMessages.WORLDBORDER_INFO_BUFFER.getFormat()
				.toText("{amount}", UtilsDouble.getString(world.getWorldBorder().getDamageThreshold()));
	}
	
	public Text getDamageAmount(final World world){
		return EEMessages.WORLDBORDER_INFO_DAMAGE.getFormat()
				.toText("{amount}", UtilsDouble.getString(world.getWorldBorder().getDamageAmount()));
	}
	
	public Text getWarningDistance(final World world){
		return EEMessages.WORLDBORDER_INFO_WARNING_DISTANCE.getFormat()
				.toText("{amount}", UtilsDouble.getString(world.getWorldBorder().getWarningDistance()));
	}
	
	public Text getWarningTime(final World world){
		return EEMessages.WORLDBORDER_INFO_WARNING_TIME.getFormat()
				.toText("{amount}", UtilsDouble.getString(world.getWorldBorder().getWarningTime()));
	}
	
	public Text getButtonLocation(final World world) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.put(Pattern.compile("\\{x}"), EReplace.of(String.valueOf(Math.floor(world.getWorldBorder().getCenter().getX()))));
		replaces.put(Pattern.compile("\\{y}"), EReplace.of(String.valueOf(Math.floor(world.getWorldBorder().getCenter().getY()))));
		replaces.put(Pattern.compile("\\{z}"), EReplace.of(String.valueOf(Math.floor(world.getWorldBorder().getCenter().getZ()))));
		replaces.put(Pattern.compile("\\{world}"), EReplace.of(world.getName()));
		
		return EEMessages.WORLDBORDER_INFO_LOCATION_POSITION.getFormat().toText(replaces).toBuilder()
				.onHover(TextActions.showText(EEMessages.WORLDBORDER_INFO_LOCATION_POSITION_HOVER.getFormat().toText(replaces)))
				.build();
	}
}
