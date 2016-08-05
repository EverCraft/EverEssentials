package fr.evercraft.essentials.sign;

import java.util.Optional;

import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;

public abstract class ESign {
	
	protected final EverEssentials plugin;
	
	private final Text title_init;
	private final Text title_enable;
	private final Text title_disable;
	
	private final String permission_create;
	private final String permission_use;
	private final String permission_break;

	protected final boolean enable;
	
	public ESign(final EverEssentials plugin, final Text title_init, final Text title_enable, final Text title_disable, 
			final EEPermissions permission_create, final EEPermissions permission_use, final EEPermissions permission_break) {
		this.plugin = plugin;
		
		this.title_init = title_init;
		this.title_enable = title_enable;
		this.title_disable = title_disable;
		
		this.permission_create = permission_create.get();
		this.permission_use = permission_use.get();
		this.permission_break = permission_break.get();
		
		this.enable = false;
	}

	public Text getTitleInit() {
		return this.title_init;
	}

	public Text getTitleEnable() {
		return this.title_enable;
	}

	public Text getTitleDisable() {
		return this.title_disable;
	}
	
	public boolean isEnable() {
		return this.enable;
	}
	
	public boolean createSign(EPlayer player, Sign sign) {
		if(player.hasPermission(this.permission_create)) {
			sign.getSignData().addElement(0, this.getTitleEnable());
			if(this.create(player, sign)) {
				player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.SIGN_CREATE.getText()));
				return true;
			}
		} else {
			player.sendMessage(EAMessages.NO_PERMISSION.get());
		}
		return false;
	}
	
	public boolean useSign(final EPlayer player, final Sign sign) {
		if(this.isValide(sign)) {
			if(!isEnable(sign)) {
				sign.getSignData().addElement(0, this.getTitleEnable());
			}
			
			if(player.hasPermission(this.permission_use)) {
				return this.use(player, sign);
			} else {
				player.sendMessage(EAMessages.NO_PERMISSION.get());
			}
		} else {
			if(isEnable(sign)) {
				sign.getSignData().addElement(0, this.getTitleDisable());
			}
			
			if(player.hasPermission(this.permission_use)) {
				this.disable(player, sign);
			} else {
				player.sendMessage(EAMessages.NO_PERMISSION.get());
			}
		}
		return false;
	}
	
	public boolean breakSign(EPlayer player, Sign sign) {
		if(player.hasPermission(this.permission_break)) {			
			return true;
		} else {
			player.sendMessage(EAMessages.NO_PERMISSION.get());
		}
		return false;
	}
	
	public boolean isEnable(Sign sign) {
		Optional<Text> title = sign.getSignData().get(0);
		return title.isPresent() && title.get().equals(title.get());
	}
	
	public boolean isValide(Sign sign) {
		return this.enable && this.valide(sign);
	}
	
	public abstract boolean create(EPlayer player, Sign sign);
	public abstract boolean use(EPlayer player, Sign sign);
	public abstract boolean disable(EPlayer player, Sign sign);
	public abstract boolean valide(Sign sign);
}
