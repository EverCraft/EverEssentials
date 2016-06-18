/**
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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.java.UtilsMap;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEHomeSet extends ECommand<EverEssentials> {
	
	private final static String DEFAULT_HOME = "home";
	
	private List<Entry<String, Integer>> permissions;
	private int permission_default;
	
	public EEHomeSet(final EverEssentials plugin) {
        super(plugin, "sethome", "setresidence");
        reload();
    }
	
	public void reload(){
		Map<String, Integer> permissions = new HashMap<String, Integer>();
		this.permission_default = this.plugin.getConfigs().get("sethome-multiple.default").getInt(1);
		for (Entry<Object, ? extends ConfigurationNode> key : this.plugin.getConfigs().get("sethome-multiple").getChildrenMap().entrySet()) {
			if(key.getKey() instanceof String) {
				permissions.put((String) key.getKey(), key.getValue().getInt(this.permission_default));
			}
		}
		this.permissions = UtilsMap.valueDESC(permissions);
	}
	
	public int getMaxHome(final EPlayer player) {
		int max = this.permission_default;
		int cpt = 0;
		while (cpt < this.permissions.size() && max == this.permission_default) {
			if (player.hasPermission(EEPermissions.SETHOME_MULTIPLE.get() + "." + this.permissions.get(cpt).getKey())) {
				max = this.permissions.get(cpt).getValue();
			}
			cpt++;
		}
		return max;
	}
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.SETHOME.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.SETHOME_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		Text help;
		if(source.hasPermission(EEPermissions.SETHOME_MULTIPLE.get())) {
			help = Text.builder("/sethome <name>").onClick(TextActions.suggestCommand("/sethome "))
					.color(TextColors.RED).build();
		} else {
			help = Text.builder("/sethome").onClick(TextActions.suggestCommand("/sethome"))
					.color(TextColors.RED).build();
		}
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if(args.size() == 0 || 
				(!source.hasPermission(EEPermissions.SETHOME_MULTIPLE.get()) && 
				!source.hasPermission(EEPermissions.SETHOME_MULTIPLE_UNLIMITED.get()))) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandSetHome((EPlayer) source);
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Si on ne connait pas le joueur
		} else if(args.size() == 1) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = commandSetHome((EPlayer) source, args.get(0)); 
				
			// La source n'est pas un joueur
			} else {
				source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(getHelp(source).get());
		}
		return resultat;
	}
	
	public boolean commandSetHome(final EPlayer player) {
		int max = getMaxHome(player);
		int homes = player.getHomes().size();
		// Si le joueur à un home qui porte déjà ce nom ou il peut encore avoir un home supplémentaire
		if(player.hasHome(DEFAULT_HOME) || homes == 0 || homes < max) {
			// Ajout d'un home
			if(player.addHome(DEFAULT_HOME)) {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SETHOME_SET.get());
				return true;
			// Impossible d'ajouter un home
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get());
			}
		// Il a déjà le nombre maximum d'home
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SETHOME_MULTIPLE_ERROR_MAX.get()
					.replaceAll("<nombre>", String.valueOf(getMaxHome(player))));
		}
		return false;
	}
	
	public boolean commandSetHome(final EPlayer player, final String home_name) {
		String name = EChat.fixLength(home_name, this.plugin.getEverAPI().getConfigs().get("maxCaractere").getInt(16));
		// Si il a la permission multihome
		if(player.hasPermission(EEPermissions.SETHOME_MULTIPLE.get())){
			int max = getMaxHome(player);
			// Si le joueur à la permissions un unlimited ou un home qui porte déjà ce nom ou il peut encore avoir un home supplémentaire
			if(player.hasPermission(EEPermissions.SETHOME_MULTIPLE_UNLIMITED.get()) || 
					player.hasHome(name) || 
					player.getHomes().size() < max) {
				// Ajout d'un home
				if(player.addHome(name)) {
					player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(EEMessages.SETHOME_MULTIPLE_SET.get())
							.replace("<home>", getButtonHome(name, player.getLocation()))
							.build());
					return true;
					// Impossible d'ajouter un home
				} else {
					player.sendMessage(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get());
				}
			// Il a déjà le nombre maximum d'home
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SETHOME_MULTIPLE_ERROR_MAX.get()
						.replaceAll("<nombre>", String.valueOf(max)));
			}
		// Il n'a pas la permission multiworld
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.SETHOME_MULTIPLE_NO_PERMISSION.get());
		}
		return false;
	}

	public Text getButtonHome(final String name, final Location<World> location){
		return EChat.of(EEMessages.HOME_NAME.get().replaceAll("<name>", name)).toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.HOME_NAME_HOVER.get()
							.replaceAll("<home>", name)
							.replaceAll("<world>", location.getExtent().getName())
							.replaceAll("<x>", String.valueOf(location.getBlockX()))
							.replaceAll("<y>", String.valueOf(location.getBlockY()))
							.replaceAll("<z>", String.valueOf(location.getBlockZ())))))
					.build();
	}
}
