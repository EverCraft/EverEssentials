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

public class EEExp extends ECommand<EverEssentials> {
	
	public EEExp(final EverEssentials plugin) {
        super(plugin, "xp", "exp");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EXP.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.EXP_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		Builder build = Text.builder("/" + this.getName() + " <")
							.append(Text.builder("give")
										.onClick(TextActions.suggestCommand("/xp give "))
										.build())
							.append(Text.of("|"))
							.append(Text.builder("set")
										.onClick(TextActions.suggestCommand("/xp set "))
										.build())
							.append(Text.of("> <lvl|exp> <" + EAMessages.ARGS_AMOUNT.get() + ">"));
		if (source.hasPermission(EEPermissions.EXP_OTHERS.get())) {
			build = build.append(Text.of(" [" + EAMessages.ARGS_PLAYER.get() + "]"));
		}
		return build.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
							.color(TextColors.RED)
							.build();
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1) {
			suggests.add("give");
			suggests.add("set");
		} else if (args.size() == 2) {
			suggests.add("lvl");
			suggests.add("exp");
		} else if (args.size() == 3) {
			suggests.add("1");
		} else if (args.size() == 4 && source.hasPermission(EEPermissions.EXP_OTHERS.get())) {
			suggests = null;
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 3) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				if (args.get(0).equals("give")){
					if (args.get(1).equals("lvl")){
						resultat = commandGiveLevel((EPlayer) source, args.get(2));
					} else if (args.get(1).equals("exp")){
						resultat = commandGiveExp((EPlayer) source, args.get(2));
					} else {
						source.sendMessage(this.help(source));
					}
				} else if (args.get(0).equals("set")){
					if (args.get(1).equals("lvl")){
						resultat = commandSetLevel((EPlayer) source, args.get(2));
					} else if (args.get(1).equals("exp")){
						resultat = commandSetExp((EPlayer) source, args.get(2));
					} else {
						source.sendMessage(this.help(source));
					}
				} else {
					source.sendMessage(this.help(source));
				}
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		} else if (args.size() == 4) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.EXP_OTHERS.get())){
				// Si la source est bien un joueur
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(3));
				// Le joueur existe
				if (optPlayer.isPresent()){
					EPlayer player = optPlayer.get();
					if (!player.equals(source)){
						if (args.get(0).equals("give")){
							if (args.get(1).equals("lvl")){
								resultat = this.commandOthersGiveLevel(source, player, args.get(2));
							} else if (args.get(1).equals("exp")){
								resultat = this.commandOthersGiveExp(source, player, args.get(2));
							} else {
								source.sendMessage(this.help(source));
							}
						} else if (args.get(0).equals("set")){
							if (args.get(1).equals("lvl")){
								resultat = this.commandOthersSetLevel(source, player, args.get(2));
							} else if (args.get(1).equals("exp")){
								resultat = this.commandOthersSetExp(source, player, args.get(2));
							} else {
								source.sendMessage(this.help(source));
							}
						} else {
							source.sendMessage(this.help(source));
						}
					} else {
						args.remove(3);
						return this.execute(source, args);
					}
				// Le joueur est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return resultat;
	}

	private boolean commandGiveLevel(final EPlayer player, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			player.addLevel(level);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_GIVE_LEVEL.get()
					.replaceAll("<level>", level.toString()));
			return true;
		} catch (NumberFormatException e) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", level_string));
			return false;
		}
	}
	
	private boolean commandGiveExp(final EPlayer player, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			player.addTotalExperience(experience);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_GIVE_EXP.get()
					.replaceAll("<experience>", experience.toString()));
			return true;
		} catch (NumberFormatException e) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", experience_string));
			return false;
		}
	}

	private boolean commandSetLevel(final EPlayer player, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			player.setLevel(level);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_SET_LEVEL.get()
					.replaceAll("<level>", level.toString()));
			return true;
		} catch (NumberFormatException e) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", level_string));
			return false;
		}
	}
	
	private boolean commandSetExp(final EPlayer player, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			player.setTotalExperience(experience);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_SET_EXP.get()
					.replaceAll("<experience>", experience.toString()));
			return true;
		} catch (NumberFormatException e) {
			player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", experience_string));
			return false;
		}
	}
	
	private boolean commandOthersGiveLevel(final CommandSource staff, final EPlayer player, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			player.addLevel(level);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_PLAYER_GIVE_LEVEL.get()
					.replaceAll("<staff>", staff.getName())
					.replaceAll("<level>", level.toString()));
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_STAFF_GIVE_LEVEL.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<level>", level.toString())));
			return true;
		} catch (NumberFormatException e) {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", level_string)));
			return false;
		}
	}
	
	private boolean commandOthersGiveExp(final CommandSource staff, final EPlayer player, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			player.addTotalExperience(experience);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_PLAYER_GIVE_EXP.get()
					.replaceAll("<staff>", staff.getName())
					.replaceAll("<experience>", experience.toString()));
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_STAFF_GIVE_EXP.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<experience>", experience.toString())));
			return true;
		} catch (NumberFormatException e) {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", experience_string)));
			return false;
		}
	}

	private boolean commandOthersSetLevel(final CommandSource staff, final EPlayer player, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			player.setLevel(level);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_PLAYER_SET_LEVEL.get()
					.replaceAll("<staff>", staff.getName())
					.replaceAll("<level>", level.toString()));
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_STAFF_SET_LEVEL.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<level>", level.toString())));
			return true;
		} catch (NumberFormatException e) {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", level_string)));
			return false;
		}
	}
	
	private boolean commandOthersSetExp(final CommandSource staff, final EPlayer player, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			player.setTotalExperience(experience);
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_PLAYER_SET_EXP.get()
					.replaceAll("<staff>", staff.getName())
					.replaceAll("<experience>", experience.toString()));
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EEMessages.EXP_OTHERS_STAFF_SET_EXP.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<experience>", experience.toString())));
			return true;
		} catch (NumberFormatException e) {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
					.replaceAll("<number>", experience_string)));
			return false;
		}
	}
}