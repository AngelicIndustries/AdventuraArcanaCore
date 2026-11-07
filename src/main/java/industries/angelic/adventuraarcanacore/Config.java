package industries.angelic.adventuraarcanacore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class Config {

    private static Configuration configuration;
    public static boolean debugMode;
    public static boolean enableCompartments = true;
    public static boolean enableWorktable = true;
    public static boolean pmpIvyForVines = true;
    public static boolean enableWildFood = true;
    public static boolean foodLinks = true;
    public static boolean wildFoodInBiomic = true;
    public static boolean beeNestsInBiomic = true;
    public static boolean enableBambooShoots = true;
    public static boolean growthcraftGrapes = true;
    public static boolean pmpBambooInBiomic = true;
    public static boolean madakeInBiomic = true;
    public static boolean bambooShootsInBiomic = true;
    public static boolean floraInBiomic = true;
    public static boolean leafDecorInBiomic = true;
    public static boolean netherFoodInBiomic = true;
    public static boolean decorInBiomic = true;
    public static boolean caveMoods;
    public static boolean wolfHowls = true;
    public static boolean extraBaubleSlots = true;
    public static boolean netherliciousDuplicates = true;
    public static boolean dropBrokenRecipes = true;
    public static double battleTowerChance = 1;
    public static String[] shaderBlockAliases = { "dimdoors:fadebloom=minecraft:red_flower" };
    public static String[] torchLighters = { "Natura:natura.flintandblaze" };
    public static int torchRainDelay = 100;
    public static boolean torchesBurnUnloaded = true;
    public static boolean glowstonePaste = true;
    public static boolean ambrosiumLighter = true;
    /** The torches that burn out, by the ID of their config category, with their name for its comment. */
    public static final String[][] TORCH_KINDS = { { "torch", "Realistic Torches' torch" },
        { "soul_torch", "Netherlicious' soul torch" }, { "foxfire_torch", "Netherlicious' foxfire torch" },
        { "shadow_torch", "Netherlicious' shadow torch" }, { "bone_torch", "Netherlicious' bone torch" },
        { "soul_bone_torch", "Netherlicious' soul bone torch" },
        { "foxfire_bone_torch", "Netherlicious' foxfire bone torch" },
        { "shadow_bone_torch", "Netherlicious' shadow bone torch" },
        { "ambrosium_torch", "The Aether's ambrosium torch" }, { "hellfire_torch", "The Aether's hellfire torch" },
        { "ethereal_torch", "The Aether's ethereal torch, of cold fire" },
        { "ambrosium_bone_torch", "AAC's ambrosium bone torch" },
        { "hellfire_bone_torch", "AAC's hellfire bone torch" },
        { "ethereal_bone_torch", "AAC's ethereal bone torch, of cold fire" } };
    public static final Map<String, Integer> torchBurnout = new HashMap<>();
    public static final Map<String, Boolean> torchRain = new HashMap<>();

    private Config() {}

    public static final class Categories {

        public static final String GENERAL = "general";
        /** Read before preInit by core.PatchSwitches, which selects the mixins. */
        public static final String PATCHES = industries.angelic.adventuraarcanacore.core.PatchSwitches.CATEGORY;
        public static final String BIOMIC = "biomic";
        public static final String SOUNDS = "sounds";
        public static final String TORCHES = "torches";

        private Categories() {}
    }

    public static void loadConfig(File file) {
        configuration = new Configuration(file);
        configuration.load();
        sync();
    }

    public static void sync() {
        configuration.setCategoryLanguageKey(Categories.GENERAL, AdventuraArcanaCore.MODID + ".config.general");
        debugMode = configuration.getBoolean(
            "debugMode",
            Categories.GENERAL,
            false,
            "Enable debug logging.",
            AdventuraArcanaCore.MODID + ".config.debugMode");
        enableCompartments = configuration
            .get(
                Categories.GENERAL,
                "enableCompartments",
                true,
                "Enable compartment blocks and recipes. Requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableCompartments")
            .setRequiresMcRestart(true)
            .getBoolean();
        enableWorktable = configuration
            .get(
                Categories.GENERAL,
                "enableWorktable",
                true,
                "Enable the worktable and its recipe. Requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableWorktable")
            .setRequiresMcRestart(true)
            .getBoolean();
        enableWildFood = configuration.get(
            Categories.GENERAL,
            "enableWildFood",
            true,
            "Wild berry bushes, fruit pods and fruiting vines for HarvestCraft's crops that have no natural form of their own. Needs HarvestCraft; requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableWildFood")
            .setRequiresMcRestart(true)
            .getBoolean();
        foodLinks = configuration.get(
            Categories.GENERAL,
            "foodLinks",
            true,
            "Other mods' wild plants drop HarvestCraft food: BOP's rice, sunflowers' seeds, PMP's berries and lettuce. Needs HarvestCraft; requires a restart.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.foodLinks")
            .setRequiresMcRestart(true)
            .getBoolean();
        enableBambooShoots = configuration.get(
            Categories.GENERAL,
            "enableBambooShoots",
            true,
            "A shoot for each bamboo species, in place of HarvestCraft's one shoot crop: picked when grown, it turns into its bamboo if left. Needs HarvestCraft; the bamboos need Plant Mega Pack, Madake needs Biomic. Requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.enableBambooShoots")
            .setRequiresMcRestart(true)
            .getBoolean();
        growthcraftGrapes = configuration.get(
            Categories.GENERAL,
            "growthcraftGrapes",
            true,
            "Growthcraft's grape is the only grape: the wild grape vines give Growthcraft's grapes and grape seeds, and NEI hides HarvestCraft's grape, grape seeds and grape crop (HarvestCraft's recipes take Growthcraft's grapes). Needs Growthcraft Grapes; requires a restart.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.growthcraftGrapes")
            .setRequiresMcRestart(true)
            .getBoolean();
        extraBaubleSlots = configuration.get(
            Categories.GENERAL,
            "extraBaubleSlots",
            true,
            "Baubles Expanded gets at least 4 ring, 2 amulet and 3 charm slots, so the Aether's accessories and Uralte Zauber's artefacts don't compete for them. Needs Baubles Expanded; requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.extraBaubleSlots")
            .setRequiresMcRestart(true)
            .getBoolean();
        netherliciousDuplicates = configuration.get(
            Categories.GENERAL,
            "netherliciousDuplicates",
            true,
            "Et Futurum's Nether Update blocks are the only ones: Netherlicious' copies (the same English name) leave the creative tabs and NEI, lose their recipes and drop as Et Futurum's, and Netherlicious' other recipes take Et Futurum's. Needs Netherlicious and Et Futurum; requires a restart; use the same setting on client and server.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.netherliciousDuplicates")
            .setRequiresMcRestart(true)
            .getBoolean();
        dropBrokenRecipes = configuration.get(
            Categories.GENERAL,
            "dropBrokenRecipes",
            true,
            "Drops crafting recipes that hold items no mod registered, which can't be crafted and crash NEI and the Netherlicious duplicate pass. BiblioCraft leaves some behind when its Reading Glasses are switched off. Requires a restart.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.dropBrokenRecipes")
            .setRequiresMcRestart(true)
            .getBoolean();
        shaderBlockAliases = configuration.get(
            Categories.GENERAL,
            "shaderBlockAliases",
            new String[] { "dimdoors:fadebloom=minecraft:red_flower" },
            "Modded blocks that shader packs treat like a vanilla block, as modded=vanilla registry names: they take its shader IDs, so a modded flower waves and is shaded like a flower. Needs the angelicaShaderBlockAliases patch; applies when shaders load.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.shaderBlockAliases")
            .getStringList();
        battleTowerChance = configuration.get(
            Categories.GENERAL,
            "battleTowerChance",
            1.0,
            "The chance that a newly generated chunk is tried as a Battle Tower site at all, on top of Battle Towers' own minimum distance between towers. 1 keeps its spacing (a tower as soon as a chunk is far enough from the others, so nearly a grid); lower values spread them irregularly and further apart. Needs the battleTowersTallWorlds patch.",
            0.0,
            1.0)
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.battleTowerChance")
            .getDouble();
        configuration.setCategoryLanguageKey(Categories.PATCHES, AdventuraArcanaCore.MODID + ".config.patches");
        configuration.setCategoryComment(
            Categories.PATCHES,
            "Patches to other mods, each applied only with its mod. Changes need a restart.");
        configuration.setCategoryRequiresMcRestart(Categories.PATCHES, true);
        patch(
            "resumePausedSounds",
            "Resume only paused sounds, preventing menu clicks from replaying on Return to Game.");
        patch(
            "growthcraftWithoutForestry",
            "Growthcraft loads without Forestry: its Forestry integration becomes optional.");
        patch(
            "toasterBread",
            "Cooking for Blockheads' toaster makes Pam's toast and corn bread, salted and abyssal toast. Hides the duplicate toast and removes other recipes that produce toast. Needs Cooking for Blockheads and HarvestCraft; use the same setting on client and server.");
        patch(
            "kitchenSneakTake",
            "Sneak-using Cooking for Blockheads' oven top with an empty hand takes the cookware there, and sneak-using its toaster takes the bread back out and stops it. Use the same setting on client and server.");
        patch(
            "toasterLever",
            "Cooking for Blockheads' toaster lever goes down when it starts and pops up when it stops, with a sound for each. The toaster saves and shares whether it runs, so everyone sees it and it keeps toasting after a reload.");
        patch(
            "toasterNoEating",
            "Putting bread into Cooking for Blockheads' toaster no longer also starts eating it when you are hungry, nor does using a full toaster with bread it can toast. Use the same setting on client and server.");
        patch(
            "toasterSaltedBread",
            "SaltyMod's salted bread is drawn in Cooking for Blockheads' toaster without the salt particles beside the loaf, which hung in the air next to the slot. Client only.");
        patch(
            "recipeListScroll",
            "The mouse wheel scrolls Cooking for Blockheads' recipe list only over the list or its scrollbar, not anywhere on the screen.");
        patch("noNaturaClouds", "Natura generates no clouds in any dimension.");
        patch(
            "projectZuluGamerules",
            "Project Zulu no longer sets keepInventory and its dropInventory, dropHotbar, dropArmor and dropXP gamerules back to its config defaults whenever a world loads, which undid /gamerule changes. It only adds its own gamerules, with those defaults, to a world that lacks them.");
        patch(
            "wildCropsStay",
            "HarvestCraft's and Harvest the Nether's crops stay where they grow wild (grass, sand, netherrack); sowing still needs farmland.");
        patch("pmpHarvestCraftBerries", "Plant Mega Pack's berry bushes give HarvestCraft's berries (with foodLinks).");
        patch(
            "realisticTorchesLighters",
            "More items light Realistic Torches' torches, which otherwise only take the vanilla flint and steel: the torchLighters (Natura's flint and blaze) with a normal flame, Netherlicious' soul, foxfire and shadow flint and steels with theirs, the Aether's Zanite and Cinerarium with hellfire, its Zanite and Cyro with cold fire and AAC's Zanite and Ambrosium with ambrosium flame.");
        patch(
            "torchClock",
            "Torches burn out on AAC's clock (torches category): Realistic Torches' torch, Netherlicious' torches and bone torches, which otherwise burn forever or vanish, and the Aether's torches, which burn forever and are drawn as vanilla's torch with their flame. Each smolders, then becomes an unlit torch that any lighter relights. AAC adds bone torches of the Aether's flames. Burning torches are kept per chunk, so only loaded chunks are looked at, once a second; Realistic Torches no longer ticks every lit torch. Needs Realistic Torches.");
        patch(
            "chestSneakPlacing",
            "A chest placed while sneaking pairs only on purpose: placed against the side of a single chest of its kind, it pairs with that one and takes its facing; placed anywhere else, it stays single. Such a chest never pairs on its own later, and no neighbor takes it. For the chests AdjaChest pairs (vanilla, Netherlicious, Twilight Forest) and, with aetherChestPairs, the Aether's. Needs AdjaChest; use the same setting on client and server.");
        patch(
            "aetherChestPairs",
            "The Aether's Skyroot, Elysian and breakable treasure chests pair as AdjaChest pairs vanilla ones: each with one neighbor of its kind facing the same way, so any number can stand side by side, and placing one turns no other. Needs AdjaChest; use the same setting on client and server.");
        patch(
            "betterFoliageFilterCache",
            "Better Foliage's block filters keep their answers in a map safe across threads. Angelica draws chunks on several threads, and the plain map Better Foliage used could find a block and then give back nothing, crashing the game (NullPointerException in BlockFilterSet.matches). Client only.");
        patch(
            "naturaBerriesStandDown",
            "Natura's wild berry bushes are not generated in Biomic's overworld while wildFoodInBiomic is on.");
        patch(
            "saltyModBeeNests",
            "SaltyMod's bee nests are seeded per chunk, so a seed gives the same nests; none in Biomic's overworld while beeNestsInBiomic is on.");
        patch(
            "saltyModDedicatedServer",
            "SaltyMod's client code stays off a dedicated server, which crashes on it otherwise.");
        patch(
            "angelicaDsWeather",
            "Dynamic Surroundings' rain sounds, splashes and auroras work with Angelica's weather, which otherwise skips them. The game stops at startup if Angelica or Dynamic Surroundings changed in a way the patch doesn't fit.");
        patch(
            "ruinsEndlessIdsStats",
            "Ruins sizes its biome statistics for EndlessIDs' biome IDs, which otherwise overflow them.");
        patch(
            "ruinsWorldHandles",
            "Ruins keeps each world's records apart: in single player a world opened after another in the same game used the first world's, so it skipped that world's chunks and ruin spots and wrote its ruins into that world's file.");
        patch(
            "distantHorizonsChunkTasks",
            "Distant Horizons generates each chunk for its distant terrain as its own server task, so its tick budget applies between chunks instead of letting nine fresh chunks hold one tick and lag the server for seconds. The game stops at startup if Distant Horizons changed in a way the patch doesn't fit.");
        patch(
            "distantHorizonsQuickShutdown",
            "Distant Horizons cancels the work it queued for the server thread before it shuts down, instead of waiting for it in vain: quitting a single-player world no longer holds the game for 10 seconds (long enough for the desktop to report it not responding), nor a server's stop for 5. The game stops at startup if Distant Horizons changed in a way the patch doesn't fit.");
        patch(
            "reccomplexNaturalFill",
            "Recurrent Complex fills the ground under a structure once per column, instead of queueing columns again and again, which could hold the server for seconds. The game stops at startup if Recurrent Complex changed in a way the patch doesn't fit.");
        patch(
            "reccomplexVillages",
            "Recurrent Complex doesn't build its natural structures into villages, where a graveyard could stand inside a house: a structure whose ground plan meets a village's and that reaches near the surface is not placed. Dungeons deep under a village still are. The game stops at startup if Recurrent Complex changed in a way the patch doesn't fit.");
        patch(
            "betterSleepingFixes",
            "Better Sleeping ends the night as soon as its share of players is asleep, not only when someone gets into bed; its sleeping message shows the real share, lying in bed makes nobody tired and the sleep bar's marker follows guiOffsetLeft.");
        patch(
            "hangGliderNerf",
            "OpenBlocks' hang glider glides about 2.5 blocks per block of height instead of 6, dives at a third of the speed, wears out after 4 minutes in the air and is repaired with leather.");
        patch(
            "openBlocksGraveHeight",
            "OpenBlocks places graves up to its maximalPosY (249 by default) below a world's top rather than below Y=256, so in a taller world (Hohenangst's 512) a player who dies high up gets a grave there, not one clamped down to Y=249.");
        patch(
            "aetherParachuteRider",
            "The Aether's parachutes vanish when their rider changes worlds or dies, instead of following them into another dimension, which crashes servers.");
        patch(
            "aetherGloveAccessories",
            "The Aether's copper and netherite gloves can be worn when their own Et Futurum recipe option is on; the Aether mixes the two up.");
        patch(
            "aetherWingFlap",
            "Each player's Aether wings flap with their own beat, slowly while they stand. The Aether flapped them with the beat of whoever was drawn before, and took other players for always flying.");
        patch(
            "bopNoWorldTypeScreen",
            "Biomes O' Plenty doesn't show its world type screen before Create World; the pack picks the world type.");
        patch(
            "distantHorizonsNoDevBuildMessage",
            "Distant Horizons doesn't warn in chat that it is a nightly/unstable build; the pack uses that build on purpose. Its other chat warnings stay.");
        patch(
            "etFuturumNoExperimentalMessage",
            "Et Futurum doesn't warn in chat about its experimental features, which Biomic turns on; the warning stays in the log.");
        patch(
            "dimDoorsEntityChunkGen",
            "A mob going through a Dimensional Doors door can generate the pocket behind it. Hodgepodge's preventEntityChunkLoading hands entity ticks empty chunks, so the dungeon was built into nothing and the game crashed.");
        patch(
            "structureMemoryPerSave",
            "Structure Memory keeps one memory per save (or server), instead of sharing it between saves with the same seed (or servers with the same spawn). A world's memory from before is no longer read.");
        patch(
            "battleTowersTallWorlds",
            "Battle Towers find the ground from the top down and build their floors relative to the world's ground level instead of vanilla's fixed heights, so they spawn in Biomic's tall worlds standing on the surface (searching up from a fixed height stopped at the first cave, and towers rose half buried from cave floors); a chunk becomes a site only by battleTowerChance, and the lines it printed for every new chunk go to the debug log.");
        patch(
            "battleTowersSites",
            "Battle Towers no longer forgets its towers when another world loads or saves, which put new towers next to old ones, and skips sites where Recurrent Complex or Ruins already built.");
        patch(
            "etFuturumConcreteTracking",
            "Falling concrete powder turns into concrete in the first water it reaches from the falling block itself, instead of from Et Futurum's per-tick tracking, which crashed the server (ConcurrentModificationException in doConcreteTracking), made two concrete from one powder falling fast into deep water, and kept powder whose chunk had unloaded.");
        patch(
            "arrowsCatchFire",
            "Arrows catch fire flying through fire or lava, and a burning arrow sets off the TNT minecart it hits, as on modern versions. Dimensional Doors' 6.x traps shoot arrows through fire with this, fire charges without it.");
        patch(
            "villageNamesFlatLookup",
            "Village Names finds a village's name horizontally, so its sign, Antique Atlas marker and waystones agree in tall worlds; it used to name a village twice, at vanilla's height and at its real one.");
        patch(
            "angelicaDimensionShaderOptions",
            "Shader packs use a dimension's own option values there, from config/adventuraarcanacore/shader_dimensions/DIMENSION/PACK.txt (for Limbo: no clouds or stars, a darker atmosphere), instead of drawing every modded dimension like the overworld.");
        patch(
            "angelicaShaderBlockAliases",
            "Shader packs treat the modded blocks in shaderBlockAliases like the vanilla blocks they name (Dimensional Doors' Fadebloom waves like a flower).");
        patch(
            "aetherSkyrootDoorIcon",
            "The Aether's Skyroot Door item draws its icon instead of the missing texture (it asked for the file with the wrong case), and WAILA shows the door with that icon and its name.");
        patch(
            "biblioCraftEasterEggDrops",
            "BiblioCraft's easter egg (mobs and players with certain names drop its tools when they die) drops nothing for tools switched off in its config, instead of stacks without an item that crash the game.");
        patch(
            "biblioCraftNoReadingEnchant",
            "BiblioCraft's Reading enchant, a helmet version of its Reading Glasses, can't be had from the enchanting table, books or anvils.");
        patch(
            "wildMobsSeaScorpionSpawnCap",
            "Wild Mobs' sea scorpions count against the water creature cap they spawn under, instead of against none of it (they spawned without end, tens of thousands in Biomic's deep oceans, bringing the server to a crawl) and against the monster cap.");
        patch(
            "adventureBackpackWornTankLight",
            "Adventure Backpack lights the fluid in a worn backpack's, copter pack's or coal jetpack's tanks where the wearer is, as it does for a placed backpack; it used to take the light of whatever CodeChickenLib drew last, often dark or black.");
        configuration.setCategoryLanguageKey(Categories.BIOMIC, AdventuraArcanaCore.MODID + ".config.biomic");
        configuration.setCategoryComment(
            Categories.BIOMIC,
            "The pack's additions to Biomic's worlds, each applied only with Biomic and the mods it names. Changes need a restart.");
        configuration.setCategoryRequiresMcRestart(Categories.BIOMIC, true);
        pmpIvyForVines = biomic(
            "pmpIvyForVines",
            "Vanilla's vine becomes Plant Mega Pack's ivy in Biomic's worlds (Swedish where hot and wet, Japanese elsewhere). Needs Plant Mega Pack.");
        wildFoodInBiomic = biomic(
            "wildFoodInBiomic",
            "HarvestCraft's food grows wild in Biomic's habitats (crop patches, berry bushes, fruit groves, fruiting vines), and HarvestCraft's own gardens, fruit trees, hives and salt stand down there. Needs HarvestCraft.");
        beeNestsInBiomic = biomic(
            "beeNestsInBiomic",
            "Et Futurum's bee nests hang on trees in Biomic's habitats, the only wild hives there. Needs Et Futurum Requiem.");
        pmpBambooInBiomic = biomic(
            "pmpBambooInBiomic",
            "Origen's bamboo grows as Plant Mega Pack's species in Biomic's worlds (Moso by default, Tropical Blue in the sinkhole's jungle, Giant Timber in the fossilized fenlands), and Growthcraft's bamboo stands down. Needs Plant Mega Pack.");
        madakeInBiomic = biomic(
            "madakeInBiomic",
            "Regions Unexplored's bamboo tree is Madake in Biomic's worlds and grows in both of the Bamboo Forest's tree groups.");
        bambooShootsInBiomic = biomic(
            "bambooShootsInBiomic",
            "Bamboo shoots grow wild where their bamboo grows in Biomic's habitats, and HarvestCraft's generic shoot crop becomes a Moso shoot when a chunk generates. Needs HarvestCraft (enableBambooShoots).");
        floraInBiomic = biomic(
            "floraInBiomic",
            "Other mods' plants in Biomic's habitats (Plant Mega Pack's above all, BOP's, SaltyMod's, Natura's), epiphytes and tree mushrooms on trunks, vines, water plants, flower patches and the sky islands' plants; Plant Mega Pack's own generator stands down. Each plant needs its mod.");
        leafDecorInBiomic = biomic(
            "leafDecorInBiomic",
            "Origen's leaf decor in BOP's and Plant Mega Pack's plants: their shrubs in bush clumps, ivy and flowering vines on cliffs, moss and willow on walls. Each plant needs its mod.");
        netherFoodInBiomic = biomic(
            "netherFoodInBiomic",
            "Harvest the Nether grows wild in Biomic's Tartarus: its crops in patches by biome, glow flowers over Soul Valley, ignis trees here and there; its own gardens and trees stand down. Needs Harvest the Nether.");
        decorInBiomic = biomic(
            "decorInBiomic",
            "BOP's and Plant Mega Pack's blocks in Biomic's cave and sea decoration: moss, ivy, glowshrooms, stone formations, bones and miner's delight in caves; Plant Mega Pack's kelp, eelgrass, sea plants and corals in the seas, in place of Biomic's ported seagrass and algae. Each block needs its mod.");
        configuration.setCategoryLanguageKey(Categories.SOUNDS, AdventuraArcanaCore.MODID + ".config.sounds");
        configuration.setCategoryComment(Categories.SOUNDS, "The pack's sound changes.");
        caveMoods = configuration.get(
            Categories.SOUNDS,
            "caveMoods",
            false,
            "Vanilla's random cave sounds play underground, with the ones Et Futurum adds. Off so scary sounds come from real creatures; other mods' replacements, such as Netherlicious's Nether moods, still play. Client side.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.caveMoods")
            .getBoolean();
        wolfHowls = configuration
            .get(
                Categories.SOUNDS,
                "wolfHowls",
                true,
                "Wild wolves howl at night in the Overworld, and wild wolves nearby may join in. Server side.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.wolfHowls")
            .getBoolean();
        configuration.setCategoryLanguageKey(Categories.TORCHES, AdventuraArcanaCore.MODID + ".config.torches");
        configuration.setCategoryComment(
            Categories.TORCHES,
            "Torches that burn out, after Realistic Torches: Realistic Torches' torch, Netherlicious' soul, foxfire and shadow torches and bone torches, and the Aether's ambrosium, hellfire and ethereal (cold fire) torches with AAC's bone torches of those flames. A lit torch smolders for the last tenth of its time, then becomes an unlit torch (the wooden ones Realistic Torches', the bone ones AAC's), which it also drops. Any lighter relights it, into its own flame. Each torch has its own settings below; Realistic Torches' torch.burnoutTime no longer applies. Needs Realistic Torches (Netherlicious for its torches and every bone torch, the Aether for its flames) and the torchClock patch. Changes need a restart.");
        configuration.setCategoryRequiresMcRestart(Categories.TORCHES, true);
        torchLighters = configuration.get(
            Categories.TORCHES,
            "torchLighters",
            new String[] { "Natura:natura.flintandblaze" },
            "Items that light torches with a normal flame, like the vanilla flint and steel, as registry names. Needs the realisticTorchesLighters patch.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.torchLighters")
            .getStringList();
        torchRainDelay = configuration.get(
            Categories.TORCHES,
            "rainDelay",
            100,
            "Ticks a torch must stand in the rain before it goes out, if rain puts it out (20 ticks = 1 second). Rain is looked for once a second.",
            0,
            72000)
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.rainDelay")
            .getInt();
        torchesBurnUnloaded = configuration.get(
            Categories.TORCHES,
            "burnUnloaded",
            true,
            "Torches keep burning while their chunk is unloaded, catching up when it loads again; off pauses them while nobody is near. Rain only reaches torches in loaded chunks.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.burnUnloaded")
            .getBoolean();
        glowstonePaste = configuration.get(
            Categories.TORCHES,
            "glowstonePaste",
            true,
            "Glowstone paste, from glowstone dust, clay and a water bucket, turns a lit Realistic Torches torch into a vanilla torch that never goes out. Needs Realistic Torches.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.glowstonePaste")
            .getBoolean();
        ambrosiumLighter = configuration.get(
            Categories.TORCHES,
            "ambrosiumLighter",
            true,
            "Zanite and Ambrosium, from an ambrosium shard and a zanite gemstone, lights torches with ambrosium flame, as the Aether's Zanite and Cinerarium lights them with hellfire and its Zanite and Cyro with cold fire. Needs the Aether and Realistic Torches.")
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config.ambrosiumLighter")
            .getBoolean();
        for (String[] torch : TORCH_KINDS) {
            String category = Categories.TORCHES + "." + torch[0];
            configuration.setCategoryLanguageKey(category, AdventuraArcanaCore.MODID + ".config.torches." + torch[0]);
            configuration.setCategoryComment(category, torch[1] + ".");
            torchBurnout.put(
                torch[0],
                configuration.get(
                    category,
                    "burnout",
                    72000,
                    "Ticks until it goes out, the last tenth smoldering (20 ticks = 1 second). 0 or less: it never does.")
                    .setLanguageKey(AdventuraArcanaCore.MODID + ".config.torchBurnout")
                    .getInt());
            torchRain.put(
                torch[0],
                configuration
                    .get(
                        category,
                        "rain",
                        !torch[0].startsWith("soul") && !torch[0].startsWith("ethereal"),
                        "Whether rain puts it out (after rainDelay), and keeps it from being lit.")
                    .setLanguageKey(AdventuraArcanaCore.MODID + ".config.torchRain")
                    .getBoolean());
        }
        dropStaleKeys();
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static boolean biomic(String key, String comment) {
        return configuration.get(Categories.BIOMIC, key, true, comment)
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config." + key)
            .setRequiresMcRestart(true)
            .getBoolean();
    }

    private static void patch(String key, String comment) {
        configuration.get(Categories.PATCHES, key, true, comment)
            .setLanguageKey(AdventuraArcanaCore.MODID + ".config." + key)
            .setRequiresMcRestart(true);
    }

    /**
     * Drops the keys of options a former version had: they stay in the file without a comment, and so show on the
     * config screen without a description. Every current option gets its comment while syncing.
     */
    private static void dropStaleKeys() {
        for (String name : configuration.getCategoryNames()) {
            ConfigCategory category = configuration.getCategory(name);
            for (String key : new ArrayList<>(category.keySet())) {
                Property property = category.get(key);
                if (property.comment == null || property.comment.trim()
                    .isEmpty()) category.remove(key);
            }
        }
    }

    public static Configuration getRawConfig() {
        return configuration;
    }
}
