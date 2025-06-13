package com.min01.gravityapi.asm;

import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.spi.FileSystemProvider;
import java.security.CodeSource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
		this.extractModFile();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public @NotNull List<ITransformer> transformers() 
	{
		return List.of(this);
	}
	
	public void extractModFile()
	{
        try 
        {
            CodeSource src = GravityTransformationService.class.getProtectionDomain().getCodeSource();
            URL jar = src.getLocation();
            for(FileSystemProvider provider : FileSystemProvider.installedProviders()) 
            {
                if(provider.getScheme().equalsIgnoreCase("union")) 
                {
                    try 
                    {
                        provider.getFileSystem(jar.toURI());
                    } 
                    catch(FileSystemNotFoundException e) 
                    {
                        provider.newFileSystem(jar.toURI(), Collections.emptyMap());
                    }
                }
            }
            Path myPath = Paths.get(jar.toURI());
            Stream<Path> walk = Files.walk(myPath, 1, new FileVisitOption[0]).peek(p -> 
            {
            }).filter(p2 ->
            {
                return p2.toString().endsWith(".jar");
            });
            Path root = Paths.get("mods", new String[0]);
            for(Path file : walk.toList()) 
            {
                Files.copy(file, root.resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch(Exception e2)
        {
            e2.printStackTrace();
        }
	}
}
