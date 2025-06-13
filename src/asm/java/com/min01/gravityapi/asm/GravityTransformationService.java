package com.min01.gravityapi.asm;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.TransformerVoteResult;

public class GravityTransformationService implements ITransformationService, ITransformer<ClassNode>
{
	public static final Logger LOGGER = LoggerFactory.getLogger("GravityAPI");
	
	@Override
	public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context)
	{
		LOGGER.info("GravityAPI " + input.name);
		return input;
	}

	@Override
	public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) 
	{
		return TransformerVoteResult.YES;
	}

	@Override
	public @NotNull Set<Target> targets()
	{
		return Set.of(Target.targetClass("net.minecraft.world.Entity"));
	}

	@Override
	public @NotNull String name() 
	{
		return "gravity-transformer";
	}

	@Override
	public void initialize(IEnvironment environment) 
	{
		
	}

	@Override
	public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException 
	{
		LOGGER.info("GravityAPI Load");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public @NotNull List<ITransformer> transformers() 
	{
		return List.of(this);
	}
}
