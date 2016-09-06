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
package fr.evercraft.essentials.listeners;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.RideEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.google.common.collect.Lists;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.event.AfkEvent;
import fr.evercraft.everapi.event.MailEvent;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.TeleportDelay;
import fr.evercraft.everapi.sponge.UtilsPainting;
import fr.evercraft.everapi.text.ETextBuilder;

public class EEPlayerListeners {
	private EverEssentials plugin;

	public EEPlayerListeners(EverEssentials plugin) {
		this.plugin = plugin;
	}

	/**
	 * Ajoute le joueur dans le cache
	 */
	@Listener
	public void onClientConnectionEvent(final ClientConnectionEvent.Auth event) {
		this.plugin.getManagerServices().getEssentials().get(event.getProfile().getUniqueId());
	}

	/**
	 * Ajoute le joueur à la liste
	 */
	@Listener
	public void onClientConnectionEvent(final ClientConnectionEvent.Join event) {
		this.plugin.getManagerServices().getEssentials().registerPlayer(event.getTargetEntity().getUniqueId());
		this.plugin.getScheduler().start();
		

		// Motd
		if (this.plugin.getMotd().isEnable()) {
			Optional<EPlayer> optPlayer = this.plugin.getEverAPI().getEServer().getEPlayer(event.getTargetEntity()); 
			if (optPlayer.isPresent()) {
				EPlayer player = optPlayer.get();
				
				for (String line : this.plugin.getMotd().getMotd()) {
		    		player.sendMessage(player.replaceVariable(line));
		    	}
			}
		}
	}

	/**
	 * Supprime le joueur de la liste
	 */
	@Listener
	public void onClientConnectionEvent(final ClientConnectionEvent.Disconnect event) {
		this.plugin.getManagerServices().getEssentials().removePlayer(event.getTargetEntity().getUniqueId());
		
		if (this.plugin.getEServer().getOnlinePlayers().size() <= 1) {
			this.plugin.getScheduler().stop();
		}
	}
	
	@Listener
	public void onPlayerDamage(DamageEntityEvent event) {
		// C'est un joueur
		if (event.getTargetEntity() instanceof Player) {
			Optional<EPlayer> player = this.plugin.getEServer().getEPlayer(event.getTargetEntity().getUniqueId());
			// Le joueur est en god
			if (player.isPresent() && player.get().isGod()) {
				Optional<DamageSource> damagesource = event.getCause().first(DamageSource.class);
				// Le joueur tombe dans le vide
				if (damagesource.isPresent() && damagesource.get().equals(DamageSources.VOID)) {
					// L'option de téléportation au spwan est activé
					if (this.plugin.getConfigs().isGodTeleportToSpawn()) {
						player.get().sendMessage(EEMessages.PREFIX.get() + EEMessages.GOD_TELEPORT.get());
						player.get().teleportSpawn();
						player.get().heal();
						event.setCancelled(true);
					}
					// Domage normal
				} else {
					player.get().heal();
					event.setCancelled(true);
				}
			}
		} else if (event.getTargetEntity() instanceof Creature) {
			if (this.plugin.getConfigs().isGameModeKill()) {
				Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
				if (optDamageSource.isPresent() && optDamageSource.get().getSource() instanceof Player) {
					Player killer = (Player) optDamageSource.get().getSource();
					if (killer.get(Keys.IS_SNEAKING).orElse(false) && killer.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL).equals(GameModes.CREATIVE)) {
						Entity entity = event.getTargetEntity();
						event.setBaseDamage(entity.get(Keys.MAX_HEALTH).orElse(Double.MAX_VALUE));
					}
				}
			}
		}
	}
	
	@Listener
	public void onPlayerDeath(DestructEntityEvent.Death event) {
		if (event.getTargetEntity() instanceof Player) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
			
			if (optPlayer.isPresent()) {
				EPlayer player = optPlayer.get();
				
				player.setBack();
			}
		}
	}
	
	
	@Listener(order=Order.LAST)
	public void onPlayerInteract(InteractEntityEvent event, @First Player player_sponge) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(player_sponge);
		
		if (optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			// GameMode : Painting
			if (event instanceof InteractEntityEvent.Secondary && event.getTargetEntity() instanceof Painting) {
				if (this.plugin.getConfigs().isGameModePaint() && player.isSneaking() && player.isCreative()) {
					Painting paint = (Painting) event.getTargetEntity();
					if (paint.get(Keys.ART).isPresent()){
						Optional<UtilsPainting> painting = UtilsPainting.get(paint.get(Keys.ART).get());
						if (painting.isPresent()){
							paint.offer(Keys.ART, painting.get().next().getArt());
						}
					}
				}
			}
			
			// AFK
			player.updateLastActivated();
			
			// Freeze
			if(player.isFreeze()) {
				event.setCancelled(true);
			}
		}
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent event) {
		if (event.getTargetEntity() instanceof Player) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
			if (optPlayer.isPresent()) {
				EPlayer player = optPlayer.get();
				
				// AFK
				if (event.getToTransform().getPitch() != event.getFromTransform().getPitch() || event.getToTransform().getYaw() != event.getFromTransform().getYaw()) {
					player.updateLastActivated();
				}
				
				
				// Mouvement des pieds
				if (!event.getFromTransform().getExtent().equals(event.getToTransform().getExtent()) ||
						Math.round(event.getFromTransform().getPosition().getX()) != Math.round(event.getToTransform().getPosition().getX()) ||
						Math.round(event.getFromTransform().getPosition().getY()) != Math.round(event.getToTransform().getPosition().getY()) ||
						Math.round(event.getFromTransform().getPosition().getZ()) != Math.round(event.getToTransform().getPosition().getZ())) {
					
					// Teleport
					Optional<TeleportDelay> teleport = player.getTeleportDelay();
					if (teleport.isPresent() && !teleport.get().canMove()) {
						player.cancelTeleportDelay();
						player.sendMessage(EEMessages.PREFIX.get() + EEMessages.TELEPORT_ERROR_DELAY.get());
					}
					
					// Freeze
					if(player.isFreeze()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@Listener
	public void onPlayerInteractInventory(InteractInventoryEvent event, @First Player player_sponge) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(player_sponge);
		if (optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			// AFK
			player.updateLastActivated();
		}
	}
	
	@Listener
	public void onPlayerChangeInventory(ChangeInventoryEvent event, @First Player player_sponge) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(player_sponge);
		if (optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			// AFK
			player.updateLastActivated();
		}
	}
	
	@Listener
    public void onPlayerWriteChat(MessageChannelEvent.Chat event, @First Player player_sponge) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(player_sponge);
		if (optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			// AFK
			player.updateLastActivated();
			
			// Ignore
			Collection<MessageReceiver> members = event.getChannel().orElse(event.getOriginalChannel()).getMembers();
			
			List<MessageReceiver> list = Lists.newArrayList(members);
	        list.removeIf(others_sponge -> {
	        	if(others_sponge instanceof Player) {
	        		Optional<EPlayer> others = this.plugin.getEServer().getEPlayer((Player) others_sponge);
	        		if(others.isPresent()) {
	        			return others.get().ignore(player);
	        		}
	        	}
	        	return false;
	        });
	        
	        if (list.size() != members.size()) {
	            event.setChannel(MessageChannel.fixed(list));
	        }
		}
    }
	
	@Listener
    public void onPlayerSendCommand(SendCommandEvent event, @First Player player_sponge) {
		Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(player_sponge);
		if (optPlayer.isPresent()) {
			EPlayer player = optPlayer.get();
			
			// AFK
			if (!event.getCommand().equalsIgnoreCase("afk")) {
				player.updateLastActivated();
			}
			
			// Freeze
			if (!event.getCommand().equalsIgnoreCase("freeze") && player.isFreeze()) {
				event.setCancelled(true);
				player.sendMessage(EEMessages.PREFIX.get() + EEMessages.FREEZE_NO_COMMAND.get());
			}
		}
    }

	@Listener
	public void onPlayerHeal(HealEntityEvent event) {
		if (!event.isCancelled() && event.getTargetEntity() instanceof Player && event.getBaseHealAmount() > event.getFinalHealAmount()) {
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
			
			if (optPlayer.isPresent()) {
				EPlayer player = optPlayer.get();
				
				if (player.isGod()) {
					event.setCancelled(true);
					this.plugin.getEServer().broadcast("EverEssentials : Test HealEntityEvent");
				}
			}
		}
	}

	@Listener
	public void onPlayerFood(ChangeDataHolderEvent.ValueChange event, @First Player player) {
		this.plugin.getEServer().broadcast("EverEssentials : Test ChangeDataHolderEvent");
	}
	
	@Listener
	public void onPlayerAFK(AfkEvent event) {
		if (event.getValue()) {
			event.getPlayer().stopTotalTimePlayed();
		} else {
			event.getPlayer().startTotalTimePlayed();
		}
	}
	
	@Listener
	public void onPlayerMail(MailEvent.Add event) {
		EPlayer player = event.getPlayer();
		if(!player.equals(event.getTo())){
			player.sendMessage(ETextBuilder.toBuilder(EEMessages.PREFIX.get()).append(EEMessages.MAIL_NEW_MESSAGE.get())
					.replace("<message>", getButtonReadMail(event.getTo()))
				.build());
		}
	}
	
	private Text getButtonReadMail(final CommandSource source){
		return EEMessages.MAIL_BUTTON_NEW_MESSAGE.getText().toBuilder()
					.onHover(TextActions.showText(EChat.of(EEMessages.MAIL_BUTTON_NEW_MESSAGE_HOVER.get()
							.replaceAll("<player>", source.getName()))))
					.onClick(TextActions.runCommand("/mail read"))
					.build();
	}
	
	@Listener
	public void onPlayerRideEntity(RideEntityEvent.Mount event) {
		// TODO
	}
}
