package com.example.quizmaster.ui.professor

import android.os.Parcel
import android.os.Parcelable

/**
 * Représente un élément de révision de quiz: la question, la bonne réponse et la réponse de l'étudiant.
 */
data class QuizReviewItem(
    val questionText: String,
    val correctAnswer: String,
    val studentAnswer: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questionText)
        parcel.writeString(correctAnswer)
        parcel.writeString(studentAnswer)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<QuizReviewItem> {
        override fun createFromParcel(parcel: Parcel): QuizReviewItem = QuizReviewItem(parcel)
        override fun newArray(size: Int): Array<QuizReviewItem?> = arrayOfNulls(size)
    }
}
