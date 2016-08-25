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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsGameMode;

public class EEGameMode extends ECommand<EverEssentials> {
	
	public EEGameMode(final EverEssentials plugin) {
        super(plugin, "gamemode", "gm");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.GAMEMODE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.GAMEMODE_DESCRIPTION.getText();
	}
	
	@Override
	public Text help(final CommandSource source) {
		Builder build = Text.builder("/" + this.getName() + " <")
				.append(Text.builder("survival").onClick(TextActions.suggestCommand("/" + this.getName() + " survival ")).build())
				.append(Text.of("|"))
				.append(Text.builder("creative").onClick(TextActions.suggestCommand("/" + this.getName() + " creative ")).build())
				.append(Text.of("|"))
				.append(Text.builder("adventure").onClick(TextActions.suggestCommand("/" + this.getName() + " adventure ")).build())
				.append(Text.of("|"))
				.append(Text.builder("spectator").onClick(TextActions.suggestCommand("/" + this.getName() + " spectator ")).build())
				.append(Text.of("|"))
				.append(Text.builder("empty").onClick(TextActions.suggestCommand("/" + this.getName() + " empty ")).build())
				.append(Text.of(">"));
		
		if (source.hasPermission(EEPermissions.GAMEMODE_OTHERS.get())){
			build = build.append(Text.of(" [" + EAMessages.ARGS_PLAYER.get() + "]"));
		}
		
		return build.color(TextColors.RED)
					.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests.add("survival");
			suggests.add("creative");
			suggests.add("adventure");
			suggests.add("spectator");
			suggests.add("empty");
		} else if (args.size() == 2 && source.hasPermission(EEPermissions.GAMEMODE_OTHERS.get())){
			suggests.addAll(this.getAllUsers());
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si on connait que le gamemode
		if (args.size() == 1) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				resultat = this.commandGameMode((EPlayer) source, args.get(0));
				// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
			
			// Si on connait le gamemode et le joueur
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.GAMEMODE_OTHERS.get())) {
				//Optional<User> user = this.plugin.getEServer().getUser(args.get(1));
				Optional<EPlayer> user = this.plugin.getEServer().getEPlayer(args.get(1));
				// Le joueur existe
				if (user.isPresent()){
					resultat = this.commandGameModeOthers(source, user.get(), args.get(0));
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}
	
	private boolean commandGameMode(final EPlayer player, final String gamemode_name) {
		Optional<GameMode> optGamemode = UtilsGameMode.getGameMode(gamemode_name); 
		// Si gamemode est correct
		if (optGamemode.isPresent()) {
			GameMode gamemode = optGamemode.get();
			// Si le nouveau gamemode est différent à celui du joueur
			if (!gamemode.equals(player.getGameMode())) {
				player.setGameMode(gamemode);
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.GAMEMODE_PLAYER_CHANGE.get()
						.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode)));
				return true;
			// Gamemode identique à celui du joueur
			} else {
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.GAMEMODE_PLAYER_EQUAL.get()
						.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode)));
			}
		// Nom du gamemode inconnue
		} else {
			player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.GAMEMODE_ERROR_NAME.getText()));
		}
		return false;
	}
	
	private boolean commandGameModeOthers(final CommandSource staff, final User user, final String gamemode_name) throws CommandException {
		Optional<GameMode> optGamemode = UtilsGameMode.getGameMode(gamemode_name); 
		
		// La source et le joueur sont identique
		if (staff instanceof EPlayer && user.getIdentifier().equals(staff.getIdentifier())) {
			return this.commandGameMode((EPlayer) staff, gamemode_name);
		// La source et le joueur sont différent
		} else {
			// Si gamemode est correct
			if (optGamemode.isPresent()) {
				GameMode gamemode_after = optGamemode.get();
				GameMode gamemode_before = user.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL);
				// Si le nouveau gamemode est différent à celui du joueur
				if (!gamemode_after.equals(gamemode_before)) {
					user.offer(Keys.GAME_MODE, gamemode_after);
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GAMEMODE_OTHERS_STAFF_CHANGE.get()
							.replaceAll("<player>", user.getName())
							.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode_after))));
					
					Optional<Player> player = user.getPlayer();
					if (player.isPresent()) {
						player.get().sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GAMEMODE_OTHERS_PLAYER_CHANGE.get()
								.replaceAll("<staff>", staff.getName())
								.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode_after))));
					}
					return true;
				// Gamemode identique à celui du joueur
				} else {
					staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.GAMEMODE_OTHERS_EQUAL.get()
							.replaceAll("<gamemode>", this.plugin.getEverAPI().getManagerUtils().getGameMode().getName(gamemode_after))
							.replaceAll("<player>", user.getName())));
				}
			// Nom du gamemode inconnue
			} else {
				staff.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.GAMEMODE_ERROR_NAME.getText()));
			}
		}
		return false;
	}
}
