package content.global.skill.herblore.item

import shared.consts.Items

enum class GuthixRest(val ingredients: Set<Int>, val product: Int) {
    HERB_TEA_MIX_1(setOf(Items.CLEAN_HARRALANDER_255), Items.HERB_TEA_MIX_4464),
    HERB_TEA_MIX_2(setOf(Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4466),
    HERB_TEA_MIX_3(setOf(Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4468),
    HERB_TEA_MIX_4(setOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4470),
    HERB_TEA_MIX_5(setOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4472),
    HERB_TEA_MIX_6(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4474),
    HERB_TEA_MIX_7(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4476),
    HERB_TEA_MIX_8(setOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_MARRENTILL_251, Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4478),
    HERB_TEA_MIX_9(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4480),
    HERB_TEA_MIX_10(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_HARRALANDER_255), Items.HERB_TEA_MIX_4482),
    COMPLETE_MIX(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251, Items.CLEAN_HARRALANDER_255), Items.GUTHIX_REST3_4419);

    companion object {
        val byIngredients: Map<Set<Int>, GuthixRest> = values().associateBy { it.ingredients }
        val byTeaId: Map<Int, GuthixRest> = values().associateBy { it.product }
    }
}