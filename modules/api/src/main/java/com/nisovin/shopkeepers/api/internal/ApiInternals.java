package com.nisovin.shopkeepers.api.internal;

import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.api.shopkeeper.offers.BookOffer;
import com.nisovin.shopkeepers.api.shopkeeper.offers.PriceOffer;
import com.nisovin.shopkeepers.api.shopkeeper.offers.TradeOffer;
import com.nisovin.shopkeepers.api.util.UnmodifiableItemStack;

/**
 * Internal (non-API) components and functionality that needs to be accessible from within the API.
 * <p>
 * Only for internal use by the Shopkeepers API!
 */
public interface ApiInternals {

	/**
	 * Gets the {@link ApiInternals}.
	 * 
	 * @return the internals, not <code>null</code>
	 * @throws IllegalStateException
	 *             if the API is not enabled currently, eg. because the plugin is not enabled currently
	 */
	public static ApiInternals getInstance() {
		return InternalShopkeepersAPI.getPlugin().getApiInternals();
	}

	// FACTORIES

	/**
	 * Creates an {@link UnmodifiableItemStack} for the given {@link ItemStack}.
	 * <p>
	 * If the given item stack is already an {@link UnmodifiableItemStack}, this returns the given item stack itself.
	 * 
	 * @param itemStack
	 *            the item stack, can be <code>null</code>
	 * @return the unmodifiable item stack, or <code>null</code> if the given item stack is <code>null</code>
	 * @see UnmodifiableItemStack#of(ItemStack)
	 */
	public UnmodifiableItemStack createUnmodifiableItemStack(ItemStack itemStack);

	/**
	 * Creates a new {@link PriceOffer}.
	 * <p>
	 * If the given item stack is an {@link UnmodifiableItemStack}, it is assumed to be immutable and therefore not
	 * copied before it is stored by the price offer. Otherwise, it is first copied.
	 * 
	 * @param item
	 *            the item being traded, not <code>null</code> or empty
	 * @param price
	 *            the price, has to be positive
	 * @return the new offer
	 * @see PriceOffer#create(ItemStack, int)
	 */
	public PriceOffer createPriceOffer(ItemStack item, int price);

	/**
	 * Creates a new {@link PriceOffer}.
	 * <p>
	 * The given item stack is assumed to be immutable and therefore not copied before it is stored by the price offer.
	 * 
	 * @param item
	 *            the item being traded, not <code>null</code> or empty
	 * @param price
	 *            the price, has to be positive
	 * @return the new offer
	 * @see PriceOffer#create(UnmodifiableItemStack, int)
	 */
	public PriceOffer createPriceOffer(UnmodifiableItemStack item, int price);

	/**
	 * Creates a new {@link TradeOffer}.
	 * <p>
	 * If the given item stacks are {@link UnmodifiableItemStack}s, they are assumed to be immutable and therefore not
	 * copied before they are stored by the trade offer. Otherwise, they are first copied.
	 * 
	 * @param resultItem
	 *            the result item, not empty
	 * @param item1
	 *            the first buy item, not empty
	 * @param item2
	 *            the second buy item, can be empty
	 * @return the new offer
	 * @see TradeOffer#create(ItemStack, ItemStack, ItemStack)
	 */
	public TradeOffer createTradeOffer(ItemStack resultItem, ItemStack item1, ItemStack item2);

	/**
	 * Creates a new {@link TradeOffer}.
	 * <p>
	 * The given item stacks are assumed to be immutable and therefore not copied before they are stored by the trade
	 * offer.
	 * 
	 * @param resultItem
	 *            the result item, not empty
	 * @param item1
	 *            the first buy item, not empty
	 * @param item2
	 *            the second buy item, can be empty
	 * @return the new offer
	 * @see TradeOffer#create(UnmodifiableItemStack, UnmodifiableItemStack, UnmodifiableItemStack)
	 */
	public TradeOffer createTradeOffer(UnmodifiableItemStack resultItem, UnmodifiableItemStack item1, UnmodifiableItemStack item2);

	/**
	 * Creates a new {@link BookOffer}.
	 * 
	 * @param bookTitle
	 *            the book title, not <code>null</code> or empty
	 * @param price
	 *            the price, has to be positive
	 * @return the new offer
	 * @see BookOffer#create(String, int)
	 */
	public BookOffer createBookOffer(String bookTitle, int price);
}
