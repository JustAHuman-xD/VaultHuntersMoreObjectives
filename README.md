# Vault Hunters More Objectives
Adds two new objectives to Vault Hunters.

## Cow Vaults
Config: `configs/the_vault/moreobjectives_cow_vault.json`<br>
Breakdown:
```json5
{
  "theme": "the_vault:classic_vault_chaos", // The required theme to create a cow vault (set to blank for any theme)
  "objective": "", // The required objective to create a cow vault (set to blank for any objective)
  "cowVaultTrigger": { // The vault modifiers required to create a cow vault
    "the_vault:wild": 5, // Five stacks of wild modifiers
    "the_vault:furious_mobs": 5, // Five stacks of furious mobs modifiers
    "the_vault:infuriated_mobs": 5, // Five stacks of infuriated mobs modifiers
  },
  "extraModifiers": { // Additional modifiers added to any created cow vaults
    "the_vault:coin_nuke": 1, // 1 stack of coin nuke modifier
  }
}
```


## Fruit Cake Vaults
Config: `configs/the_vault/moreobjectives_fruit_cake.json`<br>
Breakdown:
```json5
{
  "chance": 0.1, // The chance of a fruit cake vault spawning instead of a normal one (0.1 = 10%)
  "startModifiers": { // Additional modifiers added to any created fruit cake vaults
    "the_vault:extended": 1, // 1 stack of extended modifier
  },
  "fruits": {
    "the_vault:sweet_kiwi": {
      "icon": "the_vault:sweet_kiwi", // The item id for the icon (defaults to id, can be overridden)
      "ticks": 200, // The number of ticks added to the vault timer (IF your icon is a vault fruit, defaults to the fruits ticks, can be overridden)
      "weight": 0.6888, // The weight for this fruit to be chosen (must be explicitly set)
    },
    "the_vault:grapes": {
      "weight": 0.1721
    },
    "the_vault:bitter_lemon": {
      "weight": 0.0767
    },
    "the_vault:mango": {
      "weight": 0.0340
    },
    "the_vault:sour_orange": {
      "weight": 0.0191
    },
    "the_vault:star_fruit": {
      "weight": 0.0085
    },
    "the_vault:mystic_pear": {
      "weight": 0.0008
    }
  },
  "fruitScaling": { // Fruits to disable based on # of players in the vault
    "30": [
      "the_vault:star_fruit"
    ],
    "32": [
      "the_vault:star_fruit",
      "the_vault:mango"
    ]
  }
}
```