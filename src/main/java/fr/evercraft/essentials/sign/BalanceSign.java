package fr.evercraft.essentials.sign;

import java.math.BigDecimal;
import java.util.Optional;

import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import fr.evercraft.essentials.EEMessage.EEMessages;
import fr.evercraft.essentials.EEPermissions;
import fr.evercraft.essentials.EverEssentials;
import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.text.ETextBuilder;

public class BalanceSign extends ESign {
	
	public BalanceSign(final EverEssentials plugin, final Text title_init, final Text title_enable, final Text title_disable) {
		super(plugin, 
				title_init, title_enable, title_disable, 
				EEPermissions.SIGN_BALANCE_CREATE, EEPermissions.SIGN_BALANCE_USE, EEPermissions.SIGN_BALANCE_BREAK);
	}

	@Override
	public boolean create(EPlayer player, Sign sign) {
		return true;
	}

	@Override
	public boolean use(EPlayer player, Sign sign) {
		Optional<EconomyService> economy = this.plugin.getEverAPI().getManagerService().getEconomy();
		if(economy.isPresent()) {
			BigDecimal balance = player.getBalance();
			player.sendMessage(
					ETextBuilder.toBuilder(EEMessages.PREFIX.get())
						.append(this.replace(economy.get().getDefaultCurrency(), EEMessages.BALANCE_PLAYER.get())
								.replaceAll("<solde>", this.cast(economy.get().getDefaultCurrency(), balance)))
						.replace("<solde_format>", economy.get().getDefaultCurrency().format(balance))
						.build());
			return true;
		} 
		return false;
	}

	@Override
	public boolean disable(EPlayer player, Sign sign) {
		player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.SIGN_DISABLE.getText()));
		return true;
	}

	@Override
	public boolean valide(Sign sign) {
		return true;
	}
	
	public String replace(final Currency currency, final String message) {
		return message.replaceAll("<symbol>", currency.getSymbol().toPlain())
				.replaceAll("<money_singular>", currency.getDisplayName().toPlain())
				.replaceAll("<money_plural>", currency.getPluralDisplayName().toPlain());
	}
	
	public String cast(final Currency currency, BigDecimal amount) {
		amount.setScale(currency.getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
		return UtilsDouble.getString(amount);
	}
}
