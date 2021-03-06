package net.minecraft.server;

import java.util.Random;

public class BlockDoor extends Block {

    private static final String[] a = new String[] { "doorWood_lower", "doorWood_upper", "doorIron_lower", "doorIron_upper"};
    private final int b;

    protected BlockDoor(int i, Material material) {
        super(i, material);
        if (material == Material.ORE) {
            this.b = 2;
        } else {
            this.b = 0;
        }

        float f = 0.5F;
        float f1 = 1.0F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
    }

    public boolean c() {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = this.c_(iblockaccess, i, j, k);

        return (l & 4) != 0;
    }

    public boolean b() {
        return false;
    }

    public int d() {
        return 7;
    }

    public AxisAlignedBB b(World world, int i, int j, int k) {
        this.updateShape(world, i, j, k);
        return super.b(world, i, j, k);
    }

    public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
        this.d(this.c_(iblockaccess, i, j, k));
    }

    public int d(IBlockAccess iblockaccess, int i, int j, int k) {
        return this.c_(iblockaccess, i, j, k) & 3;
    }

    public boolean b_(IBlockAccess iblockaccess, int i, int j, int k) {
        return (this.c_(iblockaccess, i, j, k) & 4) != 0;
    }

    private void d(int i) {
        float f = 0.1875F;

        this.a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int j = i & 3;
        boolean flag = (i & 4) != 0;
        boolean flag1 = (i & 16) != 0;

        if (j == 0) {
            if (flag) {
                if (!flag1) {
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                } else {
                    this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            } else {
                this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        } else if (j == 1) {
            if (flag) {
                if (!flag1) {
                    this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            } else {
                this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
        } else if (j == 2) {
            if (flag) {
                if (!flag1) {
                    this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                } else {
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            } else {
                this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        } else if (j == 3) {
            if (flag) {
                if (!flag1) {
                    this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                } else {
                    this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            } else {
                this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {}

    public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
        if (this.material == Material.ORE) {
            return true;
        } else {
            int i1 = this.c_(world, i, j, k);
            int j1 = i1 & 7;

            j1 ^= 4;
            if ((i1 & 8) == 0) {
                world.setData(i, j, k, j1, 2);
                world.g(i, j, k, i, j, k);
            } else {
                world.setData(i, j - 1, k, j1, 2);
                world.g(i, j - 1, k, i, j, k);
            }

            world.a(entityhuman, 1003, i, j, k, 0);
            return true;
        }
    }

    public void setDoor(World world, int i, int j, int k, boolean flag) {
        int l = this.c_(world, i, j, k);
        boolean flag1 = (l & 4) != 0;

        if (flag1 != flag) {
            int i1 = l & 7;

            i1 ^= 4;
            if ((l & 8) == 0) {
                world.setData(i, j, k, i1, 2);
                world.g(i, j, k, i, j, k);
            } else {
                world.setData(i, j - 1, k, i1, 2);
                world.g(i, j - 1, k, i, j, k);
            }

            world.a((EntityHuman) null, 1003, i, j, k, 0);
        }
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        int i1 = world.getData(i, j, k);

        if ((i1 & 8) == 0) {
            boolean flag = false;

            if (world.getTypeId(i, j + 1, k) != this.id) {
                world.setAir(i, j, k);
                flag = true;
            }

            if (!world.w(i, j - 1, k)) {
                world.setAir(i, j, k);
                flag = true;
                if (world.getTypeId(i, j + 1, k) == this.id) {
                    world.setAir(i, j + 1, k);
                }
            }

            if (flag) {
                if (!world.isStatic) {
                    this.c(world, i, j, k, i1, 0);
                }
            } else {
                boolean flag1 = world.isBlockIndirectlyPowered(i, j, k) || world.isBlockIndirectlyPowered(i, j + 1, k);

                if ((flag1 || l > 0 && Block.byId[l].isPowerSource()) && l != this.id) {
                    this.setDoor(world, i, j, k, flag1);
                }
            }
        } else {
            if (world.getTypeId(i, j - 1, k) != this.id) {
                world.setAir(i, j, k);
            }

            if (l > 0 && l != this.id) {
                this.doPhysics(world, i, j - 1, k, l);
            }
        }
    }

    public int getDropType(int i, Random random, int j) {
        return (i & 8) != 0 ? 0 : (this.material == Material.ORE ? Item.IRON_DOOR.id : Item.WOOD_DOOR.id);
    }

    public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
        this.updateShape(world, i, j, k);
        return super.a(world, i, j, k, vec3d, vec3d1);
    }

    public boolean canPlace(World world, int i, int j, int k) {
        return j >= 255 ? false : world.w(i, j - 1, k) && super.canPlace(world, i, j, k) && super.canPlace(world, i, j + 1, k);
    }

    public int h() {
        return 1;
    }

    public int c_(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.getData(i, j, k);
        boolean flag = (l & 8) != 0;
        int i1;
        int j1;

        if (flag) {
            i1 = iblockaccess.getData(i, j - 1, k);
            j1 = l;
        } else {
            i1 = l;
            j1 = iblockaccess.getData(i, j + 1, k);
        }

        boolean flag1 = (j1 & 1) != 0;

        return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);
    }

    public void a(World world, int i, int j, int k, int l, EntityHuman entityhuman) {
        if (entityhuman.abilities.canInstantlyBuild && (l & 8) != 0 && world.getTypeId(i, j - 1, k) == this.id) {
            world.setAir(i, j - 1, k);
        }
    }
}
