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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;

public class EEKill  extends ECommand<EverEssentials> {
	
	public EEKill(final EverEssentials plugin) {
        super(plugin, "kill");
    }

	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.KILL.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EEMessages.KILL_DESCRIPTION.getText();
	}

	@Override
	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	@Override
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.getAllPlayers(source, true);
		}
		return Arrays.asList();
	}
	
	@Override
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// Si on ne connait pas le joueur
		if (args.size() == 1) {
			
			Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
			if (optPlayer.isPresent()){
				return this.commandKill(source, optPlayer.get());
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{player}", args.get(0))
					.sendTo(source);
			}
			
		} else {
			source.sendMessage(this.help(source));
		}
		
		return CompletableFuture.completedFuture(false);
	}
	
	private CompletableFuture<Boolean> commandKill(final CommandSource staff, final EPlayer player) { 
		// Event cancel
        if(!player.setHealth(0)) {
        	if (!player.equals(staff)) {
    			EEMessages.KILL_PLAYER_CANCEL.sender()
    				.replace("{player}", player.getName())
    				.sendTo(staff);
    		} else {
    			EEMessages.KILL_EQUALS_CANCEL.sender()
					.replace("{player}", player.getName())
					.sendTo(staff);
    		}
        	return CompletableFuture.completedFuture(false);
        }
        
        
        final MessageEvent.MessageFormatter formatter = new MessageEvent.MessageFormatter();
        MessageChannel originalChannel;
        MessageChannel channel;
        Text originalMessage;
        boolean messageCancelled = false;

        originalChannel = player.getMessageChannel();
        channel = player.getMessageChannel();

        Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
        replaces.putAll(player.getReplaces());
        replaces.put(Pattern.compile("\\{staff}"), EReplace.of(staff.getName()));
        replaces.put(Pattern.compile("\\{player}"), EReplace.of(player.getName()));
        
        if (!player.equals(staff)) {
        	messageCancelled = !EEMessages.KILL_PLAYER_DEATH_MESSAGE.getMessage().getChat().isPresent();
	        originalMessage = EEMessages.KILL_PLAYER_DEATH_MESSAGE.getFormat().toText(replaces);
        } else {
        	messageCancelled = !EEMessages.KILL_EQUALS_DEATH_MESSAGE.getMessage().getChat().isPresent();
        	originalMessage = EEMessages.KILL_EQUALS_DEATH_MESSAGE.getFormat().toText(replaces);
        }
        formatter.getBody().add(new MessageEvent.DefaultBodyApplier(originalMessage));
        
        List<NamedCause> causes = new ArrayList<NamedCause>();
        causes.add(NamedCause.of("Command", "kill"));
        causes.add(NamedCause.owner(staff));
        Cause cause = Cause.of(causes);
        
        DestructEntityEvent.Death event = SpongeEventFactory.createDestructEntityEventDeath(cause, originalChannel, Optional.of(channel), formatter, player, messageCancelled);
        this.plugin.getGame().getEventManager().post(event);

    	if (!event.isMessageCancelled() && !event.getMessage().isEmpty()) {
    		event.getChannel().ifPresent(eventChannel -> eventChannel.send(player, event.getMessage()));
    	} else {
    		if (!player.equals(staff)) {
    			EEMessages.KILL_PLAYER.sender()
    				.replace("{staff}", staff.getName())
    				.sendTo(player);
    			EEMessages.KILL_STAFF.sender()
    				.replace("{player}", player.getName())
    				.sendTo(staff);
    		} else {
    			EEMessages.KILL_EQUALS.sender()
    				.replace("{player}", player.getName())
    				.replace("{staff}", staff.getName())
    				.sendTo(player);
    		}
    	}
    	return CompletableFuture.completedFuture(true);
	}
}
