package com.min01.gravityapi.command;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.min01.gravityapi.api.GravityChangerAPI;
import com.min01.gravityapi.capabilities.GravityCapabilityImpl;
import com.min01.gravityapi.util.GCUtil;
import com.min01.gravityapi.util.RotationUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class GravityCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands
            .literal("gravity")
            .requires(source -> source.hasPermission(2));
        
        builder.then(Commands.literal("set_base_direction")
            .then(Commands.argument("direction", new DirectionArgumentType())
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    Validate.isTrue(entity != null);
                    Direction direction = DirectionArgumentType.getDirection(context, "direction");
                    GravityChangerAPI.setBaseGravityDirection(entity, direction);
                    return 1;
                })
                .then(Commands.argument("entities", EntityArgument.entities())
                    .executes(context -> {
                        Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
                        Direction direction = DirectionArgumentType.getDirection(context, "direction");
                        for (Entity entity : entities) {
                            GravityChangerAPI.setBaseGravityDirection(entity, direction);
                        }
                        return entities.size();
                    })
                )
            )
        );
        
        builder.then(Commands.literal("reset")
            .executes(context -> {
                Entity entity = context.getSource().getEntity();
                Validate.isTrue(entity != null);
                GravityChangerAPI.resetGravity(entity);
                return 1;
            })
            .then(Commands.argument("entities", EntityArgument.entities())
                .executes(context -> {
                    Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
                    for (Entity entity : entities) {
                        GravityChangerAPI.resetGravity(entity);
                    }
                    return entities.size();
                })
            )
        );
        
        builder.then(Commands.literal("set_base_strength")
            .then(Commands.argument("strength", DoubleArgumentType.doubleArg(-20, 20))
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    Validate.isTrue(entity != null);
                    double strength = DoubleArgumentType.getDouble(context, "strength");
                    return executeSetBaseStrength(List.of(entity), strength);
                })
                .then(Commands.argument("entities", EntityArgument.entities())
                    .executes(context -> {
                        Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
                        double strength = DoubleArgumentType.getDouble(context, "strength");
                        return executeSetBaseStrength(entities, strength);
                    })
                )
            )
        );
        
        builder.then(Commands.literal("view")
            .executes(context -> {
                Entity entity = context.getSource().getEntity();
                
                GravityCapabilityImpl component = GravityChangerAPI.getGravityComponent(entity);
                
                context.getSource().sendSuccess(
                    () -> Component.translatable(
                        "gravity_changer.command.inform",
                        component.getBaseGravityDirection().getName(),
                        component.getBaseGravityStrength()
                    ), false
                );
                
                return 0;
            })
        );
        
        builder.then(Commands.literal("randomize_base_direction")
            .executes(context -> {
                CommandSourceStack source = context.getSource();
                Entity entity = source.getEntity();
                Validate.isTrue(entity != null);
                return executeRandomizeBaseDirection(source, List.of(entity));
            })
            .then(Commands.argument("entities", EntityArgument.entities())
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
                    return executeRandomizeBaseDirection(source, entities);
                })
            )
        );
        
        builder.then(Commands.literal("set_relative_base_direction")
            .then(Commands.argument("relativeDirection", new LocalDirectionArgumentType())
                .executes(context -> {
                    LocalDirection relativeDirection =
                        LocalDirectionArgumentType.getDirection(context, "relativeDirection");
                    
                    Entity entity = context.getSource().getEntity();
                    
                    Validate.isTrue(entity != null);
                    
                    return executeSetRelativeBaseDir(
                        context.getSource(), relativeDirection,
                        List.of(entity)
                    );
                })
                .then(Commands.argument("entities", EntityArgument.entities())
                    .executes(context -> {
                        LocalDirection relativeDirection =
                            LocalDirectionArgumentType.getDirection(context, "relativeDirection");
                        
                        Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
                        
                        return executeSetRelativeBaseDir(
                            context.getSource(), relativeDirection,
                            entities
                        );
                    })
                )
            )
        );
        
        dispatcher.register(builder);
    }
    
    private static int executeSetBaseStrength(Collection<? extends Entity> entities, double strength) {
        for (Entity entity : entities) {
            GravityChangerAPI.setBaseGravityStrength(entity, strength);
        }
        return entities.size();
    }
    
    private static int executeRandomizeBaseDirection(CommandSourceStack source, Collection<? extends Entity> entities) {
        RandomSource random = source.getLevel().random;
        for (Entity entity : entities) {
            Direction gravityDirection = Direction.getRandom(random);
            GravityChangerAPI.setBaseGravityDirection(entity, gravityDirection);
        }
        return entities.size();
    }
    
    private static void getSendFeedback(CommandSourceStack source, Entity entity, Direction gravityDirection) {
        Component text = GCUtil.getDirectionText(gravityDirection);
        if (source.getEntity() != null && source.getEntity() == entity) {
            source.sendSuccess(() -> Component.translatable("commands.gravity.get.self", text), true);
        }
        else {
            source.sendSuccess(() -> Component.translatable("commands.gravity.get.other", entity.getDisplayName(), text), true);
        }
    }
    
    private static int executeSetRelativeBaseDir(
        CommandSourceStack source, LocalDirection relativeDirection,
        Collection<? extends Entity> entities
    ) {
        int i = 0;
        for (Entity entity : entities) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            Direction combinedRelativeDirection = switch (relativeDirection) {
                case DOWN -> Direction.DOWN;
                case UP -> Direction.UP;
                case FORWARD, BACKWARD, LEFT, RIGHT ->
                    Direction.from2DDataValue(relativeDirection.getHorizontalOffset() + Direction.fromYRot(entity.getYRot()).get2DDataValue());
            };
            Direction newGravityDirection = RotationUtil.dirPlayerToWorld(combinedRelativeDirection, gravityDirection);
            GravityChangerAPI.setBaseGravityDirection(entity, newGravityDirection);
            
            getSendFeedback(source, entity, newGravityDirection);
            i++;
        }
        return i;
    }
    
}
