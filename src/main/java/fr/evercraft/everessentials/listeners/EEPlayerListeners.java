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
package fr.evercraft.everessentials.listeners;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Art;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.entity.living.animal.Horse;
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
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;

import fr.evercraft.everapi.event.AfkEvent;
import fr.evercraft.everapi.event.MailEvent;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.essentials.Mail;
import fr.evercraft.everapi.services.essentials.TeleportDelay;
import fr.evercraft.everapi.sponge.UtilsLocation;
import fr.evercraft.everapi.sponge.UtilsPainting;
import fr.evercraft.everessentials.EverEssentials;
import fr.evercraft.everessentials.EEMessage.EEMessages;

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
		this.plugin.getEssentials().get(event.getProfile().getUniqueId());
	}

	/**
	 * Ajoute le joueur à la liste
	 */
	@Listener
	public void onClientConnectionEvent(final ClientConnectionEvent.Join event) {
		this.plugin.getEssentials().registerPlayer(event.getTargetEntity().getUniqueId());
		this.plugin.getScheduler().start();
		
		// WhiteLit
		WhitelistService whitelist = this.plugin.getEverAPI().getManagerService().getWhitelist();
		
		Optional<GameProfile> optProfile = whitelist.getWhitelistedProfiles().stream()
			.filter(profile -> 
				profile.getUniqueId().equals(event.getTargetEntity().getUniqueId()) && 
				(!profile.getName().isPresent() || !profile.getName().get().equals(event.getTargetEntity().getName())))
			.findFirst();
		
		if (optProfile.isPresent()) {
			this.plugin.getELogger().info("Whitelist : " + optProfile.get().getName().orElse(optProfile.get().getUniqueId().toString()) 
					+ " renamed in " + event.getTargetEntity().getName());
			whitelist.removeProfile(optProfile.get());
			whitelist.addProfile(event.getTargetEntity().getProfile());
		}
		
		// Motd
		if (this.plugin.getMotd().isEnable()) {
			EPlayer player = this.plugin.getEverAPI().getEServer().getEPlayer(event.getTargetEntity()); 
			player.sendMessage(this.plugin.getMotd().getMessage().toText(player.getReplaces()));
		}
	}

	/**
	 * Supprime le joueur de la liste
	 */
	@Listener
	public void onClientConnectionEvent(final ClientConnectionEvent.Disconnect event) {
		this.plugin.getEssentials().removePlayer(event.getTargetEntity().getUniqueId());
		
		if (this.plugin.getEServer().getOnlinePlayers().size() <= 1) {
			this.plugin.getScheduler().stop();
		}
	}
	
	@Listener
	public void onPlayerDamage(DamageEntityEvent event) {
		// C'est un joueur
		if (event.getTargetEntity() instanceof Player) {
			EPlayer player = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
			if (player.isGod()) {
				Optional<DamageSource> damagesource = event.getCause().first(DamageSource.class);
				// Le joueur tombe dans le vide
				if (damagesource.isPresent() && damagesource.get().equals(DamageSources.VOID)) {
					// L'option de téléportation au spwan est activé
					if (this.plugin.getConfigs().isGodTeleportToSpawn()) {
						EEMessages.GOD_TELEPORT.sendTo(player);
						player.teleportSpawn();
						player.heal();
						event.setCancelled(true);
					}
					// Domage normal
				} else {
					player.heal();
					event.setCancelled(true);
				}
			}
		} else if (event.getTargetEntity() instanceof Creature) {
			if (this.plugin.getConfigs().isGameModeKill()) {
				Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
				if (optDamageSource.isPresent() && optDamageSource.get().getSource() instanceof Player) {
					Player killer = (Player) optDamageSource.get().getSource();
					if (killer.get(Keys.IS_SNEAKING).orElse(false) && 
							killer.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL).equals(GameModes.CREATIVE)) {
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
			EPlayer player = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
			
			player.setBack();
		}
	}
	
	
	@Listener(order=Order.LAST)
	public void onPlayerInteractEntity(InteractEntityEvent event, @First Player player_sponge) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
			
		// GameMode : Painting
		if (event instanceof InteractEntityEvent.Secondary && event.getTargetEntity() instanceof Painting) {
			if (this.plugin.getConfigs().isGameModePaint() && player.isSneaking() && player.isCreative()) {
				Painting paint = (Painting) event.getTargetEntity();
				if (paint.get(Keys.ART).isPresent()) {
					Art art = paint.get(Keys.ART).get();
					Art next = UtilsPainting.next(art);
					while (!paint.offer(Keys.ART, next).isSuccessful() && !art.equals(next)){
						next = UtilsPainting.next(next);
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
	
	@Listener(order=Order.LAST)
	public void onPlayerInteract(InteractItemEvent.Secondary event, @First Player player_sponge) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
		
		if(player.getItemInMainHand().isPresent()){
			ItemStack item = player.getItemInMainHand().get();
			if(item.getType().equals(ItemTypes.COMPASS) && player.isCreative()){
				Optional<Vector3i> block = player.getViewBlock();
				if (block.isPresent()) {
					player.teleport(player.getWorld().getLocation(block.get().add(0, 1, 0)), true);
				}
			}
		}
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent event) {
		if (event.getTargetEntity() instanceof Player) {
			EPlayer player = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
				
			// AFK
			if (event.getToTransform().getPitch() != event.getFromTransform().getPitch() || 
					event.getToTransform().getYaw() != event.getFromTransform().getYaw()) {
				player.updateLastActivated();
			}
			
			if (UtilsLocation.isDifferentBlock(event.getFromTransform(), event.getToTransform())) {
				// Teleport
				Optional<TeleportDelay> teleport = player.getTeleportDelay();
				if (teleport.isPresent() && !teleport.get().canMove()) {
					player.cancelTeleportDelay();
					EEMessages.TELEPORT_ERROR_DELAY.sendTo(player);
				}
				
				// Freeze
				if(player.isFreeze()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@Listener
	public void onPlayerInteractInventory(InteractInventoryEvent event, @First Player player_sponge) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
			
		// AFK
		player.updateLastActivated();
	}
	
	@Listener
	public void onPlayerChangeInventory(ChangeInventoryEvent event, @First Player player_sponge) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
		
		// AFK
		player.updateLastActivated();
	}
	
	@Listener
    public void onPlayerWriteChat(MessageChannelEvent.Chat event, @First Player player_sponge) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
			
		// AFK
		player.updateLastActivated();
		
		// Ignore
		Collection<MessageReceiver> members = event.getChannel().orElse(event.getOriginalChannel()).getMembers();
		
		List<MessageReceiver> list = Lists.newArrayList(members);
        list.removeIf(others_sponge -> {
        	if(others_sponge instanceof Player) {
        		EPlayer others = this.plugin.getEServer().getEPlayer((Player) others_sponge);
        		return others.ignore(player);
        	}
        	return false;
        });
        
        if (list.size() != members.size()) {
            event.setChannel(MessageChannel.fixed(list));
        }
    }
	
	@Listener
    public void onPlayerSendCommand(SendCommandEvent event, @First Player player_sponge) {
		EPlayer player = this.plugin.getEServer().getEPlayer(player_sponge);
			
		// AFK
		if (!event.getCommand().equalsIgnoreCase("afk")) {
			player.updateLastActivated();
		}
		
		// Freeze
		if (!event.getCommand().equalsIgnoreCase("freeze") && player.isFreeze()) {
			event.setCancelled(true);
			EEMessages.FREEZE_NO_COMMAND.sendTo(player);
		}
    }

	@Listener
	public void onPlayerHeal(HealEntityEvent event) {
		if (!event.isCancelled() && event.getTargetEntity() instanceof Player && event.getBaseHealAmount() > event.getFinalHealAmount()) {
			EPlayer player = this.plugin.getEServer().getEPlayer((Player) event.getTargetEntity());
				
			if (player.isGod()) {
				event.setCancelled(true);
				this.plugin.getEServer().broadcast("EverEssentials : Test HealEntityEvent");
			}
		}
	}

	@Listener
	public void onPlayerFood(ChangeDataHolderEvent.ValueChange event, @First Player player) {
		/*event.getTargetHolder().getKeys().stream()
			.forEach(key -> this.plugin.getEServer().broadcast("EverEssentials : Test ChangeDataHolderEvent : " + key.getId()));*/
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
	public void onPlayerMail(MailEvent.Receive event) {
		event.getPlayer().ifPresent(player -> {
			if (!player.getIdentifier().equals(event.getMail().getTo())) {
				EEMessages.MAIL_NEW_MESSAGE.sender()
					.replace("{message}", this.getButtonReadMail(event.getMail()))
					.sendTo(player);
			}
		});
	}
	
	private Text getButtonReadMail(final Mail mail){
		return EEMessages.MAIL_BUTTON_NEW_MESSAGE.getText().toBuilder()
					.onHover(TextActions.showText(EEMessages.MAIL_BUTTON_NEW_MESSAGE_HOVER.getFormat()
							.toText("{player}", mail.getToName())))
					.onClick(TextActions.runCommand("/mail read " + mail.getID()))
					.build();
	}
	
	@Listener
	public void onPlayerRideEntity(RideEntityEvent.Mount event, @First Player player) {
		if(event.getTargetEntity() instanceof Horse && player.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL).equals(GameModes.CREATIVE)) {
			Entity entity = event.getTargetEntity();
			if(entity.get(Keys.TAMED_OWNER).isPresent()) {
				if(!entity.get(Keys.TAMED_OWNER).get().isPresent()) {
					entity.offer(Keys.TAMED_OWNER, Optional.ofNullable(player.getUniqueId()));
					
				}
			} else {
				entity.offer(Keys.TAMED_OWNER, Optional.ofNullable(player.getUniqueId()));
			}
		}
	}
	
	@Listener
	public void onPlayerRideEntity(RideEntityEvent.Dismount event, @First Player player) {
		// TODO
	}
}
