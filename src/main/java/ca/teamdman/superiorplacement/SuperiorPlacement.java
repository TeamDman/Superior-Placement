package ca.teamdman.superiorplacement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import static ca.teamdman.superiorplacement.SuperiorPlacement.MOD_ID;

@Mod(MOD_ID)
public class SuperiorPlacement {
	public static final String    MOD_ID = "superiorplacement";
	private             Direction lastDir;
	private             BlockPos  lastPos;

	public SuperiorPlacement() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
			MinecraftForge.EVENT_BUS.register(this);
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
		});
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		if (Minecraft.getInstance().player == null) return;
		if (Config.CREATIVE_ONLY.get() && !Minecraft.getInstance().player.isCreative()) return;
		if (!(Minecraft.getInstance().hitResult instanceof BlockHitResult hit)) return;
		var pos = hit.getBlockPos();
		if (Minecraft.getInstance().rightClickDelay > 0 && !pos.equals(lastPos) && (lastPos == null || !pos.equals(lastPos.relative(lastDir)))) {
			Minecraft.getInstance().rightClickDelay = 0;
		} else if (Config.FORCE_NEW_LOCATION.get() && Minecraft.getInstance().rightClickDelay == 0 && pos.equals(lastPos) && hit.getDirection().equals(lastDir)) {
			Minecraft.getInstance().rightClickDelay = 4;
		}
		lastPos = pos;
		lastDir = hit.getDirection();
	}

	public static class Config {
		public static final ForgeConfigSpec              SPEC;
		public static final ForgeConfigSpec.BooleanValue FORCE_NEW_LOCATION;
		public static final ForgeConfigSpec.BooleanValue CREATIVE_ONLY;
		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		static {
			FORCE_NEW_LOCATION = BUILDER.define("force_new_location", false);
			CREATIVE_ONLY = BUILDER.define("creative_only", false);
			SPEC = BUILDER.build();
		}
	}
}
