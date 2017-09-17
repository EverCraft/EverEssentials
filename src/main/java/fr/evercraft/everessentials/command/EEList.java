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
package fr.evercraft.everessentials.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;

import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.java.UtilsMap;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everessentials.EEPermissions;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

public class EEList extends ECommand<EverEssentials> {
	
	private static final String GROUPS_DEFAULT = "*";
	private static final String HIDDEN = "hidden";

	public EEList(final EverEssentials plugin) {
        super(plugin, "list");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.LIST.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.LIST_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 0) {
			
			// Si la source est un joueur
			if (source instanceof EPlayer) {
				return this.commandList(source, this.plugin.getEServer().getOnlineEPlayers((EPlayer) source));
			// La source n'est pas un joueur
			} else {
				return this.commandList(source, this.plugin.getEServer().getOnlineEPlayers());
			}
			
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandList(final CommandSource staff, final Collection<EPlayer> players) throws CommandException {
		// La liste des groupes avec des joueurs
		Map<String, TreeMap<String, EPlayer>> groups = new HashMap<String, TreeMap<String, EPlayer>>();
		for (EPlayer player : players) {
			Optional<SubjectReference> group = player.getGroup();
			if (group.isPresent()) {
				if (!groups.containsKey(group.get().getSubjectIdentifier())) {
					groups.put(group.get().getSubjectIdentifier(), new TreeMap<String, EPlayer>());
				}
				groups.get(group.get().getSubjectIdentifier()).put(player.getName(), player);
			} else {
				if (!groups.containsKey(EEList.GROUPS_DEFAULT)) {
					groups.put(EEList.GROUPS_DEFAULT, new TreeMap<String, EPlayer>());
				}
				groups.get(EEList.GROUPS_DEFAULT).put(player.getName(), player);
			}
		}
		
		// Cache les groupes et redimensionne
		for (Entry<Object, ? extends ConfigurationNode> config : this.plugin.getConfigs().getConfigList().getChildrenMap().entrySet()) {
			if (config.getKey() instanceof String && groups.containsKey((String) config.getKey())) {
				// Groupe caché
				if (config.getValue().getString("").equalsIgnoreCase(EEList.HIDDEN)) {
					groups.remove((String) config.getKey());
				// Redimensionne
				} else {
					int size = config.getValue().getInt(-1);
					if (size == 0) {
						groups.remove((String) config.getKey());
					} else if (size > 0 && groups.containsKey((String) config.getKey())) {
						TreeMap<String, EPlayer> group_players = groups.get((String) config.getKey());
						if (group_players.size() > size) {
							groups.put((String) config.getKey(), UtilsMap.split(group_players, size));
						}
					}
				}
			}
		}
		
		// Liste des groupes à afficher
		TreeMap<String, TreeMap<String, EPlayer>> groups_format = new TreeMap<String, TreeMap<String, EPlayer>>();
		for (Entry<Object, ? extends ConfigurationNode> config : this.plugin.getConfigs().getConfigList().getChildrenMap().entrySet()) {
			if (config.getKey() instanceof String) {
				Object value = config.getValue().getValue();
				if (value instanceof String && !((String) value).equalsIgnoreCase(HIDDEN)) {
					for (String group : ((String) value).split(" ")) {
						if (groups.containsKey(group)) {
							if (groups_format.containsKey(group)) {
								groups_format.get(group).putAll(groups.get(group));;
							} else {
								groups_format.put(group, groups.get(group));
							}
							groups.remove(group);
						}
					}
				}
			}
		}
		
		// Ajoute les groupes restant
		for (Entry<String, TreeMap<String, EPlayer>> group : groups.entrySet()) {
			if (group.getKey() != GROUPS_DEFAULT) {
				groups_format.put(group.getKey(), group.getValue());
			}
		}
		
		Text style_separator = EEMessages.LIST_SEPARATOR.getText();
		
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		List<Text> group_texts = new ArrayList<Text>();
		for (Entry<String, TreeMap<String, EPlayer>> group : groups_format.entrySet()) {
			List<Text> player_texts = new ArrayList<Text>();
			for (EPlayer player : group.getValue().values()) {
				
				if (player.isAfk()) {
					replaces.put(Pattern.compile("\\{afk}"), EReplace.of(EEMessages.LIST_TAG_AFK));
				} else {
					replaces.put(Pattern.compile("\\{afk}"), EReplace.of(""));
				}
				
				if (player.isVanish()) {
					replaces.put(Pattern.compile("\\{vanish}"), EReplace.of(EEMessages.LIST_TAG_VANISH));
				} else {
					replaces.put(Pattern.compile("\\{vanish}"), EReplace.of(""));
				}
				
				replaces.putAll(player.getReplaces());
				player_texts.add(EEMessages.LIST_PLAYER.getFormat().toText(replaces));
			}
			group_texts.add(EEMessages.LIST_GROUP.getFormat().toText(
								"{group}", group.getKey(),
								"{players}", Text.joinWith(style_separator, player_texts)));
		}
		
		if (group_texts.isEmpty()) {
			group_texts.add(EEMessages.LIST_EMPTY.getText());
		}
		
		EEMessages title;
		Integer vanish = players.size() - this.plugin.getEServer().playerNotVanish();
		
		replaces.clear();
		replaces.putAll(this.plugin.getChat().getReplaceServer());
		
		if (vanish == 0) {
			title = EEMessages.LIST_TITLE;
		} else {
			title = EEMessages.LIST_TITLE_VANISH;
			replaces.put(Pattern.compile("\\{vanish}"), EReplace.of(vanish.toString()));
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				title.getFormat().toText(replaces).toBuilder()
					.onClick(TextActions.runCommand("/list"))
					.build(), 
				group_texts, staff);
		
		return CompletableFuture.completedFuture(true);
	}
}
