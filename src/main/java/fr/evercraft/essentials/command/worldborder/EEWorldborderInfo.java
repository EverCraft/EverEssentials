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
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEWorldborderInfo extends ESubCommand<EverEssentials> {
	public EEWorldborderInfo(final EverEssentials plugin, final EEWorldborder command) {
        super(plugin, command, "info");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.WORLDBORDER.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.WORLDBORDER_INFO_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (World world : this.plugin.getEServer().getWorlds()) {
				if(this.plugin.getManagerServices().getEssentials().hasPermissionWorld(source, world)) {
					suggests.add(world.getProperties().getWorldName());
				}
			}
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 0) {
			if(source instanceof EPlayer) {
				resultat = commandWorldborder(source, ((EPlayer) source).getWorld());
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		} else if(args.size() == 1){
			resultat = commandWorldborder(source, args.get(0));
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}
	
	private boolean commandWorldborder(final CommandSource source, final String world_name) {
		Optional<World> optWorld = this.plugin.getEServer().getWorld(world_name);
		if(optWorld.isPresent()) {
			this.commandWorldborder(source, optWorld.get());
		} else {
			source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
				.replaceAll("<world>", world_name)));
			return false;
		}
		return true;
	}
	
	private boolean commandWorldborder(final CommandSource source, final World world) {
		List<Text> lists = new ArrayList<Text>();
		lists.add(getLocation(world));
		lists.add(getBorder(world));
		lists.add(getDamageThreshold(world));
		lists.add(getDamageAmount(world));
		lists.add(getWarningDistance(world));
		lists.add(getWarningTime(world));
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(EEMessages.WORLDBORDER_INFO_TITLE.get()
				.replaceAll("<world>", world.getName()))
					.toBuilder()
				.onClick(TextActions.runCommand("/" + this.getName()))
					.build(), lists, source);
		return true;
	}
	
	public Text getLocation(final World world){
		return ETextBuilder.toBuilder(EEMessages.WORLDBORDER_INFO_LOCATION.get())
				.replace("<position>", getButtonLocation(world))
				.build();
	}
	
	public Text getBorder(final World world){
		return ETextBuilder.toBuilder(EEMessages.WORLDBORDER_INFO_BORDER.get())
				.replace("<nb>", UtilsDouble.getString(world.getWorldBorder().getDiameter()))
				.build();
	}
	
	public Text getDamageThreshold(final World world){
		return ETextBuilder.toBuilder(EEMessages.WORLDBORDER_INFO_BUFFER.get())
				.replace("<nb>", UtilsDouble.getString(world.getWorldBorder().getDamageThreshold()))
				.build();
	}
	
	public Text getDamageAmount(final World world){
		return ETextBuilder.toBuilder(EEMessages.WORLDBORDER_INFO_DAMAGE.get())
				.replace("<nb>", UtilsDouble.getString(world.getWorldBorder().getDamageAmount()))
				.build();
	}
	
	public Text getWarningDistance(final World world){
		return ETextBuilder.toBuilder(EEMessages.WORLDBORDER_INFO_WARNING_DISTANCE.get())
				.replace("<nb>", UtilsDouble.getString(world.getWorldBorder().getWarningDistance()))
				.build();
	}
	
	public Text getWarningTime(final World world){
		return ETextBuilder.toBuilder(EEMessages.WORLDBORDER_INFO_WARNING_TIME.get())
				.replace("<nb>", UtilsDouble.getString(world.getWorldBorder().getWarningTime()))
				.build();
	}
	
	public Text getButtonLocation(final World world){
		return EChat.of(EEMessages.WORLDBORDER_INFO_LOCATION_POSITION.get()
					.replaceAll("<x>", String.valueOf(Math.floor(world.getWorldBorder().getCenter().getX())))
					.replaceAll("<z>", String.valueOf(Math.floor(world.getWorldBorder().getCenter().getZ())))
					.replaceAll("<world>", world.getName())).toBuilder()
				.onHover(TextActions.showText(EChat.of(EEMessages.WORLDBORDER_INFO_LOCATION_POSITION_HOVER.get()
						.replaceAll("<x>", String.valueOf(Math.floor(world.getWorldBorder().getCenter().getX())))
						.replaceAll("<z>", String.valueOf(Math.floor(world.getWorldBorder().getCenter().getZ())))
						.replaceAll("<world>", world.getName()))))
				.build();
	}
}
