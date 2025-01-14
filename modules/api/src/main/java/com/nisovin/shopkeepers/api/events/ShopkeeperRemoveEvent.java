package com.nisovin.shopkeepers.api.events;

import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;

import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.ShopkeeperRegistry;

/**
 * This event is called whenever a {@link Shopkeeper} is about to get removed from the {@link ShopkeeperRegistry}.
 * <p>
 * The shopkeeper has already been deactivated at this point.
 * <p>
 * The {@link Cause} can be used to differentiate between the different reasons for which a shopkeeper can be removed.
 */
public class ShopkeeperRemoveEvent extends ShopkeeperEvent {

	/**
	 * Indicates the cause of why a shopkeeper is being removed from the {@link ShopkeeperRegistry}.
	 */
	public enum Cause {
		/**
		 * The shopkeeper gets permanently deleted.
		 */
		DELETE,
		/**
		 * The shopkeeper gets unloaded (ex. during plugin shutdowns or when reloading shopkeepers from storage).
		 */
		UNLOAD;
	}

	private final Cause cause;

	/**
	 * Creates a new {@link ShopkeeperRemoveEvent}.
	 * 
	 * @param shopkeeper
	 *            the shopkeeper, not <code>null</code>
	 * @param cause
	 *            the cause, not <code>null</code>
	 */
	public ShopkeeperRemoveEvent(Shopkeeper shopkeeper, Cause cause) {
		super(shopkeeper);
		Validate.notNull(cause, "cause");
		this.cause = cause;
	}

	/**
	 * Gets the {@link Cause}.
	 * 
	 * @return the cause, not <code>null</code>
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
