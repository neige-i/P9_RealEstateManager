package com.openclassrooms.realestatemanager.data

import androidx.annotation.StringRes
import com.openclassrooms.realestatemanager.R

enum class PointOfInterest(@StringRes val labelId: Int) : Localized {
    BAR(R.string.label_poi_bar),
    CAFE(R.string.label_poi_cafe),
    RESTAURANT(R.string.label_poi_restaurant),
    HOSPITAL(R.string.label_poi_hospital),
    THEATER(R.string.label_poi_movie_theater),
    PARK(R.string.label_poi_park),
    STADIUM(R.string.label_poi_stadium),
    MALL(R.string.label_poi_shopping_mall),
    SCHOOL(R.string.label_poi_school),
    UNIVERSITY(R.string.label_poi_university),
    SUBWAY(R.string.label_poi_subway_station),
    TRAIN(R.string.label_poi_train_station),
    ;

    override val stringId: Int = labelId

    override fun fromStringId(@StringRes stringId: Int): Localized = values().first { it.labelId == this.stringId }

    companion object {
        fun fromLabelId(@StringRes labelId: Int): PointOfInterest {
            return values().first { it.labelId == labelId }
        }
    }
}