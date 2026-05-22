package com.example.domo.ui.reading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BookItem(
    val title: String,
    val author: String,
    val color: Color,
    val ageRange: String,
)

private val sampleBooks = listOf(
    BookItem("O Pequeno Príncipe", "Antoine de Saint-Exupéry", Color(0xFF43A047), "6–10 anos"),
    BookItem("Alice no País das Maravilhas", "Lewis Carroll", Color(0xFF8E24AA), "7–11 anos"),
    BookItem("Pinóquio", "Carlo Collodi", Color(0xFF1E88E5), "5–9 anos"),
    BookItem("Peter Pan", "J.M. Barrie", Color(0xFFFF8F00), "7–12 anos"),
    BookItem("A Bela e a Fera", "Madame de Beaumont", Color(0xFFE53935), "5–9 anos"),
    BookItem("Chapeuzinho Vermelho", "Charles Perrault", Color(0xFF00897B), "4–7 anos"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingBottomSheet(
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF8F9FF),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SheetHeader()
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(sampleBooks) { book ->
                    BookCard(book = book)
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun SheetHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoStories,
            contentDescription = null,
            tint = Color(0xFF1565C0),
            modifier = Modifier.size(28.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "Hora de Ler!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D2B6E),
            )
            Text(
                text = "Escolha um livro para começar",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF0D2B6E).copy(alpha = 0.6f),
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun BookCard(book: BookItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { /* Future: abrir o livro */ }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(book.color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = book.title.first().toString(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF555577),
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(book.color.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = book.ageRange,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = book.color,
            )
        }
    }
}
