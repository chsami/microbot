package net.runelite.client.plugins.griffinplugins.griffintrainer.models

class DynamicItemSet {
    private val itemsAndQuantities: MutableList<Pair<Int, Int>> = mutableListOf()

    fun add(itemID: Int, quantity: Int) {
        itemsAndQuantities.add(Pair(itemID, quantity))
    }

    fun getItems(): List<Pair<Int, Int>> {
        return itemsAndQuantities.reversed()
    }
}