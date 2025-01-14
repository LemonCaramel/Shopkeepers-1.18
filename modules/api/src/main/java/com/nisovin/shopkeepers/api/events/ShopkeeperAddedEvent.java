package com.nisovin.shopkeepers.api.events;

import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;

import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.ShopkeeperRegistry;

/**
 * This event is called whenever a {@link Shopkeeper} was added to the {@link ShopkeeperRegistry}.
 * <p>
 * The shopkeeper has not yet been activated at this point.
 * <p>
 * The {@link Cause} can be used to differentiate between the different reasons for which a shopkeeper can be added.
 */
public class ShopkeeperAddedEvent extends ShopkeeperEvent {

	/**
	 * Indicates the cause of why a shopkeeper is added to the {@link ShopkeeperRegistry}.
	 */
	public enum Cause {
		/**
		 * The shopkeeper was freshly created.
		 */
		CREATED,
		/**
		 * The shopkeeper was loaded from the storage.
		 */
		LOADED;
	}

	private final Cause cause;

	/**
	 * Creates a new {@link ShopkeeperAddedEvent}.
	 * 
	 * @param shopkeeper
	 *            the shopkeeper, not <code>null</code>
	 * @param cause
	 *            the cause, not <code>null</code>
	 */
	public ShopkeeperAddedEvent(Shopkeeper shopkeeper, Cause cause) {
		super(shopkeeper);
		Validate.notNull(cause, "cause");
		this.cause = cause;
	}

	/**
	 * Gets the {@link Cause}.
	 * 
	 * @return the cause
	 */
	public Cause getCause() {
		return cause;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the {@link HandlerList} of this event.
	 * 
	 * @return the handler list
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
