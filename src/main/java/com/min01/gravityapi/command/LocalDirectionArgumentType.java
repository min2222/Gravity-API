package com.min01.gravityapi.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class LocalDirectionArgumentType implements ArgumentType<LocalDirection> {
    
    public static final DynamicCommandExceptionType exceptionType =
        new DynamicCommandExceptionType(object ->
            Component.literal("Invalid Local Direction " + object)
        );
    
    public static LocalDirection getDirection(CommandContext<?> context, String str) {
        return context.getArgument(str, LocalDirection.class);
    }
    
    @Override
    public LocalDirection parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString().toLowerCase();
        return switch (s) {
            case "forward" -> LocalDirection.FORWARD;
            case "backward" -> LocalDirection.BACKWARD;
            case "left" -> LocalDirection.LEFT;
            case "right" -> LocalDirection.RIGHT;
            case "up" -> LocalDirection.UP;
            case "down" -> LocalDirection.DOWN;
            default -> throw exceptionType.createWithContext(reader, s);
        };
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
            Arrays.stream(LocalDirection.values())
                .map(d -> d.name().toLowerCase())
                .collect(Collectors.toList()),
            builder
        );
    }
    
    @Override
    public Collection<String> getExamples() {
        return Arrays.stream(LocalDirection.values())
            .map(Enum::toString).collect(Collectors.toList());
    }
}
