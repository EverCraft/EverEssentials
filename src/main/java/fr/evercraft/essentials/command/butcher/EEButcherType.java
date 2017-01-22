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
package fr.evercraft.essentials.command.butcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntityType;

public class EEButcherType extends ESubCommand<EverEssentials> {
	
	public EEButcherType(final EverEssentials plugin, final EEButcher command) {
        super(plugin, command, "type");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BUTCHER_TYPE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.BUTCHER_TYPE_DESCRIPTION.getText();
	}
	
	@Override
	public Text help(final CommandSource source) {
		Builder build = Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_ENTITY.getString() + "> <" + EAMessages.ARGS_RADIUS.getString());
		if (source.hasPermission(EEPermissions.BUTCHER_WORLD.get())) {
			build.append(Text.of("|" + EAMessages.ARGS_ALL.getString()));
		}
		return build.append(Text.of(">"))
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			for (EntityType entity : UtilsEntityType.ANIMALS){
				suggests.add(entity.getName());
			}
			for (EntityType entity : UtilsEntityType.MONSTERS){
				suggests.add(entity.getName());
			}
		} else if (args.size() == 2){
			if (source.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
				suggests.add("all");
			}
			suggests.add("100");
			suggests.add("250");
		}
		return suggests;
	}
	
	@Override
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (source instanceof EPlayer){
			EPlayer player = (EPlayer) source;
			if (args.size() == 2) {
				Optional<EntityType> optType = getEntityType(args.get(0));
				if (args.get(1).equals("all")) {
					if (optType.isPresent()) {
						// Si il a la permission
						if (player.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
							resultat = this.commandButcherType(player, optType.get());
						// Il n'a pas la permission
						} else {
							EAMessages.NO_PERMISSION.sender()
								.prefix(EEMessages.PREFIX)
								.sendTo(source);
						}
					}
				} else {
					try {
						int radius = Integer.parseInt(args.get(1));
						if (radius > 0  && radius <= this.plugin.getConfigs().getButcherMaxRadius()) {
							resultat = this.commandButcherType(player, optType.get(), radius);
						} else {
							EAMessages.NUMBER_INVALID.sender()
								.prefix(EEMessages.PREFIX)
								.sendTo(source);
						}
					} catch (NumberFormatException e) {
						EAMessages.IS_NOT_NUMBER.sender()
							.prefix(EEMessages.PREFIX)
							.replace("<number>", args.get(0))
							.sendTo(source);
					}
				}
			} else {
				source.sendMessage(this.help(source));
			}
		}
		
		return resultat;
	}

	private boolean commandButcherType(EPlayer player, EntityType type) {		
		Collection<Entity> list = player.getWorld().getEntities(entity -> entity.getType().equals(type));
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			EEMessages.BUTCHER_TYPE.sender()
				.replace("<count>", String.valueOf(list.size()))
				.replace("<entity>", getButtomEntity(type))
				.sendTo(player);
			return true;
		} else {
			EEMessages.BUTCHER_NOENTITY.sendTo(player);
			return false;
		}
	}
	
	private boolean commandButcherType(EPlayer player, EntityType type, int radius) {
		Collection<Entity> list = player.getWorld().getEntities(entity -> 
			entity.getType().equals(type) && entity.getLocation().getPosition().distance(player.getLocation().getPosition()) <= radius);
		
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			EEMessages.BUTCHER_TYPE_RADIUS.sender()
				.replace("<radius>", String.valueOf(radius))
				.replace("<count>", String.valueOf(list.size()))
				.replace("<entity>", getButtomEntity(type))
				.sendTo(player);
			return true;
		} else {
			EEMessages.BUTCHER_NOENTITY.sendTo(player);
			return false;
		}
	}
	
	public Text getButtomEntity(final EntityType type){
		return Text.builder(type.getTranslation())
				.color(EEMessages.BUTCHER_ENTITY_COLOR.getColor())
				.build();
	}
	
	public Optional<EntityType> getEntityType(String name){
		Optional<EntityType> type = UtilsEntityType.getMonsters(name);
		if (!type.isPresent()) {
			type = UtilsEntityType.getAnimals(name);
		}
		return type;
	}
}