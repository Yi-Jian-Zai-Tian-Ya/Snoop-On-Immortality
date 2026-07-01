package soi.client.model.player;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class ModelJsonBase implements IModel
{
    private final ResourceLocation model;
    private final ResourceLocation texture;

    public ModelJsonBase(ResourceLocation model, ResourceLocation texture)
    {
        this.model = model;
        this.texture = texture;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.singletonList(model);
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return Collections.singletonList(texture);
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        try
        {
            IModel base = ModelLoaderRegistry.getModelOrLogError(model, "Could not load SOI bag model");
            IModel textureModel = base.retexture(ImmutableMap.of("bag", texture.toString()));

            return textureModel.bake(state, format, bakedTextureGetter);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to bake bag model", e);
        }
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }
}