function initializeCoreMod() {
    return {
        'gravity_transform': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.entity.AreaEffectCloud'
            },
            'transformer': function(classNode) {
                var Opcodes = Java.type("org.objectweb.asm.Opcodes");
                var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
                var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
                var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
                var InsnList = Java.type("org.objectweb.asm.tree.InsnList");

                var asmapi = Java.type("net.minecraftforge.coremod.api.ASMAPI");

                var methods = classNode.methods;
                for (var i = 0; i < methods.size(); i++) {
                    var method = methods.get(i);

                    if (method.name.equals(asmapi.mapMethod("m_8119_"))) {
                        var instructions = method.instructions;
                        var insn = instructions.getFirst();

                        // Replace getX/Y/Z() with GravityUtil.getPlayerX/Y/Z(this)
						while (insn !== null) {
						    var next = insn.getNext(); // Always save next node first

						    if (
						        insn.getOpcode() === Opcodes.INVOKEVIRTUAL &&
						        insn.owner === "net/minecraft/world/entity/AreaEffectCloud" &&
						        (insn.name === "getX" || insn.name === "getY" || insn.name === "getZ") &&
						        insn.desc === "()D"
						    ) {
						        var aload0 = insn.getPrevious();
						        var replacementMethod = "getPlayer" + insn.name.charAt(insn.name.length - 1).toUpperCase();

						        var newInsnList = new InsnList();
						        newInsnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
						        newInsnList.add(new MethodInsnNode(
						            Opcodes.INVOKESTATIC,
						            "com/min01/gravityapi/util/GravityUtil",
						            replacementMethod,
						            "(Lnet/minecraft/world/entity/Entity;)D",
						            false
						        ));

						        instructions.insertBefore(aload0, newInsnList);
						        instructions.remove(aload0);
						        instructions.remove(insn);
						    }

						    insn = next; // ← ✅ move to saved next node
						}

                        // Now inject the vecPlayerToWorld logic AFTER DSTORE 13 (z local var)
                        var insertAfter = null;
                        for (var j = 0; j < instructions.size(); j++) {
                            var node = instructions.get(j);
                            if (node.getOpcode() === Opcodes.DSTORE && node.var === 13) {
                                insertAfter = node;
                            }
                        }

                        if (insertAfter !== null) {
                            // Allocate a new local variable slot for the Vec3 result
                            var vec3Index = method.maxLocals;
                            method.maxLocals += 1;

                            var inject = new InsnList();
                            inject.add(new VarInsnNode(Opcodes.DLOAD, 9));   // x
                            inject.add(new VarInsnNode(Opcodes.DLOAD, 11));  // y
                            inject.add(new VarInsnNode(Opcodes.DLOAD, 13));  // z
							inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // load 'this'
							inject.add(new MethodInsnNode(
							    Opcodes.INVOKESTATIC,
							    "com/min01/gravityapi/util/GravityUtil",
							    "getGravityDirection",
							    "(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/core/Direction;",
							    false
							));
                            inject.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "com/min01/gravityapi/util/GravityUtil",
                                "vecPlayerToWorld",
                                "(DDDLnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/Vec3;",
                                false
                            ));
                            inject.add(new VarInsnNode(Opcodes.ASTORE, vec3Index));

                            // Unpack Vec3 into local vars 9, 11, 13
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

                            asmapi.log("INFO", "Gravity transform successfully injected in AreaEffectCloud.tick()");
                        } else {
                            asmapi.log("WARN", "Could not find DSTORE 13 in AreaEffectCloud.tick(), skipping injection");
                        }
                    }
                }

                return classNode;
            }
        }
    };
}
