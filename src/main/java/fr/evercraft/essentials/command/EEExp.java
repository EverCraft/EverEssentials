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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.server.user.EUser;

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
							.append(Text.of("} {lvl|exp} {" + EAMessages.ARGS_AMOUNT.getString() + "}"));
		if (source.hasPermission(EEPermissions.EXP_OTHERS.get())) {
			build = build.append(Text.of(" [" + EAMessages.ARGS_USER.getString() + "]"));
		}
		return build.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
							.color(TextColors.RED)
							.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return Arrays.asList("give", "set");
		} else if (args.size() == 2) {
			return Arrays.asList("lvl", "exp");
		} else if (args.size() == 3) {
			return Arrays.asList("1");
		} else if (args.size() == 4 && source.hasPermission(EEPermissions.EXP_OTHERS.get())) {
			return this.getAllUsers();
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 3) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				if (args.get(0).equals("give")){
					if (args.get(1).equals("lvl")){
						return this.commandGiveLevel((EPlayer) source, args.get(2));
					} else if (args.get(1).equals("exp")){
						return this.commandGiveExp((EPlayer) source, args.get(2));
					} else {
						source.sendMessage(this.help(source));
					}
				} else if (args.get(0).equals("set")){
					if (args.get(1).equals("lvl")){
						return this.commandSetLevel((EPlayer) source, args.get(2));
					} else if (args.get(1).equals("exp")){
						return this.commandSetExp((EPlayer) source, args.get(2));
					} else {
						source.sendMessage(this.help(source));
					}
				} else {
					source.sendMessage(this.help(source));
				}
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else if (args.size() == 4) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.EXP_OTHERS.get())){
				// Si la source est bien un joueur
				Optional<EUser> user = this.plugin.getEServer().getEUser(args.get(3));
				// Le joueur existe
				if (user.isPresent()){
					if (!user.get().getIdentifier().equalsIgnoreCase(source.getIdentifier())){
						if (args.get(0).equals("give")){
							if (args.get(1).equals("lvl")){
								return this.commandOthersGiveLevel(source, user.get(), args.get(2));
							} else if (args.get(1).equals("exp")){
								return this.commandOthersGiveExp(source, user.get(), args.get(2));
							} else {
								source.sendMessage(this.help(source));
							}
						} else if (args.get(0).equals("set")){
							if (args.get(1).equals("lvl")){
								return this.commandOthersSetLevel(source, user.get(), args.get(2));
							} else if (args.get(1).equals("exp")){
								return this.commandOthersSetExp(source, user.get(), args.get(2));
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
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(3))
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandGiveLevel(final EPlayer player, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			player.addLevel(level);
			
			EEMessages.EXP_GIVE_LEVEL.sender()
				.replace("{level}", level.toString())
				.sendTo(player);
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", level_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandGiveExp(final EPlayer player, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			player.addTotalExperience(experience);
			
			EEMessages.EXP_GIVE_EXP.sender()
				.replace("{experience}", experience.toString())
				.sendTo(player);
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", experience_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
	}

	private CompletableFuture<Boolean> commandSetLevel(final EPlayer player, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			player.setLevel(level);
			
			EEMessages.EXP_SET_LEVEL.sender()
				.replace("{level}", level.toString())
				.sendTo(player);
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", level_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandSetExp(final EPlayer player, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			player.setTotalExperience(experience);
			
			EEMessages.EXP_SET_EXP.sender()
				.replace("{experience}", experience.toString())
				.sendTo(player);
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", experience_string)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandOthersGiveLevel(final CommandSource staff, final EUser user, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			user.addLevel(level);
			
			EEMessages.EXP_OTHERS_STAFF_GIVE_LEVEL.sender()
				.replace("{player}", user.getName())
				.replace("{level}", level.toString())
				.sendTo(staff);
			
			if(user instanceof EPlayer) {
				EEMessages.EXP_OTHERS_PLAYER_GIVE_LEVEL.sender()
					.replace("{staff}", staff.getName())
					.replace("{level}", level.toString())
					.sendTo((EPlayer) user);
			}
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", level_string)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandOthersGiveExp(final CommandSource staff, final EUser user, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			user.addTotalExperience(experience);
			
			EEMessages.EXP_OTHERS_STAFF_GIVE_EXP.sender()
				.replace("{player}", user.getName())
				.replace("{experience}", experience.toString())
				.sendTo(staff);
			
			if(user instanceof EPlayer) {
				EEMessages.EXP_OTHERS_PLAYER_GIVE_EXP.sender()
					.replace("{staff}", staff.getName())
					.replace("{experience}", experience.toString())
					.sendTo((EPlayer) user);
			}
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", experience_string)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
	}

	private CompletableFuture<Boolean> commandOthersSetLevel(final CommandSource staff, final EUser user, final String level_string) {
		try {
			Integer level = Integer.parseInt(level_string);
			user.setLevel(level);
			
			EEMessages.EXP_OTHERS_STAFF_SET_LEVEL.sender()
				.replace("{player}", user.getName())
				.replace("{level}", level.toString())
				.sendTo(staff);
			
			if(user instanceof EPlayer) {
				EEMessages.EXP_OTHERS_PLAYER_SET_LEVEL.sender()
					.replace("{staff}", staff.getName())
					.replace("{level}", level.toString())
					.sendTo((EPlayer) user);
			}
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", level_string)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
	}
	
	private CompletableFuture<Boolean> commandOthersSetExp(final CommandSource staff, final EUser user, final String experience_string) {
		try {
			Integer experience = Integer.parseInt(experience_string);
			user.setTotalExperience(experience);
			
			EEMessages.EXP_OTHERS_STAFF_SET_EXP.sender()
				.replace("{player}", user.getName())
				.replace("{experience}", experience.toString())
				.sendTo(staff);
			
			if(user instanceof EPlayer) {
				EEMessages.EXP_OTHERS_PLAYER_SET_EXP.sender()
					.replace("{staff}", staff.getName())
					.replace("{experience}", experience.toString())
					.sendTo((EPlayer) user);
			}
			return CompletableFuture.completedFuture(true);
		} catch (NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.replace("{number}", experience_string)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
	}
}
