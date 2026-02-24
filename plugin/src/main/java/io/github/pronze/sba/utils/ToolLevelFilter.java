package io.github.pronze.sba.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.*;

public class ToolLevelFilter {
    
    // Уровни инструментов в порядке прогрессии
    private static final Map<String, Integer> TOOL_LEVELS = new HashMap<>();
    
    static {
        // ДЕРЕВЯННЫЕ - уровень 0
        TOOL_LEVELS.put("WOODEN_PICKAXE", 0);
        TOOL_LEVELS.put("WOODEN_AXE", 0);
        TOOL_LEVELS.put("WOODEN_SHOVEL", 0);
        TOOL_LEVELS.put("WOODEN_SWORD", 0);
        
        // КАМЕННЫЕ - уровень 1
        TOOL_LEVELS.put("STONE_PICKAXE", 1);
        TOOL_LEVELS.put("STONE_AXE", 1);
        TOOL_LEVELS.put("STONE_SHOVEL", 1);
        TOOL_LEVELS.put("STONE_SWORD", 1);
        
        // ЖЕЛЕЗНЫЕ - уровень 2
        TOOL_LEVELS.put("IRON_PICKAXE", 2);
        TOOL_LEVELS.put("IRON_AXE", 2);
        TOOL_LEVELS.put("IRON_SHOVEL", 2);
        TOOL_LEVELS.put("IRON_SWORD", 2);
        
        // АЛМАЗНЫЕ - уровень 3
        TOOL_LEVELS.put("DIAMOND_PICKAXE", 3);
        TOOL_LEVELS.put("DIAMOND_AXE", 3);
        TOOL_LEVELS.put("DIAMOND_SHOVEL", 3);
        TOOL_LEVELS.put("DIAMOND_SWORD", 3);
    }
    
    /**
     * Получить тип инструмента (PICKAXE, AXE, SHOVEL, SWORD)
     */
    public static String getToolType(String materialName) {
        if (materialName.contains("PICKAXE")) return "PICKAXE";
        if (materialName.contains("AXE")) return "AXE";
        if (materialName.contains("SHOVEL")) return "SHOVEL";
        if (materialName.contains("SWORD")) return "SWORD";
        return null;
    }
    
    /**
     * Получить текущий уровень инструмента у игрока (0=дерево, 1=камень, 2=железо, 3=алм)
     */
    public static int getCurrentToolLevel(Player player, String toolType) {
        PlayerInventory inv = player.getInventory();
        
        // Проверяем весь инвентарь и броню
        ItemStack[] allItems = new ItemStack[inv.getSize() + inv.getArmorContents().length];
        System.arraycopy(inv.getContents(), 0, allItems, 0, inv.getContents().length);
        System.arraycopy(inv.getArmorContents(), 0, allItems, inv.getContents().length, inv.getArmorContents().length);
        
        int maxLevel = -1; // -1 означает, что предмета нет вообще
        
        for (ItemStack item : allItems) {
            if (item == null) continue;
            
            String materialName = item.getType().name();
            if (materialName.contains(toolType)) {
                Integer level = TOOL_LEVELS.get(materialName);
                if (level != null && level > maxLevel) {
                    maxLevel = level;
                }
            }
        }
        
        return maxLevel;
    }
    
    /**
     * Проверить, доступен ли этот предмет для покупки
     * Возвращает true, если предмет можно показать в шопе
     */
    public static boolean isItemAvailable(Player player, String itemMaterialName) {
        String toolType = getToolType(itemMaterialName);
        
        // Если это не инструмент - показываем
        if (toolType == null) {
            return true;
        }
        
        Integer itemLevel = TOOL_LEVELS.get(itemMaterialName);
        if (itemLevel == null) {
            return true; // Если не в списке - показываем
        }
        
        int currentLevel = getCurrentToolLevel(player, toolType);
        
        // Если у игрока нет инструмента, показываем деревянный (уровень 0)
        if (currentLevel == -1) {
            return itemLevel == 0;
        }
        
        // Показываем только следующий уровень
        return itemLevel == currentLevel + 1;
    }
    
    /**
     * Получить название инструмента следующего уровня
     */
    public static String getNextToolLevel(String currentToolMaterial) {
        String toolType = getToolType(currentToolMaterial);
        if (toolType == null) return null;
        
        Integer currentLevel = TOOL_LEVELS.get(currentToolMaterial);
        if (currentLevel == null) return null;
        
        int nextLevel = currentLevel + 1;
        
        // Ищем инструмент следующего уровня того же типа
        for (Map.Entry<String, Integer> entry : TOOL_LEVELS.entrySet()) {
            if (entry.getValue() == nextLevel && entry.getKey().contains(toolType)) {
                return entry.getKey();
            }
        }
        
        return null; // Максимальный уровень достигнут
    }
}
