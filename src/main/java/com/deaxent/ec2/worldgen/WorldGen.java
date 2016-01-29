package com.deaxent.ec2.worldgen;

import com.deaxent.ec2.blocks.MBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGen implements IWorldGenerator {

    private WorldGenerator gen_copper, gen_tin, gen_silver, gen_phenium;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        switch(world.provider.getDimensionId()) {
            case 0:
                this.runGenerator(this.gen_copper, world, random, chunkX, chunkZ, 15, 10, 55);
                this.runGenerator(this.gen_tin, world, random, chunkX, chunkZ, 15, 35, 64);
                this.runGenerator(this.gen_silver, world, random, chunkX, chunkZ, 15, 0, 35);
                this.runGenerator(this.gen_phenium, world, random, chunkX, chunkZ, 5, 0, 16);
                break;
            case 1:

                break;
            case -1:

                break;
        }
    }

    public WorldGen() {
        this.gen_copper = new WorldGenMinable(MBlock.ore_copper.getDefaultState(), 8);
        this.gen_tin = new WorldGenMinable(MBlock.ore_tin.getDefaultState(), 8);
        this.gen_silver = new WorldGenMinable(MBlock.ore_silver.getDefaultState(), 8);
        this.gen_phenium = new WorldGenMinable(MBlock.ore_phenium.getDefaultState(), 8);
    }

    private void runGenerator(WorldGenerator generator, World world, Random rand, int chunk_x, int chunk_z, int chanceToSpawn, int minHeight, int maxHeight) {
        if(minHeight < 0 || maxHeight > 256 || minHeight > maxHeight) {
            throw new IllegalArgumentException("Illegal Height arguments for WorldGenerator");
        }

        int heightDiff = maxHeight - minHeight + 1;
        for(int i = 0; i < chanceToSpawn; i++) {
            int x = chunk_x * 16 + rand.nextInt(16);
            int y = minHeight + rand.nextInt(heightDiff);
            int z = chunk_z * 16 + rand.nextInt(16);
            generator.generate(world, rand, new BlockPos(x, y, z));
        }
    }
}
