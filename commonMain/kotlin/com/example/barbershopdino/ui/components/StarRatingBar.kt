package com.example.barbershopdino.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = true
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val starValueFull = i.toFloat()
            val starValueHalf = i - 0.5f

            val isFull = rating >= starValueFull
            val isHalf = !isFull && rating >= starValueHalf

            val starChar = when {
                isFull -> "★"
                isHalf -> "½"
                else -> "☆"
            }

            val starColor = if (isFull || isHalf) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }

            Text(
                text = starChar,
                style = MaterialTheme.typography.titleLarge,
                color = starColor,
                modifier = if (isSelectable) {
                    Modifier.clickable {
                        // Toggle logic: if clicking on half star range, select half, else full
                        val newRating = if (rating == starValueFull) {
                            starValueHalf
                        } else if (rating == starValueHalf) {
                            if (i == 1) 0.5f else (i - 1).toFloat()
                        } else {
                            starValueFull
                        }
                        onRatingChanged(newRating)
                    }
                } else Modifier
            )
        }

        Text(
            text = "(${rating}★)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
