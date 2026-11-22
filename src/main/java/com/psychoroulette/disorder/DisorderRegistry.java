package com.psychoroulette.disorder;

import com.psychoroulette.disorder.impl.*;

import java.util.*;

public class DisorderRegistry {
    private static final Map<String, Disorder> REGISTRY = new LinkedHashMap<>();
    private static final List<Disorder> LIST = new ArrayList<>();

    public static void register(Disorder disorder) {
        REGISTRY.put(disorder.getId(), disorder);
        LIST.add(disorder);
    }

    public static void registerAll() {
        REGISTRY.clear();
        LIST.clear();

        // 20 distinct mental disorders
        register(new PyromaniaDisorder());
        register(new MegalomaniaDisorder());
        register(new KleptomaniaDisorder());
        register(new TouretteDisorder());
        register(new NarcolepsyDisorder());
        register(new AgoraphobiaDisorder());
        register(new ClaustrophobiaDisorder());
        register(new HallucinationsDisorder());
        register(new SociophobiaDisorder());
        register(new AcrophobiaDisorder());
        register(new HydrophobiaDisorder());
        register(new NyctophobiaDisorder());
        register(new OcdDisorder());
        register(new BipolarDisorder());
        register(new ParanoiaDisorder());
        register(new HyperphagiaDisorder());
        register(new HoardingDisorder());
        register(new AmnesiaDisorder());
        register(new DromomaniaDisorder());
        register(new BerserkPsychosisDisorder());
    }

    public static Disorder get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<String> getIds() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }

    public static List<Disorder> getAll() {
        return Collections.unmodifiableList(LIST);
    }

    public static Disorder getRandom(Random random, List<Disorder> exclude) {
        List<Disorder> pool = new ArrayList<>();
        for (Disorder d : LIST) {
            if (!exclude.contains(d)) {
                pool.add(d);
            }
        }
        if (pool.isEmpty()) {
            return LIST.get(random.nextInt(LIST.size()));
        }
        return pool.get(random.nextInt(pool.size()));
    }
}
