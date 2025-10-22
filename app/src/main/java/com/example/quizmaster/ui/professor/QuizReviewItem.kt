package com.example.quizmaster.ui.professor

import android.os.Parcel
import android.os.Parcelable

/**
 * Représente un élément de révision de quiz: la question et les options (et la bonne réponse).
 */
data class QuizReviewItem(
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questionText)
        parcel.writeStringList(options)
        parcel.writeString(correctAnswer)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<QuizReviewItem> {
        override fun createFromParcel(parcel: Parcel): QuizReviewItem = QuizReviewItem(parcel)
        override fun newArray(size: Int): Array<QuizReviewItem?> = arrayOfNulls(size)
    }
}
