//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.moreobjectives.configs;

public class Configs {
    public static CowVaultConfig COW_VAULT;
    public static FruitCakeConfig FRUIT_CAKE;

    public static void registerConfigs() {
        COW_VAULT = new CowVaultConfig().readConfig();
        FRUIT_CAKE = new FruitCakeConfig().readConfig();
    }
}