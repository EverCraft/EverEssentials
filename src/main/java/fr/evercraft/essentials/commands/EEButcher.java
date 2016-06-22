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
package fr.evercraft.essentials.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsEntityType;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEButcher extends ECommand<EverEssentials> {
	
	public EEButcher(final EverEssentials plugin) {
        super(plugin, "butcher", "killall");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BUTCHER.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.BUTCHER_DESCRIPTION.getText();
	}

	public Text help(CommandSource source) {
		Text help;
		if(	source.hasPermission(EEPermissions.BUTCHER_ANIMAL.get()) ||  
			source.hasPermission(EEPermissions.BUTCHER_MONSTER.get()) ||  
			source.hasPermission(EEPermissions.BUTCHER_ALL.get()) ||  
			source.hasPermission(EEPermissions.BUTCHER_TYPE.get())){
			Builder build = Text.builder("/butcher").onClick(TextActions.suggestCommand("/butcher"))
					.append(Text.of(" <"));
			if(source.hasPermission(EEPermissions.BUTCHER_ANIMAL.get())){
				build = build.append(Text.builder("animal").onClick(TextActions.suggestCommand("/butcher animal")).build());
				if(source.hasPermission(EEPermissions.BUTCHER_MONSTER.get()) ||
						source.hasPermission(EEPermissions.BUTCHER_ALL.get()) ||
						source.hasPermission(EEPermissions.BUTCHER_TYPE.get())){
					build = build.append(Text.builder("|").build());
				}
			}
			if(source.hasPermission(EEPermissions.BUTCHER_MONSTER.get())){
				build = build.append(Text.builder("monster").onClick(TextActions.suggestCommand("/butcher monster")).build());
				if(source.hasPermission(EEPermissions.BUTCHER_ALL.get()) ||
						source.hasPermission(EEPermissions.BUTCHER_TYPE.get())){
					build = build.append(Text.builder("|").build());
				}
			}
			if(source.hasPermission(EEPermissions.BUTCHER_ALL.get())){
				build = build.append(Text.builder("all").onClick(TextActions.suggestCommand("/butcher all ")).build());
				if(source.hasPermission(EEPermissions.BUTCHER_TYPE.get())){
					build = build.append(Text.builder("|").build());
				}
			}
			if(source.hasPermission(EEPermissions.BUTCHER_TYPE.get())){
				build = build.append(Text.builder("type <créature>").onClick(TextActions.suggestCommand("/butcher type ")).build());
			}
			
			if(source.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
				build = build.append(Text.builder("> <rayon|all>").build());
			} else {
				build = build.append(Text.builder("> <rayon>").build());
			}
			help = build.color(TextColors.RED).build();
		} else {
			help = Text.builder("/butcher").onClick(TextActions.suggestCommand("/butcher"))
					.color(TextColors.RED).build();
		}
		return help;
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (source instanceof Player){
			if (args.size() == 1) {
				if (source.hasPermission(EEPermissions.BUTCHER_ALL.get())) {
					suggests.add("all");
				}
				if (source.hasPermission(EEPermissions.BUTCHER_ANIMAL.get())) {
					suggests.add("animal");
				}
				if (source.hasPermission(EEPermissions.BUTCHER_MONSTER.get())) {
					suggests.add("monster");
				}
				if (source.hasPermission(EEPermissions.BUTCHER_TYPE.get())) {
					suggests.add("type");
				}
			} else if (args.size() == 2) {
				if(args.get(0).equalsIgnoreCase("type") && source.hasPermission(EEPermissions.BUTCHER_TYPE.get())) {
					for(EntityType entity : UtilsEntityType.ANIMALS){
						suggests.add(entity.getName().toUpperCase());
					}
					for(EntityType entity : UtilsEntityType.MONSTERS){
						suggests.add(entity.getName().toUpperCase());
					}
				} else {
					suggests.add("1");
					if(source.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
						suggests.add("all");
					}
				}
			} else if (args.size() == 3 && args.get(0).equalsIgnoreCase("type") && source.hasPermission(EEPermissions.BUTCHER_TYPE.get())){
				suggests.add("1");
				if(source.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
					suggests.add("all");
				}
			}
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si la source est bien un joueur
		if(source instanceof EPlayer) {
			EPlayer player = (EPlayer) source;
			if (args.size() == 2) {
				if (args.get(0).equals("animal")){
					// Si il a la permission
					if(player.hasPermission(EEPermissions.BUTCHER_ANIMAL.get())){
						if (args.get(1).equals("all")){
							// Si il a la permission
							if(player.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
								resultat = commandButcherAnimal(player);
							// Il n'a pas la permission
							} else {
								player.sendMessage(EAMessages.NO_PERMISSION.getText());
							}
						} else {
							try {
								int radius = Integer.parseInt(args.get(1));
								if(radius > 0 && radius <= this.plugin.getConfigs().getButcherMaxRadius()) {
									resultat = commandButcherAnimal(player, radius);
								} else {
									player.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.NUMBER_INVALID.getText()));
								}
							} catch (NumberFormatException e) {
								player.sendMessage(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
										.replaceAll("<number>", args.get(1)));
							}
						}
					// Il n'a pas la permission
					} else {
						player.sendMessage(EAMessages.NO_PERMISSION.getText());
					}
				} else if (args.get(0).equals("monster")){
					// Si il a la permission
					if(player.hasPermission(EEPermissions.BUTCHER_MONSTER.get())){
						if (args.get(1).equals("all")){
							// Si il a la permission
							if(player.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
								resultat = commandButcherMonster(player);
							// Il n'a pas la permission
							} else {
								player.sendMessage(EAMessages.NO_PERMISSION.getText());
							}
						} else {
							try {
								int radius = Integer.parseInt(args.get(1));
								if(radius > 0  && radius <= this.plugin.getConfigs().getButcherMaxRadius()) {
									resultat = commandButcherMonster(player, radius);
								} else {
									player.sendMessage(EEMessages.PREFIX.get() + EAMessages.NUMBER_INVALID.get());
								}
							} catch (NumberFormatException e) {
								player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
										.replaceAll("<number>", args.get(1))));
							}
						}
					// Il n'a pas la permission
					} else {
						player.sendMessage(EAMessages.NO_PERMISSION.getText());
					}
				} else if (args.get(0).equals("all")){
					// Si il a la permission
					if(player.hasPermission(EEPermissions.BUTCHER_ALL.get())){
						if (args.get(1).equals("all")){
							// Si il a la permission
							if(player.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
								resultat = commandButcherAll(player);
							// Il n'a pas la permission
							} else {
								player.sendMessage(EAMessages.NO_PERMISSION.getText());
							}
						} else {
							try {
								int radius = Integer.parseInt(args.get(1));
								if(radius > 0  && radius <= this.plugin.getConfigs().getButcherMaxRadius()) {
									resultat = commandButcherAll(player, radius);
								} else {
									player.sendMessage(EEMessages.PREFIX.get() + EAMessages.NUMBER_INVALID.get());
								}
							} catch (NumberFormatException e) {
								player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
										.replaceAll("<number>", args.get(1))));
							}
						}
					// Il n'a pas la permission
					} else {
						player.sendMessage(EAMessages.NO_PERMISSION.getText());
					}
				}
			} else if (args.size() == 3 && args.get(0).equals("type")){
				// Si il a la permission
				if(player.hasPermission(EEPermissions.BUTCHER_TYPE.get())){
					Optional<EntityType> type = getEntityType(args.get(1));
					if(type.isPresent()) {
						if (args.get(2).equals("all")){
							// Si il a la permission
							if(player.hasPermission(EEPermissions.BUTCHER_WORLD.get())){
								resultat = commandButcherType(player, type.get());
							// Il n'a pas la permission
							} else {
								player.sendMessage(EAMessages.NO_PERMISSION.getText());
							}
						} else {
							try {
								int radius = Integer.parseInt(args.get(2));
								if(radius > 0  && radius <= this.plugin.getConfigs().getButcherMaxRadius()) {
									resultat = commandButcherType(player, type.get(), radius);
								} else {
									player.sendMessage(EEMessages.PREFIX.get() + EAMessages.NUMBER_INVALID.getText());
								}
							} catch (NumberFormatException e) {
								player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.IS_NOT_NUMBER.get()
										.replaceAll("<number>", args.get(2))));
							}
						}
					} else {
						player.sendMessage(EChat.of(EAMessages.IS_NOT_ENTITY_TYPE.get()
								.replaceAll("<entity>", args.get(1))));
					}
				// Il n'a pas la permission
				} else {
					player.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			} else {
				player.sendMessage(help(source));
			}
		} else {
			source.sendMessage(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText());
		}
		return resultat;
	}

	private boolean commandButcherAnimal(final EPlayer player) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(UtilsEntityType.ANIMALS.contains(entity.getType()) && !entity.get(Keys.ANGRY).orElse(false)) {
		    		return true;
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_ANIMAL.get()
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	private boolean commandButcherAnimal(final EPlayer player, final int radius) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(UtilsEntityType.ANIMALS.contains(entity.getType()) && !entity.get(Keys.ANGRY).orElse(false)) {
			    	if(entity.getLocation().getPosition().distance(player.getLocation().getPosition()) <= radius) {
			    		return true;
			    	}
		    	}
		    	return false;
		    }
		};		
		
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_ANIMAL_RADIUS.get()
					.replaceAll("<radius>", String.valueOf(radius))
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	private boolean commandButcherMonster(final EPlayer player) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(UtilsEntityType.MONSTERS.contains(entity.getType()) && entity.get(Keys.ANGRY).orElse(false)) {
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
		    	if(UtilsEntityType.MONSTERS.contains(entity.getType()) && entity.get(Keys.ANGRY).orElse(false)) {
		    		if(entity.getLocation().getPosition().distance(player.getLocation().getPosition()) <= radius) {
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
	
	private boolean commandButcherAll(final EPlayer player) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(UtilsEntityType.MONSTERS.contains(entity.getType()) || UtilsEntityType.ANIMALS.contains(entity.getType())) {
		    		return true;
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_ALL.get()
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	private boolean commandButcherAll(final EPlayer player, int radius) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(UtilsEntityType.MONSTERS.contains(entity.getType()) || UtilsEntityType.ANIMALS.contains(entity.getType())) {
		    		if(entity.getLocation().getPosition().distance(player.getLocation().getPosition()) <= radius) {
			    		return true;
			    	}
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_ALL_RADIUS.get()
					.replaceAll("<radius>", String.valueOf(radius))
					.replaceAll("<count>", String.valueOf(list.size())));
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	private boolean commandButcherType(EPlayer player, EntityType type) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(entity.getType().equals(type)) {
			    	return true;
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.BUTCHER_TYPE.get()
							.replaceAll("<count>", String.valueOf(list.size())))
					.replace("<entity>", getButtomEntity(type))
					.build());
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	private boolean commandButcherType(EPlayer player, EntityType type, int radius) {
		Predicate<Entity> predicate = new Predicate<Entity>() {
		    @Override
		    public boolean test(Entity entity) {
		    	if(entity.getType().equals(type)) {
		    		if(entity.getLocation().getPosition().distance(player.getLocation().getPosition()) <= radius) {
			    		return true;
			    	}
		    	}
		    	return false;
		    }
		};
		Collection<Entity> list = player.getWorld().getEntities(predicate);
		if (!list.isEmpty()){
			list.forEach(entity -> entity.remove());
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(EEMessages.BUTCHER_TYPE_RADIUS.get()
							.replaceAll("<radius>", String.valueOf(radius))
							.replaceAll("<count>", String.valueOf(list.size())))
					.replace("<entity>", getButtomEntity(type))
					.build());
			return true;
		} else {
			player.sendMessage(EEMessages.PREFIX.get() + EEMessages.BUTCHER_NOENTITY.get());
			return false;
		}
	}
	
	public Text getButtomEntity(final EntityType type){
		return Text.builder(type.getTranslation())
				.color(EChat.getTextColor(EEMessages.BUTCHER_ENTITY_COLOR.get()))
				.build();
	}
	
	public Optional<EntityType> getEntityType(String name){
		Optional<EntityType> type = UtilsEntityType.getMonsters(name);
		if(!type.isPresent()) {
			type = UtilsEntityType.getAnimals(name);
		}
		return type;
	}
}
