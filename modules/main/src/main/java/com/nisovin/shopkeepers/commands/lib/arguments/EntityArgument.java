package com.nisovin.shopkeepers.commands.lib.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;

import com.nisovin.shopkeepers.commands.lib.ArgumentFilter;
import com.nisovin.shopkeepers.commands.lib.ArgumentParseException;
import com.nisovin.shopkeepers.commands.lib.ArgumentsReader;
import com.nisovin.shopkeepers.commands.lib.CommandArgument;
import com.nisovin.shopkeepers.commands.lib.CommandContextView;
import com.nisovin.shopkeepers.commands.lib.CommandInput;

/**
 * Accepts an entity specified by some form of input.
 * <p>
 * Currently this only accepts UUIDs. In the future additional inputs to specify an entity could be added here (eg. by
 * name).
 */
public class EntityArgument extends CommandArgument<Entity> {

	protected final ArgumentFilter<Entity> filter; // Not null
	private final EntityByUUIDArgument entityUUIDArgument;
	private final TypedFirstOfArgument<Entity> firstOfArgument;

	public EntityArgument(String name) {
		this(name, ArgumentFilter.acceptAny());
	}

	public EntityArgument(String name, ArgumentFilter<Entity> filter) {
		this(name, filter, EntityUUIDArgument.DEFAULT_MINIMUM_COMPLETION_INPUT);
	}

	public EntityArgument(String name, ArgumentFilter<Entity> filter, int minimumUUIDCompletionInput) {
		super(name);
		this.filter = (filter == null) ? ArgumentFilter.acceptAny() : filter;
		this.entityUUIDArgument = new EntityByUUIDArgument(name + ":uuid", filter, minimumUUIDCompletionInput) {
			@Override
			protected Iterable<UUID> getCompletionSuggestions(	CommandInput input, CommandContextView context,
																int minimumCompletionInput, String idPrefix) {
				return EntityArgument.this.getUUIDCompletionSuggestions(input, context, minimumCompletionInput, idPrefix);
			}
		};
		this.firstOfArgument = new TypedFirstOfArgument<>(name + ":firstOf", Arrays.asList(entityUUIDArgument), false, false);
		firstOfArgument.setParent(this);
	}

	@Override
	public Entity parseValue(CommandInput input, CommandContextView context, ArgumentsReader argsReader) throws ArgumentParseException {
		// Also handles argument exceptions:
		return firstOfArgument.parseValue(input, context, argsReader);
	}

	@Override
	public List<String> complete(CommandInput input, CommandContextView context, ArgumentsReader argsReader) {
		return firstOfArgument.complete(input, context, argsReader);
	}

	/**
	 * Gets the uuid completion suggestions for the given name prefix.
	 * <p>
	 * This should take this argument's entity filter into account.
	 * 
	 * @param input
	 *            the command input, not <code>null</code>
	 * @param context
	 *            the command context, not <code>null</code>
	 * @param minimumCompletionInput
	 *            the minimum input length before completion suggestions are provided
	 * @param idPrefix
	 *            the id prefix, may be empty, not <code>null</code>
	 * @return the suggestions
	 */
	protected Iterable<UUID> getUUIDCompletionSuggestions(	CommandInput input, CommandContextView context,
															int minimumCompletionInput, String idPrefix) {
		return EntityUUIDArgument.getDefaultCompletionSuggestions(input, context, minimumCompletionInput, idPrefix, filter);
	}
}
