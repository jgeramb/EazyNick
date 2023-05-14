package com.justixdev.eazynick.utilities.mojang;

import com.justixdev.eazynick.utilities.mojang.gameprofiles.GameProfileBuilder;
import com.justixdev.eazynick.utilities.mojang.gameprofiles.GameProfileBuilder_1_7;
import com.justixdev.eazynick.utilities.mojang.gameprofiles.GameProfileBuilder_1_8_R1;
import com.justixdev.eazynick.utilities.mojang.uuidfetching.UUIDFetcher;
import com.justixdev.eazynick.utilities.mojang.uuidfetching.UUIDFetcher_1_7;
import com.justixdev.eazynick.utilities.mojang.uuidfetching.UUIDFetcher_1_8_R1;

import java.io.IOException;
import java.util.UUID;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

public class MojangAPI {

    public static Object getGameProfile(String name) throws IOException {
        return getGameProfile(getUniqueId(name));
    }

    public static Object getGameProfile(UUID uniqueId) throws IOException {
        if(NMS_VERSION.equals("v1_7_R4"))
            return GameProfileBuilder_1_7.fetch(uniqueId);
        else if(NMS_VERSION.equals("v1_8_R1"))
            return GameProfileBuilder_1_8_R1.fetch(uniqueId);

        return GameProfileBuilder.fetch(uniqueId);
    }

    public static UUID getUniqueId(String name) {
        if(NMS_VERSION.equals("v1_7_R4"))
            return UUIDFetcher_1_7.getUUID(name);
        else if(NMS_VERSION.equals("v1_8_R1"))
            return UUIDFetcher_1_8_R1.getUUID(name);

        return UUIDFetcher.getUUID(name);
    }

    public static String getName(UUID uniqueId) {
        if(NMS_VERSION.equals("v1_7_R4"))
            return UUIDFetcher_1_7.getName("", uniqueId);
        else if(NMS_VERSION.equals("v1_8_R1"))
            return UUIDFetcher_1_8_R1.getName("", uniqueId);

        return UUIDFetcher.getName("", uniqueId);
    }

    public static String correctName(String rawName) {
        return getName(getUniqueId(rawName));
    }

}
