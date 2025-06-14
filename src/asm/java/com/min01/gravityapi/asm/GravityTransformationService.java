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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraftforge.coremod.api.ASMAPI;

public class GravityTransformationService implements ITransformationService, ITransformer<ClassNode>
{
	public static final Logger LOGGER = LoggerFactory.getLogger("GravityAPI");
	
	@Override
	public @NotNull ClassNode transform(ClassNode classNode, ITransformerVotingContext context) {
	    if (!"net/minecraft/world/entity/AreaEffectCloud".equals(classNode.name)) {
	        return classNode;
	    }

	    LOGGER.info("Applying gravity transformation to AreaEffectCloud.tick()");
	    
	    for (var method : classNode.methods) {
	        if (ASMAPI.mapMethod("m_8119_").equals(method.name) && "()V".equals(method.desc)) {
	            var instructions = method.instructions;

	            // Replace getX/Y/Z() with GravityUtil.getPlayerX/Y/Z(this)
	            for (var insn = instructions.getFirst(); insn != null; insn = insn.getNext()) {
	                if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL &&
	                    insn instanceof MethodInsnNode call &&
	                    "net/minecraft/world/entity/AreaEffectCloud".equals(call.owner) &&
	                    call.desc.equals("()D") &&
	                    (call.name.equals("getX") || call.name.equals("getY") || call.name.equals("getZ"))) {

	                    var aload0 = call.getPrevious();
	                    if (!(aload0 instanceof VarInsnNode aloadNode && aloadNode.getOpcode() == Opcodes.ALOAD && aloadNode.var == 0)) {
	                        continue;
	                    }

	                    var replacementMethod = "getPlayer" + Character.toUpperCase(call.name.charAt(call.name.length() - 1));

	                    var newInsnList = new InsnList();
	                    newInsnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
	                    newInsnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
	                        "com/min01/gravityapi/util/GravityUtil",
	                        replacementMethod,
	                        "(Lnet/minecraft/world/entity/Entity;)D",
	                        false));

	                    instructions.insertBefore(aload0, newInsnList);
	                    instructions.remove(aload0);
	                    instructions.remove(call);
	                }
	            }

	            // Inject vecPlayerToWorld after DSTORE 13 (z)
	            AbstractInsnNode insertAfter = null;
	            for (int i = 0; i < instructions.size(); i++) {
	                AbstractInsnNode node = instructions.get(i);
	                if (node.getOpcode() == Opcodes.DSTORE && node instanceof VarInsnNode store && store.var == 13) {
	                    insertAfter = store;
	                }
	            }

	            if (insertAfter != null) {
	                int vec3Index = method.maxLocals;
	                method.maxLocals += 1;

	                var inject = new InsnList();
	                inject.add(new VarInsnNode(Opcodes.DLOAD, 9));   // x
	                inject.add(new VarInsnNode(Opcodes.DLOAD, 11));  // y
	                inject.add(new VarInsnNode(Opcodes.DLOAD, 13));  // z
	                inject.add(new VarInsnNode(Opcodes.ALOAD, 0));   // this

	                inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
	                    "com/min01/gravityapi/util/GravityUtil",
	                    "getGravityDirection",
	                    "(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/core/Direction;",
	                    false));

	                inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
	                    "com/min01/gravityapi/util/GravityUtil",
	                    "vecPlayerToWorld",
	                    "(DDDLnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/Vec3;",
	                    false));
	                inject.add(new VarInsnNode(Opcodes.ASTORE, vec3Index));

	                // Unpack Vec3 into x/y/z
	                inject.add(new VarInsnNode(Opcodes.ALOAD, vec3Index));
	                inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/phys/Vec3", "x", "D"));
	                inject.add(new VarInsnNode(Opcodes.DSTORE, 9));

	                inject.add(new VarInsnNode(Opcodes.ALOAD, vec3Index));
	                inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/phys/Vec3", "y", "D"));
	                inject.add(new VarInsnNode(Opcodes.DSTORE, 11));

	                inject.add(new VarInsnNode(Opcodes.ALOAD, vec3Index));
	                inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/phys/Vec3", "z", "D"));
	                inject.add(new VarInsnNode(Opcodes.DSTORE, 13));

	                instructions.insert(insertAfter, inject);

	                LOGGER.info("Gravity transform injected into AreaEffectCloud.tick()");
	            } else {
	                LOGGER.warn("Could not find DSTORE 13 in AreaEffectCloud.tick(), skipping injection");
	            }
	        }
	    }

	    return classNode;
	}

	@Override
	public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) 
	{
		return TransformerVoteResult.YES;
	}

	@Override
	public @NotNull Set<Target> targets()
	{
	    return Set.of(Target.targetClass("net.minecraft.world.entity.AreaEffectCloud"));
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
