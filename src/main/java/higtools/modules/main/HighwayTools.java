package higtools.modules.main;

import higtools.HIGTools;
import higtools.modules.borers.*;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoLog;
import meteordevelopment.meteorclient.systems.modules.movement.SafeWalk;
import meteordevelopment.meteorclient.systems.modules.player.Rotation;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.world.LiquidFiller;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;

import java.util.List;

public class HighwayTools extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("profile")
        .description("Which highway profile to use.")
        .defaultValue(Mode.HighwayBuilding)
        .build()
    );

    public final Setting<Boolean> axistoggle = sgGeneral.add(new BoolSetting.Builder()
        .name("axis-toggle")
        .description("Automatically disables HighwayTools when you reach an axis. Useful when digging ring roads.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> radius = sgGeneral.add(new DoubleSetting.Builder()
        .name("axis-radius")
        .description("The radius from the axis to toggle HighwayTools.")
        .defaultValue(3)
        .min(0)
        .sliderRange(0, 15)
        .visible(axistoggle::get)
        .build()
    );

    public final Setting<Boolean> picktoggle = sgGeneral.add(new BoolSetting.Builder()
        .name("pickaxe-toggle")
        .description("Automatically disables HighwayTools when you run out of pickaxes.")
        .defaultValue(true)
        .build()
    );

    private final List<Class<? extends Module>> commonClasses = List.of(
        AutoLog.class,
        FreeLook.class,
        HandManager.class,
        InvManager.class,
        LiquidFiller.class,
        Rotation.class,
        SafeWalk.class,
        ScaffoldPlus.class
    );

    private double originX;
    private double originZ;
    private double originRatio;

    public HighwayTools() {
        super(HIGTools.MAIN, "highway-tools", "Digs, builds and repairs highways automatically.");
    }

    @Override
    public void onActivate() {
        originX = Math.abs(mc.player.getX());
        originZ = Math.abs(mc.player.getZ());
        // This is to know if a player is on a diagonal axis
        originRatio = Math.abs(originX - originZ);

        Modules modules = Modules.get();

        switch (mode.get()) {
            case HighwayBuilding -> {
                modules.get(HighwayBuilderPlus.class).toggle();
                commonClasses.forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case AxisDigging -> {
                modules.get(AxisBorer.class).toggle();
                commonClasses.forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case NegNegDigging -> {
                modules.get(NegNegBorer.class).toggle();
                modules.get(AutoCenter.class).toggle();
                commonClasses.forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case NegPosDigging -> {
                modules.get(NegPosBorer.class).toggle();
                modules.get(AutoCenter.class).toggle();
                commonClasses.forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case PosNegDigging -> {
                modules.get(PosNegBorer.class).toggle();
                modules.get(AutoCenter.class).toggle();
                commonClasses.forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case PosPosDigging -> {
                modules.get(PosPosBorer.class).toggle();
                modules.get(AutoCenter.class).toggle();
                commonClasses.forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case RingRoadDigging -> {
                modules.get(RingRoadBorer.class).toggle();
                commonClasses.forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
        }
    }

    @Override
    public void onDeactivate() {
        Modules modules = Modules.get();

        switch (mode.get()) {
            case HighwayBuilding -> {
                if (modules.get(HighwayBuilderPlus.class).isActive()) modules.get(HighwayBuilderPlus.class).toggle();
                commonClasses.stream()
                    .filter(moduleClass -> modules.get(moduleClass).isActive())
                    .forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case AxisDigging -> {
                if (modules.get(AxisBorer.class).isActive()) modules.get(AxisBorer.class).toggle();
                commonClasses.stream()
                    .filter(moduleClass -> modules.get(moduleClass).isActive())
                    .forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case NegNegDigging -> {
                if (modules.get(NegNegBorer.class).isActive()) modules.get(NegNegBorer.class).toggle();
                if (modules.get(AutoCenter.class).isActive()) modules.get(AutoCenter.class).toggle();
                commonClasses.stream()
                    .filter(moduleClass -> modules.get(moduleClass).isActive())
                    .forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case NegPosDigging -> {
                if (modules.get(NegPosBorer.class).isActive()) modules.get(NegPosBorer.class).toggle();
                if (modules.get(AutoCenter.class).isActive()) modules.get(AutoCenter.class).toggle();
                commonClasses.stream()
                    .filter(moduleClass -> modules.get(moduleClass).isActive())
                    .forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case PosNegDigging -> {
                if (modules.get(PosNegBorer.class).isActive()) modules.get(PosNegBorer.class).toggle();
                if (modules.get(AutoCenter.class).isActive()) modules.get(AutoCenter.class).toggle();
                commonClasses.stream()
                    .filter(moduleClass -> modules.get(moduleClass).isActive())
                    .forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case PosPosDigging -> {
                if (modules.get(PosPosBorer.class).isActive()) modules.get(PosPosBorer.class).toggle();
                if (modules.get(AutoCenter.class).isActive()) modules.get(AutoCenter.class).toggle();
                commonClasses.stream()
                    .filter(moduleClass -> modules.get(moduleClass).isActive())
                    .forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
            case RingRoadDigging -> {
                if (modules.get(RingRoadBorer.class).isActive()) modules.get(RingRoadBorer.class).toggle();
                commonClasses.stream()
                    .filter(moduleClass -> modules.get(moduleClass).isActive())
                    .forEach(moduleClass -> modules.get(moduleClass).toggle());
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (picktoggle.get()) {
            FindItemResult pickaxe = InvUtils.find(itemStack -> itemStack.getItem() == Items.DIAMOND_PICKAXE || itemStack.getItem() == Items.NETHERITE_PICKAXE);

            if (!pickaxe.found()) {
                error("No pickaxe found, disabling HighwayTools.");
                toggle();
            }
        }

        if (axistoggle.get()) {
            if (originX >= radius.get() && originZ >= radius.get() && originRatio >= 4) {
                // Only run expensive checks if requirements above are met
                if (Math.abs(mc.player.getX()) <= radius.get() || Math.abs(mc.player.getZ()) <= radius.get()
                    || Math.abs(Math.abs(mc.player.getX()) - Math.abs(mc.player.getZ())) <= radius.get() + 4) {

                    info("Reached axis, disabling HighwayTools.");
                    toggle();
                }
            }
        }
    }

    public enum Mode {
        HighwayBuilding,
        AxisDigging,
        NegNegDigging,
        NegPosDigging,
        PosNegDigging,
        PosPosDigging,
        RingRoadDigging
    }
}
