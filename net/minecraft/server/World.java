package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class World implements IBlockAccess {

    public boolean a = false;
    private List C = new ArrayList();
    public List entityList = new ArrayList();
    private List D = new ArrayList();
    private TreeSet E = new TreeSet();
    private Set F = new HashSet();
    public List c = new ArrayList();
    public List players = new ArrayList();
    public List e = new ArrayList();
    private long G = 16777215L;
    public int f = 0;
    protected int g = (new Random()).nextInt();
    protected int h = 1013904223;
    protected float i;
    protected float j;
    protected float k;
    protected float l;
    protected int m = 0;
    public int n = 0;
    public boolean o = false;
    private long H = System.currentTimeMillis();
    protected int p = 40;
    public int spawnMonsters;
    public Random random = new Random();
    public boolean s = false;
    public final WorldProvider worldProvider;
    protected List u = new ArrayList();
    protected IChunkProvider chunkProvider;
    protected final IDataManager w;
    protected WorldData worldData;
    public boolean isLoading;
    private boolean I;
    public WorldMapCollection z;
    private ArrayList J = new ArrayList();
    private int K = 0;
    private boolean allowMonsters = true;
    private boolean allowAnimals = true;
    static int A = 0;
    private Set N = new HashSet();
    private int O;
    private List P;
    public boolean isStatic;

    public WorldChunkManager getWorldChunkManager() {
        return this.worldProvider.b;
    }

    public World(IDataManager idatamanager, String s, long i, WorldProvider worldprovider) {
        this.O = this.random.nextInt(12000);
        this.P = new ArrayList();
        this.isStatic = false;
        this.w = idatamanager;
        this.z = new WorldMapCollection(idatamanager);
        this.worldData = idatamanager.c();
        this.s = this.worldData == null;
        if (worldprovider != null) {
            this.worldProvider = worldprovider;
        } else if (this.worldData != null && this.worldData.h() == -1) {
            this.worldProvider = WorldProvider.a(-1);
        } else {
            this.worldProvider = WorldProvider.a(0);
        }

        boolean flag = false;

        if (this.worldData == null) {
            this.worldData = new WorldData(i, s);
            flag = true;
        } else {
            this.worldData.a(s);
        }

        this.worldProvider.a(this);
        this.chunkProvider = this.b();
        if (flag) {
            this.c();
        }

        this.g();
        this.x();
    }

    protected IChunkProvider b() {
        IChunkLoader ichunkloader = this.w.a(this.worldProvider);

        return new ChunkProviderLoadOrGenerate(this, ichunkloader, this.worldProvider.b());
    }

    protected void c() {
        this.isLoading = true;
        int i = 0;
        byte b0 = 64;

        int j;

        for (j = 0; !this.worldProvider.a(i, j); j += this.random.nextInt(64) - this.random.nextInt(64)) {
            i += this.random.nextInt(64) - this.random.nextInt(64);
        }

        this.worldData.setSpawn(i, b0, j);
        this.isLoading = false;
    }

    public int a(int i, int j) {
        int k;

        for (k = 63; !this.isEmpty(i, k + 1, j); ++k) {
            ;
        }

        return this.getTypeId(i, k, j);
    }

    public void save(boolean flag, IProgressUpdate iprogressupdate) {
        if (this.chunkProvider.b()) {
            if (iprogressupdate != null) {
                iprogressupdate.a("Saving level");
            }

            this.w();
            if (iprogressupdate != null) {
                iprogressupdate.b("Saving chunks");
            }

            this.chunkProvider.saveChunks(flag, iprogressupdate);
        }
    }

    private void w() {
        this.k();
        this.w.a(this.worldData, this.players);
        this.z.a();
    }

    public int getTypeId(int i, int j, int k) {
        return i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000 ? (j < 0 ? 0 : (j >= 128 ? 0 : this.getChunkAt(i >> 4, k >> 4).getTypeId(i & 15, j, k & 15))) : 0;
    }

    public boolean isEmpty(int i, int j, int k) {
        return this.getTypeId(i, j, k) == 0;
    }

    public boolean isLoaded(int i, int j, int k) {
        return j >= 0 && j < 128 ? this.isChunkLoaded(i >> 4, k >> 4) : false;
    }

    public boolean a(int i, int j, int k, int l) {
        return this.a(i - l, j - l, k - l, i + l, j + l, k + l);
    }

    public boolean a(int i, int j, int k, int l, int i1, int j1) {
        if (i1 >= 0 && j < 128) {
            i >>= 4;
            j >>= 4;
            k >>= 4;
            l >>= 4;
            i1 >>= 4;
            j1 >>= 4;

            for (int k1 = i; k1 <= l; ++k1) {
                for (int l1 = k; l1 <= j1; ++l1) {
                    if (!this.isChunkLoaded(k1, l1)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean isChunkLoaded(int i, int j) {
        return this.chunkProvider.isChunkLoaded(i, j);
    }

    public Chunk b(int i, int j) {
        return this.getChunkAt(i >> 4, j >> 4);
    }

    public Chunk getChunkAt(int i, int j) {
        return this.chunkProvider.getOrCreateChunk(i, j);
    }

    public boolean setRawTypeIdAndData(int i, int j, int k, int l, int i1) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (j < 0) {
                return false;
            } else if (j >= 128) {
                return false;
            } else {
                Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

                return chunk.a(i & 15, j, k & 15, l, i1);
            }
        } else {
            return false;
        }
    }

    public boolean setRawTypeId(int i, int j, int k, int l) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (j < 0) {
                return false;
            } else if (j >= 128) {
                return false;
            } else {
                Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

                return chunk.a(i & 15, j, k & 15, l);
            }
        } else {
            return false;
        }
    }

    public Material getMaterial(int i, int j, int k) {
        int l = this.getTypeId(i, j, k);

        return l == 0 ? Material.AIR : Block.byId[l].material;
    }

    public int getData(int i, int j, int k) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (j < 0) {
                return 0;
            } else if (j >= 128) {
                return 0;
            } else {
                Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

                i &= 15;
                k &= 15;
                return chunk.getData(i, j, k);
            }
        } else {
            return 0;
        }
    }

    public void setData(int i, int j, int k, int l) {
        if (this.setRawData(i, j, k, l)) {
            int i1 = this.getTypeId(i, j, k);

            if (Block.t[i1 & 255]) {
                this.update(i, j, k, i1);
            } else {
                this.applyPhysics(i, j, k, i1);
            }
        }
    }

    public boolean setRawData(int i, int j, int k, int l) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (j < 0) {
                return false;
            } else if (j >= 128) {
                return false;
            } else {
                Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

                i &= 15;
                k &= 15;
                chunk.b(i, j, k, l);
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean setTypeId(int i, int j, int k, int l) {
        if (this.setRawTypeId(i, j, k, l)) {
            this.update(i, j, k, l);
            return true;
        } else {
            return false;
        }
    }

    public boolean setTypeIdAndData(int i, int j, int k, int l, int i1) {
        if (this.setRawTypeIdAndData(i, j, k, l, i1)) {
            this.update(i, j, k, l);
            return true;
        } else {
            return false;
        }
    }

    public void notify(int i, int j, int k) {
        for (int l = 0; l < this.u.size(); ++l) {
            ((IWorldAccess) this.u.get(l)).a(i, j, k);
        }
    }

    protected void update(int i, int j, int k, int l) {
        this.notify(i, j, k);
        this.applyPhysics(i, j, k, l);
    }

    public void g(int i, int j, int k, int l) {
        if (k > l) {
            int i1 = l;

            l = k;
            k = i1;
        }

        this.b(i, k, j, i, l, j);
    }

    public void h(int i, int j, int k) {
        for (int l = 0; l < this.u.size(); ++l) {
            ((IWorldAccess) this.u.get(l)).a(i, j, k, i, j, k);
        }
    }

    public void b(int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = 0; k1 < this.u.size(); ++k1) {
            ((IWorldAccess) this.u.get(k1)).a(i, j, k, l, i1, j1);
        }
    }

    public void applyPhysics(int i, int j, int k, int l) {
        this.k(i - 1, j, k, l);
        this.k(i + 1, j, k, l);
        this.k(i, j - 1, k, l);
        this.k(i, j + 1, k, l);
        this.k(i, j, k - 1, l);
        this.k(i, j, k + 1, l);
    }

    private void k(int i, int j, int k, int l) {
        if (!this.o && !this.isStatic) {
            Block block = Block.byId[this.getTypeId(i, j, k)];

            if (block != null) {
                block.doPhysics(this, i, j, k, l);
            }
        }
    }

    public boolean isChunkLoaded(int i, int j, int k) {
        return this.getChunkAt(i >> 4, k >> 4).c(i & 15, j, k & 15);
    }

    public int j(int i, int j, int k) {
        if (j < 0) {
            return 0;
        } else {
            if (j >= 128) {
                j = 127;
            }

            return this.getChunkAt(i >> 4, k >> 4).c(i & 15, j, k & 15, 0);
        }
    }

    public int getLightLevel(int i, int j, int k) {
        return this.a(i, j, k, true);
    }

    public int a(int i, int j, int k, boolean flag) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (flag) {
                int l = this.getTypeId(i, j, k);

                if (l == Block.STEP.id || l == Block.SOIL.id || l == Block.COBBLESTONE_STAIRS.id || l == Block.WOOD_STAIRS.id) {
                    int i1 = this.a(i, j + 1, k, false);
                    int j1 = this.a(i + 1, j, k, false);
                    int k1 = this.a(i - 1, j, k, false);
                    int l1 = this.a(i, j, k + 1, false);
                    int i2 = this.a(i, j, k - 1, false);

                    if (j1 > i1) {
                        i1 = j1;
                    }

                    if (k1 > i1) {
                        i1 = k1;
                    }

                    if (l1 > i1) {
                        i1 = l1;
                    }

                    if (i2 > i1) {
                        i1 = i2;
                    }

                    return i1;
                }
            }

            if (j < 0) {
                return 0;
            } else {
                if (j >= 128) {
                    j = 127;
                }

                Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

                i &= 15;
                k &= 15;
                return chunk.c(i, j, k, this.f);
            }
        } else {
            return 15;
        }
    }

    public boolean l(int i, int j, int k) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (j < 0) {
                return false;
            } else if (j >= 128) {
                return true;
            } else if (!this.isChunkLoaded(i >> 4, k >> 4)) {
                return false;
            } else {
                Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

                i &= 15;
                k &= 15;
                return chunk.c(i, j, k);
            }
        } else {
            return false;
        }
    }

    public int getHighestBlockYAt(int i, int j) {
        if (i >= -32000000 && j >= -32000000 && i < 32000000 && j <= 32000000) {
            if (!this.isChunkLoaded(i >> 4, j >> 4)) {
                return 0;
            } else {
                Chunk chunk = this.getChunkAt(i >> 4, j >> 4);

                return chunk.b(i & 15, j & 15);
            }
        } else {
            return 0;
        }
    }

    public void a(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
        if (!this.worldProvider.e || enumskyblock != EnumSkyBlock.SKY) {
            if (this.isLoaded(i, j, k)) {
                if (enumskyblock == EnumSkyBlock.SKY) {
                    if (this.l(i, j, k)) {
                        l = 15;
                    }
                } else if (enumskyblock == EnumSkyBlock.BLOCK) {
                    int i1 = this.getTypeId(i, j, k);

                    if (Block.s[i1] > l) {
                        l = Block.s[i1];
                    }
                }

                if (this.a(enumskyblock, i, j, k) != l) {
                    this.a(enumskyblock, i, j, k, i, j, k);
                }
            }
        }
    }

    public int a(EnumSkyBlock enumskyblock, int i, int j, int k) {
        if (j < 0) {
            j = 0;
        }

        if (j >= 128) {
            j = 127;
        }

        if (j >= 0 && j < 128 && i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            int l = i >> 4;
            int i1 = k >> 4;

            if (!this.isChunkLoaded(l, i1)) {
                return 0;
            } else {
                Chunk chunk = this.getChunkAt(l, i1);

                return chunk.a(enumskyblock, i & 15, j, k & 15);
            }
        } else {
            return enumskyblock.c;
        }
    }

    public void b(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (j >= 0) {
                if (j < 128) {
                    if (this.isChunkLoaded(i >> 4, k >> 4)) {
                        Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

                        chunk.a(enumskyblock, i & 15, j, k & 15, l);

                        for (int i1 = 0; i1 < this.u.size(); ++i1) {
                            ((IWorldAccess) this.u.get(i1)).a(i, j, k);
                        }
                    }
                }
            }
        }
    }

    public float m(int i, int j, int k) {
        return this.worldProvider.f[this.getLightLevel(i, j, k)];
    }

    public boolean d() {
        return this.f < 4;
    }

    public MovingObjectPosition a(Vec3D vec3d, Vec3D vec3d1) {
        return this.rayTrace(vec3d, vec3d1, false, false);
    }

    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag) {
        return this.rayTrace(vec3d, vec3d1, flag, false);
    }

    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, boolean flag, boolean flag1) {
        if (!Double.isNaN(vec3d.a) && !Double.isNaN(vec3d.b) && !Double.isNaN(vec3d.c)) {
            if (!Double.isNaN(vec3d1.a) && !Double.isNaN(vec3d1.b) && !Double.isNaN(vec3d1.c)) {
                int i = MathHelper.floor(vec3d1.a);
                int j = MathHelper.floor(vec3d1.b);
                int k = MathHelper.floor(vec3d1.c);
                int l = MathHelper.floor(vec3d.a);
                int i1 = MathHelper.floor(vec3d.b);
                int j1 = MathHelper.floor(vec3d.c);
                int k1 = this.getTypeId(l, i1, j1);
                int l1 = this.getData(l, i1, j1);
                Block block = Block.byId[k1];

                if ((!flag1 || block == null || block.d(this, l, i1, j1) != null) && k1 > 0 && block.a(l1, flag)) {
                    MovingObjectPosition movingobjectposition = block.a(this, l, i1, j1, vec3d, vec3d1);

                    if (movingobjectposition != null) {
                        return movingobjectposition;
                    }
                }

                k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec3d.a) || Double.isNaN(vec3d.b) || Double.isNaN(vec3d.c)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return null;
                    }

                    boolean flag2 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag3 = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag4 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec3d1.a - vec3d.a;
                    double d7 = vec3d1.b - vec3d.b;
                    double d8 = vec3d1.c - vec3d.c;

                    if (flag2) {
                        d3 = (d0 - vec3d.a) / d6;
                    }

                    if (flag3) {
                        d4 = (d1 - vec3d.b) / d7;
                    }

                    if (flag4) {
                        d5 = (d2 - vec3d.c) / d8;
                    }

                    boolean flag5 = false;
                    byte b0;

                    if (d3 < d4 && d3 < d5) {
                        if (i > l) {
                            b0 = 4;
                        } else {
                            b0 = 5;
                        }

                        vec3d.a = d0;
                        vec3d.b += d7 * d3;
                        vec3d.c += d8 * d3;
                    } else if (d4 < d5) {
                        if (j > i1) {
                            b0 = 0;
                        } else {
                            b0 = 1;
                        }

                        vec3d.a += d6 * d4;
                        vec3d.b = d1;
                        vec3d.c += d8 * d4;
                    } else {
                        if (k > j1) {
                            b0 = 2;
                        } else {
                            b0 = 3;
                        }

                        vec3d.a += d6 * d5;
                        vec3d.b += d7 * d5;
                        vec3d.c = d2;
                    }

                    Vec3D vec3d2 = Vec3D.create(vec3d.a, vec3d.b, vec3d.c);

                    l = (int) (vec3d2.a = (double) MathHelper.floor(vec3d.a));
                    if (b0 == 5) {
                        --l;
                        ++vec3d2.a;
                    }

                    i1 = (int) (vec3d2.b = (double) MathHelper.floor(vec3d.b));
                    if (b0 == 1) {
                        --i1;
                        ++vec3d2.b;
                    }

                    j1 = (int) (vec3d2.c = (double) MathHelper.floor(vec3d.c));
                    if (b0 == 3) {
                        --j1;
                        ++vec3d2.c;
                    }

                    int i2 = this.getTypeId(l, i1, j1);
                    int j2 = this.getData(l, i1, j1);
                    Block block1 = Block.byId[i2];

                    if ((!flag1 || block1 == null || block1.d(this, l, i1, j1) != null) && i2 > 0 && block1.a(j2, flag)) {
                        MovingObjectPosition movingobjectposition1 = block1.a(this, l, i1, j1, vec3d, vec3d1);

                        if (movingobjectposition1 != null) {
                            return movingobjectposition1;
                        }
                    }
                }

                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void makeSound(Entity entity, String s, float f, float f1) {
        for (int i = 0; i < this.u.size(); ++i) {
            ((IWorldAccess) this.u.get(i)).a(s, entity.locX, entity.locY - (double) entity.height, entity.locZ, f, f1);
        }
    }

    public void makeSound(double d0, double d1, double d2, String s, float f, float f1) {
        for (int i = 0; i < this.u.size(); ++i) {
            ((IWorldAccess) this.u.get(i)).a(s, d0, d1, d2, f, f1);
        }
    }

    public void a(String s, int i, int j, int k) {
        for (int l = 0; l < this.u.size(); ++l) {
            ((IWorldAccess) this.u.get(l)).a(s, i, j, k);
        }
    }

    public void a(String s, double d0, double d1, double d2, double d3, double d4, double d5) {
        for (int i = 0; i < this.u.size(); ++i) {
            ((IWorldAccess) this.u.get(i)).a(s, d0, d1, d2, d3, d4, d5);
        }
    }

    public boolean a(Entity entity) {
        this.e.add(entity);
        return true;
    }

    public boolean addEntity(Entity entity) {
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);
        boolean flag = false;

        if (entity instanceof EntityHuman) {
            flag = true;
        }

        if (!flag && !this.isChunkLoaded(i, j)) {
            return false;
        } else {
            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                this.players.add(entityhuman);
                this.everyoneSleeping();
            }

            this.getChunkAt(i, j).a(entity);
            this.entityList.add(entity);
            this.c(entity);
            return true;
        }
    }

    protected void c(Entity entity) {
        for (int i = 0; i < this.u.size(); ++i) {
            ((IWorldAccess) this.u.get(i)).a(entity);
        }
    }

    protected void d(Entity entity) {
        for (int i = 0; i < this.u.size(); ++i) {
            ((IWorldAccess) this.u.get(i)).b(entity);
        }
    }

    public void kill(Entity entity) {
        if (entity.passenger != null) {
            entity.passenger.mount((Entity) null);
        }

        if (entity.vehicle != null) {
            entity.mount((Entity) null);
        }

        entity.die();
        if (entity instanceof EntityHuman) {
            this.players.remove((EntityHuman) entity);
            this.everyoneSleeping();
        }
    }

    public void removeEntity(Entity entity) {
        entity.die();
        if (entity instanceof EntityHuman) {
            this.players.remove((EntityHuman) entity);
            this.everyoneSleeping();
        }

        int i = entity.bF;
        int j = entity.bH;

        if (entity.bE && this.isChunkLoaded(i, j)) {
            this.getChunkAt(i, j).b(entity);
        }

        this.entityList.remove(entity);
        this.d(entity);
    }

    public void addIWorldAccess(IWorldAccess iworldaccess) {
        this.u.add(iworldaccess);
    }

    public List getEntities(Entity entity, AxisAlignedBB axisalignedbb) {
        this.J.clear();
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (this.isLoaded(k1, 64, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        Block block = Block.byId[this.getTypeId(k1, i2, l1)];

                        if (block != null) {
                            block.a(this, k1, i2, l1, axisalignedbb, this.J);
                        }
                    }
                }
            }
        }

        double d0 = 0.25D;
        List list = this.b(entity, axisalignedbb.b(d0, d0, d0));

        for (int j2 = 0; j2 < list.size(); ++j2) {
            AxisAlignedBB axisalignedbb1 = ((Entity) list.get(j2)).e_();

            if (axisalignedbb1 != null && axisalignedbb1.a(axisalignedbb)) {
                this.J.add(axisalignedbb1);
            }

            axisalignedbb1 = entity.a_((Entity) list.get(j2));
            if (axisalignedbb1 != null && axisalignedbb1.a(axisalignedbb)) {
                this.J.add(axisalignedbb1);
            }
        }

        return this.J;
    }

    public int a(float f) {
        float f1 = this.b(f);
        float f2 = 1.0F - (MathHelper.cos(f1 * 3.1415927F * 2.0F) * 2.0F + 0.5F);

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float) ((double) f2 * (1.0D - (double) (this.d(f) * 5.0F) / 16.0D));
        f2 = (float) ((double) f2 * (1.0D - (double) (this.c(f) * 5.0F) / 16.0D));
        f2 = 1.0F - f2;
        return (int) (f2 * 11.0F);
    }

    public float b(float f) {
        return this.worldProvider.a(this.worldData.f(), f);
    }

    public int e(int i, int j) {
        Chunk chunk = this.b(i, j);
        int k = 127;

        i &= 15;

        for (j &= 15; k > 0; --k) {
            int l = chunk.getTypeId(i, k, j);
            Material material = l == 0 ? Material.AIR : Block.byId[l].material;

            if (material.isSolid() || material.isLiquid()) {
                return k + 1;
            }
        }

        return -1;
    }

    public int f(int i, int j) {
        Chunk chunk = this.b(i, j);
        int k = 127;

        i &= 15;

        for (j &= 15; k > 0; --k) {
            int l = chunk.getTypeId(i, k, j);

            if (l != 0 && Block.byId[l].material.isSolid()) {
                return k + 1;
            }
        }

        return -1;
    }

    public void c(int i, int j, int k, int l, int i1) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(i, j, k, l);
        byte b0 = 8;

        if (this.a) {
            if (this.a(nextticklistentry.a - b0, nextticklistentry.b - b0, nextticklistentry.c - b0, nextticklistentry.a + b0, nextticklistentry.b + b0, nextticklistentry.c + b0)) {
                int j1 = this.getTypeId(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c);

                if (j1 == nextticklistentry.d && j1 > 0) {
                    Block.byId[j1].a(this, nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, this.random);
                }
            }
        } else {
            if (this.a(i - b0, j - b0, k - b0, i + b0, j + b0, k + b0)) {
                if (l > 0) {
                    nextticklistentry.a((long) i1 + this.worldData.f());
                }

                if (!this.F.contains(nextticklistentry)) {
                    this.F.add(nextticklistentry);
                    this.E.add(nextticklistentry);
                }
            }
        }
    }

    public void cleanUp() {
        int i;
        Entity entity;

        for (i = 0; i < this.e.size(); ++i) {
            entity = (Entity) this.e.get(i);
            entity.p_();
            if (entity.dead) {
                this.e.remove(i--);
            }
        }

        this.entityList.removeAll(this.D);

        int j;
        int k;

        for (i = 0; i < this.D.size(); ++i) {
            entity = (Entity) this.D.get(i);
            j = entity.bF;
            k = entity.bH;
            if (entity.bE && this.isChunkLoaded(j, k)) {
                this.getChunkAt(j, k).b(entity);
            }
        }

        for (i = 0; i < this.D.size(); ++i) {
            this.d((Entity) this.D.get(i));
        }

        this.D.clear();

        for (i = 0; i < this.entityList.size(); ++i) {
            entity = (Entity) this.entityList.get(i);
            if (entity.vehicle != null) {
                if (!entity.vehicle.dead && entity.vehicle.passenger == entity) {
                    continue;
                }

                entity.vehicle.passenger = null;
                entity.vehicle = null;
            }

            if (!entity.dead) {
                this.playerJoinedWorld(entity);
            }

            if (entity.dead) {
                j = entity.bF;
                k = entity.bH;
                if (entity.bE && this.isChunkLoaded(j, k)) {
                    this.getChunkAt(j, k).b(entity);
                }

                this.entityList.remove(i--);
                this.d(entity);
            }
        }

        for (i = 0; i < this.c.size(); ++i) {
            TileEntity tileentity = (TileEntity) this.c.get(i);

            tileentity.g_();
        }
    }

    public void playerJoinedWorld(Entity entity) {
        this.entityJoinedWorld(entity, true);
    }

    public void entityJoinedWorld(Entity entity, boolean flag) {
        int i = MathHelper.floor(entity.locX);
        int j = MathHelper.floor(entity.locZ);
        byte b0 = 32;

        if (!flag || this.a(i - b0, 0, j - b0, i + b0, 128, j + b0)) {
            entity.bn = entity.locX;
            entity.bo = entity.locY;
            entity.bp = entity.locZ;
            entity.lastYaw = entity.yaw;
            entity.lastPitch = entity.pitch;
            if (flag && entity.bE) {
                if (entity.vehicle != null) {
                    entity.B();
                } else {
                    entity.p_();
                }
            }

            if (Double.isNaN(entity.locX) || Double.isInfinite(entity.locX)) {
                entity.locX = entity.bn;
            }

            if (Double.isNaN(entity.locY) || Double.isInfinite(entity.locY)) {
                entity.locY = entity.bo;
            }

            if (Double.isNaN(entity.locZ) || Double.isInfinite(entity.locZ)) {
                entity.locZ = entity.bp;
            }

            if (Double.isNaN((double) entity.pitch) || Double.isInfinite((double) entity.pitch)) {
                entity.pitch = entity.lastPitch;
            }

            if (Double.isNaN((double) entity.yaw) || Double.isInfinite((double) entity.yaw)) {
                entity.yaw = entity.lastYaw;
            }

            int k = MathHelper.floor(entity.locX / 16.0D);
            int l = MathHelper.floor(entity.locY / 16.0D);
            int i1 = MathHelper.floor(entity.locZ / 16.0D);

            if (!entity.bE || entity.bF != k || entity.bG != l || entity.bH != i1) {
                if (entity.bE && this.isChunkLoaded(entity.bF, entity.bH)) {
                    this.getChunkAt(entity.bF, entity.bH).a(entity, entity.bG);
                }

                if (this.isChunkLoaded(k, i1)) {
                    entity.bE = true;
                    this.getChunkAt(k, i1).a(entity);
                } else {
                    entity.bE = false;
                }
            }

            if (flag && entity.bE && entity.passenger != null) {
                if (!entity.passenger.dead && entity.passenger.vehicle == entity) {
                    this.playerJoinedWorld(entity.passenger);
                } else {
                    entity.passenger.vehicle = null;
                    entity.passenger = null;
                }
            }
        }
    }

    public boolean containsEntity(AxisAlignedBB axisalignedbb) {
        List list = this.b((Entity) null, axisalignedbb);

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);

            if (!entity.dead && entity.aH) {
                return false;
            }
        }

        return true;
    }

    public boolean b(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        if (axisalignedbb.a < 0.0D) {
            --i;
        }

        if (axisalignedbb.b < 0.0D) {
            --k;
        }

        if (axisalignedbb.c < 0.0D) {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    Block block = Block.byId[this.getTypeId(k1, l1, i2)];

                    if (block != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean c(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        if (axisalignedbb.a < 0.0D) {
            --i;
        }

        if (axisalignedbb.b < 0.0D) {
            --k;
        }

        if (axisalignedbb.c < 0.0D) {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    Block block = Block.byId[this.getTypeId(k1, l1, i2)];

                    if (block != null && block.material.isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean d(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        if (this.a(i, k, i1, j, l, j1)) {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        int j2 = this.getTypeId(k1, l1, i2);

                        if (j2 == Block.FIRE.id || j2 == Block.LAVA.id || j2 == Block.STATIONARY_LAVA.id) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean a(AxisAlignedBB axisalignedbb, Material material, Entity entity) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        if (!this.a(i, k, i1, j, l, j1)) {
            return false;
        } else {
            boolean flag = false;
            Vec3D vec3d = Vec3D.create(0.0D, 0.0D, 0.0D);

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        Block block = Block.byId[this.getTypeId(k1, l1, i2)];

                        if (block != null && block.material == material) {
                            double d0 = (double) ((float) (l1 + 1) - BlockFluids.c(this.getData(k1, l1, i2)));

                            if ((double) l >= d0) {
                                flag = true;
                                block.a(this, k1, l1, i2, entity, vec3d);
                            }
                        }
                    }
                }
            }

            if (vec3d.c() > 0.0D) {
                vec3d = vec3d.b();
                double d1 = 0.014D;

                entity.motX += vec3d.a * d1;
                entity.motY += vec3d.b * d1;
                entity.motZ += vec3d.c * d1;
            }

            return flag;
        }
    }

    public boolean a(AxisAlignedBB axisalignedbb, Material material) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    Block block = Block.byId[this.getTypeId(k1, l1, i2)];

                    if (block != null && block.material == material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean b(AxisAlignedBB axisalignedbb, Material material) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    Block block = Block.byId[this.getTypeId(k1, l1, i2)];

                    if (block != null && block.material == material) {
                        int j2 = this.getData(k1, l1, i2);
                        double d0 = (double) (l1 + 1);

                        if (j2 < 8) {
                            d0 = (double) (l1 + 1) - (double) j2 / 8.0D;
                        }

                        if (d0 >= axisalignedbb.b) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Explosion a(Entity entity, double d0, double d1, double d2, float f) {
        return this.createExplosion(entity, d0, d1, d2, f, false);
    }

    public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag) {
        Explosion explosion = new Explosion(this, entity, d0, d1, d2, f);

        explosion.a = flag;
        explosion.a();
        explosion.a(true);
        return explosion;
    }

    public float a(Vec3D vec3d, AxisAlignedBB axisalignedbb) {
        double d0 = 1.0D / ((axisalignedbb.d - axisalignedbb.a) * 2.0D + 1.0D);
        double d1 = 1.0D / ((axisalignedbb.e - axisalignedbb.b) * 2.0D + 1.0D);
        double d2 = 1.0D / ((axisalignedbb.f - axisalignedbb.c) * 2.0D + 1.0D);
        int i = 0;
        int j = 0;

        for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                    double d3 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * (double) f;
                    double d4 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * (double) f1;
                    double d5 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * (double) f2;

                    if (this.a(Vec3D.create(d3, d4, d5), vec3d) == null) {
                        ++i;
                    }

                    ++j;
                }
            }
        }

        return (float) i / (float) j;
    }

    public void a(EntityHuman entityhuman, int i, int j, int k, int l) {
        if (l == 0) {
            --j;
        }

        if (l == 1) {
            ++j;
        }

        if (l == 2) {
            --k;
        }

        if (l == 3) {
            ++k;
        }

        if (l == 4) {
            --i;
        }

        if (l == 5) {
            ++i;
        }

        if (this.getTypeId(i, j, k) == Block.FIRE.id) {
            this.a(entityhuman, 1004, i, j, k, 0);
            this.setTypeId(i, j, k, 0);
        }
    }

    public TileEntity getTileEntity(int i, int j, int k) {
        Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

        return chunk != null ? chunk.d(i & 15, j, k & 15) : null;
    }

    public void setTileEntity(int i, int j, int k, TileEntity tileentity) {
        Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

        if (chunk != null) {
            chunk.a(i & 15, j, k & 15, tileentity);
        }
    }

    public void o(int i, int j, int k) {
        Chunk chunk = this.getChunkAt(i >> 4, k >> 4);

        if (chunk != null) {
            chunk.e(i & 15, j, k & 15);
        }
    }

    public boolean p(int i, int j, int k) {
        Block block = Block.byId[this.getTypeId(i, j, k)];

        return block == null ? false : block.a();
    }

    public boolean d(int i, int j, int k) {
        Block block = Block.byId[this.getTypeId(i, j, k)];

        return block == null ? false : block.material.h() && block.b();
    }

    public boolean doLighting() {
        if (this.K >= 50) {
            return false;
        } else {
            ++this.K;

            try {
                int i = 500;

                boolean flag;

                while (this.C.size() > 0) {
                    --i;
                    if (i <= 0) {
                        flag = true;
                        return flag;
                    }

                    ((MetadataChunkBlock) this.C.remove(this.C.size() - 1)).a(this);
                }

                flag = false;
                return flag;
            } finally {
                --this.K;
            }
        }
    }

    public void a(EnumSkyBlock enumskyblock, int i, int j, int k, int l, int i1, int j1) {
        this.a(enumskyblock, i, j, k, l, i1, j1, true);
    }

    public void a(EnumSkyBlock enumskyblock, int i, int j, int k, int l, int i1, int j1, boolean flag) {
        if (!this.worldProvider.e || enumskyblock != EnumSkyBlock.SKY) {
            ++A;
            if (A == 50) {
                --A;
            } else {
                int k1 = (l + i) / 2;
                int l1 = (j1 + k) / 2;

                if (!this.isLoaded(k1, 64, l1)) {
                    --A;
                } else if (!this.b(k1, l1).g()) {
                    int i2 = this.C.size();
                    int j2;

                    if (flag) {
                        j2 = 5;
                        if (j2 > i2) {
                            j2 = i2;
                        }

                        for (int k2 = 0; k2 < j2; ++k2) {
                            MetadataChunkBlock metadatachunkblock = (MetadataChunkBlock) this.C.get(this.C.size() - k2 - 1);

                            if (metadatachunkblock.a == enumskyblock && metadatachunkblock.a(i, j, k, l, i1, j1)) {
                                --A;
                                return;
                            }
                        }
                    }

                    this.C.add(new MetadataChunkBlock(enumskyblock, i, j, k, l, i1, j1));
                    j2 = 1000000;
                    if (this.C.size() > 1000000) {
                        System.out.println("More than " + j2 + " updates, aborting lighting updates");
                        this.C.clear();
                    }

                    --A;
                }
            }
        }
    }

    public void g() {
        int i = this.a(1.0F);

        if (i != this.f) {
            this.f = i;
        }
    }

    public void setSpawnFlags(boolean flag, boolean flag1) {
        this.allowMonsters = flag;
        this.allowAnimals = flag1;
    }

    public void doTick() {
        this.i();
        long i;

        if (this.everyoneDeeplySleeping()) {
            boolean flag = false;

            if (this.allowMonsters && this.spawnMonsters >= 1) {
                flag = SpawnerCreature.a(this, this.players);
            }

            if (!flag) {
                i = this.worldData.f() + 24000L;
                this.worldData.a(i - i % 24000L);
                this.s();
            }
        }

        SpawnerCreature.spawnEntities(this, this.allowMonsters, this.allowAnimals);
        this.chunkProvider.unloadChunks();
        int j = this.a(1.0F);

        if (j != this.f) {
            this.f = j;

            for (int k = 0; k < this.u.size(); ++k) {
                ((IWorldAccess) this.u.get(k)).a();
            }
        }

        i = this.worldData.f() + 1L;
        if (i % (long) this.p == 0L) {
            this.save(false, (IProgressUpdate) null);
        }

        this.worldData.a(i);
        this.a(false);
        this.j();
    }

    private void x() {
        if (this.worldData.l()) {
            this.j = 1.0F;
            if (this.worldData.j()) {
                this.l = 1.0F;
            }
        }
    }

    protected void i() {
        if (!this.worldProvider.e) {
            if (this.m > 0) {
                --this.m;
            }

            int i = this.worldData.k();

            if (i <= 0) {
                if (this.worldData.j()) {
                    this.worldData.b(this.random.nextInt(12000) + 3600);
                } else {
                    this.worldData.b(this.random.nextInt(168000) + 12000);
                }
            } else {
                --i;
                this.worldData.b(i);
                if (i <= 0) {
                    this.worldData.a(!this.worldData.j());
                }
            }

            int j = this.worldData.m();

            if (j <= 0) {
                if (this.worldData.l()) {
                    this.worldData.c(this.random.nextInt(12000) + 12000);
                } else {
                    this.worldData.c(this.random.nextInt(168000) + 12000);
                }
            } else {
                --j;
                this.worldData.c(j);
                if (j <= 0) {
                    this.worldData.b(!this.worldData.l());
                }
            }

            this.i = this.j;
            if (this.worldData.l()) {
                this.j = (float) ((double) this.j + 0.01D);
            } else {
                this.j = (float) ((double) this.j - 0.01D);
            }

            if (this.j < 0.0F) {
                this.j = 0.0F;
            }

            if (this.j > 1.0F) {
                this.j = 1.0F;
            }

            this.k = this.l;
            if (this.worldData.j()) {
                this.l = (float) ((double) this.l + 0.01D);
            } else {
                this.l = (float) ((double) this.l - 0.01D);
            }

            if (this.l < 0.0F) {
                this.l = 0.0F;
            }

            if (this.l > 1.0F) {
                this.l = 1.0F;
            }
        }
    }

    private void y() {
        this.worldData.c(0);
        this.worldData.b(false);
        this.worldData.b(0);
        this.worldData.a(false);
    }

    protected void j() {
        this.N.clear();

        int i;
        int j;
        int k;
        int l;

        for (int i1 = 0; i1 < this.players.size(); ++i1) {
            EntityHuman entityhuman = (EntityHuman) this.players.get(i1);

            i = MathHelper.floor(entityhuman.locX / 16.0D);
            j = MathHelper.floor(entityhuman.locZ / 16.0D);
            byte b0 = 9;

            for (k = -b0; k <= b0; ++k) {
                for (l = -b0; l <= b0; ++l) {
                    this.N.add(new ChunkCoordIntPair(k + i, l + j));
                }
            }
        }

        if (this.O > 0) {
            --this.O;
        }

        Iterator iterator = this.N.iterator();

        while (iterator.hasNext()) {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();

            i = chunkcoordintpair.x * 16;
            j = chunkcoordintpair.z * 16;
            Chunk chunk = this.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z);
            int j1;
            int k1;
            int l1;

            if (this.O == 0) {
                this.g = this.g * 3 + this.h;
                k = this.g >> 2;
                l = k & 15;
                j1 = k >> 8 & 15;
                k1 = k >> 16 & 127;
                l1 = chunk.getTypeId(l, k1, j1);
                l += i;
                j1 += j;
                if (l1 == 0 && this.j(l, k1, j1) <= this.random.nextInt(8) && this.a(EnumSkyBlock.SKY, l, k1, j1) <= 0) {
                    EntityHuman entityhuman1 = this.a((double) l + 0.5D, (double) k1 + 0.5D, (double) j1 + 0.5D, 8.0D);

                    if (entityhuman1 != null && entityhuman1.d((double) l + 0.5D, (double) k1 + 0.5D, (double) j1 + 0.5D) > 4.0D) {
                        this.makeSound((double) l + 0.5D, (double) k1 + 0.5D, (double) j1 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
                        this.O = this.random.nextInt(12000) + 6000;
                    }
                }
            }

            if (this.random.nextInt(100000) == 0 && this.v() && this.u()) {
                this.g = this.g * 3 + this.h;
                k = this.g >> 2;
                l = i + (k & 15);
                j1 = j + (k >> 8 & 15);
                k1 = this.e(l, j1);
                if (this.s(l, k1, j1)) {
                    this.a((Entity) (new EntityWeatherStorm(this, (double) l, (double) k1, (double) j1)));
                    this.m = 2;
                }
            }

            int i2;

            if (this.random.nextInt(16) == 0) {
                this.g = this.g * 3 + this.h;
                k = this.g >> 2;
                l = k & 15;
                j1 = k >> 8 & 15;
                k1 = this.e(l + i, j1 + j);
                if (this.getWorldChunkManager().getBiome(l + i, j1 + j).c() && k1 >= 0 && k1 < 128 && chunk.a(EnumSkyBlock.BLOCK, l, k1, j1) < 10) {
                    l1 = chunk.getTypeId(l, k1 - 1, j1);
                    i2 = chunk.getTypeId(l, k1, j1);
                    if (this.v() && i2 == 0 && Block.SNOW.canPlace(this, l + i, k1, j1 + j) && l1 != 0 && l1 != Block.ICE.id && Block.byId[l1].material.isSolid()) {
                        this.setTypeId(l + i, k1, j1 + j, Block.SNOW.id);
                    }

                    if (l1 == Block.STATIONARY_WATER.id && chunk.getData(l, k1 - 1, j1) == 0) {
                        this.setTypeId(l + i, k1 - 1, j1 + j, Block.ICE.id);
                    }
                }
            }

            for (k = 0; k < 80; ++k) {
                this.g = this.g * 3 + this.h;
                l = this.g >> 2;
                j1 = l & 15;
                k1 = l >> 8 & 15;
                l1 = l >> 16 & 127;
                i2 = chunk.b[j1 << 11 | k1 << 7 | l1] & 255;
                if (Block.n[i2]) {
                    Block.byId[i2].a(this, j1 + i, l1, k1 + j, this.random);
                }
            }
        }
    }

    public boolean a(boolean flag) {
        int i = this.E.size();

        if (i != this.F.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        } else {
            if (i > 1000) {
                i = 1000;
            }

            for (int j = 0; j < i; ++j) {
                NextTickListEntry nextticklistentry = (NextTickListEntry) this.E.first();

                if (!flag && nextticklistentry.e > this.worldData.f()) {
                    break;
                }

                this.E.remove(nextticklistentry);
                this.F.remove(nextticklistentry);
                byte b0 = 8;

                if (this.a(nextticklistentry.a - b0, nextticklistentry.b - b0, nextticklistentry.c - b0, nextticklistentry.a + b0, nextticklistentry.b + b0, nextticklistentry.c + b0)) {
                    int k = this.getTypeId(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c);

                    if (k == nextticklistentry.d && k > 0) {
                        Block.byId[k].a(this, nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, this.random);
                    }
                }
            }

            return this.E.size() != 0;
        }
    }

    public List b(Entity entity, AxisAlignedBB axisalignedbb) {
        this.P.clear();
        int i = MathHelper.floor((axisalignedbb.a - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.d + 2.0D) / 16.0D);
        int k = MathHelper.floor((axisalignedbb.c - 2.0D) / 16.0D);
        int l = MathHelper.floor((axisalignedbb.f + 2.0D) / 16.0D);

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.isChunkLoaded(i1, j1)) {
                    this.getChunkAt(i1, j1).a(entity, axisalignedbb, this.P);
                }
            }
        }

        return this.P;
    }

    public List a(Class oclass, AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor((axisalignedbb.a - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.d + 2.0D) / 16.0D);
        int k = MathHelper.floor((axisalignedbb.c - 2.0D) / 16.0D);
        int l = MathHelper.floor((axisalignedbb.f + 2.0D) / 16.0D);
        ArrayList arraylist = new ArrayList();

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.isChunkLoaded(i1, j1)) {
                    this.getChunkAt(i1, j1).a(oclass, axisalignedbb, arraylist);
                }
            }
        }

        return arraylist;
    }

    public void b(int i, int j, int k, TileEntity tileentity) {
        if (this.isLoaded(i, j, k)) {
            this.b(i, k).f();
        }

        for (int l = 0; l < this.u.size(); ++l) {
            ((IWorldAccess) this.u.get(l)).a(i, j, k, tileentity);
        }
    }

    public int a(Class oclass) {
        int i = 0;

        for (int j = 0; j < this.entityList.size(); ++j) {
            Entity entity = (Entity) this.entityList.get(j);

            if (oclass.isAssignableFrom(entity.getClass())) {
                ++i;
            }
        }

        return i;
    }

    public void a(List list) {
        this.entityList.addAll(list);

        for (int i = 0; i < list.size(); ++i) {
            this.c((Entity) list.get(i));
        }
    }

    public void b(List list) {
        this.D.addAll(list);
    }

    public boolean a(int i, int j, int k, int l, boolean flag, int i1) {
        int j1 = this.getTypeId(j, k, l);
        Block block = Block.byId[j1];
        Block block1 = Block.byId[i];
        AxisAlignedBB axisalignedbb = block1.d(this, j, k, l);

        if (flag) {
            axisalignedbb = null;
        }

        if (axisalignedbb != null && !this.containsEntity(axisalignedbb)) {
            return false;
        } else {
            if (block == Block.WATER || block == Block.STATIONARY_WATER || block == Block.LAVA || block == Block.STATIONARY_LAVA || block == Block.FIRE || block == Block.SNOW) {
                block = null;
            }

            return i > 0 && block == null && block1.canPlace(this, j, k, l, i1);
        }
    }

    public PathEntity findPath(Entity entity, Entity entity1, float f) {
        int i = MathHelper.floor(entity.locX);
        int j = MathHelper.floor(entity.locY);
        int k = MathHelper.floor(entity.locZ);
        int l = (int) (f + 16.0F);
        int i1 = i - l;
        int j1 = j - l;
        int k1 = k - l;
        int l1 = i + l;
        int i2 = j + l;
        int j2 = k + l;
        ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2);

        return (new Pathfinder(chunkcache)).a(entity, entity1, f);
    }

    public PathEntity a(Entity entity, int i, int j, int k, float f) {
        int l = MathHelper.floor(entity.locX);
        int i1 = MathHelper.floor(entity.locY);
        int j1 = MathHelper.floor(entity.locZ);
        int k1 = (int) (f + 8.0F);
        int l1 = l - k1;
        int i2 = i1 - k1;
        int j2 = j1 - k1;
        int k2 = l + k1;
        int l2 = i1 + k1;
        int i3 = j1 + k1;
        ChunkCache chunkcache = new ChunkCache(this, l1, i2, j2, k2, l2, i3);

        return (new Pathfinder(chunkcache)).a(entity, i, j, k, f);
    }

    public boolean isBlockFacePowered(int i, int j, int k, int l) {
        int i1 = this.getTypeId(i, j, k);

        return i1 == 0 ? false : Block.byId[i1].c(this, i, j, k, l);
    }

    public boolean isBlockPowered(int i, int j, int k) {
        return this.isBlockFacePowered(i, j - 1, k, 0) ? true : (this.isBlockFacePowered(i, j + 1, k, 1) ? true : (this.isBlockFacePowered(i, j, k - 1, 2) ? true : (this.isBlockFacePowered(i, j, k + 1, 3) ? true : (this.isBlockFacePowered(i - 1, j, k, 4) ? true : this.isBlockFacePowered(i + 1, j, k, 5)))));
    }

    public boolean isBlockFaceIndirectlyPowered(int i, int j, int k, int l) {
        if (this.d(i, j, k)) {
            return this.isBlockPowered(i, j, k);
        } else {
            int i1 = this.getTypeId(i, j, k);

            return i1 == 0 ? false : Block.byId[i1].a(this, i, j, k, l);
        }
    }

    public boolean isBlockIndirectlyPowered(int i, int j, int k) {
        return this.isBlockFaceIndirectlyPowered(i, j - 1, k, 0) ? true : (this.isBlockFaceIndirectlyPowered(i, j + 1, k, 1) ? true : (this.isBlockFaceIndirectlyPowered(i, j, k - 1, 2) ? true : (this.isBlockFaceIndirectlyPowered(i, j, k + 1, 3) ? true : (this.isBlockFaceIndirectlyPowered(i - 1, j, k, 4) ? true : this.isBlockFaceIndirectlyPowered(i + 1, j, k, 5)))));
    }

    public EntityHuman a(Entity entity, double d0) {
        return this.a(entity.locX, entity.locY, entity.locZ, d0);
    }

    public EntityHuman a(double d0, double d1, double d2, double d3) {
        double d4 = -1.0D;
        EntityHuman entityhuman = null;

        for (int i = 0; i < this.players.size(); ++i) {
            EntityHuman entityhuman1 = (EntityHuman) this.players.get(i);
            double d5 = entityhuman1.d(d0, d1, d2);

            if ((d3 < 0.0D || d5 < d3 * d3) && (d4 == -1.0D || d5 < d4)) {
                d4 = d5;
                entityhuman = entityhuman1;
            }
        }

        return entityhuman;
    }

    public EntityHuman a(String s) {
        for (int i = 0; i < this.players.size(); ++i) {
            if (s.equals(((EntityHuman) this.players.get(i)).name)) {
                return (EntityHuman) this.players.get(i);
            }
        }

        return null;
    }

    public byte[] c(int i, int j, int k, int l, int i1, int j1) {
        byte[] abyte = new byte[l * i1 * j1 * 5 / 2];
        int k1 = i >> 4;
        int l1 = k >> 4;
        int i2 = i + l - 1 >> 4;
        int j2 = k + j1 - 1 >> 4;
        int k2 = 0;
        int l2 = j;
        int i3 = j + i1;

        if (j < 0) {
            l2 = 0;
        }

        if (i3 > 128) {
            i3 = 128;
        }

        for (int j3 = k1; j3 <= i2; ++j3) {
            int k3 = i - j3 * 16;
            int l3 = i + l - j3 * 16;

            if (k3 < 0) {
                k3 = 0;
            }

            if (l3 > 16) {
                l3 = 16;
            }

            for (int i4 = l1; i4 <= j2; ++i4) {
                int j4 = k - i4 * 16;
                int k4 = k + j1 - i4 * 16;

                if (j4 < 0) {
                    j4 = 0;
                }

                if (k4 > 16) {
                    k4 = 16;
                }

                k2 = this.getChunkAt(j3, i4).a(abyte, k3, l2, j4, l3, i3, k4, k2);
            }
        }

        return abyte;
    }

    public void k() {
        this.w.b();
    }

    public void setTime(long i) {
        this.worldData.a(i);
    }

    public long getSeed() {
        return this.worldData.b();
    }

    public long getTime() {
        return this.worldData.f();
    }

    public ChunkCoordinates getSpawn() {
        return new ChunkCoordinates(this.worldData.c(), this.worldData.d(), this.worldData.e());
    }

    public boolean a(EntityHuman entityhuman, int i, int j, int k) {
        return true;
    }

    public void a(Entity entity, byte b0) {}

    public IChunkProvider o() {
        return this.chunkProvider;
    }

    public void d(int i, int j, int k, int l, int i1) {
        int j1 = this.getTypeId(i, j, k);

        if (j1 > 0) {
            Block.byId[j1].a(this, i, j, k, l, i1);
        }
    }

    public IDataManager p() {
        return this.w;
    }

    public WorldData q() {
        return this.worldData;
    }

    public void everyoneSleeping() {
        this.I = !this.players.isEmpty();
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            if (!entityhuman.isSleeping()) {
                this.I = false;
                break;
            }
        }
    }

    protected void s() {
        this.I = false;
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            if (entityhuman.isSleeping()) {
                entityhuman.a(false, false, true);
            }
        }

        this.y();
    }

    public boolean everyoneDeeplySleeping() {
        if (this.I && !this.isStatic) {
            Iterator iterator = this.players.iterator();

            EntityHuman entityhuman;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entityhuman = (EntityHuman) iterator.next();
            } while (entityhuman.isDeeplySleeping());

            return false;
        } else {
            return false;
        }
    }

    public float c(float f) {
        return (this.k + (this.l - this.k) * f) * this.d(f);
    }

    public float d(float f) {
        return this.i + (this.j - this.i) * f;
    }

    public boolean u() {
        return (double) this.c(1.0F) > 0.9D;
    }

    public boolean v() {
        return (double) this.d(1.0F) > 0.2D;
    }

    public boolean s(int i, int j, int k) {
        if (!this.v()) {
            return false;
        } else if (!this.isChunkLoaded(i, j, k)) {
            return false;
        } else if (this.e(i, k) > j) {
            return false;
        } else {
            BiomeBase biomebase = this.getWorldChunkManager().getBiome(i, k);

            return biomebase.c() ? false : biomebase.d();
        }
    }

    public void a(String s, WorldMapBase worldmapbase) {
        this.z.a(s, worldmapbase);
    }

    public WorldMapBase a(Class oclass, String s) {
        return this.z.a(oclass, s);
    }

    public int b(String s) {
        return this.z.a(s);
    }

    public void e(int i, int j, int k, int l, int i1) {
        this.a((EntityHuman) null, i, j, k, l, i1);
    }

    public void a(EntityHuman entityhuman, int i, int j, int k, int l, int i1) {
        for (int j1 = 0; j1 < this.u.size(); ++j1) {
            ((IWorldAccess) this.u.get(j1)).a(entityhuman, i, j, k, l, i1);
        }
    }
}
