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
package fr.evercraft.essentials.listeners;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.essentials.service.ESubject;
import fr.evercraft.everapi.plugin.EChat;

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
		
		/*Optional<EPlayer> player = this.plugin.getEverAPI().getEServer().getEPlayer(event.getTargetEntity());
    	if(player.isPresent()) {
    		sendListMessage(player.get(), this.plugin.getMotd().getMotd());
    	}*/
    }
    
	/**
	 * Supprime le joueur de la liste
	 */
    @Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Disconnect event) {
    	this.plugin.getManagerServices().getEssentials().removePlayer(event.getTargetEntity().getUniqueId());
    }
    
    @Listener
	public void onSignChange(ChangeSignEvent event, @First Player player) {
		SignData signData = event.getText();
		Optional<ListValue<Text>> value = signData.getValue(Keys.SIGN_LINES);
		if(value.isPresent()) {
			signData = signData.set(value.get().set(0, EChat.of(this.plugin.getChat().replace(value.get().get(0).toPlain()))));
			signData = signData.set(value.get().set(1, EChat.of(this.plugin.getChat().replace(value.get().get(1).toPlain()))));
			signData = signData.set(value.get().set(2, EChat.of(this.plugin.getChat().replace(value.get().get(2).toPlain()))));
			signData = signData.set(value.get().set(3, EChat.of(this.plugin.getChat().replace(value.get().get(3).toPlain()))));
		}
	}

    @Listener
	public void onPlayerDamage(DamageEntityEvent event) {
    	if(event.getTargetEntity() instanceof Player) {
	    	ESubject subject = this.plugin.getManagerServices().getEssentials().get(event.getTargetEntity().getUniqueId());
	    	if(subject != null && subject.isGod()) {
	    		event.setBaseDamage(0);
	    		event.setCancelled(true);
	    	}
    	}
    }
    
    @Listener
	public void onPlayerHeal(HealEntityEvent event) {
    	if(event.getTargetEntity() instanceof Player && event.getBaseHealAmount() > event.getFinalHealAmount()) {
	    	ESubject subject = this.plugin.getManagerServices().getEssentials().get(event.getTargetEntity().getUniqueId());
	    	if(subject != null && subject.isGod()) {
	    		event.setCancelled(true);
	    		this.plugin.getEServer().broadcast("EverEssentials : Test HealEntityEvent");
	    	}
    	}
    }
    
    @Listener
	public void onPlayerFood(ChangeDataHolderEvent.ValueChange event, @First Player player) {
    	this.plugin.getEServer().broadcast("EverEssentials : Test ChangeDataHolderEvent");
    }
}
