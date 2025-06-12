package com.min01.gravityapi.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import com.min01.gravityapi.capabilities.GravityCapabilities;
import com.min01.gravityapi.capabilities.GravityCapabilityImpl;
import com.min01.gravityapi.capabilities.IGravityCapability;
import com.mojang.math.Axis;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class GravityUtil
{
	public static final Map<Integer, Entity> ENTITY_MAP = new HashMap<>();
	public static final Map<Integer, Entity> ENTITY_MAP2 = new HashMap<>();
	
	public static void getClientLevel(Consumer<Level> consumer)
	{
		LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).filter(ClientLevel.class::isInstance).ifPresent(level -> 
		{
			consumer.accept(level);
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> T getEntityByUUID(Level level, UUID uuid)
	{
		Method m = ObfuscationReflectionHelper.findMethod(Level.class, "m_142646_");
		try 
		{
			LevelEntityGetter<Entity> entities = (LevelEntityGetter<Entity>) m.invoke(level);
			return (T) entities.get(uuid);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean hasGravity(Entity entity)
	{
		return getGravityDirection(entity) != Direction.DOWN;
	}
	
	public static void resetGravity(Entity entity)
	{
		setGravityDirection(entity, Direction.DOWN);
	}
	
	public static void setGravityDirection(Entity entity, Direction direction)
	{
		IGravityCapability cap = entity.getCapability(GravityCapabilities.GRAVITY).orElse(new GravityCapabilityImpl());
		cap.setGravityDirection(direction);
	}
	
	public static Direction getGravityDirection(Entity entity)
	{
		IGravityCapability cap = entity.getCapability(GravityCapabilities.GRAVITY).orElse(new GravityCapabilityImpl());
		return cap.getGravityDirection();
	}
	
    private static final Quaternionf[] WORLD_ROTATION_QUATERNIONS = new Quaternionf[6];
    
    static 
    {
        WORLD_ROTATION_QUATERNIONS[0] = new Quaternionf();
        
        WORLD_ROTATION_QUATERNIONS[1] = Axis.ZP.rotationDegrees(-180);
        
        WORLD_ROTATION_QUATERNIONS[2] = Axis.XP.rotationDegrees(-90);
        
        WORLD_ROTATION_QUATERNIONS[3] = Axis.XP.rotationDegrees(-90);
        WORLD_ROTATION_QUATERNIONS[3].mul(Axis.YP.rotationDegrees(-180));
        
        WORLD_ROTATION_QUATERNIONS[4] = Axis.XP.rotationDegrees(-90);
        WORLD_ROTATION_QUATERNIONS[4].mul(Axis.YP.rotationDegrees(-90));
        
        WORLD_ROTATION_QUATERNIONS[5] = Axis.XP.rotationDegrees(-90);
        WORLD_ROTATION_QUATERNIONS[5].mul(Axis.YP.rotationDegrees(-270));
    }
    
    private static final Direction[][] DIR_PLAYER_TO_WORLD = new Direction[6][];
    
    static 
    {
        for(Direction gravityDirection : Direction.values())
        {
            DIR_PLAYER_TO_WORLD[gravityDirection.get3DDataValue()] = new Direction[6];
            for(Direction direction : Direction.values()) 
            {
                Vec3 directionVector = Vec3.atLowerCornerOf(direction.getNormal());
                directionVector = vecPlayerToWorld(directionVector, gravityDirection);
                DIR_PLAYER_TO_WORLD[gravityDirection.get3DDataValue()][direction.get3DDataValue()] = Direction.getNearest(directionVector.x, directionVector.y, directionVector.z);
            }
        }
    }
    
    public static Direction dirPlayerToWorld(Direction direction, Direction gravityDirection) 
    {
        return DIR_PLAYER_TO_WORLD[gravityDirection.get3DDataValue()][direction.get3DDataValue()];
    }
    
    /**
     * Note: this is the rotation that rotates the world for rendering, not the entity.
     * Note: don't modify the quaternion object in-place.
     * TODO change return value to {@link Quaternionfc}
     */
    public static Quaternionf getWorldRotationQuaternion(Direction direction) 
    {
        return WORLD_ROTATION_QUATERNIONS[direction.get3DDataValue()];
    }
    
    public static Vec3 rotate(Vec3 vec, Quaternionf quaternionf) 
    {
        Vector3f vector3f = vec.toVector3f();
        vector3f.rotate(quaternionf);
        return new Vec3(vector3f);
    }
    
    public static Vec3 maskPlayerToWorld(double x, double y, double z, Direction gravityDirection)
    {
        return switch (gravityDirection) 
        {
            case DOWN, UP -> new Vec3(x, y, z);
            case NORTH, SOUTH -> new Vec3(x, z, y);
            case WEST, EAST -> new Vec3(y, z, x);
        };
    }
    
    public static Vec3 maskPlayerToWorld(Vec3 vec3d, Direction gravityDirection)
    {
        return maskPlayerToWorld(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }
	
	public static Vec3 vecWorldToPlayer(double x, double y, double z, Direction gravityDirection) 
	{
		return switch (gravityDirection) 
		{
			case DOWN -> new Vec3(x, y, z);
			case UP -> new Vec3(-x, -y, z);
			case NORTH -> new Vec3(x, z, -y);
			case SOUTH -> new Vec3(-x, -z, -y);
			case WEST -> new Vec3(-z, x, -y);
			case EAST -> new Vec3(z, -x, -y);
		};
	}

	public static Vec3 vecWorldToPlayer(Vec3 vec3d, Direction gravityDirection)
	{
		return vecWorldToPlayer(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
	}

	public static Vec3 vecPlayerToWorld(double x, double y, double z, Direction gravityDirection) 
	{
		return switch (gravityDirection) 
		{
			case DOWN -> new Vec3(x, y, z);
			case UP -> new Vec3(-x, -y, z);
			case NORTH -> new Vec3(x, -z, y);
			case SOUTH -> new Vec3(-x, -z, -y);
			case WEST -> new Vec3(y, -z, -x);
			case EAST -> new Vec3(-y, -z, x);
		};
	}

	public static Vec3 vecPlayerToWorld(Vec3 vec3d, Direction gravityDirection) 
	{
		return vecPlayerToWorld(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
	}
	
    public static Vector3f vecWorldToPlayer(float x, float y, float z, Direction gravityDirection)
    {
        return switch (gravityDirection)
        {
            case DOWN -> new Vector3f(x, y, z);
            case UP -> new Vector3f(-x, -y, z);
            case NORTH -> new Vector3f(x, z, -y);
            case SOUTH -> new Vector3f(-x, -z, -y);
            case WEST -> new Vector3f(-z, x, -y);
            case EAST -> new Vector3f(z, -x, -y);
        };
    }
    
    public static Vector3f vecWorldToPlayer(Vector3f vector3F, Direction gravityDirection)
    {
        return vecWorldToPlayer(vector3F.x(), vector3F.y(), vector3F.z(), gravityDirection);
    }
    
    public static Vector3f vecPlayerToWorld(float x, float y, float z, Direction gravityDirection)
    {	
        return switch (gravityDirection) 
        {
            case DOWN -> new Vector3f(x, y, z);
            case UP -> new Vector3f(-x, -y, z);
            case NORTH -> new Vector3f(x, -z, y);
            case SOUTH -> new Vector3f(-x, -z, -y);
            case WEST -> new Vector3f(y, -z, -x);
            case EAST -> new Vector3f(-y, -z, x);
        };
    }
    
    public static Vector3f vecPlayerToWorld(Vector3f vector3F, Direction gravityDirection) 
    {
        return vecPlayerToWorld(vector3F.x(), vector3F.y(), vector3F.z(), gravityDirection);
    }
	
    public static AABB boxWorldToPlayer(AABB box, Direction gravityDirection) 
    {
        return new AABB(vecWorldToPlayer(box.minX, box.minY, box.minZ, gravityDirection), vecWorldToPlayer(box.maxX, box.maxY, box.maxZ, gravityDirection));
    }
	
    public static AABB boxPlayerToWorld(AABB box, Direction gravityDirection) 
    {
        return new AABB(vecPlayerToWorld(box.minX, box.minY, box.minZ, gravityDirection), vecPlayerToWorld(box.maxX, box.maxY, box.maxZ, gravityDirection));
    }
}
