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
	
	@Override
	public void beginScanning(IEnvironment environment) {
		System.out.println("HAI1");
		/*
		try {
			Field f = FMLLoader.class.getDeclaredField("coreModProvider");
			f.setAccessible(true);
			ICoreModProvider icmp = (ICoreModProvider) f.get(null);
			f.set(null, new FakeCoreModProvider(icmp));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		*/
		try {
			CodeSource src = SyncFuLocator.class.getProtectionDomain().getCodeSource();
			URL jar = src.getLocation();
			if (!jar.toString().endsWith(".jar")) {
				LOGGER.warn(M_LOCATOR, "This be dev!!!");
				return;
			}
			URI uri = new URI("jar:".concat(jar.toString()).concat("!/"));
			//Thanks SO https://stackoverflow.com/a/48298758
			for (FileSystemProvider provider: FileSystemProvider.installedProviders()) {
		        if (provider.getScheme().equalsIgnoreCase("jar")) {
		            try {
		                provider.getFileSystem(uri);
		            } catch (FileSystemNotFoundException e) {
		                // in this case we need to initialize it first:
		                provider.newFileSystem(uri, Collections.emptyMap());
		            }
		        }
		    }
	        Path myPath = Paths.get(uri);
	        System.out.println(myPath);
	        Stream<Path> walk = Files.walk(myPath, 1).peek(p -> LOGGER.warn(M_LOCATOR, "Found {}", p)).filter(p -> p.toString().endsWith(".jar"));
	        Path root = FMLPaths.MODSDIR.get();
	        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
	        	Path file = it.next();
	        	LOGGER.info(M_LOCATOR, "Found target jar: {}", file);
	        	Files.copy(file, root.resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
	        }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
