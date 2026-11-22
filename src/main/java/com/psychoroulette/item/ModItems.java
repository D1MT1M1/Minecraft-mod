package com.psychoroulette.item;

import com.psychoroulette.PsychoRouletteMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static Item MIRACLE_PILL;
    public static Item REROLL_TOKEN;

    private static Item registerItem(String name, Function<Item.Properties, Item> factory) {
        Identifier id = PsychoRouletteMod.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Item item = factory.apply(new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void register() {
        MIRACLE_PILL = registerItem("miracle_pill", properties -> new MiraclePillItem(properties.stacksTo(16)));
        REROLL_TOKEN = registerItem("reroll_token", properties -> new RerollTokenItem(properties.stacksTo(16)));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            entries.accept(MIRACLE_PILL);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(REROLL_TOKEN);
        });
    }
}
