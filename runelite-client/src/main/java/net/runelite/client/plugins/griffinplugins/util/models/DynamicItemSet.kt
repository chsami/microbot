package net.runelite.client.plugins.griffinplugins.griffintrainer.models

class DynamicItemSet {
    private val itemsAndQuantities: MutableList<Triple<Int, Int, Boolean>> = mutableListOf()

    fun add(itemID: Int, quantity: Int, equip: Boolean) {
        itemsAndQuantities.add(Triple(itemID, quantity, equip))
    }

    fun getItems(): List<Triple<Int, Int, Boolean>> {
        return itemsAndQuantities.reversed()
    }
}