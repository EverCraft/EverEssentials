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
import java.util.function.Predicate;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntityType;

public class EEButcherMonster extends ESubCommand<EverEssentials> {
	public EEButcherMonster(final EverEssentials plugin, final EEButcher command) {
        super(plugin, command, "monster");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BUTCHER_MONSTER.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(EEMessages.BUTCHER_MONSTER_DESCRIPTION.get());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			if (source.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
				suggests.add("all");
			}
			suggests.add("100");
			suggests.add("250");
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_RADIUS.get() + "|" + EAMessages.ARGS_ALL.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		if (source instanceof EPlayer){
			EPlayer player = (EPlayer) source;
			if (args.size() == 1) {
				if (args.get(0).equals("all")){
					// Si il a la permission
					if (player.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
						resultat = commandButcherMonster(player);
					// Il n'a pas la permission
					} else {
						player.sendMessage(EAMessages.NO_PERMISSION.getText());
					}
				} else {
					try {
						int radius = Integer.parseInt(args.get(0));
						if (radius > 0  && radius <= this.plugin.getConfigs().getButcherMaxRadius()) {
							resultat = commandButcherMonster(player, radius);
						} else {
							player.sendMessage(EEMessages.PREFIX.get() + EAMessages.NUMBER_INVALID.getText());
						}
					} catch (NumberFormatException e) {
						player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
								.replaceAll("<number>", args.get(0)));
					}
				}
			} else {
				source.sendMessage(this.help(source));
			}
		}
		return resultat;
	}

	private boolean commandButcherMonster(final EPlayer player) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if (UtilsEntityType.MONSTERS.contains(entity.getType()) && entity.get(Keys.ANGRY).orElse(true)) {
		    		return true;
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_MONSTER.get()
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	private boolean commandButcherMonster(final EPlayer player, final int radius) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if (UtilsEntityType.MONSTERS.contains(entity.getType()) && entity.get(Keys.ANGRY).orElse(true)) {
		    		if (entity.getLocation().getPosition().distance(player.getLocation().getPosition()) <= radius) {
			    		return true;
			    	}
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_MONSTER_RADIUS.get()
					.replaceAll("<radius>", String.valueOf(radius))
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
}