package com.fantasticsource.dynamicstealth.common.items;

import com.fantasticsource.mctools.cliententity.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.Vec3d;

public class CameraHandler
{
    protected static final Camera CAMERA = Camera.getCamera();
    protected static final Minecraft MC = Minecraft.getMinecraft();
    protected static final GameSettings GS = MC.gameSettings;

    public static void setTarget(Vec3d position, float yaw, float pitch, int mode)
    {
        CAMERA.activate(Minecraft.getMinecraft().world, position.x, position.y, position.z, yaw, pitch, GS.thirdPersonView);
    }

    public static void deactivate()
    {
        CAMERA.deactivate();
    }
}
