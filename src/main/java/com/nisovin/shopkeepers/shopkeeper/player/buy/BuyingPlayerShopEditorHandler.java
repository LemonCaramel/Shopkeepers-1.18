package com.nisovin.shopkeepers.shopkeeper.player.buy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.config.Settings;
import com.nisovin.shopkeepers.shopkeeper.TradingRecipeDraft;
import com.nisovin.shopkeepers.shopkeeper.offers.SKPriceOffer;
import com.nisovin.shopkeepers.shopkeeper.player.PlaceholderItems;
import com.nisovin.shopkeepers.shopkeeper.player.PlayerShopEditorHandler;
import com.nisovin.shopkeepers.util.ItemUtils;

public class BuyingPlayerShopEditorHandler extends PlayerShopEditorHandler {

	protected BuyingPlayerShopEditorHandler(SKBuyingPlayerShopkeeper shopkeeper) {
		super(shopkeeper);
	}

	@Override
	public SKBuyingPlayerShopkeeper getShopkeeper() {
		return (SKBuyingPlayerShopkeeper) super.getShopkeeper();
	}

	@Override
	protected List<TradingRecipeDraft> getTradingRecipes() {
		SKBuyingPlayerShopkeeper shopkeeper = this.getShopkeeper();

		// Add the shopkeeper's offers:
		List<SKPriceOffer> offers = shopkeeper.getOffers();
		List<TradingRecipeDraft> recipes = new ArrayList<>(offers.size() + 8); // Heuristic initial capacity
		offers.forEach(offer -> {
			ItemStack tradedItem = offer.getItem(); // Copy
			ItemStack currencyItem = Settings.createCurrencyItem(offer.getPrice());
			TradingRecipeDraft recipe = new TradingRecipeDraft(currencyItem, tradedItem, null);
			recipes.add(recipe);
		});

		// Add new empty recipe drafts for items from the container without existing offer:
		// We only add one recipe per similar item:
		List<ItemStack> newRecipes = new ArrayList<>();
		ItemStack[] containerContents = shopkeeper.getContainerContents(); // Empty if the container is not found
		for (ItemStack containerItem : containerContents) {
			if (ItemUtils.isEmpty(containerItem)) continue; // Ignore empty ItemStacks

			// Replace placeholder item, if this is one:
			containerItem = PlaceholderItems.replace(containerItem);

			if (Settings.isAnyCurrencyItem(containerItem)) continue; // Ignore currency items

			if (shopkeeper.getOffer(containerItem) != null) {
				// There is already a recipe for this item:
				continue;
			}

			if (ItemUtils.contains(newRecipes, containerItem)) {
				// We already added a new recipe for this item:
				continue;
			}

			// Add new empty recipe:
			containerItem = ItemUtils.copySingleItem(containerItem); // Ensures a stack size of 1
			ItemStack currencyItem = Settings.createZeroCurrencyItem();
			TradingRecipeDraft recipe = new TradingRecipeDraft(currencyItem, containerItem, null);
			recipes.add(recipe);
			newRecipes.add(containerItem);
		}

		return recipes;
	}

	@Override
	protected void clearRecipes() {
		SKBuyingPlayerShopkeeper shopkeeper = this.getShopkeeper();
		shopkeeper.clearOffers();
	}

	@Override
	protected void addRecipe(TradingRecipeDraft recipe) {
		assert recipe != null && recipe.isValid();
		assert recipe.getItem2() == null;

		ItemStack priceItem = recipe.getResultItem();
		assert priceItem != null;
		if (priceItem.getType() != Settings.currencyItem.getType()) return; // Checking this just in case
		assert priceItem.getAmount() > 0;
		int price = priceItem.getAmount();

		ItemStack tradedItem = recipe.getItem1();
		assert tradedItem != null;
		// Replace placeholder item, if this is one:
		tradedItem = PlaceholderItems.replace(tradedItem);

		SKBuyingPlayerShopkeeper shopkeeper = this.getShopkeeper();
		shopkeeper.addOffer(ShopkeepersAPI.createPriceOffer(tradedItem, price));
	}

	@Override
	protected void handleTradesClick(Session session, InventoryClickEvent event) {
		assert this.isTradesArea(event.getRawSlot());
		int rawSlot = event.getRawSlot();
		if (this.isResultRow(rawSlot)) {
			// Modifying cost:
			int column = rawSlot - RESULT_ITEM_OFFSET;
			ItemStack tradedItem = event.getInventory().getItem(column + ITEM_1_OFFSET);
			if (ItemUtils.isEmpty(tradedItem)) return;
			this.handleUpdateTradeCostItemOnClick(event, Settings.createCurrencyItem(1), Settings.createZeroCurrencyItem());
		} else if (this.isItem1Row(rawSlot)) {
			// Modifying bought item quantity:
			this.handleUpdateItemAmountOnClick(event, 1);
		} else if (this.isItem2Row(rawSlot)) {
			// Not used by the buying shopkeeper.
		}
	}
}
